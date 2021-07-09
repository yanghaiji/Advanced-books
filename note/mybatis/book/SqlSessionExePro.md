#### Mybatis SqlSession 执行SQL流程

在看源码之前，我们需要了解一些基本知识

**SqlSession**

```
SqlSession是一个接口，它有两个实现类：
	- DefaultSqlSession：默认实现类
	- SqlSessionManager：已经弃用的实现类，所以我们不需要关注他
SqlSession是与数据库交互的顶层类，通常与ThreadLocal绑定，一个会话使用一个SqlSession，SqlSession是线程不安全的，使用完毕需要close()

public class DefaultSqlSession implements SqlSession {
	private final Configuration configuration;
	private final Executor executor;
}
SqlSession中最重要的两个变量：
	- Configuration：核心配置类，也是初始化时传过来的
	- Executor：实际执行SQL的执行器
```

**Executor**

```
Executor是一个接口，有三个实现类
	- BatchExecutor 重用语句，并执行批量更新
	- ReuseExecutor 重用预处理语句prepared statements
	- SimpleExecutor 普通的执行器，默认使用
```

了解完基本知识后，我们接着往下看代码。

当创建完SqlSessionFactory后，就可以创建SqlSession，然后使用SqlSession进行增删改查：

```
// 1. 读取配置文件，读成字节输入流，注意：现在还没解析
InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
// 2. 解析配置文件，封装Configuration对象   创建DefaultSqlSessionFactory对象
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

SqlSession sqlSession = sqlSessionFactory.openSession();
List<Object> objects = sqlSession.selectList("namespace.id");
```

**我们先去看`openSession()`方法，创建了SqlSession**

```
//6. 进入openSession方法
@Override
public SqlSession openSession() {
    //getDefaultExecutorType()传递的是SimpleExecutor
    // level：数据库事物级别，null
    return openSessionFromDataSource(configuration.getDefaultExecutorType(), null, false);
}

//7. 进入openSessionFromDataSource。
//ExecutorType 为Executor的类型，TransactionIsolationLevel为事务隔离级别，autoCommit是否开启事务
//openSession的多个重载方法可以指定获得的SeqSession的Executor类型和事务的处理
private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
    Transaction tx = null;
    try {
        // 获得 Environment 对象
        final Environment environment = configuration.getEnvironment();
        // 创建 Transaction 对象
        final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
        tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
        // 创建 Executor 对象
        final Executor executor = configuration.newExecutor(tx, execType);
        // 创建 DefaultSqlSession 对象
        return new DefaultSqlSession(configuration, executor, autoCommit);
    } catch (Exception e) {
        // 如果发生异常，则关闭 Transaction 对象
        closeTransaction(tx); // may have fetched a connection so lets call close()
        throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    } finally {
        ErrorContext.instance().reset();
    }
}
```

通过源码可以清晰的看到，会话工厂创建了`Environment`，`Transaction`，`Executor`，`DefaultSqlSession`对象，并且对于会话对象来说，他的`autoCommit`默认为`false`，默认不自动提交。

**然后我回到原来的代码，接着就需要使用SqlSession进行增删改查操作了**

所以我们进入`selectList()`查看

```
//8.进入selectList方法，多个重载方法
@Override
public <E> List<E> selectList(String statement) {
    return this.selectList(statement, null);
}

@Override
public <E> List<E> selectList(String statement, Object parameter) {
    return this.selectList(statement, parameter, RowBounds.DEFAULT);
}

@Override
public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
    try {
        // 获得 MappedStatement 对象
        MappedStatement ms = configuration.getMappedStatement(statement);
        // 执行查询
        return executor.query(ms, wrapCollection(parameter), rowBounds, Executor.NO_RESULT_HANDLER);
    } catch (Exception e) {
        throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
    } finally {
        ErrorContext.instance().reset();
    }
}
```

`selectList`有多个重载方法，进入到最终方法后，我们可以看到它做了两件事

- 通过`statementId`，从`Configuration`中取`MappedStatement`对象，就是存放了sql语句，返回值类型，输入值类型的对象
- 然后委派`Executor`执行器去执行具体的增删改查方法

**所以，对于实际JDBC的操作，我们还需要进入Executor中查看**

#### Mybatis之Executor

我们继续从刚刚`selectList`源码中，进入`Executor`查看

```
return executor.query(ms, wrapCollection(parameter), rowBounds, Executor.NO_RESULT_HANDLER);
```



