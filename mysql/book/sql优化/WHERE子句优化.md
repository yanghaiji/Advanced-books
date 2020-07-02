## WHERE子句优化

这些示例使用 `SELECT`语句，但是相同的优化适用`WHERE`于`DELETE`和 `UPDATE`语句中的子句 。

您可能会想重写查询以使算术运算更快，同时又牺牲了可读性。由于MySQL自动进行类似的优化，因此您通常可以避免这项工作，而将查询保留为更易于理解和维护的形式。MySQL执行的一些优化如下：

- 删除不必要的括号：

  ```sql
     ((a AND b) AND c OR (((a AND b) AND (c AND d))))
     
  -> (a AND b AND c) OR (a AND b AND c AND d)
  ```

- 恒定折叠：

  ```sql
     (a<b AND b=c) AND a=5
     
  -> b>5 AND b=c AND a=5
  ```

- 恒定条件消除：

  ```sql
     (b>=5 AND b=5) OR (b=6 AND 5=5) OR (b=7 AND 5=6)
     
  -> b=5 OR b=6
  ```

- 索引使用的常量表达式仅计算一次。

- `COUNT(*)`上没有一个单一的表`WHERE`是从该表信息直接检索`MyISAM` 和`MEMORY`表。`NOT NULL`当仅与一个表一起使用时，对于任何表达式也可以执行此操作。

- 早期检测无效的常量表达式。MySQL快速检测到某些 `SELECT`语句是不可能的，并且不返回任何行。

- `HAVING WHERE`如果您不使用`GROUP BY`或汇总功能（`COUNT()`， `MIN()`等），则与合并 。

- 对于连接中的每个表，`WHERE`构造一个更简单 `WHERE`的表以获得表的快速 评估，并尽快跳过行。

- 在查询中的任何其他表之前，首先读取所有常量表。常量表可以是以下任意一个：

  - 空表或具有一行的表。
  - 与a 或 索引`WHERE` 上的子句一起使用的表，其中所有索引部分都与常量表达式进行比较，并定义为。 `PRIMARY KEY UNIQUE NOT NULL`

  以下所有表均用作常量表：

  ```sql
  SELECT * FROM t WHERE primary_key=1;
  SELECT * FROM t1,t2
    WHERE t1.primary_key=1 AND t2.primary_key=t1.id;
  ```

- 通过尝试所有可能的方法，找到用于联接表的最佳联接组合。如果`ORDER BY`and `GROUP BY`子句中的所有列 都来自同一表，则在连接时优先使用该表。

- 如果有一个`ORDER BY`子句和另一个`GROUP BY`子句，或者如果 `ORDER BY`或`GROUP BY` 包含联接队列中第一个表以外的表中的列，则会创建一个临时表。

- 如果使用`SQL_SMALL_RESULT` 修饰符，MySQL将使用内存中的临时表。

- 查询每个表索引，并使用最佳索引，除非优化程序认为使用表扫描更有效。一次使用扫描是基于最佳索引是否跨越了表的30％以上，但是固定百分比不再决定使用索引还是扫描。现在，优化器更加复杂，其估计基于其他因素，例如表大小，行数和I / O大小。

- 在某些情况下，MySQL甚至可以在不查询数据文件的情况下从索引中读取行。如果索引中使用的所有列都是数字，则仅索引树用于解析查询。

- 在输出每一行之前，`HAVING`将跳过不匹配该子句的那些行 。

快速查询的一些示例：

```sql
SELECT COUNT(*) FROM tbl_name;

SELECT MIN(key_part1),MAX(key_part1) FROM tbl_name;

SELECT MAX(key_part2) FROM tbl_name
  WHERE key_part1=constant;

SELECT ... FROM tbl_name
  ORDER BY key_part1,key_part2,... LIMIT 10;

SELECT ... FROM tbl_name
  ORDER BY key_part1 DESC, key_part2 DESC, ... LIMIT 10;
```

假设索引列是数字，MySQL仅使用索引树来解析以下查询：

```sql
SELECT key_part1,key_part2 FROM tbl_name WHERE key_part1=val;

SELECT COUNT(*) FROM tbl_name
  WHERE key_part1=val1 AND key_part2=val2;

SELECT key_part2 FROM tbl_name GROUP BY key_part1;
```

以下查询使用索引来按排序顺序检索行，而无需单独的排序遍历：

```sql
SELECT ... FROM tbl_name
  ORDER BY key_part1,key_part2,... ;

SELECT ... FROM tbl_name
  ORDER BY key_part1 DESC, key_part2 DESC, ... ;
```
