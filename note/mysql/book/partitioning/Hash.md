## Hash 分区

分区依据HASH主要用于确保在预定数量的分区之间均匀分布数据。对于范围或列表分区，必须明确指定将给定列值或一组列值存储在哪个分区中；
使用散列分区，MySQL会为您解决这一问题，您只需要基于要散列的列值和要划分的分区表的分区数来指定列值或表达式。

要使用哈希分区对表进行分区，必须在CREATE table语句中附加partition BY HASH（expr）子句，其中expr是返回整数的表达式。
这可以是一个列的名称，其类型是MySQL的整数类型之一。此外，您很可能希望使用PARTITIONS num来执行此操作，
其中num是一个正整数，表示表要划分到的分区数。

以下语句创建一个在store_id列上使用哈希的表， 并分为4个分区：
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
PARTITION BY HASH(store_id)
PARTITIONS 4;
```
如果不包含PARTITIONS子句，则分区数默认为1。

使用PARTITIONS不带数字的关键字会导致语法错误。

您还可以使用SQL表达式为返回整数 expr。例如，您可能想根据雇用员工的年份进行划分。可以按如下所示完成：

expr必须返回一个非恒定的，非随机的整数值（换句话说，它应该是可变的但是确定的）
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
PARTITION BY HASH( YEAR(hired) )
PARTITIONS 4;
```
最有效的散列函数是对单个表列进行操作的函数，其值随列值的增加或减少一致，因为这允许对分区范围进行“修剪”。
也就是说，表达式随它所基于的列的值变化越密切，MySQL就越能有效地使用表达式进行哈希分区。

例如，where date_col是type的列DATE，则表示表达式 TO_DAYS(date_col)随的值直接变化date_col，因为对于的值的每次更改，
date_col表达式的值都会以一致的方式更改。YEAR(date_col)相对于 的表达式方差 date_col不如的直接 TO_DAYS(date_col)变化，
因为不是的所有可能变化都会date_col产生的等效变化 YEAR(date_col)。即使这样， YEAR(date_col)也很适合使用散列函数，
因为它随的一部分直接变化，date_col并且在date_col导致比例变化很大 YEAR(date_col)。

相比之下，假设您有一个名为int_col类型 的列 INT。现在考虑表达式 POW(5-int_col,3) + 6。对于哈希函数而言，
这将是一个糟糕的选择，因为int_col不能保证值的变化会导致表达式值的变化。将的值更改int_col给定的数量可能会导致表达式的值产生很大的不同。
例如，改变int_col从 5对6产生的变化-1表达式的值，但改变的值int_col，从 6对7产生的变化-7 在表达式值中。

换句话说，列值与表达式的值的曲线越接近方程式所描绘的直线（ 其中 有一些非零常数），则表达式越适合哈希。这与以下事实有关：表达式越非线性，
它倾向于产生的分区之间的数据分布就越不均匀。 y=cxc

从理论上讲，也可以对涉及多个列值的表达式进行修剪，但是要确定哪种表达式合适则可能非常困难且耗时。因此，不建议特别使用涉及多列的哈希表达式。

## 线性哈希分区

MySQL还支持线性散列，这与常规散列的不同之处在于，线性散列使用线性二乘幂算法，而常规散列则使用散列函数值的模数。

从语法上讲，线性散列分区和常规散列之间的唯一区别是LINEAR在PARTITION BY子句中添加了 关键字，如下所示：

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
PARTITION BY LINEAR HASH( YEAR(hired) )
PARTITIONS 4;
```

给定一个表达式*`expr`*，使用线性哈希时存储记录的分区是分区中的分区号*`N`*，其中的*`num`*分区 号*`N`*是根据以下算法得出的：

1. 找到大于2的下一个幂 *`num`*。我们把这个值 *`V`*; 可以计算为：

   ```simple
   V = POWER(2, CEILING(LOG(2, num)))
   ```

   （假定*`num`*为13。 `LOG(2,13)`则为3.7004397181411。 `CEILING(3.7004397181411)`为4，*`V`*= = `POWER(2,4)`为16。）

2. 设置*`N`*= *`F`*（*`column_list`*）＆（*`V`*-1）

3. 而*`N`*> = *`num`*：

   - 设置*`V`*= *`V`*/ 2
   - 设置*`N`*= *`N`*＆（*`V`*--1）

假设`t1`使用以下语句创建使用线性哈希分区并具有6个分区的表：

```sql
CREATE TABLE t1 (col1 INT, col2 CHAR(5), col3 DATE)
    PARTITION BY LINEAR HASH( YEAR(col3) )
    PARTITIONS 6;
```

现在，假设您要插入两个记录以使其 `t1`具有`col3` 列值`'2003-04-14'`和 `'1998-10-19'`。其中第一个的分区号确定如下：

```simple
V = POWER(2, CEILING( LOG(2,6) )) = 8
N = YEAR('2003-04-14') & (8 - 1)
   = 2003 & 7
   = 3

(3 >= 6 is FALSE: record stored in partition #3)
```

计算第二条记录所在的分区号，如下所示：

```simple
V = 8
N = YEAR('1998-10-19') & (8 - 1)
  = 1998 & 7
  = 6

(6 >= 6 is TRUE: additional step required)

N = 6 & ((8 / 2) - 1)
  = 6 & 3
  = 2

(2 >= 6 is FALSE: record stored in partition #2)
```

通过线性哈希进行分区的优势在于，可以更快，更快速地添加，删除，合并和分割分区，这在处理包含大量数据（terabytes）的表时可能是有益的。缺点是，与使用常规哈希分区获得的分布相比，数据不太可能在分区之间均匀分布。





