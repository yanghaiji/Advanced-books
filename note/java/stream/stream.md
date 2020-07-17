##  Stream 讲解与示例

流只能运行（调用中间或终端流操作）一次。 这排除了例如“分叉”流，其中相同的源提供两条或多条流水线，或同一流的多遍。 
如果流实现可能会丢失IllegalStateException，如果它检测到该流被重用。 然而，由于一些流操作可能返回其接收器而不是新的流对象，所以在所有情况下可能无法检测到重用。 

Streams有一个BaseStream.close()方法和实现AutoCloseable ，但几乎所有的流实例实际上不需要在使用后关闭。
 一般来说，只有来源为IO通道的流（如Files.lines(Path, Charset)返回的流 ）才需要关闭。 
 大多数流都由集合，数组或生成函数支持，这些函数不需要特殊的资源管理。 （如果流确实需要关闭，则可以在try -with-resources语句中将其声明为资源。） 
 
流管线可以顺序执行，也可以在parallel中执行。 此执行模式是流的属性。 流被创建为具有顺序或并行执行的初始选择。 
（例如， Collection.stream()创建一个顺序流，并且Collection.parallelStream()创建一个并行的）。
执行模式的选择可以由BaseStream.sequential()或BaseStream.parallel()方法修改，并且可以使用BaseStream.isParallel()方法进行查询。 

### 1.stream.collect

将指定的流转换成集合;Collectors相关操作,请参考这里[5.1 java-8-collectors](collectors.md)   
- 示例:

```java
    List<String> collect = stream.collect(Collectors.toList());
```

### 2.stream.filter

数据过滤,同if(){}相同

- 示例:
 
 ```java
    List<String> s0337 = collect.stream().
                   filter("S0337"::equalsIgnoreCase)
                   .collect(Collectors.toList());
 ```

### 3.stream.distinct

数据去重

- 示例:
 
 ```java
    List<String> distinct = collect.stream().distinct().collect(Collectors.toList());
 ```

### 4.stream.sorted

数据排序

- 示例:
 
 ```java
    List<String> sorted = collect.stream().sorted().collect(Collectors.toList());
           System.out.println("数据排序一:"+distinct);

    //根据指定字段排序
    List<StreamData> entity = Stream.of(new StreamData("Yang", "28"),
            new StreamData("Yang", "29"),
            new StreamData("Yang", "21")).collect(Collectors.toList());
    List<StreamData> entitySorted = entity.stream()
                    .sorted(Comparator.comparing(StreamData::getCode))
                    .collect(Collectors.toList());
    System.out.println("数据排序二:"+entitySorted.toString());
 ```
### 5.stream.map

- 示例:
 
 ```java
    List<String> map = collect.stream().map(s -> s.replaceAll("S", "Y")).collect(Collectors.toList());
    List<String> mapEntity = entitySorted.stream().map(StreamData::getCode).collect(Collectors.toList());
    System.out.println("map:"+map);
    System.out.println("mapEntity:"+mapEntity);
 ```

### 6.stream.flatMap

- 示例:
 
 ```java
    List<String> flatMap = collect.stream().flatMap(s -> Arrays.stream(s.split("0"))).collect(Collectors.toList());
 ```
### 7.stream.findFirst

- 示例:
 
 ```java
    Optional<String> first = collect.stream().findFirst();
    System.out.println("第一个元素"+first.get());
    Optional<String> any = collect.stream().findAny();
    System.out.println("第一个元素"+any.get());
 ```
### 8.stream.peek
提前查看元素

- 示例:
 
 ```java
    List<String> peek = collect.stream().peek(System.out::println).collect(Collectors.toList());
 ```

### 9.stream.limit
获取指定的条数

- 示例:
 
 ```java
   List<String> limit = collect.stream().limit(2).collect(Collectors.toList());
 ```

### 9.stream.skip
返回指定下标后的元素

- 示例:
 
 ```java
    List<String> skip = collect.stream().skip(1).collect(Collectors.toList());
 ```

### 9.stream.count
以下代码 等同于 collect.size()

- 示例:
 
 ```java
    long count = collect.stream().count();
 ```
### 10.stream.max
获取最大值，最小值同理
- 示例:
 
 ```java
    Optional<String> max = collect.stream().max(Comparator.comparing(s -> s));
    Optional<StreamData> maxEntity = entitySorted.stream().max(Comparator.comparing(StreamData::getCode));
 ```

### 以上代码示例

    source-code\src\main\java\com\javayh\advanced\java\stream