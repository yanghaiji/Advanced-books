## java-8-collectors

### 1.Stream.collect()方法
Stream.collect（）是Java 8的Stream API的终端方法之一。它允许我们对Stream实例中保存的数据元素执行可变的折叠操作（将元素重新打包到某些数据结构并应用一些其他逻辑，将它们串联等）。

此操作的策略是通过Collector接口实现提供的。

在以下示例中，我们将重用以下列表：
```java
List<String> givenList = Arrays.asList("a", "bb", "ccc", "dd");
```
### 2.Collectors.toList()
toList收集器可用于将所有Stream元素收集到List实例中。要记住的重要一点是，我们不能使用此方法来假设任何特定的List实现。如果要对此进行更多控制，请改用toCollection。

让我们创建一个代表一系列元素的Stream实例，并将它们收集到一个List实例中：

```java
List<String> result = givenList.stream()
  .collect(toList());
```

### 3.Collectors.toSet()
ToSet收集器可用于将所有Stream元素收集到Set实例中。要记住的重要一点是，我们不能使用此方法假定任何特定的Set实现。如果我们想对此有更多的控制，可以使用toCollection。

让我们创建一个代表一系列元素的Stream实例，并将它们收集到Set实例中：

```java
Set<String> result = givenList.stream()
  .collect(toSet());
```

### 4. Collectors.toCollection()
您可能已经注意到，使用toSet和toList收集器时，您无法对其实现进行任何假设。如果要使用自定义实现，则需要将toCollection收集器与您选择的提供的收集一起使用。

让我们创建一个代表一系列元素的Stream实例，并将它们收集到LinkedList实例中：
```java
List<String> result = givenList.stream()
  .collect(toCollection(LinkedList::new))
```
请注意，这不适用于任何不可变的集合。在这种情况下，您将需要编写一个自定义的Collector实现或使用collectionAndThen。

### 5. Collectors.toMap()

*ToMap*收集器可用于将*Stream*元素收集到*Map*实例中。为此，我们需要提供两个功能：

- keyMapper
- valueMapper

*keyMapper*将用于从*Stream*元素中提取*Map*键，*valueMapper*将用于提取与给定键关联的值。

让我们将这些元素收集到一个*Map中*，该*Map*将字符串存储为键，并将其长度存储为值：

```java
Map<String, Integer> result = givenList.stream()
  .collect(toMap(Function.identity(), String::length))
```

Function.identity（）只是用于定义一个接受并返回相同值的函数的快捷方式。

如果我们的集合包含重复的元素会怎样？与*toSet*相反， *toMap*不会静默过滤重复项。这是可以理解的–应该如何确定该密钥要选择哪个值？

```java
List<String> listWithDuplicates = Arrays.asList("a", "bb", "c", "d", "bb");
assertThatThrownBy(() -> {
    listWithDuplicates.stream().collect(toMap(Function.identity(), String::length));
}).isInstanceOf(IllegalStateException.class);
```

请注意，*toMap*甚至不会评估值是否也相等。如果看到重复的键，则会立即引发*IllegalStateException*。

在发生键冲突的情况下，我们应该将*toMap*与另一个签名一起使用：

Map<String, Integer> result = givenList.stream()
  .collect(toMap(Function.identity(), String::length, (item, identicalItem) -> item));
这里的第三个参数是BinaryOperator，我们可以在其中指定希望如何处理冲突。在这种情况下，我们将只选择这两个冲突值中的任何一个，因为我们知道相同的字符串也将始终具有相同的长

### Collectors.collectingAndThen()
*CollectingAndThen*是一个特殊的收集器，允许在收集结束后立即对结果执行其他操作。

让我们将*Stream*元素收集到*List*实例，然后将结果转换为*ImmutableList*实例：

```java
List<String> result = givenList.stream()
  .collect(collectingAndThen(toList(), ImmutableList::copyOf))
```

### Collectors.joining()
*联接*收集器可用于联接Stream 元素。

我们可以通过以下方式将他们加入一起：

```java
String result = givenList.stream()
  .collect(joining());
```

这将导致：

```java
"abbcccdd"
```

您还可以指定自定义分隔符，前缀，后缀：

```java
String result = givenList.stream()
  .collect(joining(" "));
```

