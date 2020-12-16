## Redis 操作命令 ☞ List

- [1. LPUSH](#LPUSH)
- [2. LPUSHX](#LPUSHX)
- [3. LRANGE](#LRANGE)
- [4. LREM](#LREM)
- [5. LSET](#LSET)
- [6. LTRIM](#LTRIM)
- [7. LPOP](#LPOP)
- [8. LPOS](#LPOS)
- [9. LINDEX](#LINDEX)
- [10. LINSERT](#LINSERT)
- [11. RPUSH](#RPUSH)
- [12. RPUSHX](#RPUSHX)
- [13. RPOP](#RPOP)
- [14. RPOPLPUSH](#RPOPLPUSH)


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

### LPOS

**自6.0.6起可用。**

**时间复杂度：** O（N），其中N是平均情况下列表中元素的数量。当搜索列表开头或结尾附近的元素时，或者提供MAXLEN选项时，该命令可能会在恒定时间内运行。

该命令返回Redis列表内匹配元素的索引。默认情况下，如果没有给出任何选项，它将从头到尾扫描列表，寻找“ element”的第一个匹配项。如果找到该元素，则返回其索引（列表中从零开始的位置）。否则，如果找不到匹配项，则返回NULL。

```
> RPUSH mylist a b c 1 2 3 c c
> LPOS mylist c
2
```

#### 语法

`LPOS key element [RANK rank] [COUNT num-matches] [MAXLEN len]`

可选参数和选项可以修改命令的行为。`RANK`如果存在多个匹配项，则该选项指定要返回的第一个元素的“等级”。等级1表示返回第一个比赛，等级2表示返回第二个比赛，依此类推。

例如，在上面的示例中，元素“ c”多次出现，如果我想要第二个匹配项的索引，我将编写：

```
> LPOS mylist c RANK 2
6
```

也就是说，第二个出现的“ c”在位置6。参数的负“ rank”`RANK`告诉[LPOS](https://redis.io/commands/lpos)从尾到头反转搜索方向。

因此，我们要说的是，从列表的末尾开始给我第一个元素：

```
> LPOS mylist c RANK -1
7
```

请注意，索引仍然以“自然”方式报告，即考虑第一个元素从列表的开头开始，索引为0，下一个元素为索引1，依此类推。这基本上意味着无论排名是正数还是负数，返回的索引都是稳定的。

有时我们不仅要返回第N个匹配元素，还要返回所有前N个匹配元素的位置。这可以使用`COUNT`选项来实现。

```
> LPOS mylist c COUNT 2
[2,6]
```

我们可以结合`COUNT`和`RANK`，以便`COUNT`尝试返回最多指定数目的匹配项，但从第N个匹配项开始（如该`RANK`选项所指定）。

```
> LPOS mylist c RANK -1 COUNT 2
[7,6]
```

当`COUNT`使用时，可以指定0作为匹配的数量，以此来告诉我们希望所有的比赛，发现返回指标阵列的命令。这比给出非常大的`COUNT`选项要好，因为它更通用。

```
> LPOS mylist c COUNT 0
[2,6,7]
```

当`COUNT`被使用并且没有发现匹配，则返回一个空数组。但是，当`COUNT`不使用且没有匹配项时，该命令将返回NULL。

最后，该`MAXLEN`选项告诉命令仅将提供的元素与给定的最大列表项数进行比较。因此，举例来说，指定`MAXLEN 1000`将确保该命令仅执行1000次比较，从而在列表的子集（第一部分或最后部分取决于我们使用正数还是负数的事实）上有效地运行算法。这对于限制命令的最大复杂度很有用。当我们希望很快找到匹配项，但又要确保在不正确的情况下运行该命令时，它也很有用。



#### 返回值

该命令返回表示匹配元素的整数，如果不匹配，则返回null。但是，如果`COUNT`给出了该选项，则命令返回一个数组（如果没有匹配项，则为空）。

#### 例子

```
redis> RPUSH mylist a b c d 1 2 3 4 3 3 3
(integer) 11
redis> LPOS mylist 3
(integer) 6
redis> LPOS mylist 3 COUNT 0 RANK 2
1) (integer) 8
2) (integer) 9
3) (integer) 10
redis> 
```

### LINDEX

**起始版本：1.0.0**

**时间复杂度：** O（N）其中N是要遍历到索引处的元素的数目。这使得请求列表O（1）的第一个或最后一个元素。

返回列表里的元素的索引 index 存储在 key 里面。 下标是从0开始索引的，所以 0 是表示第一个元素， 1 表示第二个元素，并以此类推。 负数索引用于指定从列表尾部开始索引的元素。在这种方法下，-1 表示最后一个元素，-2 表示倒数第二个元素，并以此往前推。

当 key 位置的值不是一个列表的时候，会返回一个error。

#### 返回值

bulk-reply：请求的对应元素，或者当 index 超过范围的时候返回 nil。

#### 例子

```
redis> LPUSH mylist "World"
(integer) 1
redis> LPUSH mylist "Hello"
(integer) 2
redis> LINDEX mylist 0
"Hello"
redis> LINDEX mylist -1
"World"
redis> LINDEX mylist 3
(nil)
redis> 
```

### LINSERT

**起始版本：2.2.0**

**时间复杂度：**O(N)  其中N是在看到值轴之前要遍历的元素数。这意味着在列表（head）的左端插入某个地方可以被认为是O（1），而在右端（tail）的某个地方插入是O（N）。

把 value 插入存于 key 的列表中在基准值 pivot 的前面或后面。

当 key 不存在时，这个list会被看作是空list，任何操作都不会发生。

当 key 存在，但保存的不是一个list的时候，会返回error。

#### 语法
`LINSERT key BEFORE|AFTER pivot value`
#### 返回值

integer-reply: 经过插入操作后的list长度，或者当 pivot 值找不到的时候返回 -1。

#### 例子

```
redis> RPUSH mylist "Hello"
(integer) 1
redis> RPUSH mylist "World"
(integer) 2
redis> LINSERT mylist BEFORE "World" "There"
(integer) 3
redis> LRANGE mylist 0 -1
1) "Hello"
2) "There"
3) "World"
redis> 
```

### LLEN

**起始版本：1.0.0**

**时间复杂度：**O(1)

返回存储在 key 里的list的长度。 如果 key 不存在，那么就被看作是空list，并且返回长度为 0。 当存储在 key 里的值不是一个list的话，会返回error。

#### 返回值

integer-reply: key对应的list的长度。

#### 例子

```
redis> LPUSH mylist "World"
(integer) 1
redis> LPUSH mylist "Hello"
(integer) 2
redis> LLEN mylist
(integer) 2
redis> 
```

### RPUSH
**起始版本：1.0.0**

**时间复杂度：**O(1)

向存于 key 的列表的尾部插入所有指定的值。如果 key 不存在，那么会创建一个空的列表然后再进行 push 操作。 当 key 保存的不是一个列表，那么会返回一个错误。

可以使用一个命令把多个元素打入队列，只需要在命令后面指定多个参数。元素是从左到右一个接一个从列表尾部插入。 比如命令 RPUSH mylist a b c 会返回一个列表，其第一个元素是 a ，第二个元素是 b ，第三个元素是 c。

#### 返回值

integer-reply: 在 push 操作后的列表长度。

#### 历史

  ( > = 2.4): 接受多个 value 参数。 在老于 2.4 的 Redis 版本中，一条命令只能 push 单一个值。

#### 例子

```
redis> RPUSH mylist "hello"
(integer) 1
redis> RPUSH mylist "world"
(integer) 2
redis> LRANGE mylist 0 -1
1) "hello"
2) "world"
redis> 
```

### RPUSHX

**起始版本：2.2.0**

**时间复杂度：**O(1)

将值 value 插入到列表 key 的表尾, 当且仅当 key 存在并且是一个列表。 和 RPUSH命令相反, 当 key 不存在时，RPUSHX 命令什么也不做。

#### 返回值

integer-reply: RPUSHX 命令执行之后，表的长度。

#### 例子

```
redis> RPUSH mylist "Hello"
(integer) 1
redis> RPUSHX mylist "World"
(integer) 2
redis> RPUSHX myotherlist "World"
(integer) 0
redis> LRANGE mylist 0 -1
1) "Hello"
2) "World"
redis> LRANGE myotherlist 0 -1
(empty list or set)
redis> 
```

### RPOP
**起始版本：1.0.0**

**时间复杂度：**O(1)

移除并返回存于 key 的 list 的最后一个元素。

#### 返回值

bulk-string-reply: 最后一个元素的值，或者当 key 不存在的时候返回 nil。

#### 例子

```
redis> RPUSH mylist "one"
(integer) 1
redis> RPUSH mylist "two"
(integer) 2
redis> RPUSH mylist "three"
(integer) 3
redis> RPOP mylist
"three"
redis> LRANGE mylist 0 -1
1) "one"
2) "two"
redis> 
```

### RPOPLPUSH
**起始版本：1.2.0**

**时间复杂度：**O(1)

原子性地返回并移除存储在 source 的列表的最后一个元素（列表尾部元素）， 并把该元素放入存储在 destination 的列表的第一个元素位置（列表头部）。

例如：假设 source 存储着列表 a,b,c， destination存储着列表 x,y,z。 执行 RPOPLPUSH 得到的结果是 source 保存着列表 a,b ，而 destination 保存着列表 c,x,y,z。

如果 source 不存在，那么会返回 nil 值，并且不会执行任何操作。 如果 source 和 destination 是同样的，那么这个操作等同于移除列表最后一个元素并且把该元素放在列表头部， 所以这个命令也可以当作是一个旋转列表的命令。

#### 返回值

bulk-string-reply: 被移除和放入的元素

#### 例子

```
redis> RPUSH mylist "one"
(integer) 1
redis> RPUSH mylist "two"
(integer) 2
redis> RPUSH mylist "three"
(integer) 3
redis> RPOPLPUSH mylist myotherlist
"three"
redis> LRANGE mylist 0 -1
1) "one"
2) "two"
redis> LRANGE myotherlist 0 -1
1) "three"
redis> 
```

模式：安全的队列

Redis通常都被用做一个处理各种后台工作或消息任务的消息服务器。 一个简单的队列模式就是：生产者把消息放入一个列表中，等待消息的消费者用 RPOP命令（用轮询方式）， 或者用 BRPOP 命令（如果客户端使用阻塞操作会更好）来得到这个消息。

然而，因为消息有可能会丢失，所以这种队列并是不安全的。例如，当接收到消息后，出现了网络问题或者消费者端崩溃了， 那么这个消息就丢失了。

RPOPLPUSH (或者其阻塞版本的 BRPOPLPUSH） 提供了一种方法来避免这个问题：消费者端取到消息的同时把该消息放入一个正在处理中的列表。 当消息被处理了之后，该命令会使用 LREM 命令来移除正在处理中列表中的对应消息。

另外，可以添加一个客户端来监控这个正在处理中列表，如果有某些消息已经在这个列表中存在很长时间了（即超过一定的处理时限）， 那么这个客户端会把这些超时消息重新加入到队列中。

#### 模式：循环列表

RPOPLPUSH 命令的 source 和 destination 是相同的话， 那么客户端在访问一个拥有n个元素的列表时，可以在 O(N) 时间里一个接一个获取列表元素， 而不用像 LRANGE那样需要把整个列表从服务器端传送到客户端。

上面这种模式即使在以下两种情况下照样能很好地工作： * 有多个客户端同时对同一个列表进行旋转（rotating）：它们会取得不同的元素，直到列表里所有元素都被访问过，又从头开始这个操作。 * 有其他客户端在往列表末端加入新的元素。

这个模式让我们可以很容易地实现这样一个系统：有 N 个客户端，需要连续不断地对一批元素进行处理，而且处理的过程必须尽可能地快。 一个典型的例子就是服务器上的监控程序：它们需要在尽可能短的时间内，并行地检查一批网站，确保它们的可访问性。

值得注意的是，使用这个模式的客户端是易于扩展（scalable）且安全的（reliable），因为即使客户端把接收到的消息丢失了， 这个消息依然存在于队列中，等下次迭代到它的时候，由其他客户端进行处理。



