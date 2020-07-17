### 2. Extra

该`Extra`列 `EXPLAIN`输出包含MySQL解决查询的额外信息。以下列表说明了可以在此列中显示的值。每个项目还针对JSON格式的输出指示哪个属性显示`Extra`值。对于其中一些，有一个特定的属性。其他显示为`message` 属性的文本。

如果你想使你的查询尽可能快，看出来`Extra`的列值`Using filesort`和`Using temporary`，或在JSON格式的`EXPLAIN`输出，用于 `using_filesort`和 `using_temporary_table`性能等于 `true`。

- `Child of  ` table ` pushed join@1`（JSON：`message` 文本）

  该表是*`table`*可以向下推到NDB内核的联接中的子级引用 。启用下推联接时，仅适用于NDB群集。

- `const row not found`（JSON属性： `const_row_not_found`）

  对于查询，该表为空。 `SELECT ... FROM tbl_name`

- `Deleting all rows`（JSON属性： `message`）

  对于`DELETE`，某些存储引擎（如`MyISAM`）支持一种处理程序方法，该方法以一种简单而快速的方式删除所有表行。`Extra`如果引擎使用此优化，则显示此值。

- `Distinct`（JSON属性： `distinct`）

  MySQL正在寻找不同的值，因此在找到第一个匹配的行后，它将停止为当前行组合搜索更多行。

- `FirstMatch` (tbl_name)` JSON属性：`first_match）

  半连接FirstMatch连接快捷方式策略用于*`tbl_name`*。

- `Full scan on NULL key`（JSON属性： `message`）

  当优化器无法使用索引查找访问方法时，这会作为子查询优化的后备策略而发生。

- `Impossible HAVING`（JSON属性： `message`）

  该`HAVING`子句始终为false，无法选择任何行。

- `Impossible WHERE`（JSON属性： `message`）

  该`WHERE`子句始终为false，无法选择任何行。

- `Impossible WHERE noticed after reading const tables`（JSON属性： `message`）

  MySQL已经读取了所有 `const`（和 `system`）表，并注意到该`WHERE`子句始终为false。

- `LooseScan` (**m .. n**) （JSON属性：`message`）

  使用半连接的LooseScan策略。 *`m`*和 *`n`*是关键零件号。

- `No matching min/max row`（JSON属性： `message`）

  没有行满足查询的条件，例如 。 `SELECT MIN(...) FROM ... WHERE ` condition

- `no matching row in const table`（JSON属性：`message`）

  对于具有联接的查询，存在一个空表或一个表中没有满足唯一索引条件的行。

- `No matching rows after partition pruning`（JSON属性： `message`）

  对于`DELETE`或 `UPDATE`，优化器在分区修剪后找不到要删除或更新的内容。它的含义类似于`Impossible WHERE` for `SELECT`语句。

- `No tables used`（JSON属性： `message`）

  查询没有`FROM`子句，或者有 `FROM DUAL`子句。

  对于`INSERT`或 `REPLACE`语句， `EXPLAIN`在没有任何`SELECT`部分时显示此值。例如，出现的`EXPLAIN INSERT INTO t VALUES(10)`原因是因为等同于 `EXPLAIN INSERT INTO t SELECT 10 FROM DUAL`。

- `Not exists`（JSON属性： `message`）

  MySQL能够对`LEFT JOIN` 查询进行优化，并且在找到符合`LEFT JOIN`条件的一行后，不检查该表中的更多行是否为上一行。这是可以通过这种方式优化的查询类型的示例：

  ```sql
  SELECT * FROM t1 LEFT JOIN t2 ON t1.id=t2.id
    WHERE t2.id IS NULL;
  ```

  假设`t2.id`定义为 `NOT NULL`。在这种情况下，MySQL 使用的值 扫描 `t1`并查找行 。如果MySQL在中找到匹配的行 ，它将知道它 永远不会是 ，并且不会扫描具有相同值的其余行。换句话说，对于in中的每一行，MySQL 实际上只需进行一次查找，无论in中实际匹配多少行。 `t2``t1.id``t2``t2.id``NULL``t2``id``t1``t2``t2`

- `Plan isn't ready yet` （JSON属性：无）

  `EXPLAIN FOR CONNECTION`当优化器尚未完成为在命名连接中执行的语句创建执行计划时， 就会出现此值。如果执行计划输出包含多行，则`Extra`取决于优化程序确定完整执行计划的进度，其中任何一行或所有行都可以具有此 值。