```
//此方法在SimpleExecutor的父类BaseExecutor中实现
@Override
public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
    //根据传入的参数动态获得SQL语句，最后返回用BoundSql对象表示
    BoundSql boundSql = ms.getBoundSql(parameter);
    //为本次查询创建缓存的Key
    CacheKey key = createCacheKey(ms, parameter, rowBounds, boundSql);
    // 查询
    return query(ms, parameter, rowBounds, resultHandler, key, boundSql);
}
```

拆分成了三大步：

（1）先调用`MappedStatement`的`getBoundSql`方法，获取解析后的SQL语句，解析工作是由`SqlSourceBuilder`完成的

> 什么叫解析后的SQL语句呢？因为Mybatis编写SQL语句时，会用到动态SQL，比如`#{}`占位符，这种占位符JDBC是不认识的，所以需要将其转换成`？`占位符，并且将其内部的字段名存储起来，后面填充参数的时候好使用反射获取值。

```
/**
 * 执行解析原始 SQL ，成为 SqlSource 对象
 *
 * @param originalSql 原始 SQL
 * @param parameterType 参数类型
 * @param additionalParameters 附加参数集合。可能是空集合，也可能是 {@link org.apache.ibatis.scripting.xmltags.DynamicContext#bindings} 集合
 * @return SqlSource 对象
 */
public SqlSource parse(String originalSql, Class<?> parameterType, Map<String, Object> additionalParameters) {
    // 创建 ParameterMappingTokenHandler 对象
    ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(configuration, parameterType, additionalParameters);
    // 创建 GenericTokenParser 对象
    GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
    // 执行解析
    String sql = parser.parse(originalSql);
    // 创建 StaticSqlSource 对象
    return new StaticSqlSource(configuration, sql, handler.getParameterMappings());
}
```

上面代码就可以看到，会将拆分`#{`和`}`，进行解析

（2）根据查询条件，创建缓存`key`，用来接下来去缓存查找是否有已经执行过的结果

（3）调用重载`query()`方法

**接着我们进入重载方法查看：**

```
@Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
        ErrorContext.instance().resource(ms.getResource()).activity("executing a query").object(ms.getId());
        // 已经关闭，则抛出 ExecutorException 异常
        if (closed) {
            throw new ExecutorException("Executor was closed.");
        }
        // 清空本地缓存，如果 queryStack 为零，并且要求清空本地缓存。
        if (queryStack == 0 && ms.isFlushCacheRequired()) {
            clearLocalCache();
        }
        List<E> list;
        try {
            // queryStack + 1
            queryStack++;
            // 从一级缓存中，获取查询结果
            list = resultHandler == null ? (List<E>) localCache.getObject(key) : null;
            // 获取到，则进行处理
            if (list != null) {
                handleLocallyCachedOutputParameters(ms, key, parameter, boundSql);
            // 获得不到，则从数据库中查询
            } else {
                list = queryFromDatabase(ms, parameter, rowBounds, resultHandler, key, boundSql);
            }
        } finally {
            // queryStack - 1
            queryStack--;
        }
        if (queryStack == 0) {
            // 执行延迟加载
            for (DeferredLoad deferredLoad : deferredLoads) {
                deferredLoad.load();
            }
            // issue #601
            // 清空 deferredLoads
            deferredLoads.clear();
            // 如果缓存级别是 LocalCacheScope.STATEMENT ，则进行清理
            if (configuration.getLocalCacheScope() == LocalCacheScope.STATEMENT) {
                // issue #482
                clearLocalCache();
            }
        }
        return list;
    }
```

主要的逻辑：

- 从一级缓存取数据，如果有直接使用缓存的进行接下来的操作
- 如果没有，从数据库查询

**进入queryFromDatabase()方法：**

```
// 从数据库中读取操作
private <E> List<E> queryFromDatabase(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
    List<E> list;
    // 在缓存中，添加占位对象。此处的占位符，和延迟加载有关，可见 `DeferredLoad#canLoad()` 方法
    localCache.putObject(key, EXECUTION_PLACEHOLDER);
    try {
        // 执行读操作
        list = doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
    } finally {
        // 从缓存中，移除占位对象
        localCache.removeObject(key);
    }
    // 添加到缓存中
    localCache.putObject(key, list);
    // 暂时忽略，存储过程相关
    if (ms.getStatementType() == StatementType.CALLABLE) {
        localOutputParameterCache.putObject(key, parameter);
    }
    return list;
}

