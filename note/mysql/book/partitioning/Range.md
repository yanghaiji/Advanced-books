## 范围分区

按范围进行分区的表将进行分区，以使每个分区都包含行，分区的表达式值位于给定范围内。范围应该是连续的，但不能重叠，
并且是使用VALUES LESS THAN运算符定义的 。对于接下来的几个示例，假设您正在创建一个表，如下所示，以保存20个视频商店链（编号1至20）的人员记录：

```sql
CREATE TABLE employees (
    id INT NOT NULL,
    fname VARCHAR(30),
    lname VARCHAR(30),
    hired DATE NOT NULL DEFAULT '1970-01-01',
    separated DATE NOT NULL DEFAULT '9999-12-31',
    job_code INT NOT NULL,
    store_id INT NOT NULL
);
```
注： employees此处使用 的表没有主键或唯一键。尽管出于本讨论的目的而显示了示例，但您应记住，表在实践中极有可能具有主键，唯一键或同时具有两者，
并且分区列的允许选择取决于用于这些列的列。键（如果有）

可以根据需要以多种方式对该表进行分区。一种方法是使用 store_id列。例如，您可能决定通过添加一个PARTITION BY RANGE子句来对表进行4种分区，
 如下所示：

```sql
CREATE TABLE employees (
    id INT NOT NULL,
    fname VARCHAR(30),
    lname VARCHAR(30),
    hired DATE NOT NULL DEFAULT '1970-01-01',
    separated DATE NOT NULL DEFAULT '9999-12-31',
    job_code INT NOT NULL,
    store_id INT NOT NULL
)
PARTITION BY RANGE (store_id) (
    PARTITION p0 VALUES LESS THAN (6),
    PARTITION p1 VALUES LESS THAN (11),
    PARTITION p2 VALUES LESS THAN (16),
    PARTITION p3 VALUES LESS THAN (21)
);
```

在此分区方案中，与在商店1至5上工作的雇员相对应的所有行都存储在分区中 `p0`，而在商店6到10中所用的那些行则存储在分区中`p1`，依此类推。
请注意，每个分区的定义顺序是从最低到最高。这是`PARTITION BY RANGE`语法要求。`if ... elseif ...`在这方面，
您可以认为它类似于C或Java中的一系列语句。

这是很容易确定包含数据的新行 `(72, 'Mitchell', 'Wilson', '1998-06-25', NULL, 13)`插入分区`p2`，但是当你的链增加了21会发生什么？
在这种方案下，没有规则覆盖`store_id` 大于20的行，因此由于服务器不知道将其放置在何处而导致错误。
您可以通过在语句中使用“ catchall ” `VALUES LESS THAN`子句来防止这种情况发生，该子句`CREATE TABLE`提供的所有值都大于显式命名的最大值：

```sql
CREATE TABLE employees (
    id INT NOT NULL,
    fname VARCHAR(30),
    lname VARCHAR(30),
    hired DATE NOT NULL DEFAULT '1970-01-01',
    separated DATE NOT NULL DEFAULT '9999-12-31',
    job_code INT NOT NULL,
    store_id INT NOT NULL
)
PARTITION BY RANGE (store_id) (
    PARTITION p0 VALUES LESS THAN (6),
    PARTITION p1 VALUES LESS THAN (11),
    PARTITION p2 VALUES LESS THAN (16),
    PARTITION p3 VALUES LESS THAN MAXVALUE
);
```  
MAXVALUE表示一个始终大于最大可能整数值的整数值（在数学语言中，它用作 最小上限）。现在，任何store_id列值大于或等于16（定义的最大值）
的行都存储在partition中p3。在将来的某个时候（商店数量增加到25、30或更多），您可以使用 ALTER TABLE语句为21-25、26-30等商店添加新分区

可以以几乎相同的方式，根据员工的工作代码（即，根据job_code列值的范围）对表进行分区 。例如，假设普通（店内）工人使用两位数字的工作代码，
办公室和支持人员使用三位数字的代码，管理职位使用四位数字的代码，则可以创建分区表使用以下语句：

