## Java NIO 教程
Java NIO是java 1.4之后新出的一套IO接口，这里的的新是相对于原有标准的Java IO和Java Networking接口。NIO提供了一种完全不同的操作方式。

NIO中的N可以理解为Non-blocking，不单纯是New

Java NIO: Channels and Buffers
标准的IO编程接口是面向字节流和字符流的。而NIO是面向通道和缓冲区的，数据总是从通道中读到buffer缓冲区内，或者从buffer写入到通道中。

Java NIO: Non-blocking IO
Java NIO使我们可以进行非阻塞IO操作。比如说，单线程中从通道读取数据到buffer，同时可以继续做别的事情，当数据读取到buffer中后，线程再继续处理数据。写数据也是一样的。

Java NIO: Selectors
NIO中有一个“slectors”的概念。selector可以检测多个通道的事件状态（例如：链接打开，数据到达）这样单线程就可以操作多个通道的数据。 所有这些都会在后续章节中更详细的介绍。