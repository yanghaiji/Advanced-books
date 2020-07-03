# Map merge
- [介绍](#1-介绍)
- [初始化](#2-初始化)
- [Map.merge()](#3-Map.merge())
- [Stream.concat()](#4-Stream.concat())
- [Stream.of()](#5-Stream.of())
- [Simple Streaming](#6-Simple-Streaming)
- [StreamEx](#7-StreamEx)
- [Test源码](#8-Test源码)

## 1. 介绍

本入门教程将介绍Java8中如何合并两个map。

更具体说来，我们将研究不同的合并方案，包括Map含有重复元素的情况。

## 2. 初始化

我们定义两个map实例

```java
private static Map<String, Employee> map1 = new HashMap<>();
private static Map<String, Employee> map2 = new HashMap<>();
```

*Employee类*

```java
public class Employee {
    private Long id;
    private String name;
    // 此处省略构造方法, getters, setters方法
}
```

然后往map中存入一些数据

```java
 Employee employee1 = new Employee(1L, "Henry");
 map1.put(employee1.getName(), employee1);
 Employee employee2 = new Employee(22L, "Annie");
 map1.put(employee2.getName(), employee2);
 Employee employee3 = new Employee(8L, "John");
 map1.put(employee3.getName(), employee3);

 Employee employee4 = new Employee(2L, "George");
 map2.put(employee4.getName(), employee4);
 Employee employee5 = new Employee(3L, "Henry");
 map2.put(employee5.getName(), employee5);
```

特别需要注意的是*employee1* 和 *employee5在map中有完全相同的key（name）。*

## 3. Map.merge()

Java8为 **java.util.Map接口新增了merge()函数。**

 *merge()* 函数的作用是: 如果给定的key之前没设置value 或者value为null, 则将给定的value关联到这个key上.

否则，通过给定的remaping函数计算的结果来替换其value。如果remapping函数的计算结果为null，将解除此结果。

First, let’s construct a new *HashMap* by copying all the entries from the *map1*:

首先，我们通过拷贝map1中的元素来构造一个新的*HashMap*

```java
Map<String, Employee> map3 = new HashMap<>(map1);
```

然后引入merge函数和合并规则

```java
map3.merge(key, value, (v1, v2) -> new Employee(v1.getId(),v2.getName())
```

最后对map2进行迭代将其元素合并到map3中

```java
map2.forEach(
  (key, value) -> map3.merge(key, value, (v1, v2) -> new Employee(v1.getId(),v2.getName())));
```

运行程序并打印结果如下：

```java
John=Employee{id=8, name='John'}
Annie=Employee{id=22, name='Annie'}
George=Employee{id=2, name='George'}
Henry=Employee{id=1, name='Henry'}
```

最终，通过结果可以看出，实现了两个map的合并，对重复的key也合并为同一个元素。

注意最后一个*Employee*的id来自map1而name来自map2.

原因是我们的merge函数的定义

```java
(v1, v2) -> new Employee(v1.getId(), v2.getName())
```

## 4. Stream.concat()

*Java8的Stream* API 也为解决该问题提供了较好的解决方案。

首先需要将两个map合为一个**Stream。**

```java
Stream combined = Stream.concat(map1.entrySet().stream(), map2.entrySet().stream());
```

我们需要将entry sets作为参数，然后利用*Collectors.toMap()*:将结果放到新的map中。

```java
Map<String, Employee> result = combined.collect(
  Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
```

该方法可以实现map的合并，但是有重复key会报*IllegalStateException异常。*

为了解决这个问题，我们需要加入lambda表达式merger作为第三个参数

```java
(value1, value2) -> new Employee(value2.getId(), value1.getName())
```

当检测到有重复Key时就会用到该lambda表达式。

现在把上面代码组合在一起：

```java
Map<String, Employee> result = Stream.concat(map1.entrySet().stream(), map2.entrySet().stream())
  .collect(Collectors.toMap(
    Map.Entry::getKey, 
    Map.Entry::getValue,
    (value1, value2) -> new Employee(value2.getId(), value1.getName())));
```

最终的结果

```java
George=Employee{id=2, name='George'}
John=Employee{id=8, name='John'}
Annie=Employee{id=22, name='Annie'}
Henry=Employee{id=3, name='Henry'}
```

从结果可以看出重复的key *“**Henry**”将合并为一个新的键值对，id取自map2，name取自map1。*

 

## 5. Stream.of()

通过Stream.of()方法不需要借助其他stream就可以实现map的合并。

```java
Map<String, Employee> map3 = Stream.of(map1, map2)
  .flatMap(map -> map.entrySet().stream())
  .collect(Collectors.toMap(
    Map.Entry::getKey,
    Map.Entry::getValue,
    (v1, v2) -> new Employee(v1.getId(), v2.getName())));
```

首先将map1和map2的元素合并为同一个流，然后再转成map。通过使用v1的id和v2的name来解决重复key的问题。

map3的运行打印结果如下：

## 6. Simple Streaming

我们还可以借助stream的管道操作来实现map合并。

```java
Map<String, Employee> map3 = map2.entrySet()
  .stream()
  .collect(Collectors.toMap(
    Map.Entry::getKey,
    Map.Entry::getValue,
    (v1, v2) -> new Employee(v1.getId(), v2.getName()),
  () -> new HashMap<>(map1)));
```

结果如下：

```java
{John=Employee{id=8, name='John'}, 
Annie=Employee{id=22, name='Annie'}, 
George=Employee{id=2, name='George'}, 
Henry=Employee{id=1, name='Henry'}}
```

## 7. StreamEx

我们还可以使**Stream API** 的增强库

如果您采用种方式，请引入一下依赖

```xml
<dependency>
    <groupId>one.util</groupId>
    <artifactId>streamex</artifactId>
    <version>0.6.5</version>
</dependency>
```

```java
Map<String, Employee> map3 = EntryStream.of(map1)
  .append(EntryStream.of(map2))
  .toMap((e1, e2) -> e1);
```

注意 *(e1, e2) -> e1* 表达式来处理重复key的问题，如果没有该表达式依然会报*IllegalStateException异常。*

结果：

```java
{George=Employee{id=2, name='George'}, 
John=Employee{id=8, name='John'}, 
Annie=Employee{id=22, name='Annie'}, 
Henry=Employee{id=1, name='Henry'}}
```

## 8. Test源码

```java
public class MapTest {

    private static Map<String, Employee> map1 = new HashMap<>();
    private static Map<String, Employee> map2 = new HashMap<>();

    public static void main(String[] args) {
        Employee employee1 = new Employee(1L, "Henry");
        map1.put(employee1.getName(), employee1);
        Employee employee2 = new Employee(22L, "Annie");
        map1.put(employee2.getName(), employee2);
        Employee employee3 = new Employee(8L, "John");
        map1.put(employee3.getName(), employee3);
        Employee employee4 = new Employee(2L, "George");
        map2.put(employee4.getName(), employee4);
        Employee employee5 = new Employee(3L, "Henry");
        map2.put(employee5.getName(), employee5);

        // merge
        Map<String, Employee> map3 = new HashMap<>(map1);
        map2.forEach(
                (key, value)
                        -> map3.merge(key, value, (v1, v2) -> new Employee(v2.getId(),v2.getName())));
        System.out.println(map3);

        // concat
        Map<String, Employee> result = Stream.concat(map1.entrySet().stream(), map2.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (value1, value2) -> new Employee(value2.getId(), value1.getName())));
        System.out.println(result);

        // of
        Map<String, Employee> map4 = Stream.of(map1, map2)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> new Employee(v1.getId(), v2.getName())));
        System.out.println(map4);

        // Simple Streaming
        Map<String, Employee> map5 = map2.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> new Employee(v1.getId(), v2.getName()),
                        () -> new HashMap<>(map1)));
        System.out.println(map5);
        
        // StreamEx
        Map<String, Employee> map6 = EntryStream.of(map1)
                .append(EntryStream.of(map2))
                .toMap((e1, e2) -> e1);
        System.out.println(map6);
    }
}
```

