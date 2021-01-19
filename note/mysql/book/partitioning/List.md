## 列表分区

MySQL中的列表分区在许多方面与范围分区类似。与按范围分区一样，每个分区都必须显式定义。这两种类型的分区之间的主要区别在于，
在列表分区中，每个分区都是基于列值在一组值列表中的一个列表中的成员身份来定义和选择的，而不是在一组连续的值范围中的一个列表中。
这是通过使用按列表分区（expr）来实现的，其中expr是一个列值或基于列值的表达式并返回一个整数值，然后通过（value\LIST）
中的值来定义每个分区，其中value\LIST是一个逗号分隔的整数列表。

与按范围定义分区的情况不同，列表分区不需要以任何特定顺序声明

对于下面的示例，我们假设要分区的表的基本定义由CREATE TABLE此处所示的语句提供 ：

```sql
CREATE TABLE employees (
    id INT NOT NULL,
    fname VARCHAR(30),
    lname VARCHAR(30),
    hired DATE NOT NULL DEFAULT '1970-01-01',
    separated DATE NOT NULL DEFAULT '9999-12-31',
    job_code INT,
    store_id INT
);
```
假设有20个视频商店分布在4个专营权中，如下表所示。

| 地区 | 商店编号             |
| :--- | :------------------- |
| 北   | 3、5、6、9、17       |
| 东   | 1，2，10，11，19，20 |
| 西方 | 4、12、13、14、18    |
| 中央 | 7、8、15、16         |

要以某种方式对表进行分区，以便将属于同一区域的商店的行存储在同一分区中，可以使用以下CREATE TABLE 所示的语句：
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
PARTITION BY LIST(store_id) (
    PARTITION pNorth VALUES IN (3,5,6,9,17),
    PARTITION pEast VALUES IN (1,2,10,11,19,20),
    PARTITION pWest VALUES IN (4,12,13,14,18),
    PARTITION pCentral VALUES IN (7,8,15,16)
);
```
这样可以轻松地在表中添加或删除与特定区域相关的员工记录。例如，假设西部地区的所有商店都出售给另一家公司。在MySQL 5.7中，
可以使用查询删除与该区域内商店工作的员工有关的所有行，该查询的 ALTER TABLE employees TRUNCATE PARTITION pWest执行比等效DELETE 
语句要有效得多`DELETE FROM employees WHERE store_id IN (4,12,13,14,18);`。
（使用ALTER TABLE employees DROP PARTITION pWest还会删除所有这些行，但也会pWest从表的定义中删除该分区 ；
您将需要使用一条ALTER TABLE ... ADD PARTITION语句来恢复表的原始分区方案。）

与RANGE分区一样，可以将LIST分区与通过哈希或键进行分区组合以产生复合分区（子分区）。




