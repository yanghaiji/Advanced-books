## 1. Explain

在较早的MySQL版本中，分区和扩展信息是使用 `EXPLAIN PARTITIONS`和生成的 `EXPLAIN EXTENDED`。仍然可以识别这些语法的向后兼容性，但是默认情况下现在启用了分区和扩展输出，因此`PARTITIONS` 和`EXTENDED`关键字已不再使用。使用它们会导致警告，并且`EXPLAIN`在将来的MySQL版本中会将其从语法中删除。

你不能使用已弃用`PARTITIONS` ，并`EXTENDED`在相同的关键字共同 `EXPLAIN`声明。此外，这些关键字都不能与该`FORMAT`选项一起使用 。

如果您不知道您所使用的Mysql的版本好，请输入如下命令进行查看：

```sql
SELECT version();
```

### 注

本文已`Mysql 5.7.28`进行讲解

#### 解释输出列

每个输出行`EXPLAIN` 提供有关一个表的信息。

![image-20200630170527861](C:\Users\haiyang\AppData\Roaming\Typora\typora-user-images\image-20200630170527861.png)

**EXPLAIN输出列**

| 列              | JSON名称        | 含义                   |
| --------------- | --------------- | ---------------------- |
| `id`            | `select_id`     | 该`SELECT`标识符       |
| `select_type`   | 没有            | 该`SELECT`类型         |
| `table`         | `table_name`    | 输出行表               |
| `partitions`    | `partitions`    | 匹配的分区             |
| `type`          | `access_type`   | 联接类型               |
| `possible_keys` | `possible_keys` | 可能的索引选择         |
| `key`           | `key`           | 实际选择的索引         |
| `key_len`       | `key_length`    | 所选键的长度           |
| `ref`           | `ref`           | 与索引比较的列         |
| `rows`          | `rows`          | 估计要检查的行         |
| `filtered`      | `filtered`      | 按表条件过滤的行百分比 |
| `Extra`         | 没有            | 附加信息               |

> 注：JSON属性`NULL`不会显示在JSON格式的`EXPLAIN` 输出中。

- `id`（JSON名： `select_id`）

  `SELECT`标识符。这是`SELECT`查询中的序号 。`NULL`如果该行引用其他行的并集结果，则该值为。在这种情况下，该 `table`列显示的值类似于 表明该行引用的行的并 集是和的值 。

- `select_type` （JSON名称：无）

  类型`SELECT`，可以是下表中显示的任何类型。JSON格式`EXPLAIN`将`SELECT`类型公开 为a的属性 `query_block`，除非它为 `SIMPLE`或`PRIMARY`。表格中还会显示JSON名称（如果适用）。

  | `select_type` 值       | JSON名称                     | 含义                                                         |
  | ---------------------- | ---------------------------- | ------------------------------------------------------------ |
  | `SIMPLE`               | 没有                         | 简单`SELECT`（不使用 `UNION`或子查询）                       |
  | `PRIMARY`              | 没有                         | 最外层 `SELECT`                                              |
  | `UNION`                | 没有                         | 第二个或之后的`SELECT`陈述 `UNION`                           |
  | `DEPENDENT UNION`      | `dependent`（`true`）        | 中的第二个或更高版本的`SELECT`语句 `UNION`，取决于外部查询   |
  | `UNION RESULT`         | `union_result`               | 的结果`UNION`。                                              |
  | `SUBQUERY`             | 没有                         | 首先`SELECT`在子查询                                         |
  | `DEPENDENT SUBQUERY`   | `dependent`（`true`）        | 首先`SELECT`在子查询中，取决于外部查询                       |
  | `DERIVED`              | 没有                         | 派生表                                                       |
  | `MATERIALIZED`         | `materialized_from_subquery` | 物化子查询                                                   |
  | `UNCACHEABLE SUBQUERY` | `cacheable`（`false`）       | 子查询，其结果无法缓存，必须针对外部查询的每一行重新进行评估 |
  | `UNCACHEABLE UNION`    | `cacheable`（`false`）       | `UNION` 属于不可缓存子查询的中的第二个或更高版本的选择（请参阅参考资料 `UNCACHEABLE SUBQUERY`） |

  `DEPENDENT`通常表示使用相关子查询。

- `table`（JSON名： `table_name`）

  输出行所引用的表的名称。这也可以是以下值之一：

  - <unionM,N>：该行指的是具有和`id`值的行 的 *`M`*并集 *`N`*。
  - <derived*`N`*>：该行是指用于与该行的派生表结果`id`的值 *`N`*。派生表可能来自（例如）`FROM`子句中的子查询 。
  - <subquery*`N`*>：该行是指该行的物化子查询的结果，其`id` 值为*`N`*。

