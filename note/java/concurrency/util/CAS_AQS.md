## CAS 介绍与分析

###  ⭐ CAS 是什么？

CAS是英文单词**CompareAndSwap**的缩写，中文意思是：比较并替换。CAS需要有3个操作数：内存地址V，旧的预期值A，即将要更新的目标值B。

CAS指令执行时，当且仅当内存地址V的值与预期值A相等时，将内存地址V的值修改为B，否则就什么都不做。整个比较并替换的操作是一个原子操作。

在Java中有 `AtomicInteger` 为代表的都是 通过cas 进行控制,这里以 `incrementAndGet()`为例,调用的代码如下：

```java
    public final int incrementAndGet() {
        return U.getAndAddInt(this, VALUE, 1) + 1;
    }

 	@HotSpotIntrinsicCandidate
    public final int getAndAddInt(Object o, long offset, int delta) {
        int v;
        do {
            v = getIntVolatile(o, offset);
        } while (!weakCompareAndSetInt(o, offset, v, v + delta));
        return v;
    }

    @HotSpotIntrinsicCandidate
    public final boolean weakCompareAndSetInt(Object o, long offset,
                                              int expected,
                                              int x) {
        return compareAndSetInt(o, offset, expected, x);
    }

    @HotSpotIntrinsicCandidate
    public final native boolean compareAndSetInt(Object o, long offset,
                                                 int expected,
                                                 int x);
```