- `Range checked for each record (index map: *`N`*)`（JSON属性： `message`）

  MySQL找不到很好的索引来使用，但是发现一些索引可以在已知先前表中的列值之后使用。对于上表中的每个行组合，MySQL检查是否可以使用`range`或 `index_merge`访问方法来检索行。这不是很快，但是比完全没有索引的连接要快。适用标准如“范围优化”和 “索引合并优化”中所述，除了上表的所有列值都是已知的并且被视为常量。

  索引从1开始编号，其顺序`SHOW INDEX`与表中显示的顺序相同。索引图值 *`N`*是指示哪些索引为候选的位掩码值。例如，值`0x19`（二进制11001）表示将考虑索引1、4和5。

- `Scanned *`N`* databases`（JSON属性： `message`）

  这表示在处理`INFORMATION_SCHEMA`表查询时服务器执行了多少目录扫描 ，如“优化INFORMATION_SCHEMA查询”中所述。的值*`N`*可以是0、1或 `all`。

- `Select tables optimized away`（JSON属性：`message`）

  优化器确定1）最多应返回一行，以及2）要生成该行，必须读取确定的行集。当在优化阶段可以读取要读取的行时（例如，通过读取索引行），则在查询执行期间无需读取任何表。

  当查询被隐式分组（包含聚合函数但没有`GROUP BY`子句）时，满足第一个条件 。当每个使用的索引执行一次行查找时，满足第二个条件。读取的索引数决定了要读取的行数。

  考虑以下隐式分组查询：

  ```sql
  SELECT MIN(c1), MIN(c2) FROM t1;
  ```

  假设`MIN(c1)`可以通过读取一个索引行`MIN(c2)` 来检索，并且可以通过从另一索引中读取一行来进行检索。即，对于每一列`c1`和 `c2`，存在其中列是索引的第一列的索引。在这种情况下，将通过读取两个确定性行来返回一行。

  `Extra`如果要读取的行不确定，则不会出现 此值。考虑以下查询：

  ```sql
  SELECT MIN(c2) FROM t1 WHERE c1 <= 10;
  ```

  假设这`(c1, c2)`是一个覆盖指数。使用此索引，`c1 <= 10`必须扫描所有具有的行以找到最小值 `c2`。相比之下，请考虑以下查询：

  ```sql
  SELECT MIN(c2) FROM t1 WHERE c1 = 10;
  ```

  在这种情况下，第一个索引行`c1 = 10`包含最小值`c2` 。仅一行必须读取才能产生返回的行。

  对于维护每个表的行数准确的存储引擎（例如`MyISAM`，但不是 `InnoDB`），对于缺少该子句或始终为true且没有 子句的查询，`Extra` 可能会出现此值。（这是一个隐式分组查询的实例，其中存储引擎影响是否可以读取确定数量的行。） `COUNT(*)``WHERE``GROUP BY`

- `Skip_open_table`， `Open_frm_only`， `Open_full_table`（JSON属性： `message`）

  这些值指示适用于`INFORMATION_SCHEMA` 表查询的文件打开优化，如“优化INFORMATION_SCHEMA查询”中所述。

  - `Skip_open_table`：不需要打开表文件。通过扫描数据库目录，该信息已在查询中可用。
  - `Open_frm_only`：仅`.frm`需要打开表的文件。
  - `Open_full_table`：未优化的信息查找。的`.frm`， `.MYD`和 `.MYI`文件必须被打开。

- `Start temporary`，`End temporary`（JSON属性： `message`）

  这表明临时表用于半联接重复淘汰策略。

- `unique row not found`（JSON属性： `message`）

  对于诸如的查询，没有行满足 索引或表中的条件。 `SELECT ... FROM *`tbl_name`*``UNIQUE``PRIMARY KEY`

- `Using filesort`（JSON属性： `using_filesort`）

  MySQL必须额外进行一遍，以找出如何按排序顺序检索行。排序是通过根据联接类型遍历所有行并存储与该`WHERE`子句匹配的所有行的排序键和指向该行的指针来完成的。然后对键进行排序，并按排序顺序检索行。

- `Using index`（JSON属性： `using_index`）

  仅使用索引树中的信息从表中检索列信息，而不必进行其他查找以读取实际行。当查询仅使用属于单个索引的列时，可以使用此策略。

  对于`InnoDB`具有用户定义的聚集索引的表，即使列中`Using index`不存在 该索引也可以使用`Extra`。如果`type`is `index`和 `key`is 就是这种情况 `PRIMARY`。

- `Using index condition`（JSON属性： `using_index_condition`）

  通过访问索引元组并首先对其进行测试以确定是否读取完整的表行来读取表。这样，除非必要，否则索引信息将用于延迟（“ 下推 ”）读取整个表行。

