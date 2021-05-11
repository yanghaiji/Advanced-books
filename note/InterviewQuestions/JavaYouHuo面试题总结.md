# Java面试题总结



## 基础篇



## 集合篇

### List 篇

1. **ArrayList是线程安全的吗**

   肯定不是呀，想都不想想，不要犹豫

2. **你用过哪些线程安全的List，为什么他们是线程安全的**

   - Vector 在JDK 1.0时就有了，在关键的方法上都有synchronized的修饰，这是个方法锁

   - CopyOnWriteArrayList/CopyOnWriteArraySet，在关键的方法上都有synchronized的修饰，这是一个同步对象锁实现

   - 源码解析

     > **Vector** 
     >
     > ```java
     >     public synchronized boolean add(E e) {
     >         modCount++;
     >         add(e, elementData, elementCount);
     >         return true;
     >     }
     > ```
     >
     >  **CopyOnWriteArrayList**
     >
     > ```java
     >     public boolean add(E e) {
     >         synchronized (lock) {
     >             Object[] es = getArray();
     >             int len = es.length;
     >             es = Arrays.copyOf(es, len + 1);
     >             es[len] = e;
     >             setArray(es);
     >             return true;
     >         }
     >     }
     > ```
     >
     > 

3. **List的特点**

   底层技术数组实现，所以空间连续，查询速度快，但是新增、删除会比较慢，其原因是可能会导致对数组重新进行分配。

4. **ArrayList 与 LinkedList**

   - 数据量大时ArrayList 查询速度快于LinkedList，主要原因是LinkedList不支持随机访问
   - LinkedList的新增和删除的速度要远高于ArrayList,LinkedList底层是双向链表(队列)，只需要重新构建指针链接即可，但是ArrayList是数组实现，会导致对数组重新进行分配，前者的时间复杂度是O(1),后者是O(n)

---

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

5. **HashMap是如何解决hash冲突的**

   首先我们需要知道什么时候会发生冲突，存在相同的hashcode，那么他们确定的索引位置就相同，这时判断他们的key是否相同，如果不相同，这时就是产生了hash冲突。 

   Hash冲突后，那么HashMap的单个bucket里存储的不是一个 Entry，而是一个 Entry 链。 

   系统只能必须按顺序遍历每个 Entry，直到找到想搜索的 Entry 为止——如果恰好要搜索的 Entry 位于该 Entry 链的最末端（该 Entry 是最早放入该 bucket 中）， 那系统必须循环到最后才能找到该元素。

   > 关键部分的源码 在put 方法
   >
   > ```java
   > else {
   >     for (int binCount = 0; ; ++binCount) {
   >         if ((e = p.next) == null) {
   >             // 通过链表解决冲突
   >             p.next = newNode(hash, key, value, null);
   >             if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
   >                 treeifyBin(tab, hash);
   >             break;
   >         }
   >         if (e.hash == hash &&
   >             ((k = e.key) == key || (key != null && key.equals(k))))
   >             break;
   >         p = e;
   >     }
   > }
   > ```

 