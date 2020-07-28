## HashMap 源码分析

基于哈希表的实现的Map接口。 此实现提供了所有可选的map操作，并允许null的值和null键。 （ HashMap类大致相当于Hashtable ，除了它是不同步的，并允许null）。这个类不能保证map的顺序; 特别是，它不能保证订单在一段时间内保持不变。 
假设哈希函数在这些存储桶之间正确分散元素，这个实现为基本操作（ get和put ）提供了恒定的时间性能。 收集视图的迭代需要与HashMap实例（桶数）加上其大小（键值映射数）的“容量” 成正比 。 因此，如果迭代性能很重要，不要将初始容量设置得太高（或负载因子太低）是非常重要的。 

HashMap的一个实例有两个影响其性能的参数： 初始容量和负载因子 。 容量是哈希表中的桶数，初始容量只是创建哈希表时的容量。 负载因子是在容量自动增加之前允许哈希表得到满足的度量。 当在散列表中的条目的数量超过了负载因数和电流容量的乘积，哈希表被重新散列 （即，内部数据结构被重建），使得哈希表具有桶的大约两倍。 

作为一般规则，默认负载因子（0.75）提供了时间和空间成本之间的良好折中。 更高的值会降低空间开销，但会增加查找成本（反映在HashMap类的大部分操作中，包括get和put ）。 在设置其初始容量时，应考虑map中预期的条目数及其负载因子，以便最小化重新组播操作的数量。 如果初始容量大于最大条目数除以负载因子，则不会发生重新排列操作。 

如果许多映射要存储在HashMap实例中，则以足够大的容量创建映射将允许映射的存储效率高于使其根据需要执行自动重新排序以增长表。 请注意，使用同一个hashCode()多个密钥是降低任何哈希表的hashCode()的一种方法。 为了改善影响，当按键是Comparable时，这个类可以使用键之间的比较顺序来帮助打破关系。 

请注意，此实现不同步。 如果多个线程同时访问哈希映射，并且至少有一个线程在结构上修改了映射，那么它必须在外部进行同步。 （结构修改是添加或删除一个或多个映射的任何操作;仅改变与实例已经包含的密钥相关联的值不是结构修改。）这通常通过对自然地封装映射的一些对象进行同步来实现。 如果没有这样的对象存在，map应该使用Collections.synchronizedMap方法“包装”。 这最好在创建时完成，以防止意外的不同步访问map： 

  Map m = Collections.synchronizedMap(new HashMap(...)); 所有这个类的“集合视图方法”返回的迭代器都是故障快速的 ：如果映射在迭代器创建之后的任何时间被结构地修改，除了通过迭代器自己的remove方法之外，迭代器将抛出一个ConcurrentModificationException 。 因此，面对并发修改，迭代器将快速而干净地失败，而不是在未来未确定的时间冒着任意的非确定性行为。 

请注意，迭代器的故障快速行为无法保证，因为一般来说，在不同步并发修改的情况下，无法做出任何硬性保证。 失败快速迭代器尽力扔掉ConcurrentModificationException 。 因此，编写依赖于此异常的程序的正确性将是错误的：迭代器的故障快速行为应仅用于检测错误。 


### 源码分析

#### 内部变量

- 默认长度
    ```java
     static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
    ```
- 最大长度
    ```java
     static final int MAXIMUM_CAPACITY = 1 << 30;
    ```
- 默认负载因子
    ```java
     static final float DEFAULT_LOAD_FACTOR = 0.75f;
    ```
- 链表转换成树的长度

由于HashMap在JDK8及其以后的数据结构进行了调整，结构为链表加红黑树，当链表的长度大于8时将其转变为红黑树，以减小时间复杂度
    ```java
     static final int TREEIFY_THRESHOLD = 8;
    ```

#### 构造方法

这里介绍一种，看懂了它，其他的构造一看自然就通了

```java
    public HashMap(int initialCapacity, float loadFactor) {
        // (1)
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        // (2)        
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        // (3)   
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        // (4)   
        this.loadFactor = loadFactor;
        // (5)   
        this.threshold = tableSizeFor(initialCapacity);
    }
    static final int tableSizeFor(int cap) {
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
```
当我创建一个Map时可以指指定它的初始化长度以及负载因子的大小，如:`Map<String,String> map = new HashMap<>(16, 0.75f);` 
 (1) (2) (3)是对我们传入参数的校验， (4)对负载因子进行赋值，
 (5)threshold 如果尚未分配表数组，则字段保留初始阵列容量，或零表示默认初始容量
 
#### 常用方法 

