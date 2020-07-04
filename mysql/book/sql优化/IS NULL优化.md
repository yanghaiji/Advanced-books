## IS NULL优化

MySQL能够执行在相同的优化 ，它可以使用 。例如，MySQL能使用索引和范围来搜索 与。 *`col_name`* `IS NULL`*`col_name`* `=` *`constant_value`*`NULL``IS NULL`

例子：

```sql
SELECT * FROM tbl_name WHERE key_col IS NULL;

SELECT * FROM tbl_name WHERE key_col <=> NULL;

SELECT * FROM tbl_name
  WHERE key_col=const1 OR key_col=const2 OR key_col IS NULL;
```

如果`WHERE`子句包含声明为的列的 条件，则 该表达式将被优化。如果该列仍然可能产生（例如，如果它来自a右侧的表），则 不会进行此优化。 *`col_name`* `IS NULL``NOT NULL``NULL``LEFT JOIN`

MySQL还可以优化组合 ，这种形式在已解决的子查询中很常见。 显示 何时使用此优化。 `*`col_name`* = *`expr`* OR *`col_name`* IS NULL``EXPLAIN``ref_or_null`

此优化可以处理`IS NULL`任何关键部分。

假设在列`a`和 `b`表上都有索引，则对查询进行一些优化的示例`t2`：

```sql
SELECT * FROM t1 WHERE t1.a=expr OR t1.a IS NULL;

SELECT * FROM t1, t2 WHERE t1.a=t2.a OR t2.a IS NULL;

SELECT * FROM t1, t2
  WHERE (t1.a=t2.a OR t2.a IS NULL) AND t2.b=t1.b;

SELECT * FROM t1, t2
  WHERE t1.a=t2.a AND (t2.b=t1.b OR t2.b IS NULL);

SELECT * FROM t1, t2
  WHERE (t1.a=t2.a AND t2.a IS NULL AND ...)
  OR (t1.a=t2.a AND t2.a IS NULL AND ...);
```

`ref_or_null`通过首先读取参考键，然后单独搜索具有`NULL`键值的行来工作。

优化只能处理一个`IS NULL`级别。在以下查询中，MySQL仅在表达式上使用键查找`(t1.a=t2.a AND t2.a IS NULL)`，而不能在上使用键部分 `b`：

```sql
SELECT * FROM t1, t2
  WHERE (t1.a=t2.a AND t2.a IS NULL)
  OR (t1.b=t2.b AND t2.b IS NULL);
```

