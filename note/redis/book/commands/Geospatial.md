## Redis 操作命令 ☞ Geospatial(空间地理位置)

- [1. GEOADD](#GEOADD)
- [2. GEODIST](#GEODIST)
- [3. GEOHASH](#GEOHASH)
- [4. GEOPOS](#GEOPOS)
- [5. GEORADIUS](#GEORADIUS)
- [6. GEORADIUSBYMEMBER](#GEORADIUSBYMEMBER)
- [7. GEOSEARCH](#GEOSEARCH)
- [8. GEOSEARCHSTORE](#GEOSEARCHSTORE)


### GEOADD

**自3.2.0起可用。**

**时间复杂度：** 添加的每一项的O（log（N）），其中N是排序集中的元素数。

#### 语法

`GEOADD key longitude latitude member [longitude latitude member ...]`

将指定的地理空间位置（纬度、经度、名称）添加到指定的`key`中。这些数据将会存储到`sorted set`这样的目的是为了方便使用GEORADIUS或者GEORADIUSBYMEMBER命令对数据进行半径查询等操作。

该命令以采用标准格式的参数x,y,所以经度必须在纬度之前。这些坐标的限制是可以被编入索引的，区域面积可以很接近极点但是不能索引。具体的限制，由EPSG:900913 / EPSG:3785 / OSGEO:41001 规定如下：

- 有效的经度从-180度到180度。
- 有效的纬度从-85.05112878度到85.05112878度。

当坐标位置超出上述指定范围时，该命令将会返回一个错误。

#### 它是如何工作的？

sorted set使用一种称为Geohash的技术进行填充。经度和纬度的位是交错的，以形成一个独特的52位整数. 我们知道，一个sorted set 的double score可以代表一个52位的整数，而不会失去精度。

这种格式允许半径查询检查的1 + 8个领域需要覆盖整个半径，并丢弃元素以外的半径。通过计算该区域的范围，通过计算所涵盖的范围，从不太重要的部分的排序集的得分，并计算得分范围为每个区域的sorted set中的查询。

#### 使用什么样的地球模型（Earth model）？

这只是假设地球是一个球体，因为使用的距离公式是Haversine公式。这个公式仅适用于地球，而不是一个完美的球体。当在社交网站和其他大多数需要查询半径的应用中使用时，这些偏差都不算问题。但是，在最坏的情况下的偏差可能是0.5%，所以一些地理位置很关键的应用还是需要谨慎考虑。

#### 返回值

integer-reply, 具体的:

- 添加到sorted set元素的数目，但不包括已更新score的元素。

#### 例子 
```java
127.0.0.1:6379> geoadd china:key 116.23 40.22 beijing
(integer) 1
127.0.0.1:6379> geoadd china:key 121.48 31.40 shanghai
(integer) 1
127.0.0.1:6379> geoadd china:key 113.88 22.55 shengzhen
(integer) 1
127.0.0.1:6379> geodist china:key beijing shanghai
"1088785.4302"
127.0.0.1:6379> geodist china:key beijing shanghai km
"1088.7854"
127.0.0.1:6379>
```

### GEODIST

**自3.2.0起可用。**

**时间复杂度：** O（log（N））

#### 语法
`GEODIST key member1 member2 [m|km|ft|mi]`

返回由排序集表示的地理空间索引中两个成员之间的距离。

给定一个使用GEOADD命令填充的代表地理空间索引的排序集，该命令返回指定单位中两个指定成员之间的距离。

如果缺少一个或两个成员，该命令将返回NULL。

单位必须是以下之一，默认为米：

- **m** 为米。
- **km** 为公里。
- **mi** 数英里。
- **ft** 英尺。

假设地球是一个完美的球体，则计算距离，因此在极端情况下误差可能高达0.5％。

#### 返回值

批量字符串回复，特别是：

该命令以指定的单位以双精度值（以字符串表示）返回距离；如果缺少一个或两个元素，则返回NULL。

#### 例子

```java
127.0.0.1:6379> geoadd china:key 121.48 31.40 shanghai
(integer) 1
127.0.0.1:6379> geodist china:key beijing shanghai
"1088785.4302"
127.0.0.1:6379> geodist china:key beijing shanghai km
"1088.7854"
127.0.0.1:6379> geodist china:key beijing shanghai mi
"676.5416"
127.0.0.1:6379>
```

### GEOHASH 

**自3.2.0起可用。**

**时间复杂度：**每个请求成员的O（log（N）），其中N是排序集中的元素数。

#### 语法
`GEOHASH key member [member ...]`

返回有效的Geohash字符串，该字符串表示一个或多个元素在代表地理空间索引的排序后的集合值中的位置（使用GEOADD在其中添加了元素）。

通常，Redis使用Geohash技术的变体表示元素的位置，其中位置是使用52位整数编码的。与标准相比，编码也有所不同，因为在编码和解码过程中使用的初始最小和最大坐标是不同的。但是，此命令以[Wikipedia文章中所述](https://en.wikipedia.org/wiki/Geohash)且与[geohash.org](http://geohash.org/)网站兼容的字符串形式**返回标准Geohash**。

#### Geohash字符串属性

该命令返回11个字符的Geohash字符串，因此与Redis内部52位表示形式相比，不会损失任何精度。返回的Geohashhes具有以下属性：

1. 可以从右侧删除字符来缩短它们。它将失去精度，但仍将指向同一区域。
2. 可以在`geohash.org`URL中使用它们，例如`http://geohash.org/`。这是此类URL的示例。
3. 前缀相似的字符串在附近，但事实并非如此，前缀不同的字符串也可能在附近。

#### 返回值

数组回复，特别是：

该命令返回一个数组，其中每个元素是与作为参数传递给命令的每个成员名称相对应的Geohash。

#### 示例

```
127.0.0.1:6379> geohash china:key beijing shanghai shengzhen
1) "wx4sucu47r0"
2) "wtw6sk5n300"
3) "ws0br3hgk20"
127.0.0.1:6379>
```

### GEOPOS

**自3.2.0起可用。**

**时间复杂度：**每个请求成员的O（log（N）），其中N是排序集中的元素数。

#### 语法
`GEOPOS key member [member ...]`

返回由*key*的排序集表示的地理空间索引的所有指定成员的位置（经度，纬度）。

给定一个使用GEOADD命令填充的代表地理空间索引的排序集，通常对获取指定成员的坐标很有用。当通过GEOADD填充地理空间索引时，坐标将转换为52位geohash，因此返回的坐标可能与用于添加元素的坐标不完全相同，但是可能会引入一些小误差。

该命令可以接受可变数量的参数，因此即使指定了单个元素，它也始终返回位置数组。

#### 返回值

数组回复，特别是：

该命令返回一个数组，其中每个元素是一个两个元素的数组，分别表示作为参数传递给命令的每个成员名称的经度和纬度（x，y）。

不存在的元素报告为数组的NULL元素。

#### 例子

```
127.0.0.1:6379> geopos china:key beijing shanghai
1) 1) "116.23000055551529"
   2) "40.220001033873984"
2) 1) "121.48000091314316"
   2) "31.400000253193539"
127.0.0.1:6379>  
```

### GEORADIUS

**时间复杂度：** O(N+log(M)) 其中N是由圆心和半径分隔的圆形区域边界框内的元素数，M是索引内的项数。

#### 语法
`GEORADIUS key longitude latitude radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count] [ASC|DESC] [STORE key] [STOREDIST key]`

以给定的经纬度为中心， 返回键包含的位置元素当中， 与中心的距离不超过给定最大距离的所有位置元素。

范围可以使用以下其中一个单位：

- **m** 表示单位为米。
- **km** 表示单位为千米。
- **mi** 表示单位为英里。
- **ft** 表示单位为英尺。

在给定以下可选项时， 命令会返回额外的信息：

- `WITHDIST`: 在返回位置元素的同时， 将位置元素与中心之间的距离也一并返回。 距离的单位和用户给定的范围单位保持一致。
- `WITHCOORD`: 将位置元素的经度和维度也一并返回。
- `WITHHASH`: 以 52 位有符号整数的形式， 返回位置元素经过原始 geohash 编码的有序集合分值。 这个选项主要用于底层应用或者调试， 实际中的作用并不大。

命令默认返回未排序的位置元素。 通过以下两个参数， 用户可以指定被返回位置元素的排序方式：

- `ASC`: 根据中心的位置， 按照从近到远的方式返回位置元素。
- `DESC`: 根据中心的位置， 按照从远到近的方式返回位置元素。

在默认情况下， GEORADIUS 命令会返回所有匹配的位置元素。 虽然用户可以使用 **COUNT ``** 选项去获取前 N 个匹配元素， 但是因为命令在内部可能会需要对所有被匹配的元素进行处理， 所以在对一个非常大的区域进行搜索时， 即使只使用 `COUNT` 选项去获取少量元素， 命令的执行速度也可能会非常慢。 但是从另一方面来说， 使用 `COUNT` 选项去减少需要返回的元素数量， 对于减少带宽来说仍然是非常有用的。

#### 返回值

bulk-string-reply, 具体的:

- 在没有给定任何 `WITH` 选项的情况下， 命令只会返回一个像 [“New York”,”Milan”,”Paris”] 这样的线性（linear）列表。
- 在指定了 `WITHCOORD` 、 `WITHDIST` 、 `WITHHASH` 等选项的情况下， 命令返回一个二层嵌套数组， 内层的每个子数组就表示一个元素。

在返回嵌套数组时， 子数组的第一个元素总是位置元素的名字。 至于额外的信息， 则会作为子数组的后续元素， 按照以下顺序被返回：

1. 以浮点数格式返回的中心与位置元素之间的距离， 单位与用户指定范围时的单位一致。
2. geohash 整数。
3. 由两个元素组成的坐标，分别为经度和纬度。

举个例子， `GEORADIUS Sicily 15 37 200 km WITHCOORD WITHDIST` 这样的命令返回的每个子数组都是类似以下格式的：

```
["Palermo","190.4424",["13.361389338970184","38.115556395496299"]]
```

#### 例子

```
redis> GEOADD Sicily 13.361389 38.115556 "Palermo" 15.087269 37.502669 "Catania"
(integer) 2
redis> GEORADIUS Sicily 15 37 200 km WITHDIST
1) 1) "Palermo"
   2) "190.4424"
2) 1) "Catania"
   2) "56.4413"
redis> GEORADIUS Sicily 15 37 200 km WITHCOORD
1) 1) "Palermo"
   2) 1) "13.361389338970184"
      2) "38.115556395496299"
2) 1) "Catania"
   2) 1) "15.087267458438873"
      2) "37.50266842333162"
redis> GEORADIUS Sicily 15 37 200 km WITHDIST WITHCOORD
1) 1) "Palermo"
   2) "190.4424"
   3) 1) "13.361389338970184"
      2) "38.115556395496299"
2) 1) "Catania"
   2) "56.4413"
   3) 1) "15.087267458438873"
      2) "37.50266842333162"
redis> 
```

### GEORADIUSBYMEMBER

**自3.2.0起可用。**

**时间复杂度：** O（N + log（M）），其中N是圆形区域的边界框内的元素数量，该元素由中心和半径分隔，M是索引内的项目数。

该命令与[GEORADIUS](https://www.redis.io/commands/georadius)完全一样，唯一的区别在于，它不是使用经度和纬度值作为要查询的区域的中心，而是使用已排序集合表示的地理空间索引中已经存在的成员的名称。

指定成员的位置用作查询的中心。

请检查下面的示例和[GEORADIUS](https://www.redis.io/commands/georadius)文档，以获取有关该命令及其选项的更多信息。

请注意，`GEORADIUSBYMEMBER_RO`自Redis 3.2.10和Redis 4.0.0起，此功能也可用，以便提供可在副本中使用的只读命令。有关更多信息，请参见[GEORADIUS](https://www.redis.io/commands/georadius)页面。

#### 例子
```
redis> GEOADD Sicily 13.583333 37.316667 "Agrigento"
(integer) 1
redis> GEOADD Sicily 13.361389 38.115556 "Palermo" 15.087269 37.502669 "Catania"
(integer) 2
redis> GEORADIUSBYMEMBER Sicily Agrigento 100 km
1) "Agrigento"
2) "Palermo"
redis> 
```

### GEOSEARCH

**自6.2起可用。**

**时间复杂度：** O（N + log（M）），其中N是作为过滤器提供的形状周围的网格对齐边界框区域中的元素数，M是形状内的项目数
#### 语法
`GEOSEARCH key [FROMMEMBER member] [FROMLONLAT longitude latitude] [BYRADIUS radius m|km|ft|mi] [BYBOX width height m|km|ft|mi] [ASC|DESC] [COUNT count] [WITHCOORD] [WITHDIST] [WITHHASH]`

使用[GEOADD](https://www.redis.io/commands/geoadd)返回填充有地理空间信息的排序集中的成员，这些成员在给定形状指定的区域的边界内。此命令扩展了[GEORADIUS](https://www.redis.io/commands/georadius)命令，因此除了在圆形区域内搜索之外，它还支持在矩形区域内搜索。

查询的中心点由以下强制性选项之一提供：

- `FROMMEMBER`：使用给定现有项``在排序集中的位置。
- `FROMLONLAT`：使用给定``和``。

查询的形状由以下强制性选项之一提供：

- `BYRADIUS`：与[GEORADIUS](https://www.redis.io/commands/georadius)相似，根据给定范围在圆形区域内搜索``。
- `BYBOX`：在由``和确定的轴对齐矩形内搜索``。

该命令可以选择使用以下选项返回其他信息：

- `WITHDIST`：还返回返回的项目到指定中心的距离。距离以与指定半径或高度和宽度参数相同的单位返回。
- `WITHCOORD`：还返回匹配项的经度和纬度。
- `WITHHASH`：还以52位无符号整数的形式返回该项目的原始geohash编码的排序集得分。这仅对低级黑客或调试有用，否则对普通用户没有什么意义。

默认情况下，该命令返回未排序的项目。可以使用以下两个选项之一来调用两种不同的排序方法：

- `ASC`：相对于中心，从最近到最远对返回的项目进行排序。
- `DESC`：相对于中心，从最远到最近对返回的项目进行排序。

默认情况下，将返回所有匹配项。通过使用**COUNT``**选项，可以将结果限制为前N个匹配项。但是请注意，命令内部需要执行与指定区域匹配的项数成比例的工作量，因此`COUNT`即使返回的结果很少，使用很小的选项查询很大的区域也可能很慢。另一方面，`COUNT`如果通常仅使用第一个结果，则这可能是减少带宽使用的非常有效的方法。

#### 返回值

[数组回复](https://www.redis.io/topics/protocol#array-reply)，特别是：

- 没有`WITH`指定任何选项，该命令仅返回一个线性数组，例如[“ New York”，“ Milan”，“ Paris”]。
- 如果`WITHCOORD`，`WITHDIST`或者`WITHHASH`指定了选项，该命令返回一个数组的数组，其中每个子阵列表示一个项目。

当将其他信息作为每个项目的数组数组返回时，子数组中的第一个项目始终是返回项目的名称。其他信息按以下顺序作为子数组的连续元素返回。

1. 从中心到浮点数的距离，以形状中指定的相同单位。
2. geohash整数。
3. 坐标为两个x，y数组（经度，纬度）。

#### 例子
```
redis> GEOADD Sicily 13.361389 38.115556 "Palermo" 15.087269 37.502669 "Catania"
(integer) 2
redis> GEOADD Sicily 12.758489 38.788135 "edge1" 17.241510 38.788135 "edge2"
(integer) 2
redis> GEOSEARCH Sicily FROMLONLAT 15 37 BYRADIUS 200 km ASC
1) "Catania"
2) "Palermo"
redis> GEOSEARCH Sicily FROMLONLAT 15 37 BYBOX 400 400 km ASC
1) "Catania"
2) "Palermo"
3) "edge2"
4) "edge1"
redis> 
```

### GEOSEARCHSTORE

**自6.2起可用。**

**时间复杂度：** O（N + log（M）），其中N是作为过滤器提供的形状周围的网格对齐边界框区域中的元素数，M是形状内的项目数

此命令类似于[GEOSEARCH](https://www.redis.io/commands/geosearch)，但是将结果存储在目标密钥中。

默认情况下，它将结果`destintion`及其地理空间信息存储在排序集中。

使用该`STOREDIST`选项时，命令将这些项目存储在一个排序集中的项目集中，该项目中它们与圆或框的中心的距离（作为浮点数）以该形状指定的相同单位存储。

#### 语法

`GEOSEARCHSTORE destination source [FROMMEMBER member] [FROMLONLAT longitude latitude] [BYRADIUS radius m|km|ft|mi] [BYBOX width height m|km|ft|mi] [ASC|DESC] [COUNT count] [WITHCOORD] [WITHDIST] [WITHHASH] [STOREDIST]`