##### put
```java
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        // (1) 
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        // (2)低位对齐，高位不足的补零，如果对应的二进制位同时为 1，那么计算结果才为 1，
        // 否则为 0。因此，任何数与 0 进行按位与运算，其结果都为 0。
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else {
            Node<K,V> e; K k;
            //(3)
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            //(5)
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
  else {
         // 如果桶中已经有元素存在了
         Node<K, V> e;
         K k;
         // 如果桶中第一个元素的key与待插入元素的key相同，保存到e中用于后续修改value值
         if (p.hash == hash &&
                 ((k = p.key) == key || (key != null && key.equals(k))))
             e = p;
         else if (p instanceof TreeNode)
             // 如果第一个元素是树节点，则调用树节点的putTreeVal插入元素
             e = ((TreeNode<K, V>) p).putTreeVal(this, tab, hash, key, value);
         else { (6)
             // 遍历这个桶对应的链表，binCount用于存储链表中元素的个数
             for (int binCount = 0; ; ++binCount) {
                 // 如果链表遍历完了都没有找到相同key的元素，说明该key对应的元素不存在，则在链表最后插入一个新节点
                 if ((e = p.next) == null) {
                     p.next = newNode(hash, key, value, null);
                     // 如果插入新节点后链表长度大于8，则判断是否需要树化，因为第一个元素没有加到binCount中，所以这里-1
                     if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                         treeifyBin(tab, hash);
                     break;
                 }
                 // 如果待插入的key在链表中找到了，则退出循环
                 if (e.hash == hash &&
                         ((k = e.key) == key || (key != null && key.equals(k))))
                     break;
                 p = e;
             }
         }
            // (4)
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }
     final Node<K, V>[] resize() {
         // 旧数组
         Node<K, V>[] oldTab = table;
         // 旧容量
         int oldCap = (oldTab == null) ? 0 : oldTab.length;
         // 旧扩容门槛
         int oldThr = threshold;
         int newCap, newThr = 0;
         if (oldCap > 0) {
             if (oldCap >= MAXIMUM_CAPACITY) {
                 // 如果旧容量达到了最大容量，则不再进行扩容
                 threshold = Integer.MAX_VALUE;
                 return oldTab;
             } else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
                 // 如果旧容量的两倍小于最大容量并且旧容量大于默认初始容量（16），则容量扩大为两倍，扩容门槛也扩大为两倍
                 newThr = oldThr << 1; // double threshold
         } else if (oldThr > 0) // initial capacity was placed in threshold
             // 使用非默认构造方法创建的map，第一次插入元素会走到这里
             // 如果旧容量为0且旧扩容门槛大于0，则把新容量赋值为旧门槛
             newCap = oldThr;
         else {               // zero initial threshold signifies using defaults
             // 调用默认构造方法创建的map，第一次插入元素会走到这里
             // 如果旧容量旧扩容门槛都是0，说明还未初始化过，则初始化容量为默认容量，扩容门槛为默认容量*默认装载因子
             newCap = DEFAULT_INITIAL_CAPACITY;
             newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
         }
         if (newThr == 0) {
             // 如果新扩容门槛为0，则计算为容量*装载因子，但不能超过最大容量
             float ft = (float) newCap * loadFactor;
             newThr = (newCap < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY ?
                     (int) ft : Integer.MAX_VALUE);
         }
         // 赋值扩容门槛为新门槛
         threshold = newThr;
         // 新建一个新容量的数组
         @SuppressWarnings({"rawtypes", "unchecked"})
         Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];
         // 把桶赋值为新数组
         table = newTab;
         // 如果旧数组不为空，则搬移元素
         if (oldTab != null) {
             // 遍历旧数组
             for (int j = 0; j < oldCap; ++j) {
                 Node<K, V> e;
                 // 如果桶中第一个元素不为空，赋值给e
                 if ((e = oldTab[j]) != null) {
                     // 清空旧桶，便于GC回收  
                     oldTab[j] = null;
                     // 如果这个桶中只有一个元素，则计算它在新桶中的位置并把它搬移到新桶中
                     // 因为每次都扩容两倍，所以这里的第一个元素搬移到新桶的时候新桶肯定还没有元素
                     if (e.next == null)
                         newTab[e.hash & (newCap - 1)] = e;
                     else if (e instanceof TreeNode)
                         // 如果第一个元素是树节点，则把这颗树打散成两颗树插入到新桶中去
                         ((TreeNode<K, V>) e).split(this, newTab, j, oldCap);
                     else { // preserve order
                         // 如果这个链表不止一个元素且不是一颗树
                         // 则分化成两个链表插入到新的桶中去
                         // 比如，假如原来容量为4，3、7、11、15这四个元素都在三号桶中
                         // 现在扩容到8，则3和11还是在三号桶，7和15要搬移到七号桶中去
                         // 也就是分化成了两个链表
                         Node<K, V> loHead = null, loTail = null;
                         Node<K, V> hiHead = null, hiTail = null;
                         Node<K, V> next;
                         do {
                             next = e.next;
                             // (e.hash & oldCap) == 0的元素放在低位链表中
                             // 比如，3 & 4 == 0
                             if ((e.hash & oldCap) == 0) {
                                 if (loTail == null)
                                     loHead = e;
                                 else
                                     loTail.next = e;
                                 loTail = e;
                             } else {
                                 // (e.hash & oldCap) != 0的元素放在高位链表中
                                 // 比如，7 & 4 != 0
                                 if (hiTail == null)
                                     hiHead = e;
                                 else
                                     hiTail.next = e;
                                 hiTail = e;
                             }
                         } while ((e = next) != null);
                         // 遍历完成分化成两个链表了
                         // 低位链表在新桶中的位置与旧桶一样（即3和11还在三号桶中）
                         if (loTail != null) {
                             loTail.next = null;
                             newTab[j] = loHead;
                         }
                         // 高位链表在新桶中的位置正好是原来的位置加上旧容量（即7和15搬移到七号桶了）
                         if (hiTail != null) {
                             hiTail.next = null;
                             newTab[j + oldCap] = hiHead;
                         }
                     }
                 }
             }
         }
         return newTab;
    }
```

当我执行put的时候，最终执行的时`putVal(int hash, K key, V value, boolean onlyIfAbsent,  boolean evict) `这个方法,
- (1)当我们初次添加时或满足条件时，或当 ++size > threshold =  (initialCapacity *  loadFactor) 会执行resize()来 初始化或加倍表大小
- 当满足 (2) 中的计算条件时会进行 new Node<>(hash, key, value, next);
- 当满足(3)时,说明key以及存在，这时我们替换器value值
- 如果满足(5) `p instanceof TreeNode` 则会进行tree的存储
- 当不满足 以上条件，则会进行(6)

