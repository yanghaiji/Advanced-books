## Redis 操作命令 ☞ Strings

- [1. APPEND](#APPEND)
- [2. DECR](#DECR)
- [3. DECR](#DECRBY)
- [4. GET](#GET)
- [5. GETRANGE](#GETRANGE)
- [6. GETSET](#GETSET)
- [7. INCR](#INCR)
- [8. INCRBYFLOAT](#INCRBYFLOAT)
- [9. MGET](#MGET)
- [10. SET](#SET)
- [11. MSET](#MSET)
- [12. SETNX](#SETNX)
- [12. MSETNX](#MSETNX)

### APPEND

>起始版本：2.0.0
>
>时间复杂度：O(1)。均摊时间复杂度是O(1)
>
>因为redis用的动态字符串的库在每次分配空间的时候会增加一倍的可用空闲空间，
>所以在添加的value较小而且已经存在的 value是任意大小的情况下，均摊时间复杂度是O(1) 。

如果 key 已经存在，并且值为字符串，那么这个命令会把 value 追加到原来值（value）的结尾。 如果 key 不存在，那么它将首先创建一个空字符串的key，再执行追加操作，这种情况 APPEND 将类似于 SET 操作。

#### 语法

`APPEND key value`

#### 返回值 

Integer reply：返回append后字符串值（value）的长度。

#### 示例：
```
127.0.0.1:6379> exists javayh
(integer) 0
127.0.0.1:6379> append javayh "Hello "
(integer) 6
127.0.0.1:6379> append javayh "World"
(integer) 11
127.0.0.1:6379> get javayh
"Hello World"
```

### DECR
>起始版本：1.0.0
>
>时间复杂度：O(1)
>
>对key对应的数字做减1操作。如果key不存在，那么在操作之前，这个key对应的值会被置为0。如果key有一个错误类型的value或者是一个不能表示成数字的字符串，就返回错误。这个操作最大支持在64位有符号的整型数字。

#### 语法

`DECR key`

#### 返回值

数字：减小之后的value

#### 示例
```
127.0.0.1:6379> set javayh 10
OK
127.0.0.1:6379> decr javayh
(integer) 9
127.0.0.1:6379> get javayh
"9"
127.0.0.1:6379> set test 234293482390480948029348230948
OK
127.0.0.1:6379> decr test
(error) ERR value is not an integer or out of range
127.0.0.1:6379>
```

### DECRBY
>起始版本：1.0.0
>
>时间复杂度：O(1)
>
>对key对应的数字做减1操作。如果key不存在，那么在操作之前，这个key对应的值会被置为0。如果key有一个错误类型的value或者是一个不能表示成数字的字符串，就返回错误。这个操作最大支持在64位有符号的整型数字。

#### 语法

`DECRBY key decrement`

#### 返回值

数字：减小之后的value

#### 示例
```
127.0.0.1:6379> set javayh 10
OK
127.0.0.1:6379> decrby javayh 7
(integer) 7
127.0.0.1:6379> get javayh
"7"
```

### GET

>起始版本：1.0.0
>
>时间复杂度：O(1)
>
>返回key的value。如果key不存在，返回特殊值nil。如果key的value不是string，就返回错误，因为GET只处理string类型的values。

#### 语法

`GET key`

#### 返回值

simple-string-reply : key对应的value，或者nil（key不存在时）

#### 例子

```
redis> GET nonexisting
(nil)
redis> SET javayh "Hello"
OK
redis> GET javayh
"Hello"
redis> 
```

### GETRANGE

>始版本：2.4.0
> 
> 时间复杂度：O(N) N是字符串长度，复杂度由最终返回长度决定，但由于通过一个字符串创建子字符串是很容易的，它可以被认为是O(1)。
>
> 这个命令是被改成GETRANGE的，在小于2.0的Redis版本中叫SUBSTR。 返回key对应的字符串value的子串，这个子串是由start和end位移决定的（两者都在string内）。可以用负的位移来表示从string尾部开始数的下标。所以-1就是最后一个字符，-2就是倒数第二个，以此类推。
  这个函数处理超出范围的请求时，都把结果限制在string内。

#### 语法

`GETRANGE key start end`

#### 返回值

key对应知道下标的value，

#### 例子

```
127.0.0.1:6379> set javayh "java you huo"
OK
127.0.0.1:6379> getrange javayh 0 3
"java"
127.0.0.1:6379> getrange javayh -5 -1
"u huo"
127.0.0.1:6379> 
```

### GETSET
>起始版本：1.0.0
>
 >时间复杂度：O(1)

自动将key对应到value并且返回原来key对应的value。如果key存在但是对应的value不是字符串，就返回错误。
#### 设计模式

GETSET可以和INCR一起使用实现支持重置的计数功能。举个例子：每当有事件发生的时候，一段程序都会调用INCR给key mycounter加1，但是有时我们需要获取计数器的值，并且自动将其重置为0。这可以通过GETSET mycounter “0”来实现：

```
INCR mycounter
GETSET mycounter "0"
GET mycounter
```
#### 返回值
bulk-string-reply: 返回之前的旧值，如果之前Key不存在将返回nil。

#### 语法
`GETSET key value`

#### 示例
```
127.0.0.1:6379> incr test
(integer) 1
127.0.0.1:6379> get test
"1"
127.0.0.1:6379> getset test 0
"1"
127.0.0.1:6379> get test
"0"
127.0.0.1:6379> 
```

### INCR

>起始版本：1.0.0
> 
>时间复杂度：O(1)

对存储在指定`key`的数值执行原子的加1操作。

如果指定的key不存在，那么在执行incr操作之前，会先将它的值设定为`0`。

如果指定的key中存储的值不是字符串类型（fix：）或者存储的字符串类型不能表示为一个整数，

那么执行这个命令时服务器会返回一个错误(eq:(error) ERR value is not an integer or out of range)。

这个操作仅限于64位的有符号整型数据。

**注意**: 由于redis并没有一个明确的类型来表示整型数据，所以这个操作是一个字符串操作。

执行这个操作的时候，key对应存储的字符串被解析为10进制的**64位有符号整型数据**。

事实上，Redis 内部采用整数形式（Integer representation）来存储对应的整数值，所以对该类字符串值实际上是用整数保存，也就不存在存储整数的字符串表示（String representation）所带来的额外消耗。

#### 返回值

integer-reply :执行递增操作后`key`对应的值。

#### 例子

```
redis> SET mykey "10"
OK
redis> INCR mykey
(integer) 11
redis> GET mykey
"11"
redis> 
```

#### 实例：计数器

Redis的原子递增操作最常用的使用场景是计数器。

使用思路是：每次有相关操作的时候，就向Redis服务器发送一个incr命令。

例如这样一个场景：我们有一个web应用，我们想记录每个用户每天访问这个网站的次数。

web应用只需要通过拼接用户id和代表当前时间的字符串作为key，每次用户访问这个页面的时候对这个key执行一下incr命令。

这个场景可以有很多种扩展方法:

- 通过结合使用`INCR`和EXPIRE命令，可以实现一个只记录用户在指定间隔时间内的访问次数的计数器
- 客户端可以通过GETSET命令获取当前计数器的值并且重置为0
- 通过类似于DECR或者INCRBY等原子递增/递减的命令，可以根据用户的操作来增加或者减少某些值 比如在线游戏，需要对用户的游戏分数进行实时控制，分数可能增加也可能减少。

#### 实例: 限速器

限速器是一种可以限制某些操作执行速率的特殊场景。

传统的例子就是限制某个公共api的请求数目。

假设我们要解决如下问题：限制某个api每秒每个ip的请求次数不超过10次。

我们可以通过incr命令来实现两种方法解决这个问题。

#### 实例: 限速器 1

更加简单和直接的实现如下：

```
FUNCTION LIMIT_API_CALL(ip)
ts = CURRENT_UNIX_TIME()
keyname = ip+":"+ts
current = GET(keyname)
IF current != NULL AND current > 10 THEN
    ERROR "too many requests per second"
ELSE
    MULTI
        INCR(keyname,1)
        EXPIRE(keyname,10)
    EXEC
    PERFORM_API_CALL()
END
```

这种方法的基本点是每个ip每秒生成一个可以记录请求数的计数器。

但是这些计数器每次递增的时候都设置了10秒的过期时间，这样在进入下一秒之后，redis会自动删除前一秒的计数器。

注意上面伪代码中我们用到了MULTI和EXEC命令，将递增操作和设置过期时间的操作放在了一个事务中， 从而保证了两个操作的原子性。

#### 实例: 限速器 2

另外一个实现是对每个ip只用一个单独的计数器（不是每秒生成一个），但是需要注意避免竟态条件。 我们会对多种不同的变量进行测试。

```
FUNCTION LIMIT_API_CALL(ip):
current = GET(ip)
IF current != NULL AND current > 10 THEN
    ERROR "too many requests per second"
ELSE
    value = INCR(ip)
    IF value == 1 THEN
        EXPIRE(value,1)
    END
    PERFORM_API_CALL()
END
```

上述方法的思路是，从第一个请求开始设置过期时间为1秒。如果1秒内请求数超过了10个，那么会抛异常。

否则，计数器会清零。

**上述代码中**，可能会进入竞态条件，比如客户端在执行INCR之后，没有成功设置EXPIRE时间。这个ip的key 会造成内存泄漏，直到下次有同一个ip发送相同的请求过来。

把上述INCR和EXPIRE命令写在lua脚本并执行EVAL命令可以避免上述问题（只有redis版本>＝2.6才可以使用）

```
local current
current = redis.call("incr",KEYS[1])
if tonumber(current) == 1 then
    redis.call("expire",KEYS[1],1)
end
```

还可以通过使用redis的list来解决上述问题避免进入竞态条件。

实现代码更加复杂并且利用了一些redis的新的feature，可以记录当前请求的客户端ip地址。这个有没有好处 取决于应用程序本身。

```
FUNCTION LIMIT_API_CALL(ip)
current = LLEN(ip)
IF current > 10 THEN
    ERROR "too many requests per second"
ELSE
    IF EXISTS(ip) == FALSE
        MULTI
            RPUSH(ip,ip)
            EXPIRE(ip,1)
        EXEC
    ELSE
        RPUSHX(ip,ip)
    END
    PERFORM_API_CALL()
END
```

The `RPUSHX` command only pushes the element if the key already exists.

RPUSHX命令会往list中插入一个元素，如果key存在的话

上述实现也可能会出现竞态，比如我们在执行EXISTS指令之后返回了false，但是另外一个客户端创建了这个key。

后果就是我们会少记录一个请求。但是这种情况很少出现，所以我们的请求限速器还是能够运行良好的。

**注**

有了`INCR` 自然大家也会联想到 `INCRBY key increment` ，这里就不作过多的介绍了

### INCRBYFLOAT

>起始版本：2.6.0
 >
>时间复杂度：O(1)

通过指定浮点数`key`来增长浮点数(存放于string中)的值. 当键不存在时,先将其值设为0再操作.下面任一情况都会返回错误:

- key 包含非法值(不是一个string).
- 当前的key或者相加后的值不能解析为一个双精度的浮点值.(超出精度范围了)

如果操作命令成功, 相加后的值将替换原值存储在对应的键值上, 并以string的类型返回. string中已存的值或者相加参数可以任意选用指数符号,但相加计算的结果会以科学计数法的格式存储. 无论各计算的内部精度如何, 输出精度都固定为小数点后17位.

#### 返回值

Bulk-string-reply: 当前`key`增加increment后的值。

#### 例子

```
redis> SET mykey 10.50
OK
redis> INCRBYFLOAT mykey 0.1
"10.6"
redis> SET mykey 5.0e3
OK
redis> INCRBYFLOAT mykey 2.0e2
"5200"
redis> 
```

#### 执行细节

该命令总是衍生为一个链接复制以及追加文件的set操作 , 所以底层浮点数的实现的差异并不是造成不一致的源头

### MGET

#### 语法
`MGET key [key ...]`

**起始版本：1.0.0**

**时间复杂度：**O(N) 其中N是要检索key的数。

返回所有指定的key的value。对于每个不对应string或者不存在的key，都返回特殊值`nil`。正因为此，这个操作从来不会失败。

#### 返回值

array-reply: 指定的key对应的values的list

#### 例子

```
redis> SET key1 "Hello"
OK
redis> SET key2 "World"
OK
redis> MGET key1 key2 nonexisting
1) "Hello"
2) "World"
3) (nil)
redis> 
```


### SET
>自1.0.0起可用。
>
>时间复杂度： O（1）
>
设置key为保留字符串value。如果key已经拥有一个值，则无论其类型如何，它都会被覆盖。成功进行SET操作后，与密钥关联的任何先前生存时间都会被丢弃。

#### 选件

在SET命令支持一组修改其行为的选项：

- `EX` *seconds-*设置指定的到期时间，以秒为单位。
- `PX` *毫秒*-设置指定的到期时间（以毫秒为单位）。
- `NX` -仅设置不存在的密钥。
- `XX` -仅设置已存在的密钥。
- `KEEPTTL` -保留与钥匙关联的生存时间。
- GET返回存储在key处的旧值；如果key不存在，则返回nil

注意: 由于SET命令加上选项已经可以完全取代SETNX, SETEX, PSETEX的功能，所以在将来的版本中，redis可能会不推荐使用并且最终抛弃这几个命令。

#### 返回值

简单字符串回复：`OK`如果SET执行正确。 批量字符串回复：设GET选项时，旧值存储在key处；如果key不存在，则为nil。 空回复：如果返回一个空批量回复SET因为用户指定的，不能执行操作`NX`或`XX`选项，但在条件不成立，或者如果用户指定的`NX`和GET不见面的选项。

#### 历史

- `>= 2.6.12`：增加了`EX`，`PX`，`NX`和`XX`选项。
- `>= 6.0`：添加了该`KEEPTTL`选项。
- `>= 6.2`：添加了GET选项。

#### 例子

```
redis> SET mykey "Hello"
"OK"
redis> GET mykey
"Hello"
redis> SET anotherkey "will expire in a minute" EX 60
"OK"
redis> 
```

#### 模式

**注意：**不建议使用以下模式来支持Redlock算法，该算法实现起来稍微复杂一些，但是提供了更好的保证，并且具有容错能力。

该命令`SET resource-name anystring NX EX max-lock-time`是使用Redis实现锁定系统的简单方法。

如果以上命令返回，则客户端可以获取锁`OK`（如果命令返回Nil，则可以在一段时间后重试），然后仅使用DEL即可删除锁。

达到到期时间后，锁将自动释放。

可以如下修改修改解锁模式，使该系统更强大：

- 而不是设置固定的字符串，而是设置一个不可猜测的大随机字符串，称为令牌。
- 而不是使用DEL释放锁定，而是发送一个仅在值匹配时才删除密钥的脚本。

这样可以避免客户端在到期时间之后尝试删除该锁，以删除稍后获得该锁的另一个客户端创建的密钥。

解锁脚本的示例类似于以下内容：

```
if redis.call("get",KEYS[1]) == ARGV[1]
then
    return redis.call("del",KEYS[1])
else
    return 0
end
```

该脚本应使用 `EVAL ...script... 1 resource-name token-value`


### MSET

**起始版本：1.0.1**

**时间复杂度：**O(N) 

对应给定的keys到他们相应的values上。`MSET`会用新的value替换已经存在的value，就像普通的SET命令一样。如果你不想覆盖已经存在的values，请参看命令MSETNX。

`MSET`是原子的，所以所有给定的keys是一次性set的。客户端不可能看到这种一部分keys被更新而另外的没有改变的情况。

#### 语法
`MSET key value [key value ...]`
#### 返回值

simple-string-reply：总是OK，因为MSET不会失败。

#### 例子

```
redis> MSET key1 "Hello" key2 "World"
OK
redis> GET key1
"Hello"
redis> GET key2
"World"
redis> 
```

### SETNX

**起始版本：1.0.0**

**时间复杂度：**O(1)

将`key`设置值为`value`，如果`key`不存在，这种情况下等同SET命令。 当`key`存在时，什么也不做。`SETNX`是”**SET** if **N**ot e**X**ists”的简写。

#### 返回值

Integer reply, 特定值:

- `1` 如果key被设置了
- `0` 如果key没有被设置

#### 例子

```
redis> SETNX mykey "Hello"
(integer) 1
redis> SETNX mykey "World"
(integer) 0
redis> GET mykey
"Hello"
redis> 
```

#### 设计模式：锁定 SETNX

**请注意：**

1. 不鼓励使用以下模式来支持Redlock算法，该算法实现起来稍微复杂一些，但是提供了更好的保证，并且具有容错能力。
2. 无论如何，我们都记录了旧模式，因为某些现有的实现链接到此页面作为参考。此外，这是一个有趣的示例，说明如何使用Redis命令挂载编程原语。
3. 无论如何，即使假设一个单实例锁定原语（从2.6.12开始），也可以使用SET命令获取该锁定，并使用一个简单的Lua脚本来释放一个更简单的锁定原语（等效于此处讨论的锁定原语）。锁。该模式记录在SET命令页面中。

就是说，SETNX可以用作锁定原语，并且在过去一直被用作锁定原语。例如，要获取钥匙的锁`foo`，客户端可以尝试以下操作：

```
SETNX lock.foo <current Unix time + lock timeout + 1>
```

如果SETNX返回`1`客户端获得的锁，则将`lock.foo`密钥设置为Unix时间，此时不应再将该锁视为有效。客户端稍后将使用`DEL lock.foo`以释放锁。

如果SETNX返回，则`0`该密钥已被其他客户端锁定。如果它是非阻塞锁，则可以返回到调用方，也可以进入一个循环重试以保持该锁，直到成功或某种超时到期为止。



### 处理死锁

在上述锁定算法中，存在一个问题：如果客户端发生故障，崩溃或无法释放锁定，该怎么办？由于锁定键包含UNIX时间戳，因此可以检测到这种情况。如果这样的时间戳等于当前的Unix时间，则该锁定不再有效。

发生这种情况时，我们不能只对密钥调用DEL来删除锁，然后尝试发出SETNX，因为这里存在竞争状态，当多个客户端检测到到期的锁并试图释放它时。

- C1和C2读取`lock.foo`以检查时间戳记，因为它们都`0`在执行SETNX之后接收到 ，因为锁仍然由C3持有，而C3在持有该锁之后崩溃了。
- C1发送 `DEL lock.foo`
- C1发送`SETNX lock.foo`成功
- C2发送 `DEL lock.foo`
- C2发送`SETNX lock.foo`成功
- **错误**：由于竞争情况，C1和C2均获得了锁定。

幸运的是，使用以下算法可以避免此问题。让我们看看我们明智的客户端C4如何使用好的算法：

- C4发送`SETNX lock.foo`以获取锁

- 崩溃的客户端C3仍然保留它，因此Redis将回复`0`C4。

- C4发送`GET lock.foo`以检查锁是否过期。如果不是，它将休眠一段时间并从头开始重试。

- 相反，如果由于Unix时间`lock.foo`早于当前Unix时间而使锁过期，则C4尝试执行：

  ```
  GETSET lock.foo <current Unix timestamp + lock timeout + 1>
  ```

- 由于GETSET语义，C4可以检查存储在的旧值 `key`是否仍是过期的时间戳。如果是，则已获取锁。

- 如果另一个客户端（例如C5）比C4快，并通过GETSET操作获得了锁定，则C4 GETSET操作将返回未过期的时间戳。C4将仅从第一步重新启动。请注意，即使C4在将来几秒钟设置了密钥，这也不是问题。

为了使此锁定算法更可靠，持有锁的客户端应始终检查超时是否过期，然后再使用DEL解锁密钥，因为客户端失败可能很复杂，不仅会崩溃，而且还会浪费很多时间进行某些操作并在很多时间后尝试发布DEL（当LOCK已被另一个客户端持有时）。

### MSETNX

**起始版本：1.0.1**

**时间复杂度：**O(N) 

对应给定的keys到他们相应的values上。`MSET`会用新的value替换已经存在的value，就像普通的SET命令一样。如果你不想覆盖已经存在的values，请参看命令MSETNX。

`MSET`是原子的，所以所有给定的keys是一次性set的。客户端不可能看到这种一部分keys被更新而另外的没有改变的情况。

#### 返回值

simple-string-reply：总是OK，因为MSET不会失败。

#### 例子

```
redis> MSET key1 "Hello" key2 "World"
OK
redis> GET key1
"Hello"
redis> GET key2
"World"
redis> 
```



