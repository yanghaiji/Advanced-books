### Map篇

1. **new HashMap(15) , new HashMap(16) , new HashMap(17) 他们的初始化容量是多少**

   - new HashMap(15) 初始化容量为 16
   - new HashMap(16) 初始化容量为 16
   - new HashMap(17) 初始化容量为 32

   **解析**

   >这个问题很简单,但是也有坑，看一下构造函数就可以知道
   >
   >```java
   >    public HashMap(int initialCapacity, float loadFactor) {
   >        if (initialCapacity < 0)
   >            throw new IllegalArgumentException("Illegal initial capacity: " +
   >                                               initialCapacity);
   >        if (initialCapacity > MAXIMUM_CAPACITY)
   >            initialCapacity = MAXIMUM_CAPACITY;
   >        if (loadFactor <= 0 || Float.isNaN(loadFactor))
   >            throw new IllegalArgumentException("Illegal load factor: " +
   >                                               loadFactor);
   >        this.loadFactor = loadFactor;
   >        this.threshold = tableSizeFor(initialCapacity);
   >    }
   >```
   >
   >重点在与这句代码 ` this.threshold = tableSizeFor(initialCapacity);`
   >
   >```java
   >    static final int tableSizeFor(int cap) {
   >        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
   >        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
   >    }
   >```
   >
   >对于给定的目标容量，返回两倍大小的幂
   >
   >如果我们传入参数为 new HashMap(10); this.threshold = tableSizeFor(initialCapacity);为16
   >
   >当你new HashMap(17); this.threshold = tableSizeFor(initialCapacity);为32

2. **为什么HashMap不是线程安全的，你都知道哪些安全的Map，说明为什么是线程安全的**

   - HashMap没有锁自然不安全
   - Hashtable 所有的方法都有synchronized修饰
   - ConcurrentHashMap 分段加锁的机制，效率比HashTable高

3. **HashMap底层的数据结构什么**

   - HashMap用到数组，链表（单链表），树（红黑树）三种数据结构和哈希算法。

   - 当key发生hash冲突时，就需要使用到单链表，当链表达到一定长度时会转为红黑树，来提升查询效率；当红黑树的节点在指定范围时会转为单链表

   - 转换的阈值为单链表长度为8，实现反转的主要是源码里的这连个参数

     ```
         static final int TREEIFY_THRESHOLD = 8;
     
         static final int UNTREEIFY_THRESHOLD = 6;
     ```

4. **HashMap 的扩容机制，如new HashMap(8,0.75f),什么时候进行扩容**

   > 首先我们要知道什么时候会进行扩容操作,肯定是put的时候，在put的时候有这样两端段代码
   >
   > ```java
   > if ((tab = table) == null || (n = tab.length) == 0)
   >     n = (tab = resize()).length;
   > 
   > if (++size > threshold)
   >     resize();
   > ```
   >
   > 第一次put是，会重新计算threshold的值，以本文为例，第一put后 threshold= 6
   >
   > 当你的put次数等于6时就会触发扩容机制
   >
   > 关于resize()方法详细的代码可以参看[HashMap 源码重识 这篇博客](https://blog.csdn.net/weixin_38937840/article/details/114107184)