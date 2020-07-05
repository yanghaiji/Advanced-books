## Java字符串池指南 

### 1.概述
该字符串对象是在Java语言中最常用的类。

在这篇快速文章中，我们将探讨Java字符串池-JVM在其中存储字符串的特殊内存区域。 

### 2.字符串实习
由于Java 中String的不可变性，因此JVM可以通过在池中仅存储每个文字String的一个副本来优化为其分配的内存量。这个过程称为实习。

当我们创建一个String变量并为其分配值时，JVM会在池中搜索相等值的String。

如果找到，则Java编译器将简单地返回对其内存地址的引用，而无需分配额外的内存。

如果未找到，它将被添加到池中（被阻止），并返回其引用。

让我们编写一个小测试来验证这一点：

```java
String constantString1 = "Java 有货";
String constantString2 = "Java 有货";
         
assertThat(constantString1)
  .isSameAs(constantString2);
```
### 3. 使用构造函数分配的字符串
当我们 通过  new  运算符创建  String时，Java编译器将创建一个新对象并将其存储在为JVM保留的堆空间中。

 这样创建的每个  String都将指向具有其自己地址的不同内存区域。

让我们看看这与前面的情况有何不同：
```java
String constantString = "Java 有货";
String newString = new String("Java 有货");
  
assertThat(constantString).isNotSameAs(newString);
```
### 4. 字符串  文字与字符串对象
当我们使用new（）运算符创建  String对象时  ，它总是在堆内存中创建一个新对象。另一方面，如果我们使用String文字语法（例如“ Java”）创建对象，
则它可能会从String池返回现有对象（如果已存在）。否则，它将创建一个新的String对象，并将其放入字符串池中以备将来重用。

从高层次来看，这两个都是String对象，但是主要区别在于new（）运算符始终创建一个新的String对象。另外，当我们使用文字创建一个String时，它会被插入。

当我们比较使用String文字和  new运算符创建的两个String对象时，这将更加清楚：
```java
String first = "java"; 
String second = "java"; 
System.out.println(first == second); // True
```
在此示例中，String对象将具有相同的引用。

接下来，让我们使用new创建两个不同的对象，并检查它们是否具有不同的引用：
```java
String third = new String("java");
String fourth = new String("java"); 
System.out.println(third == fourth); // False
```
类似地，当我们将String文字与使用==运算符使用new（）运算符创建的String对象进行比较时，它将返回false：
```java
String fifth = "Baeldung";
String sixth = new String("Baeldung");
System.out.println(fifth == sixth); // False
```
通常，应尽可能使用String文字表示法。它更易于阅读，并且使编译器有机会优化我们的代码
### 5.手动实习
我们可以通过在要进行实习的对象上调用intern（）方法来在Java字符串池中手动实习一个String。

手动实习String会将其引用存储在池中，并且JVM将在需要时返回此引用。

让我们为此创建一个测试用例：
```java
String constantString = "interned Java";
String newString = new String("interned java");
 
assertThat(constantString).isNotSameAs(newString);
 
String internedString = newString.intern();
 
assertThat(constantString)
  .isSameAs(internedString);
```
### 6.垃圾收集
在Java 7之前，JVM 将Java字符串池放置在PermGen空间中，该空间的大小是固定的—无法在运行时扩展，并且不符合垃圾回收的条件。

在PermGen中代替String而不是Heap的风险是，如果我们实习太多的String，我们可以从JVM中获得OutOfMemory错误。

从Java 7开始，Java字符串池存储在Heap空间中，该空间由JVM进行垃圾回收。
这种方法的优点是减少了OutOfMemory错误的风险，因为未引用的字符串将从池中删除，从而释放内存。

### 7.性能和优化
在Java 6中，我们唯一可以执行的优化是使用MaxPermSize JVM选项在程序调用期间增加PermGen空间：

> -XX:MaxPermSize=1G

在Java 7中，我们提供了更详细的选项来检查和扩展/减小池的大小。让我们看一下两个查看池大小的选项：
> -XX:+PrintFlagsFinal
>
> -XX:+PrintStringTableStatistics

如果要根据存储桶增加池大小，可以使用StringTableSize JVM选项：
> -XX:StringTableSize=4901
