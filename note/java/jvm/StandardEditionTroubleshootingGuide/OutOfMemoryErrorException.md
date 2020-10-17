### OutOfMemoryError Exception 介绍

内存泄漏的一个常见迹象是`java.lang.OutOfMemoryError`。通常，当Java堆中没有足够的空间分配对象时，会抛出此错误。在这种情况下，垃圾回收器时无法腾出空间来容纳新对象，堆也无法进一步扩展。此外，当本机内存不足，无法支持Java类的加载时，可能会抛出此错误。在极少数情况下`java.lang.OutOfMemoryError`在执行垃圾收集花费了大量时间且释放的内存很少时，可能会引发。

当java.lang.OutOfMemoryError抛出异常时，也会打印堆栈跟踪。

这个java.lang.OutOfMemoryError当无法满足本机分配时（例如，如果交换空间很低），本机库代码也会引发异常。

诊断OutOfMemoryError异常的早期步骤是确定异常的原因。它是因为Java堆已满，还是因为本机堆已满而抛出？为了帮助您找到原因，异常的文本在末尾包含一条详细消息，如以下异常所示。

- java.lang.OutOfMemoryError: Java heap space

  - 原因：详细消息Java堆空间指示无法在Java堆中分配对象。 此错误不一定表示内存泄漏。 该问题可以像配置问题一样简单，
  其中指定的堆大小（或默认大小，如果未指定）对于应用程序来说是不够的。在其他情况下，尤其是对于寿命长的应用程序，
  该消息可能表明该应用程序无意间持有了对对象的引用，这防止了对象被垃圾回收。
  这与内存泄漏的Java语言等效。 注意：应用程序调用的API也可能无意中包含对象引用
  过度使用` finalizers`的应用程序可能会导致此错误。 如果类具有finalize方法，则该类型的对象在垃圾回收时不会回收其空间。
   取而代之的是，在进行垃圾回收之后，将对象排队等待定案，这将在以后发生。 在Oracle Sun实施中，
   ` finalizers`由为终结队列提供服务的守护程序线程执行。 如果` finalizers`线程无法跟上` finalizers`队列的速度，
   则Java堆可能会填满，并且会抛出此类`OutOfMemoryError`异常。 一种可能导致这种情况的情况是，应用程序创建了高优先级线程，
   这些线程导致终结处理队列以比` finalizers`线程为该队列提供服务的速率快的速率增加。

  - 操作：您可以在监视未决终结的对象中找到有关如何监视未决终结的对象的更多信息。

- java.lang.OutOfMemoryError: GC Overhead limit exceeded

  - 原因：详细消息“超出了`GC`开销限制”表明垃圾收集器一直在运行，并且Java程序的进度非常缓慢。 
  进行垃圾回收之后，如果Java进程花费了其大约98％的时间用于垃圾回收，并且正在恢复的内存少于2％，
  并且到目前为止已经执行了最后5个（编译时间常数）连续垃圾 集合，则引发`java.lang.OutOfMemoryError`。 
  通常会抛出此异常，因为实时数据量几乎无法容纳到Java堆中，而新分配的可用空间却很少。
  - 操作：增加堆大小。 可以通过命令行标志`-XX：-UseGCOverheadLimit`关闭超出GC开销限制的`java.lang.OutOfMemoryError`异常。

- java.lang.OutOfMemoryError: Requested array size exceeds VM limit

  - 原因：详细消息“请求的数组大小超出VM限制”指示该应用程序（或该应用程序使用的API）试图分配一个大于堆大小的数组。
   例如，如果应用程序尝试分配512 MB的数组，但最大堆大小为256 MB，则将抛出OutOfMemoryError，其原因是请求的数组大小超出了VM限制。
    行动：通常问题可能是配置问题（堆大小太小），或者是导致应用程序尝试创建巨大数组的错误（例如，当使用计算算法来计算数组中的元素数时） 尺寸不正确）。

- java.lang.OutOfMemoryError: Metaspace

  - 原因：Java类元数据（Java类的虚拟机内部表示形式）分配在本机内存（在此称为元空间）中。 如果类元数据的元空间用尽，则抛出java.lang.OutOfMemoryError异常，
  并带有详细信息MetaSpace。 可用于类元数据的元空间的数量受参数MaxMetaSpaceSize的限制，该参数在命令行上指定。 
  当类元数据所需的本机内存量超过MaxMetaSpaceSize时，将引发带有详细信息MetaSpace的java.lang.OutOfMemoryError异常。
  - 操作：如果在命令行上设置了MaxMetaSpaceSize，请增加其值。 MetaSpace是从与Java堆相同的地址空间分配的。 
  减少Java堆的大小将为MetaSpace提供更多空间。 只有在Java堆中有多余的可用空间时，这才是正确的权衡。 

- java.lang.OutOfMemoryError: request size bytes for reason. Out of swap space?

  - 原因：详细信息“请求的大小为原因字节。交换空间不足？”似乎是OutOfMemoryError异常。
  但是，当来自本机堆的分配失败并且本机堆可能快要用尽时，Java HotSpot VM代码报告此明显的异常。
  该消息指示失败的请求的大小（以字节为单位）以及内存请求的原因。通常，原因是报告分配失败的源模块的名称，尽管有时是实际原因。
  - 操作：抛出此错误消息时，VM会调用致命错误处理机制（即，它会生成致命错误日志文件，其中包含有关崩溃时线程，进程和系统的有用信息）。
  对于本机堆耗尽，日志中的堆内存和内存映射信息可能会很有用。
    如果引发此类OutOfMemoryError异常，则可能需要在操作系统上使用故障排除实用程序来进一步诊断问题。

-  java.lang.OutOfMemoryError: Compressed class space

  - 原因：在64位平台上，指向类元数据的指针可以由32位偏移量表示（使用UseCompressedOops）。这由命令行标志`UseCompressedClassPointers`（默认情况下为打开）控制。
  如果使用`UseCompressedClassPointers`，则可用于类元数据的空间量固定为`CompressedClassSpaceSize`。
  如果`UseCompressedClassPointers`所需的空间超过`CompressedClassSpaceSize`，则抛出带有详细信息压缩类空间的`java.lang.OutOfMemoryError`。
  - 操作：增加`CompressedClassSpaceSize`以关闭`UseCompressedClassPointers`。注意：`CompressedClassSpaceSize`的可接受大小有界限。
  例如`-XX：CompressedClassSpaceSize = 4g`，超过可接受的范围将导致显示如下消息
    `4294967296`的`CompressedClassSpaceSize`无效；必须介于`1048576`和`3221225472`之间。
  - 注意：有多种类型的类元数据-容器元数据和其他元数据。只有`class`元数据存储在`CompressedClassSpaceSize`界定的空间中。其他元数据存储在`Metaspace`中。

- java.lang.OutOfMemoryError: reason stack_trace_with_native_method

  - 原因：如果错误消息的详细信息部分是“ reason stack_trace_with_native_method”，并且在堆栈跟踪中打印出顶部框架是本机方法的堆栈，
  则表明本机方法遇到分配失败。 该消息与上一条消息之间的区别在于，在Java本机接口（JNI）或本机方法中而不是在JVM代码中检测到分配失败。
  - 操作：如果引发了这种类型的OutOfMemoryError异常，则可能需要使用操作系统的本机实用程序来进一步诊断问题。

