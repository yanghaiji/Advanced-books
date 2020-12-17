## Redis 操作命令 ☞ Hash

- [1. HDEL](#HDEL)
- [2. HEXISTS](#HEXISTS)
- [3. HGET](#HGET)
- [4. HGETALL](#HGETALL)
- [5. HKEYS](#HKEYS)
- [6. HLEN](#HLEN)
- [7. HMGET](#HMGET)
- [8. HMSET](#HMSET)
- [9. HSET](#HSET)
- [10. HSETNX](#HSETNX)
- [11. HSTRLEN](#HSTRLEN)
- [12. HVALS](#HVALS)



### HDEL

**起始版本：2.0.0**

**时间复杂度：**O(N) N是被删除的字段数量。

从 key 指定的哈希集中移除指定的域。在哈希集中不存在的域将被忽略。

如果 key 指定的哈希集不存在，它将被认为是一个空的哈希集，该命令将返回0。
#### 语法
`HDEL key field [field ...]`
#### 返回值

integer-reply： 返回从哈希集中成功移除的域的数量，不包括指出但不存在的那些域

历史

- 在 2.4及以上版本中 ：可接受多个域作为参数。小于 2.4版本 的 Redis 每次调用只能移除一个域 要在早期版本中以原子方式从哈希集中移除多个域，可用 MULTI/EXEC块。

#### 例子

```
redis> HSET myhash field1 "foo"
(integer) 1
redis> HDEL myhash field1
(integer) 1
redis> HDEL myhash field2
(integer) 0
redis> 
```

### HEXISTS

**起始版本：2.0.0**

**时间复杂度：**O(1)

返回hash里面field是否存在
#### 语法
`HEXISTS key field`
#### 返回值

integer-reply, 含义如下：

- 1 hash里面包含该field。
- 0 hash里面不包含该field或者key不存在。

#### 例子

```
redis> HSET myhash field1 "foo"
(integer) 1
redis> HEXISTS myhash field1
(integer) 1
redis> HEXISTS myhash field2
(integer) 0
redis> 
```

### HGET

**起始版本：2.0.0**

**时间复杂度：**O(1)

返回 key 指定的哈希集中该字段所关联的值

#### 语法

```
HGET key field
```




#### 返回值

bulk-string-reply：该字段所关联的值。当字段不存在或者 key 不存在时返回nil。

#### 例子

```
redis> HSET myhash field1 "foo"
(integer) 1
redis> HGET myhash field1
"foo"
redis> HGET myhash field2
(nil)
redis> 
```

### HGETALL

**起始版本：2.0.0**

**时间复杂度：** 其中N是散列的大小

返回 key 指定的哈希集中所有的字段和值。返回值中，每个字段名的下一个是它的值，所以返回值的长度是哈希集大小的两倍

#### 语法

`HGETALL key`

#### 返回值

array-reply：哈希集中字段和值的列表。当 key 指定的哈希集不存在时返回空列表。

#### 例子

```
redis> HSET myhash field1 "Hello"
(integer) 1
redis> HSET myhash field2 "World"
(integer) 1
redis> HGETALL myhash
1) "field1"
2) "Hello"
3) "field2"
4) "World"
redis> 
```

### HINCRBY

增加 `key` 指定的哈希集中指定字段的数值。如果 `key` 不存在，会创建一个新的哈希集并与 `key` 关联。如果字段不存在，则字段的值在该操作执行前被设置为 0

`HINCRBY` 支持的值的范围限定在 64位 有符号整数
#### 语法
`HINCRBY key field increment`
#### 返回值

integer-reply：增值操作执行后的该字段的值。

#### 例子

```
redis> HSET myhash field 5
(integer) 1
redis> HINCRBY myhash field 1
(integer) 6
redis> HINCRBY myhash field -1
(integer) 5
redis> HINCRBY myhash field -10
(integer) -5
redis> 
```

### HKEYS

**起始版本：2.0.0**

**时间复杂度：**O(N) where N is the size of the hash.

返回 key 指定的哈希集中所有字段的名字。
#### 语法
`HKEYS key`
#### 返回值

array-reply：哈希集中的字段列表，当 key 指定的哈希集不存在时返回空列表。

#### 例子

```
redis> HSET myhash field1 "Hello"
(integer) 1
redis> HSET myhash field2 "World"
(integer) 1
redis> HKEYS myhash
1) "field1"
2) "field2"
redis> 
```

### HLEN

**起始版本：2.0.0**

**时间复杂度：**O(1)

返回 `key` 指定的哈希集包含的字段的数量。
#### 语法
`HLEN key`
#### 返回值

integer-reply： 哈希集中字段的数量，当 `key` 指定的哈希集不存在时返回 0

#### 例子

```
redis> HSET myhash field1 "Hello"
(integer) 1
redis> HSET myhash field2 "World"
(integer) 1
redis> HLEN myhash
(integer) 2
redis> 
```

### HMGET

**起始版本：2.0.0**

**时间复杂度：**O(N) where N is the number of fields being requested.

返回 `key` 指定的哈希集中指定字段的值。

对于哈希集中不存在的每个字段，返回 `nil` 值。因为不存在的keys被认为是一个空的哈希集，对一个不存在的 `key` 执行 HMGET 将返回一个只含有 `nil` 值的列表

#### 语法

`HMGET key field [field ...]`

#### 返回值

array-reply ：含有给定字段及其值的列表，并保持与请求相同的顺序。

#### 例子

```
redis> HSET myhash field1 "Hello"
(integer) 1
redis> HSET myhash field2 "World"
(integer) 1
redis> HMGET myhash field1 field2 nofield
1) "Hello"
2) "World"
3) (nil)
redis> 
```

### HMSET

**自2.0.0起可用。**

**时间复杂度：** O（N），其中N是要设置的字段数。

将指定的字段设置为存储在中的哈希中的相应值 `key`。该命令将覆盖哈希中已经存在的所有指定字段。如果`key`不存在，则创建一个包含哈希的新密钥。

根据Redis 4.0.0，HMSET被视为已弃用。请在新代码中使用HSET。

#### 例子

```
redis> HMSET myhash field1 "Hello" field2 "World"
OK
redis> HGET myhash field1
"Hello"
redis> HGET myhash field2
"World"
redis> 
```

### HSET

**自2.0.0起可用。**

**时间复杂度：**对于每个添加的字段/值对，为O（1），因此当使用多个字段/值对调用命令时，O（N）要添加N个字段/值对。

设置`field`在存储在哈希`key`来`value`。如果`key`不存在，则创建一个包含哈希的新密钥。如果`field`散列中已经存在，则将其覆盖。

从Redis 4.0.0开始，HSET是可变的，并允许多个`field`/`value`对。

#### 语法

`HSET key field value [field value ...]`

#### 返回值

整数回复：添加的字段数。

#### 例子

```
redis> **HSET myhash field1 "Hello"**
(integer) 1

redis> **HGET myhash field1**
"Hello"
redis> 
```

### HSETNX

**起始版本：2.0.0**

**时间复杂度：**O(1)

只在 `key` 指定的哈希集中不存在指定的字段时，设置字段的值。如果 `key` 指定的哈希集不存在，会创建一个新的哈希集并与 `key` 关联。如果字段已存在，该操作无效果。

#### 返回值

integer-reply：含义如下

- 1：如果字段是个新的字段，并成功赋值
- 0：如果哈希集中已存在该字段，没有操作被执行

#### 例子

```
redis> HSETNX myhash field "Hello"
(integer) 1
redis> HSETNX myhash field "World"
(integer) 0
redis> HGET myhash field
"Hello"
redis> 
```

### HSTRLEN

**起始版本：3.2.0**

**时间复杂度：**O(1)

返回hash指定field的value的字符串长度，如果hash或者field不存在，返回0.

#### 返回值
[integer-reply:返回hash指定field的value的字符串长度，如果hash或者field不存在，返回0.

#### 例子

```
redis> HMSET myhash f1 HelloWorld f2 99 f3 -256
OK
redis> HSTRLEN myhash f1
(integer) 10
redis> HSTRLEN myhash f2
(integer) 2
redis> HSTRLEN myhash f3
(integer) 4
redis> 
```

### HVALS

**起始版本：2.0.0**

**时间复杂度：**O(N) 其中N是散列的大小

返回 key 指定的哈希集中所有字段的值。

返回值

array-reply：哈希集中的值的列表，当 key 指定的哈希集不存在时返回空列表。

#### 例子

```
redis> HSET myhash field1 "Hello"
(integer) 1
redis> HSET myhash field2 "World"
(integer) 1
redis> HVALS myhash
1) "Hello"
2) "World"
redis> 
```