- `partitions`（JSON名： `partitions`）

  查询将从中匹配记录的分区。该值适用`NULL`于未分区的表。

- `type`（JSON名： `access_type`）

  联接类型。有关不同类型的描述，请参见 `EXPLAIN` 连接类型

- `possible_keys`（JSON名： `possible_keys`）

  该`possible_keys`列指示MySQL可以选择从中查找表中各行的索引。请注意，此列完全独立于表的顺序，如的输出所示 `EXPLAIN`。这意味着`possible_keys`在实践中，某些键可能无法与生成的表顺序一起使用。

  如果此列是`NULL`（或在JSON格式的输出中未定义），则没有相关的索引。在这种情况下，您可以通过检查该`WHERE` 子句以检查它是否引用了一些适合索引的列，从而提高查询性能。如果是这样，请创建一个适当的索引并`EXPLAIN`再次检查查询 。

  要查看表具有哪些索引，请使用。 `SHOW INDEX FROM ` `tbl_name`

- `key`（JSON名：`key`）

  该`key`列指示MySQL实际决定使用的密钥（索引）。如果MySQL决定使用`possible_keys` 索引之一来查找行，则将该索引列为键值。

  可能`key`会命名该值中不存在的索引 `possible_keys`。如果所有`possible_keys`索引都不适合查找行，但是查询选择的所有列都是其他索引的列，则可能发生这种情况。也就是说，命名索引覆盖了选定的列，因此尽管不使用索引来确定要检索的行，但索引扫描比数据行扫描更有效。

  对于`InnoDB`，即使查询也选择了主键，辅助索引也可能覆盖选定的列，因为`InnoDB`主键值与每个辅助索引一起存储。如果 `key`为`NULL`，则MySQL没有找到可用于更有效地执行查询的索引。

  要强制MySQL使用或忽略列出的索引 `possible_keys`列，使用 `FORCE INDEX`，`USE INDEX`或`IGNORE INDEX`在您的查询。

  对于`MyISAM`表，运行 `ANALYZE TABLE`有助于优化器选择更好的索引。

- `key_len`（JSON名： `key_length`）

  该`key_len`列指示MySQL决定使用的密钥的长度。的值 `key_len`使您能够确定MySQL实际使用的多部分键的多少部分。如果该`key`列显示 `NULL`，则该`key_len` 列也显示`NULL`。

  由于密钥存储格式的原因，一列可以使用的密钥长度`NULL` 比一`NOT NULL`列大。

- `ref`（JSON名：`ref`）

  该`ref`列显示将哪些列或常量与该`key`列中命名的索引进行比较，以 从表中选择行。

  如果值为`func`，则使用的值是某些函数的结果。要查看哪个功能，请使用 `SHOW WARNINGS`以下 `EXPLAIN`命令查看扩展 `EXPLAIN`输出。该函数实际上可能是算术运算符之类的运算符。

- `rows`（JSON名： `rows`）

  该`rows`列指示MySQL认为执行查询必须检查的行数。

  对于`InnoDB`表，此数字是估计值，可能并不总是准确的。

- `filtered`（JSON名： `filtered`）

  该`filtered`列指示将被表条件过滤的表行的估计百分比。最大值为100，这表示未过滤行。值从100减小表示过滤量增加。 `rows`显示了检查的估计行数，`rows`× `filtered`显示了将与下表连接的行数。例如，如果 `rows`为1000且 `filtered`为50.00（50％），则与下表连接的行数为1000×50％= 500。

- `Extra` （JSON名称：无）

  此列包含有关MySQL如何解析查询的其他信息。

  该`Extra`列没有对应的JSON属性 ；但是，此列中可能出现的值显示为JSON属性或该`message`属性的文本。

#### 说明联接类型

该`type`列 `EXPLAIN`输出介绍如何联接表。在JSON格式的输出中，这些作为`access_type`属性的值找到。以下列表描述了连接类型，从最佳类型到最差类型：

- `system`

  该表只有一行（=系统表）。这是`const`联接类型的特例 。

- `const`

  该表最多具有一个匹配行，该行在查询开始时读取。因为只有一行，所以优化器的其余部分可以将这一行中列的值视为常量。 `const`表非常快，因为它们只能读取一次。

  `const`在将a `PRIMARY KEY`或 `UNIQUE`index的所有部分与常数值进行比较时使用。在以下查询中，*`tbl_name`*可以用作`const` 表：

  ```sql
  SELECT * FROM tbl_name WHERE primary_key=1;
  
  SELECT * FROM tbl_name
    WHERE primary_key_part1=1 AND primary_key_part2=2;
  ```