```sql
CREATE TABLE employees (
    id INT NOT NULL,
    fname VARCHAR(30),
    lname VARCHAR(30),
    hired DATE NOT NULL DEFAULT '1970-01-01',
    separated DATE NOT NULL DEFAULT '9999-12-31',
    job_code INT NOT NULL,
    store_id INT NOT NULL
)
PARTITION BY RANGE (job_code) (
    PARTITION p0 VALUES LESS THAN (100),
    PARTITION p1 VALUES LESS THAN (1000),
    PARTITION p2 VALUES LESS THAN (10000)
);
```
在这种情况下，与店内工作人员有关的所有行都将存储在分区中p0，与行内办公室和支持人员有关的行将与分区p1中的经理有关p2。

也可以在VALUES LESS THAN子句中使用表达式。但是，MySQL必须能够在LESS THAN（<）比较中评估表达式的返回值。

您可以使用基于两DATE列之一的表达式，而不是根据商店编号来拆分表数据 。例如，让我们假设您希望根据每个员工离开公司的
年份进行划分；即的值 YEAR(separated)。CREATE TABLE此处显示了实现这种分区方案的语句示例 ：

```sql
CREATE TABLE employees (
    id INT NOT NULL,
    fname VARCHAR(30),
    lname VARCHAR(30),
    hired DATE NOT NULL DEFAULT '1970-01-01',
    separated DATE NOT NULL DEFAULT '9999-12-31',
    job_code INT,
    store_id INT
)
PARTITION BY RANGE ( YEAR(separated) ) (
    PARTITION p0 VALUES LESS THAN (1991),
    PARTITION p1 VALUES LESS THAN (1996),
    PARTITION p2 VALUES LESS THAN (2001),
    PARTITION p3 VALUES LESS THAN MAXVALUE
);
```
在此方案中，对于1991年之前离开的所有员工，行都存储在partition中p0；对于那些谁留在1991年至1995年，在p1; 
对于那些谁留在1996年年内至2000年，在 p2; 对于2000年以后离开的任何工人，请在p3。

也可以使用函数根据列 RANGE的值 通过来对表进行分区 ，如本示例所示： TIMESTAMPUNIX_TIMESTAMP()

```sql
CREATE TABLE quarterly_report_status (
    report_id INT NOT NULL,
    report_status VARCHAR(20) NOT NULL,
    report_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)
PARTITION BY RANGE ( UNIX_TIMESTAMP(report_updated) ) (
    PARTITION p0 VALUES LESS THAN ( UNIX_TIMESTAMP('2008-01-01 00:00:00') ),
    PARTITION p1 VALUES LESS THAN ( UNIX_TIMESTAMP('2008-04-01 00:00:00') ),
    PARTITION p2 VALUES LESS THAN ( UNIX_TIMESTAMP('2008-07-01 00:00:00') ),
    PARTITION p3 VALUES LESS THAN ( UNIX_TIMESTAMP('2008-10-01 00:00:00') ),
    PARTITION p4 VALUES LESS THAN ( UNIX_TIMESTAMP('2009-01-01 00:00:00') ),
    PARTITION p5 VALUES LESS THAN ( UNIX_TIMESTAMP('2009-04-01 00:00:00') ),
    PARTITION p6 VALUES LESS THAN ( UNIX_TIMESTAMP('2009-07-01 00:00:00') ),
    PARTITION p7 VALUES LESS THAN ( UNIX_TIMESTAMP('2009-10-01 00:00:00') ),
    PARTITION p8 VALUES LESS THAN ( UNIX_TIMESTAMP('2010-01-01 00:00:00') ),
    PARTITION p9 VALUES LESS THAN (MAXVALUE)
);
```

### 范围分区的使用范围

当满足以下一个或多个条件时，范围分区特别有用：

- 您想要或需要删除“旧”数据。如果您使用的是employees表中先前显示的分区方案 ，则只需 ALTER TABLE employees DROP PARTITION p0;
 删除与在1991年前停止在公司工作的员工有关的所有行。对于具有许多行的表，这可能比运行更加高效 DELETE的查询等 
 `DELETE FROM employees WHERE YEAR(separated) <= 1990`;。
 
