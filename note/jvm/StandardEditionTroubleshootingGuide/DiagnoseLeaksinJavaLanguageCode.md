## 诊断Java语言代码中的泄漏

诊断Java语言代码中的泄漏可能很困难。 通常，它需要非常详细的应用程序知识。 另外，该过程通常是反复的和漫长的。 本节提供有关可用于诊断Java语言代码中的内存泄漏的工具的信息。



### 以下是用于诊断Java语言代码泄漏的两个实用程序

1.NetBeans Profiler：NetBeans Profiler可以非常快速地找到内存泄漏。 商业内存泄漏调试工具可能需要很长时间才能在大型应用程序中定位泄漏。 但是，NetBeans Profiler使用此类对象通常演示的内存分配和回收模式。 此过程还包括缺少内存回收。 探查器可以检查这些对象的分配位置，这通常足以确定泄漏的根本原因。

2.jhat实用程序：jhat实用程序在调试意外的对象保留（或内存泄漏）时很有用。 它提供了一种浏览对象转储，查看堆中所有可访问对象以及了解哪些引用使对象保持活动状态的方法。

要使用jhat，必须获取正在运行的应用程序的一个或多个堆转储，并且转储必须为二进制格式。 创建转储文件后，可以将其用作jhat的输入



### 以下各节描述了诊断Java语言代码泄漏的其他方法



#### Create a Heap Dump

- 堆转储提供有关堆内存分配的详细信息。 有几种创建堆转储的方法：

  如果HPROF与应用程序一起启动，则可以创建堆转储。

  ```java
  java -agentlib:hprof=file=snapshot.hprof,format=b application
  ```

  > 注意：
  >
  > 如果JVM是嵌入式的或未使用允许提供其他选项的命令行启动器启动，则可能可以使用JAVA_TOOLS_OPTIONS环境变量，以便将-agentlib选项自动添加到命令行。
  >
  > 该环境变量允许您指定工具的初始化，特别是使用-agentlib或-javaagent选项启动本机或Java编程语言代理。 在以下示例中，设置了环境变量，以便在启动应用程序时启动HPROF分析器：
  >
  > ```
  > export JAVA_TOOL_OPTIONS="-agentlib:hprof"
  > ```

- jmap实用程序可用于创建正在运行的进程的堆转储

  建议使用最新的实用程序jcmd代替jmap实用程序，以增强诊断功能并降低性能开销。

  Example  Create a Heap Dump using jcmd`jcmd  GC.heap_dump filename=Myheapdump `

  Example  Create a Heap Dump using jmap ` jmap -dump:format=b,file=snapshot.jmap pid`

  无论JVM是如何启动的，在前面的示例中，jmap工具都会在名为snapshot.jmap的文件中生成头转储快照。 jmap输出文件应包含所有原始数据，但不包括任何显示对象创建位置的堆栈跟踪。、

  如果在运行应用程序时指定-XX：+ HeapDumpOnOutOfMemoryError命令行选项，则当抛出OutOfMemoryError异常时，JVM将生成堆转储。

#### 获取堆直方图

您可以尝试通过检查堆直方图来快速缩小内存泄漏的范围。 它可以通过几种方式获得：

- 如果Java进程是通过-XX：+ PrintClassHistogram命令行选项启动的，则Control + Break处理程序将生成堆直方图。

- 您可以使用jmap实用程序从正在运行的进程获取堆直方图：

  建议使用最新的实用程序jcmd代替jmap实用程序，以增强诊断功能并降低性能开销。 请参见jcmd实用程序的有用命令。示例3-4中的命令使用jcmd为正在运行的进程创建堆直方图，其结果类似于以下jmap命令。

  Example  Create a Heap Histogram using jcmd`jcmd  GC.class_histogram filename=Myheaphistogram ``jmap -histo pid`

  输出显示堆中每种类类型的总大小和实例计数。 如果获得了一系列直方图（例如，每2分钟一次），则您可能能够观察到可以进行进一步分析的趋势。

- 您可以使用jmap实用程序从核心文件获取堆直方图

  Create a Heap Histogram using jmap

  `jmap -histo core_file`

  例如，如果在运行应用程序时指定-XX：+ HeapDumpOnOutOfMemoryError命令行选项，则当抛出OutOfMemoryError异常时，JVM将生成堆转储。 然后，您可以在核心文件上执行jmap以获得直方图

  Execute jmap on the Core File

  `$ jmap -histo \ /java/re/javase/6/latest/binaries/solaris-sparc/bin/java core.27421、`

  ```
  Attaching to core core.27421 from executable 
  /java/re/javase/6/latest/binaries/solaris-sparc/bin/java, please wait...
  Debugger attached successfully.
  Server compiler detected.
  JVM version is 1.6.0-beta-b63
  Iterating over heap. This may take a while...
  Heap traversal took 8.902 seconds.
  
  Object Histogram:
   
  Size      Count   Class description
  -------------------------------------------------------
  86683872  3611828 java.lang.String
  20979136  204     java.lang.Object[]
  403728    4225    * ConstMethodKlass
  306608    4225    * MethodKlass
  220032    6094    * SymbolKlass
  152960    294     * ConstantPoolKlass
  108512    277     * ConstantPoolCacheKlass
  104928    294     * InstanceKlassKlass
  68024     362     byte[]
  65600     559     char[]
  31592     359     java.lang.Class
  27176     462     java.lang.Object[]
  25384     423     short[]
  17192     307     int[]
  :
  ```

  显示OutOfMemoryError异常是由java.lang.String对象的数量（堆中的3,611,828个实例）引起的。 如果不作进一步分析，则不清楚在何处分配字符串。 但是，该信息仍然有用。 继续使用HPROF和jhat之类的工具进行调查，以查找字符串的分配位置以及哪些引用使它们保持活动状态并防止垃圾被收集

#### 监视即将完成的对象

​	当“ Java堆空间”详细信息引发OutOfMemoryError异常时，原因可能是过度使用了终结器。为了诊断这一点，您有几个选项可以监视即将完成的对象的数量：

- JConsole管理工具可用于监视未完成的对象的数量。此工具在“摘要”选项卡窗格上的内存统计信息中报告挂起的完成计数。该计数是近似值，但它可用于表征应用程序并了解其是否在很大程度上取决于最终确定。

- 在Oracle Solaris和Linux操作系统上，可以将jmap实用程序与-finalizerinfo选项一起使用，以打印有关等待完成的对象的信息。

- 应用程序可以使用java.lang.management.MemoryMXBean类的getObjectPendingFinalizationCount方法报告即将完成的对象的大概数量。在“自定义诊断工具”中可以找到指向API文档和示例代码的链接。可以轻松扩展示例代码，以包括未决终结计数的报告。