- `eq_ref`

  对于先前表中的每行组合，从此表中读取一行。除了 `system`和 `const`类型，这是最好的联接类型。当连接使用索引的所有部分并且索引为a `PRIMARY KEY`或`UNIQUE NOT NULL`index时使用。

  `eq_ref`可用于使用`=`运算符进行比较的索引列 。比较值可以是常量，也可以是使用在此表之前读取的表中列的表达式。在以下示例中，MySQL可以使用 `eq_ref`联接进行处理 *`ref_table`*：

  ```sql
  SELECT * FROM ref_table,other_table
    WHERE ref_table.key_column=other_table.column;
  
  SELECT * FROM ref_table,other_table
    WHERE ref_table.key_column_part1=other_table.column
    AND ref_table.key_column_part2=1;
  ```

- `ref`

  对于先前表中的每个行组合，将从该表中读取具有匹配索引值的所有行。`ref`如果联接仅使用键的最左前缀，或者如果键不是a `PRIMARY KEY`或 `UNIQUE`索引（换句话说，如果联接无法根据键值选择单个行），则使用。如果使用的键仅匹配几行，则这是一种很好的联接类型。

  `ref`可以用于使用`=`或`<=>` 运算符进行比较的索引列 。在以下示例中，MySQL可以使用 `ref`联接进行处理 *`ref_table`*：

  ```sql
  SELECT * FROM ref_table WHERE key_column=expr;
  
  SELECT * FROM ref_table,other_table
    WHERE ref_table.key_column=other_table.column;
  
  SELECT * FROM ref_table,other_table
    WHERE ref_table.key_column_part1=other_table.column
    AND ref_table.key_column_part2=1;
  ```

- `fulltext`

  使用`FULLTEXT` 索引执行联接。

- `ref_or_null`

  这种连接类型类似于 `ref`，但是除了MySQL会额外搜索包含`NULL`值的行。此联接类型优化最常用于解析子查询。在以下示例中，MySQL可以使用 `ref_or_null`联接进行处理*`ref_table`*：

  ```sql
  SELECT * FROM ref_table
    WHERE key_column=expr OR key_column IS NULL;
  ```

- `index_merge`

  此联接类型指示使用索引合并优化。在这种情况下，`key`输出行中的列包含使用的索引列表，并`key_len`包含使用的索引 的最长键部分的列表。

- `unique_subquery`

  此类型替换 以下形式的`eq_ref`]某些 `IN`子查询：

  ```sql
  value IN (SELECT primary_key FROM single_table WHERE some_expr)
  ```

  `unique_subquery`只是一个索引查找函数，它完全替代了子查询以提高效率。

- `index_subquery`

  此连接类型类似于 `unique_subquery`。它代替`IN`子查询，但适用于以下形式的子查询中的非唯一索引：

  ```sql
  value IN (SELECT key_column FROM single_table WHERE some_expr)
  ```

- `range`

  使用索引选择行，仅检索给定范围内的行。的`key` 输出行中的列指示使用哪个索引。将`key_len`包含已使用的时间最长的关键部分。该`ref`列 `NULL`适用于此类型。

  `range`当一个键列使用任何的相比于恒定可使用 `=`， `<>`， `>`， `>=`， `<`， `<=`， `IS NULL`， `<=>`， `BETWEEN`， `LIKE`，或 `IN()`运营商：

  ```sql
  SELECT * FROM tbl_name
    WHERE key_column = 10;
  
  SELECT * FROM tbl_name
    WHERE key_column BETWEEN 10 and 20;
  
  SELECT * FROM tbl_name
    WHERE key_column IN (10,20,30);
  
  SELECT * FROM tbl_name
    WHERE key_part1 = 10 AND key_part2 IN (10,20,30);
  ```

- `index`

  该`index`联接类型是一样的 `ALL`，只是索引树被扫描。这发生两种方式：

  - 如果索引是查询的覆盖索引，并且可用于满足表中所需的所有数据，则仅扫描索引树。在这种情况下，`Extra`列为 `Using index`。仅索引扫描通常比索引扫描更快， `ALL`因为索引的大小通常小于表数据。
  - 使用对索引的读取执行全表扫描，以按索引顺序查找数据行。 `Uses index`没有出现在 `Extra`列中。

  当查询仅使用属于单个索引一部分的列时，MySQL可以使用此联接类型。

- `ALL`

  对来自先前表的行的每个组合进行全表扫描。如果该表是未标记的第一个表 `const`，则通常不好，在其他所有情况下通常 *非常*糟糕。通常，可以`ALL`通过添加索引来避免这种情况，这些 索引允许基于早期表中的常量值或列值从表中检索行。