这里我们可以看出,最终调用的是`Unsafe` 类(如果对Unsafe类不了解，请阅读 [Unsafe应用解析](https://github.com/yanghaiji/Advanced-books/blob/master/note/java/concurrency/util/Unsafe.md))

`compareAndSetInt`需要三个参数，分别是内存位置 offset，旧的预期值 expected和新的值 x。操作时，先从内存位置读取到值，然后和预期值expected比较。如果相等，则将此内存位置的值改为新值x，返回 true。如果不相等，说明和其他线程冲突了，则不做任何改变，返回 false。

这种机制在不阻塞其他线程的情况下避免了并发冲突，比独占锁的性能高很多。 CAS 在 Java 的原子类和并发包中有大量使用。

### ⭐ 底层实现

CAS 主要分三步，读取-比较-修改。其中比较是在检测是否有冲突，如果检测到没有冲突后，其他线程还能修改这个值，那么 CAS 还是无法保证正确性。所以最关键的是要保证比较-修改这两步操作的原子性。

CAS 底层是靠调用 CPU 指令集的 cmpxchg 完成的，它是 x86 和 Intel 架构中的 compare and exchange 指令。在多核的情况下，这个指令也不能保证原子性，需要在前面加上  lock 指令。lock 指令可以保证一个 CPU 核心在操作期间独占一片内存区域。那么 这又是如何实现的呢？

在处理器中，一般有两种方式来实现上述效果：总线锁和缓存锁。在多核处理器的结构中，CPU 核心并不能直接访问内存，而是统一通过一条总线访问。总线锁就是锁住这条总线，使其他核心无法访问内存。这种方式代价太大了，会导致其他核心停止工作。而缓存锁并不锁定总线，只是锁定某部分内存区域。当一个 CPU 核心将内存区域的数据读取到自己的缓存区后，它会锁定缓存对应的内存区域。锁住期间，其他核心无法操作这块内存区域。

CAS 就是通过这种方式实现比较和交换操作的原子性的。值得注意的是， CAS 只是保证了操作的原子性，并不保证变量的可见性，因此变量需要加上 volatile 关键字。

### ⭐ ABA 问题

上面提到，CAS 保证了比较和交换的原子性。但是从读取到开始比较这段期间，其他核心仍然是可以修改这个值的。如果核心将 A 修改为 B，CAS 可以判断出来。但是如果核心将 A 修改为 B 再修改回 A。那么 CAS 会认为这个值并没有被改变，从而继续操作。这是和实际情况不符的。解决方案是加一个版本号。

---



## AQS 介绍与分析

AQS 全称 AbstractQueuedSynchronizer。AQS 中有两个重要的成员：

- 成员变量 state。用于表示锁现在的状态，用 volatile 修饰，保证内存一致性。同时所用对 state 的操作都是使用 CAS 进行的。state 为0表示没有任何线程持有这个锁，线程持有该锁后将 state 加1，释放时减1。多次持有释放则多次加减。
- 还有一个双向链表，链表除了头结点外，每一个节点都记录了线程的信息，代表一个等待线程。这是一个 FIFO 的链表。

下面以 ReentrantLock 非公平锁的代码看看 AQS 的原理。

-  说明：

  ```java
  public ReentrantLock(boolean fair) {
      sync = fair ? new FairSync() : new NonfairSync();
  }
  ```

  `ReentrantLock` 默认是非公平锁， 如果想实现公平锁，在创建时传入true 即可,从源码看，无论是 `FairSync` 还是`NonfairSync` 都继承了`Sync` ，而 `Sync` 有继承了 `AbstractQueuedSynchronizer` 即我们所说的`AQS`

  ![image-20210302142037731](https://img-blog.csdnimg.cn/20210302202850247.png)

请求锁时有三种可能：

1. 如果没有线程持有锁，则请求成功，当前线程直接获取到锁。
2. 如果当前线程已经持有锁，则使用 CAS 将 state 值加1，表示自己再次申请了锁，释放锁时减1。这就是可重入性的实现。
3. 如果由其他线程持有锁，那么将自己添加进等待队列。

```java
    public void lock() {
        sync.lock();
    }

	@ReservedStackAccess
    final void lock() {
        if (!initialTryLock())
            acquire(1);
    }

	// initialTryLock() 方法
    final boolean initialTryLock() {
        Thread current = Thread.currentThread();
        // 第一次尝试 cas 
        //如果当前状态值等于预期值，则以原子方式将同步状态设置为给定的更新值。
        //此操作具有volatile读写的内存语义
        // compareAndSetState​(int expect, int update)
        // expect - 预期值 update - 新值 
        if (compareAndSetState(0, 1)) { // first attempt is unguarded
            // 这里调用的是AbstractOwnableSynchronizer的方法
            // 设置当前拥有独占访问权限的线程。
            setExclusiveOwnerThread(current);
            return true;
        }
        // 返回最后由 setExclusiveOwnerThread设置的线程，如果从未设置，则返回 null 。
        else if (getExclusiveOwnerThread() == current) {
            // getState() 返回同步状态的当前值。 此操作具有volatile读取的内存语义。
            int c = getState() + 1;
            if (c < 0) // overflow
                throw new Error("Maximum lock count exceeded");
            // 设置同步状态的值。 此操作具有volatile写入的内存语义。
            setState(c);
            return true;
        } else
            return false;
    }


	// acquire(1);
	// 以独占模式获取，忽略中断。 通过至少调用一次tryAcquire(int)实现 ，返回成功。
	// 否则线程排队，可能反复阻塞和解除阻塞，调用tryAcquire(int)直到成功。 
	// 该方法可用于实现方法Lock.lock() 。
	public final void acquire(int arg) {
        if (!tryAcquire(arg))
            acquire(null, arg, false, false, false, 0L);
    }

	// tryAcquire 需要根据 所看的Sync来决定 
	// 这里的代码与 initialTryLock 的逻辑类似，不做过多的讲解
    protected final boolean tryAcquire(int acquires) {
        if (getState() == 0 && compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(Thread.currentThread());
            return true;
        }
        return false;
    }

	// acquire(null, arg, false, false, false, 0L);
    final int acquire(Node node, int arg, boolean shared,
                      boolean interruptible, boolean timed, long time) {
        Thread current = Thread.currentThread();
        byte spins = 0, postSpins = 0;   // retries upon unpark of first thread
        boolean interrupted = false, first = false;
        Node pred = null;                // predecessor of node when enqueued

        /*
         * Repeatedly:
         *  Check if node now first
         *    if so, ensure head stable, else ensure valid predecessor
         *  if node is first or not yet enqueued, try acquiring
         *  else if node not yet created, create it
         *  else if not yet enqueued, try once to enqueue
         *  else if woken from park, retry (up to postSpins times)
         *  else if WAITING status not set, set and retry
         *  else park and clear WAITING status, and check cancellation
         */

        for (;;) {
            if (!first && (pred = (node == null) ? null : node.prev) != null &&
                !(first = (head == pred))) {
                if (pred.status < 0) {
                    //可能从tail重复遍历，取消已取消的节点，直到没有找到为止。
                    //将可能被重新链接成为下一个合格收购者的节点打开。
                    cleanQueue();           // predecessor cancelled
                    continue;
                } else if (pred.prev == null) {
                    Thread.onSpinWait();    // ensure serialization
                    continue;
                }
            }
            if (first || pred == null) {
                boolean acquired;
                // 尝试获取共享锁
                try {
                    if (shared)
                        acquired = (tryAcquireShared(arg) >= 0);
                    else
                        acquired = tryAcquire(arg);
                } catch (Throwable ex) {
                    cancelAcquire(node, interrupted, false);
                    throw ex;
                }
                if (acquired) {
                    if (first) {
                        // 将node 赋值给 head
                        node.prev = null;
                        head = node;
                        pred.next = null;
                        node.waiter = null;
                        if (shared)
                            // 在共享模式下唤醒给定的节点
                            signalNextIfShared(node);
                        if (interrupted)
                            // 调用本线程的interrupt 进行线程中断
                            current.interrupt();
                    }
                    return 1;
                }
            }
            if (node == null) {                 // allocate; retry before enqueue
                if (shared)
                    node = new SharedNode();
                else
                    node = new ExclusiveNode();
            } else if (pred == null) {          // try to enqueue
                node.waiter = current;
                Node t = tail;
                node.setPrevRelaxed(t);         // avoid unnecessary fence
                if (t == null)
                    tryInitializeHead();
                else if (!casTail(t, node))
                    node.setPrevRelaxed(null);  // back out
                else
                    t.next = node;
            } else if (first && spins != 0) {
                --spins;                        // reduce unfairness on rewaits
                Thread.onSpinWait();
            } else if (node.status == 0) {
                node.status = WAITING;          // enable signal and recheck
            } else {
                long nanos;
                spins = postSpins = (byte)((postSpins << 1) | 1);
                if (!timed)
                    LockSupport.park(this);
                else if ((nanos = time - System.nanoTime()) > 0L)
                    LockSupport.parkNanos(this, nanos);
                else
                    break;
                node.clearStatus();
                if ((interrupted |= Thread.interrupted()) && interruptible)
                    break;
            }
        }
        return cancelAcquire(node, interrupted, interruptible);
    }
	
```

加锁的逻辑基本上我们整体的看了一遍，接下来我们在看一下如何进行解锁。

```java
    // 解锁操作
	public void unlock() {
        sync.release(1);
    }

	// 解锁操作
    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            signalNext(head);
            return true;
        }
        return false;
    }
	
	// 这里与 initialTryLock() 方法相同
    @ReservedStackAccess
    protected final boolean tryRelease(int releases) {
        int c = getState() - releases;
        // 返回最后由setExclusiveOwnerThread设置的线程，如果从未设置，则返回null 。 
        // 此方法不会强制执行任何同步或volatile字段访问。
        if (getExclusiveOwnerThread() != Thread.currentThread())
            throw new IllegalMonitorStateException();
        boolean free = (c == 0);
        if (free)
            setExclusiveOwnerThread(null);
        setState(c);
        return free;
    }

    private static void signalNext(Node h) {
        Node s;
        if (h != null && (s = h.next) != null && s.status != 0) {
            s.getAndUnsetStatus(WAITING);
            // 如果给定线程尚不可用，则为其提供许可。
            LockSupport.unpark(s.waiter);
        }
    }
```