- 您经常运行直接取决于用于对表进行分区的列的查询。
例如，当执行诸如的查询时 `EXPLAIN SELECT COUNT(*) FROM employees WHERE separated BETWEEN '2000-01-01' AND '2000-12-31' GROUP BY store_id;`，
MySQL可以快速确定仅p2 需要扫描分区，因为其余分区不能包含满足该WHERE 子句的任何记录。 

### 基于时间间隔的分区方案
如果希望在MySQL 5.7中基于范围或时间间隔实现分区方案，则有两个选择：

由分区表RANGE，以及用于分隔表达，使用上的一个功能的操作 DATE， TIME或 DATETIME柱并返回一个整数值，如下所示：

```sql
CREATE TABLE members (
    firstname VARCHAR(25) NOT NULL,
    lastname VARCHAR(25) NOT NULL,
    username VARCHAR(16) NOT NULL,
    email VARCHAR(35),
    joined DATE NOT NULL
)
PARTITION BY RANGE( YEAR(joined) ) (
    PARTITION p0 VALUES LESS THAN (1960),
    PARTITION p1 VALUES LESS THAN (1970),
    PARTITION p2 VALUES LESS THAN (1980),
    PARTITION p3 VALUES LESS THAN (1990),
    PARTITION p4 VALUES LESS THAN MAXVALUE
);
```

在MySQL 5.7中，还可以使用函数RANGE基于TIMESTAMP列 的值对 表进行分区UNIX_TIMESTAMP()，如以下示例所示：
```sql
CREATE TABLE quarterly_report_status (
    report_id INT NOT NULL,
    report_status VARCHAR(20) NOT NULL,
    report_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)
PARTITION BY RANGE ( UNIX_TIMESTAMP(report_updated) ) (
    PARTITION p0 VALUES LESS THAN ( UNIX_TIMESTAMP('2008-01-01 00:00:00') ),
    PARTITION p1 VALUES LESS THAN ( UNIX_TIMESTAMP('2008-04-01 00:00:00') ),
    PARTITION p2 VALUES LESS THAN ( UNIX_TIMESTAMP('2008-07-01 00:00:00') ),
    PARTITION p3 VALUES LESS THAN ( UNIX_TIMESTAMP('2008-10-01 00:00:00') ),
    PARTITION p4 VALUES LESS THAN ( UNIX_TIMESTAMP('2009-01-01 00:00:00') ),
    PARTITION p5 VALUES LESS THAN ( UNIX_TIMESTAMP('2009-04-01 00:00:00') ),
    PARTITION p6 VALUES LESS THAN ( UNIX_TIMESTAMP('2009-07-01 00:00:00') ),
    PARTITION p7 VALUES LESS THAN ( UNIX_TIMESTAMP('2009-10-01 00:00:00') ),
    PARTITION p8 VALUES LESS THAN ( UNIX_TIMESTAMP('2010-01-01 00:00:00') ),
    PARTITION p9 VALUES LESS THAN (MAXVALUE)
);
```
RANGE COLUMNS使用DATE或 DATETIME列作为分区列，按来 对表进行分区。例如， members可以使用joined列直接定义表 ，如下所示：

```sql
CREATE TABLE members (
    firstname VARCHAR(25) NOT NULL,
    lastname VARCHAR(25) NOT NULL,
    username VARCHAR(16) NOT NULL,
    email VARCHAR(35),
    joined DATE NOT NULL
)
PARTITION BY RANGE COLUMNS(joined) (
    PARTITION p0 VALUES LESS THAN ('1960-01-01'),
    PARTITION p1 VALUES LESS THAN ('1970-01-01'),
    PARTITION p2 VALUES LESS THAN ('1980-01-01'),
    PARTITION p3 VALUES LESS THAN ('1990-01-01'),
    PARTITION p4 VALUES LESS THAN MAXVALUE
);
```

