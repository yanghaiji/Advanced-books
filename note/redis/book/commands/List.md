## Redis 操作命令 ☞ List

- [1. LPUSH](#LPUSH)
- [2. LPUSHX](#LPUSHX)
- [3. LRANGE](#LRANGE)
- [4. LREM](#LREM)
- [5. LSET](#LSET)
- [6. LTRIM](#LTRIM)
- [7. LPOP](#LPOP)
- [8. HMSET](#HMSET)
- [9. HSET](#HSET)
- [10. HSETNX](#HSETNX)
- [11. HSTRLEN](#HSTRLEN)
- [12. HVALS](#HVALS)


### LPUSH

**起始版本：1.0.0**

**时间复杂度：** O（1）表示添加的每个元素，因此O（N）在使用多个参数调用命令时添加N个元素。

将所有指定的值插入到存于 key 的列表的头部。如果 key 不存在，那么在进行 push 操作前会创建一个空列表。 如果 key 对应的值不是一个 list 的话，那么会返回一个错误。

可以使用一个命令把多个元素 push 进入列表，只需在命令末尾加上多个指定的参数。元素是从最左端的到最右端的、一个接一个被插入到 list 的头部。 所以对于这个命令例子 `LPUSH mylist a b c`，返回的列表是 c 为第一个元素， b 为第二个元素， a 为第三个元素。

#### 语法

`LPUSH key value [value ...]`

#### 返回值

integer-reply: 在 push 操作后的 list 长度。

#### 历史

-    ( >= 2.4 ): 接受多个 value 参数。版本老于 2.4 的 Redis 只能每条命令 push 一个值。

#### 例子

```
redis> LPUSH mylist "world"
(integer) 1
redis> LPUSH mylist "hello"
(integer) 2
redis> LRANGE mylist 0 -1
1) "hello"
2) "world"
redis> 
```

### LPUSHX
**自2.2.0起可用。**

**时间复杂度：**每个添加的元素为O（1），因此当使用多个参数调用命令时，O（N）要添加N个元素。

仅将指定的值插入存储在的列表的开头`key`，前提是`key` 已经存在并保存一个列表。与LPUSH相反，`key`尚不存在时将不执行任何操作。
#### 语法
`LPUSHX key value`
#### 返回值

整数回复：推送操作后列表的长度。

#### 历史

- `>= 4.0`：接受多个`element`参数。在低于4.0的Redis版本中，可以为每个命令推送单个值。

#### 例子

```
redis> LPUSH mylist "World"
(integer) 1
redis> LPUSHX mylist "Hello"
(integer) 2
redis> LPUSHX myotherlist "Hello"
(integer) 0
redis> LRANGE mylist 0 -1
1) "Hello"
2) "World"
redis> LRANGE myotherlist 0 -1
(empty list or set)
redis> 
```

### LRANGE 

**自1.0.0起可用。**

**时间复杂度：** O（S + N）其中S是小列表到HEAD的起始偏移量，大列表到最近的头（HEAD或TAIL）的起始偏移量；N是指定范围内的元素数。

返回存储在中的列表的指定元素`key`。偏移量`start`和`stop`是从零开始的索引，`0`它们是列表的第一个元素（列表的头），`1`是下一个元素，依此类推。

这些偏移量也可以是负数，表示从列表末尾开始的偏移量。例如，`-1`是列表的最后一个元素，`-2`倒数第二个，依此类推。

#### 与各种编程语言中的范围函数保持一致

请注意，如果您有一个从0到100的数字列表，`LRANGE list 0 10`将返回11个元素，即，其中包括最右边的项目。这**可能会或可能不会**与在您选择的编程语言范围相关的功能（认为Ruby的行为是一致的`Range.new`，`Array#slice` 或Python的`range()`功能）。

#### 超出范围的索引

超出范围的索引不会产生错误。如果`start`大于列表的末尾，则返回一个空列表。如果`stop`大于列表的实际末尾，则Redis会将其视为列表的最后一个元素。



#### 返回值

数组回复：指定范围内的元素列表。

#### 例子

```
redis> LPUSH mylist "World"
(integer) 1
redis> LPUSHX mylist "Hello"
(integer) 2
redis> LPUSHX myotherlist "Hello"
(integer) 0
redis> LRANGE mylist 0 -1
1) "Hello"
2) "World"
redis> LRANGE myotherlist 0 -1
(empty list or set)
redis> 
```

### LREM

**起始版本：1.0.0**

**时间复杂度：**O(N) N 表示当前集合的长度

从存于 key 的列表里移除前 count 次出现的值为 value 的元素。 这个 count 参数通过下面几种方式影响这个操作：

- count > 0: 从头往尾移除值为 value 的元素。
- count < 0: 从尾往头移除值为 value 的元素。
- count = 0: 移除所有值为 value 的元素。

比如， LREM list -2 “hello” 会从存于 list 的列表里移除最后两个出现的 “hello”。

需要注意的是，如果list里没有存在key就会被当作空list处理，所以当 key 不存在的时候，这个命令会返回 0。

#### 返回值

integer-reply: 被移除的元素个数。

#### 例子

```
redis> RPUSH mylist "hello"
(integer) 1
redis> RPUSH mylist "hello"
(integer) 2
redis> RPUSH mylist "foo"
(integer) 3
redis> RPUSH mylist "hello"
(integer) 4
redis> LREM mylist -2 "hello"
(integer) 2
redis> LRANGE mylist 0 -1
1) "hello"
2) "foo"
redis> 
```

### LSET

**起始版本：1.0.0**

**时间复杂度：** O(N) N时列标的长度. O(1) 设置列表的第一个或最后一个元素

设置 index 位置的list元素的值为 value。
当index超出范围时会返回一个error。

#### 例子

```
redis> RPUSH mylist "one"
(integer) 1
redis> RPUSH mylist "two"
(integer) 2
redis> RPUSH mylist "three"
(integer) 3
redis> LSET mylist 0 "four"
OK
redis> LSET mylist -2 "five"
OK
redis> LRANGE mylist 0 -1
1) "four"
2) "five"
3) "three"
redis> 
```

### LTRIM

**起始版本：1.0.0**

**时间复杂度：**  O(N) N时列标的长度. O(1) 设置列表的第一个或最后一个元素

修剪(trim)一个已存在的 list，这样 list 就会只包含指定范围的指定元素。start 和 stop 都是由0开始计数的， 这里的 0 是列表里的第一个元素（表头），1 是第二个元素，以此类推。

例如： `LTRIM foobar 0 2` 将会对存储在 foobar 的列表进行修剪，只保留列表里的前3个元素。

start 和 end 也可以用负数来表示与表尾的偏移量，比如 -1 表示列表里的最后一个元素， -2 表示倒数第二个，等等。

超过范围的下标并不会产生错误：如果 start 超过列表尾部，或者 start > end，结果会是列表变成空表（即该 key 会被移除）。 如果 end 超过列表尾部，Redis 会将其当作列表的最后一个元素。

`LTRIM` 的一个常见用法是和 LPUSH 一起使用。 例如：

- LPUSH mylist someelement
- LTRIM mylist 0 99

这一对命令会将一个新的元素 push 进列表里，并保证该列表不会增长到超过100个元素。这个是很有用的，比如当用 Redis 来存储日志。 需要特别注意的是，当用这种方式来使用 LTRIM 的时候，操作的复杂度是 O(1) ， 因为平均情况下，每次只有一个元素会被移除。


#### 例子

```
redis> RPUSH mylist "one"
(integer) 1
redis> RPUSH mylist "two"
(integer) 2
redis> RPUSH mylist "three"
(integer) 3
redis> LTRIM mylist 1 -1
OK
redis> LRANGE mylist 0 -1
1) "two"
2) "three"
redis> 
```

### LPOP
**起始版本：1.0.0**

**时间复杂度：**O(1)

移除并且返回 key 对应的 list 的第一个元素。

#### 返回值

bulk-string-reply: 返回第一个元素的值，或者当 key 不存在时返回 nil。

#### 例子

```
redis> RPUSH mylist "one"
(integer) 1
redis> RPUSH mylist "two"
(integer) 2
redis> RPUSH mylist "three"
(integer) 3
redis> LPOP mylist
"one"
redis> LRANGE mylist 0 -1
1) "two"
2) "three"
redis> 
```