这将导致：

```java
"a bb ccc dd"
```

或者你可以写：

```java
String result = givenList.stream()
  .collect(joining(" ", "PRE-", "-POST"));
```

这将导致：

```java
"PRE-a bb ccc dd-POST"
```
### Collectors.counting()

计数是一个简单的收集器，允许简单地计数所有Stream元素。

现在我们可以写：

```java
Long result = givenList.stream()
  .collect(counting());
```
### Collectors.summarizingDouble/Long/Int()

*SummarizingDouble / Long / Int*是一个收集器，它返回一个特殊类，该类包含有关提取元素*流*中数字数据的统计信息。

我们可以通过执行以下操作获取有关字符串长度的信息：

```java
DoubleSummaryStatistics result = givenList.stream()
  .collect(summarizingDouble(String::length));
```

在这种情况下，将满足以下条件：

```java
assertThat(result.getAverage()).isEqualTo(2);
assertThat(result.getCount()).isEqualTo(4);
assertThat(result.getMax()).isEqualTo(3);
assertThat(result.getMin()).isEqualTo(1);
assertThat(result.getSum()).isEqualTo(8);
```
### Collectors.averagingDouble / Long / Int()

*AveragingDouble / Long / Int*是一个收集器，仅返回提取元素的平均值。

我们可以通过执行以下操作来获得平均字符串长度：

```java
Double result = givenList.stream()
  .collect(averagingDouble(String::length));
```

### Collector .s ummingDouble / Long / Int（）

*SummingDouble / Long / Int*是仅返回所提取元素之和的收集器。

通过执行以下操作，我们可以得出所有字符串长度的总和：

```java
Double result = givenList.stream()
  .collect(summingDouble(String::length));
```

### Collectors.maxBy（）/ minBy（）

*MaxBy* / *MinBy*收集器根据提供的*Comparator*实例返回*Stream*的最大/最小元素。

我们可以通过执行以下操作来选择最大的元素：

```java
Optional<String> result = givenList.stream()
  .collect(maxBy(Comparator.naturalOrder()));
```

请注意，返回值包装在*Optional*实例中。这迫使用户重新考虑空的收集角案件。



### Collectors.groupingBy（）

*GroupingBy*收集器用于按某些属性对对象进行分组，并将结果存储在*Map*实例中。

我们可以按字符串长度对它们进行分组，并将分组结果存储在*Set*实例中：

```java
Map<Integer, Set<String>> result = givenList.stream()
  .collect(groupingBy(String::length, toSet()));
```

这将导致以下情况成立：

```java
assertThat(result)
  .containsEntry(1, newHashSet("a"))
  .containsEntry(2, newHashSet("bb", "dd"))
  .containsEntry(3, newHashSet("ccc"));
```

注意，*groupingBy*方法的第二个参数是一个*收集器*，您可以自由使用您选择的任何*收集器*。

### Collectors.partitioningBy（)

*PartitioningBy*是*groupingBy的*一种特殊情况，它接受一个*谓词*实例并将*Stream*元素收集到一个*Map*实例中，该实例将*布尔*值存储为键，并将集合存储为值。在“ true”键下，您可以找到与给定*谓词*相匹配的元素的集合，在“ false”键下，您可以找到与给定*谓词*不匹配的元素的集合。

你可以写：

```java
Map<Boolean, List<String>> result = givenList.stream()
  .collect(partitioningBy(s -> s.length() > 2))
```

结果导致包含以下内容的地图：

```java
{false=["a", "bb", "dd"], true=["ccc"]}
```

### Collectors.teeing（）

让我们使用到目前为止学习到的收集器从给定*Stream中*找到最大和最小数目：

```java
List<Integer> numbers = Arrays.asList(42, 4, 2, 24);
Optional<Integer> min = numbers.stream().collect(minBy(Integer::compareTo));
Optional<Integer> max = numbers.stream().collect(maxBy(Integer::compareTo));
// do something useful with min and max
```

在这里，我们使用了两个不同的收集器，然后将这两个收集器的结果相结合以创建有意义的东西。在Java 12之前，为了涵盖此类用例，我们必须对给定的*Stream进行*两次操作，将中间结果存储到临时变量中，然后再将这些结果合并。

