## 5.7 B树和哈希索引的比较

了解B树和哈希数据结构可以帮助预测对使用索引中的这些数据结构的不同存储引擎执行不同查询的方式，
特别是对于MEMORY允许您选择B树或哈希索引的存储引擎。

### B树索引特征
B树索引可以在使用表达式中使用的对列的比较 =， >， >=， <， <=，或BETWEEN运营商。
LIKE 如果to的参数LIKE是一个不以通配符开头的常量字符串，则该索引也可以用于比较 。例如，以下SELECT语句使用索引：
```sql
SELECT * FROM tbl_name WHERE key_col LIKE 'Patrick%';
SELECT * FROM tbl_name WHERE key_col LIKE 'Pat%_ck%';
```
在第一条语句中，仅考虑带有的行。在第二条语句中，仅考虑带有的行。 'Patrick' <= key_col < 'Patricl''Pat' <= key_col < 'Pau'

以下SELECT语句不使用索引：
```sql
SELECT * FROM tbl_name WHERE key_col LIKE '%Patrick%';
SELECT * FROM tbl_name WHERE key_col LIKE other_col;
```

在第一个语句中，该LIKE 值以通配符开头。在第二条语句中，该LIKE值不是常数。

如果使用且 长度超过三个字符，则MySQL使用Turbo Boyer-Moore算法初始化字符串的模式，然后使用该模式更快地执行搜索。 ... LIKE '%string%'string

如果使用col_name IS NULL索引，则 使用的搜索会使用col_name索引。

没有覆盖子句中所有AND级别的 任何索引都 WHERE不会用于优化查询。换句话说，为了能够使用索引，必须在每个AND组中使用索引的前缀 。

以下WHERE子句使用索引：
```sql
... WHERE index_part1=1 AND index_part2=2 AND other_column=3

    /* index = 1 OR index = 2 */
... WHERE index=1 OR A=10 AND index=2

    /* optimized like "index_part1='hello'" */
... WHERE index_part1='hello' AND index_part3=5

    /* Can use index on index1 but not on index2 or index3 */
... WHERE index1=1 AND index2=2 OR index1=3 AND index3=3;
```

这些WHERE子句 不使用索引：
```sql
   /* index_part1 is not used */
... WHERE index_part2=1 AND index_part3=2

    /*  Index is not used in both parts of the WHERE clause  */
... WHERE index=1 OR A=10

    /* No index spans all rows  */
... WHERE index_part1=1 OR index_part2=10
```
有时，即使索引可用，MySQL也不使用索引。发生这种情况的一种情况是，优化器估计使用索引将需要MySQL访问表中很大比例的行。
（在这种情况下，表扫描可能会更快，因为它需要更少的查找。）但是，如果这样的查询LIMIT仅用于检索某些行，
则MySQL仍将使用索引，因为它可以更快地找到索引。几行返回结果。

#### 哈希指数特征

哈希索引与刚刚讨论的索引具有一些不同的特征：

- 它们仅用于使用`=`or `<=>` 运算符的相等比较 （但*非常*快）。它们不用于比较运算符，
例如`<`用于查找值范围的运算符 。依赖于这种单值查找类型的系统称为“ 键值存储 ”；要将MySQL用于此类应用程序，请尽可能使用哈希索引。
- 优化器无法使用哈希索引来加快`ORDER BY`操作速度 。（此索引类型不能用于按顺序搜索下一个条目。）
- MySQL无法确定两个值之间大约有多少行（范围优化器使用它来决定要使用哪个索引）。如果将`MyISAM`或 `InnoDB`表更改为哈希索引 `MEMORY`表，这可能会影响某些查询。
- 仅整个键可用于搜索行。（对于B树索引，键的任何最左边的前缀都可用于查找行。）