@Override
public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
    Statement stmt = null;
    try {
        Configuration configuration = ms.getConfiguration();
        // 传入参数创建StatementHanlder对象来执行查询
        StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
        // 创建jdbc中的statement对象
        stmt = prepareStatement(handler, ms.getStatementLog());
        // 执行 StatementHandler  ，进行读操作
        return handler.query(stmt, resultHandler);
    } finally {
        // 关闭 StatementHandler 对象
        closeStatement(stmt);
    }
}
```

通过代码可以看到，对于实际与JDBC交互的代码，`Executor`也懒得搞，又像`SqlSession`一样，委派给小弟`StatementHandler`了。

#### Mybatis之StatementHandler

我们从刚刚的Executor的代码查看

```
@Override
public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
    Statement stmt = null;
    try {
        Configuration configuration = ms.getConfiguration();
        // 传入参数创建StatementHanlder对象来执行查询
        StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
        // 创建jdbc中的statement对象
        stmt = prepareStatement(handler, ms.getStatementLog());
        // 执行 StatementHandler  ，进行读操作
        return handler.query(stmt, resultHandler);
    } finally {
        // 关闭 StatementHandler 对象
        closeStatement(stmt);
    }
}
```

可以看到，这里创建完`StatementHandler`后，回调用`prepareStatement()`方法，用来创建Statement对象

我们进入`prepareStatement`方法中查看

```
// 初始化 StatementHandler 对象
private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
    Statement stmt;
    // 获得 Connection 对象
    Connection connection = getConnection(statementLog);
    // 创建 Statement 或 PrepareStatement 对象
    stmt = handler.prepare(connection, transaction.getTimeout());
    // 设置 SQL 上的参数，例如 PrepareStatement 对象上的占位符
    handler.parameterize(stmt);
    return stmt;
}

@Override
public void parameterize(Statement statement) throws SQLException {
    //使用ParameterHandler对象来完成对Statement的设值
    parameterHandler.setParameters((PreparedStatement) statement);
}
```

这里可以看到，它实际是使用`ParameterHandler`来设置`Statement`的参数

```
@Override
public void setParameters(PreparedStatement ps) {
    ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
    // 遍历 ParameterMapping 数组
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    if (parameterMappings != null) {
        for (int i = 0; i < parameterMappings.size(); i++) {
            // 获得 ParameterMapping 对象
            ParameterMapping parameterMapping = parameterMappings.get(i);
            if (parameterMapping.getMode() != ParameterMode.OUT) {
                // 获得值
                Object value;
                String propertyName = parameterMapping.getProperty();
                if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (parameterObject == null) {
                    value = null;
                } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    value = parameterObject;
                } else {
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    value = metaObject.getValue(propertyName);
                }
                // 获得 typeHandler、jdbcType 属性
                TypeHandler typeHandler = parameterMapping.getTypeHandler();
                JdbcType jdbcType = parameterMapping.getJdbcType();
                if (value == null && jdbcType == null) {
                    jdbcType = configuration.getJdbcTypeForNull();
                }
                // 设置 ? 占位符的参数
                try {
                    typeHandler.setParameter(ps, i + 1, value, jdbcType);
                } catch (TypeException | SQLException e) {
                    throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                }
            }
        }
    }
}
```

这段代码的主要目的，就是获取入参，然后根据值，来设置`？`占位符的参数

`TypeHandler`是具体进行参数设置的对象

所以`handler.prepare(connection, transaction.getTimeout());`方法，就是使用`ParameterHandler`来对占位符位置的参数进行值设置

**然后我们回到Executor，查看`handler.query()`方法**

```
@Override
public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
    PreparedStatement ps = (PreparedStatement) statement;
    // 执行查询
    ps.execute();
    // 处理返回结果
    return resultSetHandler.handleResultSets(ps);
}
```

代码很简单，这里直接使用JDBC的`PreparedStatement`来进行SQL执行，然后使用`ResultSetHandler`进行结果数据封装处理。

**进入ResultSetHandler**

```
@Override
public List<Object> handleResultSets(Statement stmt) throws SQLException {
    ErrorContext.instance().activity("handling results").object(mappedStatement.getId());

    // 多 ResultSet 的结果集合，每个 ResultSet 对应一个 Object 对象。而实际上，每个 Object 是 List<Object> 对象。
    // 在不考虑存储过程的多 ResultSet 的情况，普通的查询，实际就一个 ResultSet ，也就是说，multipleResults 最多就一个元素。
    final List<Object> multipleResults = new ArrayList<>();

    int resultSetCount = 0;
    // 获得首个 ResultSet 对象，并封装成 ResultSetWrapper 对象
    ResultSetWrapper rsw = getFirstResultSet(stmt);

    // 获得 ResultMap 数组
    // 在不考虑存储过程的多 ResultSet 的情况，普通的查询，实际就一个 ResultSet ，也就是说，resultMaps 就一个元素。
    List<ResultMap> resultMaps = mappedStatement.getResultMaps();
    int resultMapCount = resultMaps.size();
    validateResultMapsCount(rsw, resultMapCount); // 校验
    while (rsw != null && resultMapCount > resultSetCount) {
        // 获得 ResultMap 对象
        ResultMap resultMap = resultMaps.get(resultSetCount);
        // 处理 ResultSet ，将结果添加到 multipleResults 中
        handleResultSet(rsw, resultMap, multipleResults, null);
        // 获得下一个 ResultSet 对象，并封装成 ResultSetWrapper 对象
        rsw = getNextResultSet(stmt);
        // 清理
        cleanUpAfterHandlingResultSet();
        // resultSetCount ++
        resultSetCount++;
    }

    // 因为 `mappedStatement.resultSets` 只在存储过程中使用，本系列暂时不考虑，忽略即可
    // ···

    // 如果是 multipleResults 单元素，则取首元素返回
    return collapseSingleResultList(multipleResults);
}

 // 处理 ResultSet ，将结果添加到 multipleResults 中