- `Using index for group-by`（JSON属性：`using_index_for_group_by`）

  与`Using index`表访问方法类似，`Using index for group-by` 表示MySQL找到了一个索引，该索引可用于检索a `GROUP BY`或 `DISTINCT`查询的所有列，而无需对实际表进行任何额外的磁盘访问。此外，以最有效的方式使用索引，因此对于每个组，仅读取少数索引条目。

- `Using join buffer (Block Nested Loop)`， `Using join buffer (Batched Key Access)` （JSON属性：`using_join_buffer`）

  来自较早联接的表被部分读取到联接缓冲区中，然后从缓冲区中使用它们的行来执行与当前表的联接。 `(Block Nested Loop)`表示使用块嵌套循环算法，并`(Batched Key Access)`表示使用批处理密钥访问算法。也就是说，将`EXPLAIN`缓冲输出前行中的表中的键 ，并从出现行所在的表中批量提取匹配的行 `Using join buffer`。

  在JSON格式的输出中，的值 `using_join_buffer`始终为`Block Nested Loop`或之一 `Batched Key Access`。

- `Using MRR`（JSON属性： `message`）

  使用多范围读取优化策略读取表。

- `Using sort_union(...)`，`Using union(...)`，`Using intersect(...)`（JSON属性： `message`）

  这些指示了特定算法，该算法显示了如何针对`index_merge`联接类型合并索引扫描 。

- `Using temporary`（JSON属性： `using_temporary_table`）

  为了解决该查询，MySQL需要创建一个临时表来保存结果。如果查询包含`GROUP BY`和 `ORDER BY`子句以不同的方式列出列，通常会发生这种情况。

- `Using where`（JSON属性： `attached_condition`）

  甲`WHERE`子句用于限制来匹配下一个表或发送到客户端的行。除非您特别打算从表中获取或检查所有行，否则如果查询中的`Extra`值不是 `Using where`并且表连接类型为`ALL`或 ，则 查询中可能会出错`index`。

  `Using where`在JSON格式的输出中没有直接对应的内容；该 `attached_condition`属性包含使用的任何`WHERE`条件。

- `Using where with pushed condition`（JSON属性：`message`）

  此产品适用于`NDB`表*只*。这意味着NDB Cluster正在使用条件下推优化来提高在非索引列和常量之间进行直接比较的效率。在这种情况下，条件被“ 下推 ”到群集的数据节点，并同时在所有数据节点上进行评估。这样就无需通过网络发送不匹配的行，并且在可以但不使用条件下推的情况下，可以将此类查询的速度提高5到10倍。

- `Zero limit`（JSON属性： `message`）

  该查询有一个`LIMIT 0`子句，不能选择任何行。

#### 解释输出解释

通过获取输出`rows` 列中值的乘积，可以很好地表明联接的良好程度`EXPLAIN`。这应该大致告诉您MySQL必须检查多少行才能执行查询。如果使用`max_join_size`系统变量限制查询，则 此行乘积还用于确定`SELECT` 执行哪些多表语句以及中止哪个多表语句。

以下示例显示了如何根据提供的信息逐步优化多表联接 `EXPLAIN`。

假设您在`SELECT`此处显示了该 语句，并计划使用进行检查 `EXPLAIN`：

```sql
EXPLAIN SELECT tt.TicketNumber, tt.TimeIn,
               tt.ProjectReference, tt.EstimatedShipDate,
               tt.ActualShipDate, tt.ClientID,
               tt.ServiceCodes, tt.RepetitiveID,
               tt.CurrentProcess, tt.CurrentDPPerson,
               tt.RecordVolume, tt.DPPrinted, et.COUNTRY,
               et_1.COUNTRY, do.CUSTNAME
        FROM tt, et, et AS et_1, do
        WHERE tt.SubmitTime IS NULL
          AND tt.ActualPC = et.EMPLOYID
          AND tt.AssignedPC = et_1.EMPLOYID
          AND tt.ClientID = do.CUSTNMBR;
```

对于此示例，进行以下假设：

- 被比较的列已声明如下。

  | 表   | 列           | 数据类型   |
  | ---- | ------------ | ---------- |
  | `tt` | `ActualPC`   | `CHAR(10)` |
  | `tt` | `AssignedPC` | `CHAR(10)` |
  | `tt` | `ClientID`   | `CHAR(10)` |
  | `et` | `EMPLOYID`   | `CHAR(15)` |
  | `do` | `CUSTNMBR`   | `CHAR(15)` |

- 这些表具有以下索引。

  | 表   | 指数         |
  | ---- | ------------ |
  | `tt` | `ActualPC`   |
  | `tt` | `AssignedPC` |
  | `tt` | `ClientID`   |
  | `et` | `EMPLOYID`   |
  | `do` | `CUSTNMBR`   |