幸运的是，Java 12提供了一个内置的收集器，代表我们执行这些步骤：我们要做的就是提供两个收集器和合并器功能。


```java
numbers.stream().collect(teeing(
  minBy(Integer::compareTo), // The first collector
  maxBy(Integer::compareTo), // The second collector
  (min, max) -> // Receives the result from those collectors and combines them
));
```

## 定制Collector

如果要编写Collector实现，则需要实现Collector接口并指定其三个通用参数：

```java
public interface Collector<T, A, R> {...}
```

1. **T** –可用于收集的对象类型，
2. **A** –可变累加器对象的类型，
3. **R** –最终结果的类型。

让我们写一个示例收集器，将元素收集到*ImmutableSet*实例中。我们从指定正确的类型开始：

```java
private class ImmutableSetCollector<T>
  implements Collector<T, ImmutableSet.Builder<T>, ImmutableSet<T>> {...}
```

由于我们需要一个可变的集合来进行内部集合操作处理，因此我们不能为此使用*ImmutableSet*。我们需要使用其他可变的集合或可以为我们临时累积对象的任何其他类。
在这种情况下，我们将继续使用*ImmutableSet.Builder*，现在我们需要实现5种方法：

- *Supplier > **供应商**（）*
- *BiConsumer ，T> **累加器**（）*
- *BinaryOperator > **组合器**（）*
- *函数，ImmutableSet > **修整器**（）*
- *设置 **特征**（）*

provider（）方法返回一个 *Supplier*实例，该实例生成一个空的累加器实例，因此，在这种情况下，我们可以简单地编写：

```java
@Override
public Supplier<ImmutableSet.Builder<T>> supplier() {
    return ImmutableSet::builder;
}
```

accumulator（）方法返回一个函数，该函数用于将新元素添加到现有的*累加器*对象中，因此我们仅使用 *Builder*的 *add*方法。

```java
@Override
public BiConsumer<ImmutableSet.Builder<T>, T> accumulator() {
    return ImmutableSet.Builder::add;
}
```

***Combiner（）*** 方法返回一个函数，该函数用于将两个累加器合并在一起：

```java
@Override
public BinaryOperator<ImmutableSet.Builder<T>> combiner() {
    return (left, right) -> left.addAll(right.build());
}
```

***finisher（）***方法返回一个函数，该函数用于将累加器转换为最终结果类型，因此在这种情况下，我们将仅使用 *Builder*的 *build*方法：

```java
@Override
public Function<ImmutableSet.Builder<T>, ImmutableSet<T>> finisher() {
    return ImmutableSet.Builder::build;
}
```

***features（）***方法用于为Stream提供一些其他信息，这些信息将用于内部优化。在这种情况下，我们不关注 *Set中*的元素顺序，因此我们将使用 *Characteristics.UNORDERED*。要获取有关此主题的更多信息，请选中“*特性* 'JavaDoc”。

```java
@Override public Set<Characteristics> characteristics() {
    return Sets.immutableEnumSet(Characteristics.UNORDERED);
}
```

这是完整的实现及其用法：

```java
public class ImmutableSetCollector<T>
  implements Collector<T, ImmutableSet.Builder<T>, ImmutableSet<T>> {
 
@Override
public Supplier<ImmutableSet.Builder<T>> supplier() {
    return ImmutableSet::builder;
}
 
@Override
public BiConsumer<ImmutableSet.Builder<T>, T> accumulator() {
    return ImmutableSet.Builder::add;
}
 
@Override
public BinaryOperator<ImmutableSet.Builder<T>> combiner() {
    return (left, right) -> left.addAll(right.build());
}
 
@Override
public Function<ImmutableSet.Builder<T>, ImmutableSet<T>> finisher() {
    return ImmutableSet.Builder::build;
}
 
@Override
public Set<Characteristics> characteristics() {
    return Sets.immutableEnumSet(Characteristics.UNORDERED);
}
 
public static <T> ImmutableSetCollector<T> toImmutableSet() {
    return new ImmutableSetCollector<>();
}
```

在这里：

```java
List<String> givenList = Arrays.asList("a", "bb", "ccc", "dddd");
 
ImmutableSet<String> result = givenList.stream()
  .collect(toImmutableSet());
```