private void handleResultSet(ResultSetWrapper rsw, ResultMap resultMap, List<Object> multipleResults, ResultMapping parentMapping) throws SQLException {
    try {
        // 暂时忽略，因为只有存储过程的情况，调用该方法，parentMapping 为非空
        if (parentMapping != null) {
            handleRowValues(rsw, resultMap, null, RowBounds.DEFAULT, parentMapping);
        } else {
            // 如果没有自定义的 resultHandler ，则创建默认的 DefaultResultHandler 对象
            if (resultHandler == null) {
                // 创建 DefaultResultHandler 对象
                DefaultResultHandler defaultResultHandler = new DefaultResultHandler(objectFactory);
                // 处理 ResultSet 返回的每一行 Row
                handleRowValues(rsw, resultMap, defaultResultHandler, rowBounds, null);
                // 添加 defaultResultHandler 的处理的结果，到 multipleResults 中
                multipleResults.add(defaultResultHandler.getResultList());
            } else {
                // 处理 ResultSet 返回的每一行 Row
                handleRowValues(rsw, resultMap, resultHandler, rowBounds, null);
            }
        }
    } finally {
        // issue #228 (close resultsets)
        // 关闭 ResultSet 对象
        closeResultSet(rsw.getResultSet());
    }
}
```

代码比较多，实际最重要的代码就是

```
// 添加 defaultResultHandler 的处理的结果，到 multipleResults 中
multipleResults.add(defaultResultHandler.getResultList());
```

将处理后的结果封装到集合中返回,这样基本Mybatis逻辑就走完了.

我们来回顾一下，都用到了哪些类

![](C:\Dylan\YangHaiji\安装包_学习文档\sqlSession.png)

#### 简单总结

SqlSessionFactoryBuilder：

- 解析核心配置文件，创建Configuration
  - `XMLConfigBuilder.parse()`：解析核心配置文件
  - `XMLMapperBuilder.parse()`：解析映射配置文件MappedStatement
- 创建SqlSessionFactory，默认创建DefaultSqlSessionFactory

SqlSessionFactory：

- `openSession()`：构建Executor，SqlSession等

SqlSession：

- 根据statementId获取MappedStatement
- 委派给Executor执行器执行

Executor：

- 使用SqlSourceBuilder，将SQL解析成JDBC认可的
- 查询缓存，是否存在结果
- 结果不存在，委派给StatementHandler处理器

StatementHandler：

- PreparedStatement：处理参数，将参数赋值到占位符上
  - TypeHandler：具体设置值的类
- ResultSetHandler：封装结果集，封装成设置的返回值类型
  - TypeHandler：根据结果集，取出对应列