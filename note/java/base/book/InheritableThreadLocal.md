# InheritableThreadLocal详解

### 简介 

在之前我们详细介绍了[ThreadLocal](https://blog.csdn.net/weixin_38937840/article/details/117073579),原理及设计，从源码层面上分析了ThreadLocal。但是它只能解决当前线程的信息传递，当然 ThreadLocal的最初设计也是这样的，但当我们需要将当先信息传递给子线程时ThreadLocal已经明显无法满足。这时`InheritableThreadLocal` 就显得尤为重要！

---

### 应用

为什么今天会谈到`InheritableThreadLocal`呢？这还是应为在项目上使用线程池的时候发现，子线程无法获取前端信息的信息，导致程序出错....... 

> **InheritableThreadLocal**  真的可以将当前线程的信息传递给子线程吗？我们可以模拟一下，同时与**ThreadLocal**进行对比

```
public class Test {
    public static void main(String[] args) {
        InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();
        inheritableThreadLocal.set("haiji");
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        threadLocal.set("java有货");

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            System.out.println("当前线程的名字:"+Thread.currentThread().getName());
            System.out.println("从 threadLocal 内获取值:"+threadLocal.get());
            System.out.println("从 inheritableThreadLocal 内获取值:"+inheritableThreadLocal.get());
        });
    }
}
```

>**验证的结果输出：**
>
>当前线程的名字:pool-1-thread-1
>从 threadLocal 内获取值:null
>从 inheritableThreadLocal 内获取值:haiji

---

### InheritableThreadLocal 的原理

首先我们看看 InheritableThreadLocal 的源码

```
public class InheritableThreadLocal<T> extends ThreadLocal<T> {
    /**
     * Creates an inheritable thread local variable.
     */
    public InheritableThreadLocal() {}

    /**
     * Computes the child's initial value for this inheritable thread-local
     * variable as a function of the parent's value at the time the child
     * thread is created.  This method is called from within the parent
     * thread before the child is started.
     * <p>
     * This method merely returns its input argument, and should be overridden
     * if a different behavior is desired.
     *
     * @param parentValue the parent thread's value
     * @return the child thread's initial value
     */
    // 在子线程创建时，根据父值的值计算该可继承线程局部变量的子级初始值。 
    protected T childValue(T parentValue) {
        return parentValue;
    }

    /**
     * Get the map associated with a ThreadLocal.
     *
     * @param t the current thread
     */
    ThreadLocalMap getMap(Thread t) {
       return t.inheritableThreadLocals;
    }

    /**
     * Create the map associated with a ThreadLocal.
     *
     * @param t the current thread
     * @param firstValue value for the initial entry of the table.
     */
    void createMap(Thread t, T firstValue) {
        t.inheritableThreadLocals = new ThreadLocalMap(this, firstValue);
    }
}

```

源码很简单，重载了  `getMap` 与 `createMap`，我们想了解原理还是需要从 Thread 这个类进行，Thread内部维护了两个重要的变量 threadLocals / inheritableThreadLocals

```
    /* ThreadLocal values pertaining to this thread. This map is maintained
     * by the ThreadLocal class. */
    ThreadLocal.ThreadLocalMap threadLocals = null;

    /*
     * InheritableThreadLocal values pertaining to this thread. This map is
     * maintained by the InheritableThreadLocal class.
     */
    ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;
```

Thread类中包含 *threadLocals* 和 *inheritableThreadLocals* 两个变量，其中 **inheritableThreadLocals** 即主要存储可自动向子线程中传递的ThreadLocal.ThreadLocalMap。
 接下来看一下父线程创建子线程的流程，我们从最简单的方式说起：

#### 4.1、用户创建Thread

```cpp
Thread thread = new Thread();
```

#### 4.2、Thread创建

```java
    /**
     * Allocates a new {@code Thread} object. This constructor has the same
     * effect as {@linkplain #Thread(ThreadGroup,Runnable,String) Thread}
     * {@code (null, null, gname)}, where {@code gname} is a newly generated
     * name. Automatically generated names are of the form
     * {@code "Thread-"+}<i>n</i>, where <i>n</i> is an integer.
     */
    public Thread() {
        init(null, null, "Thread-" + nextThreadNum(), 0);
    }
```

#### 4.3、Thread初始化

```csharp
    /**
     * 默认情况下，设置inheritThreadLocals可传递
     */
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize) {
        init(g, target, name, stackSize, null, true);
    }
```

```dart
    /**
     * 初始化一个线程.
     * 此函数有两处调用，
     * 1、上面的 init()，不传AccessControlContext，inheritThreadLocals=true
     * 2、传递AccessControlContext，inheritThreadLocals=false
     */
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize, AccessControlContext acc,
                      boolean inheritThreadLocals) {
        ......（其他代码）

        if (inheritThreadLocals && parent.inheritableThreadLocals != null)
            this.inheritableThreadLocals =
                ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);

        ......（其他代码）
    }
```

可以看到，采用默认方式产生子线程时，inheritThreadLocals=true；若此时父线程inheritableThreadLocals不为空，则将父线程inheritableThreadLocals传递至子线程。

#### 4.4、ThreadLocal.createInheritedMap

让我们继续追踪createInheritedMap：

```cpp
    static ThreadLocalMap createInheritedMap(ThreadLocalMap parentMap) {
        return new ThreadLocalMap(parentMap);
    }
```

```csharp
        /**
         * 构建一个包含所有parentMap中Inheritable ThreadLocals的ThreadLocalMap
         * 该函数只被 createInheritedMap() 调用.
         */
        private ThreadLocalMap(ThreadLocalMap parentMap) {
            Entry[] parentTable = parentMap.table;
            int len = parentTable.length;
            setThreshold(len);
            // ThreadLocalMap 使用 Entry[] table 存储ThreadLocal
            table = new Entry[len];

            // 逐一复制 parentMap 的记录
            for (int j = 0; j < len; j++) {
                Entry e = parentTable[j];
                if (e != null) {
                    @SuppressWarnings("unchecked")
                    ThreadLocal<Object> key = (ThreadLocal<Object>) e.get();
                    if (key != null) {
                        // 可能会有同学好奇此处为何使用childValue，而不是直接赋值，
                        // 毕竟childValue内部也是直接将e.value返回；
                        // 个人理解，主要为了减轻阅读代码的难度
                        Object value = key.childValue(e.value);
                        Entry c = new Entry(key, value);
                        int h = key.threadLocalHashCode & (len - 1);
                        while (table[h] != null)
                            h = nextIndex(h, len);
                        table[h] = c;
                        size++;
                    }
                }
            }
        }
```

从ThreadLocalMap可知，子线程将parentMap中的所有记录逐一复制至自身线程。