- 这些`tt.ActualPC`值分布不均。

最初，在执行任何优化之前，该 `EXPLAIN`语句会产生以下信息：

```none
table type possible_keys key  key_len ref  rows  Extra
et    ALL  PRIMARY       NULL NULL    NULL 74
do    ALL  PRIMARY       NULL NULL    NULL 2135
et_1  ALL  PRIMARY       NULL NULL    NULL 74
tt    ALL  AssignedPC,   NULL NULL    NULL 3872
           ClientID,
           ActualPC
      Range checked for each record (index map: 0x23)
```

因为`type`是 `ALL`针对每个表的，所以此输出表明MySQL正在生成所有表的笛卡尔积；也就是说，行的每种组合。这需要相当长的时间，因为必须检查每个表中的行数的乘积。对于当前情况，此乘积为74×2135×74×3872 = 45,268,558,720行。如果桌子更大，您只能想象需要多长时间。

这里的一个问题是，如果将索引声明为相同的类型和大小，则MySQL可以更有效地在列上使用索引。在这种情况下，`VARCHAR`与 `CHAR`被认为是相同的，如果它们被声明为相同的大小。 `tt.ActualPC`声明为 `CHAR(10)`和`et.EMPLOYID` 是`CHAR(15)`，因此长度不匹配。

要解决此列长度之间的差异，请使用 从10个字符`ALTER TABLE`延长 `ActualPC`到15个字符：

```sql
mysql> ALTER TABLE tt MODIFY ActualPC VARCHAR(15);
```

现在`tt.ActualPC`和 `et.EMPLOYID`都是 `VARCHAR(15)`。`EXPLAIN`再次执行该 语句将产生以下结果：

```none
table type   possible_keys key     key_len ref         rows    Extra
tt    ALL    AssignedPC,   NULL    NULL    NULL        3872    Using
             ClientID,                                         where
             ActualPC
do    ALL    PRIMARY       NULL    NULL    NULL        2135
      Range checked for each record (index map: 0x1)
et_1  ALL    PRIMARY       NULL    NULL    NULL        74
      Range checked for each record (index map: 0x1)
et    eq_ref PRIMARY       PRIMARY 15      tt.ActualPC 1
```

这不是完美的，但是更好：`rows`值的乘积 少了74倍。此版本在几秒钟内执行。

可以进行第二种更改以消除`tt.AssignedPC = et_1.EMPLOYID`和`tt.ClientID = do.CUSTNMBR`比较的列长不匹配：

```sql
mysql> ALTER TABLE tt MODIFY AssignedPC VARCHAR(15),
                      MODIFY ClientID   VARCHAR(15);
```

修改之后， `EXPLAIN`产生如下所示的输出：

```none
table type   possible_keys key      key_len ref           rows Extra
et    ALL    PRIMARY       NULL     NULL    NULL          74
tt    ref    AssignedPC,   ActualPC 15      et.EMPLOYID   52   Using
             ClientID,                                         where
             ActualPC
et_1  eq_ref PRIMARY       PRIMARY  15      tt.AssignedPC 1
do    eq_ref PRIMARY       PRIMARY  15      tt.ClientID   1
```

在这一点上，查询尽可能地被优化。剩下的问题是，默认情况下，MySQL假定该`tt.ActualPC` 列中的值是均匀分布的，而表则不是这种情况`tt`。幸运的是，很容易告诉MySQL分析密钥分布：

```sql
mysql> ANALYZE TABLE tt;
```

使用其他索引信息，联接是完美的，并 `EXPLAIN`产生以下结果：

```none
table type   possible_keys key     key_len ref           rows Extra
tt    ALL    AssignedPC    NULL    NULL    NULL          3872 Using
             ClientID,                                        where
             ActualPC
et    eq_ref PRIMARY       PRIMARY 15      tt.ActualPC   1
et_1  eq_ref PRIMARY       PRIMARY 15      tt.AssignedPC 1
do    eq_ref PRIMARY       PRIMARY 15      tt.ClientID   1
```

在`rows`从输出列 `EXPLAIN`是一个受过教育的猜测从MySQL联接优化。通过将`rows`乘积与查询返回的实际行数进行比较，检查数字是否接近真实 值。如果数字完全不同，则可以通过`STRAIGHT_JOIN`在 `SELECT`语句中使用并尝试在`FROM`子句中以不同顺序列出表来 获得更好的性能 。（但是，`STRAIGHT_JOIN`由于它禁用了半联接转换， 可能会阻止使用索引。

在某些情况下，可能会执行`EXPLAIN SELECT`与子查询一起使用时会修改数据的语句。