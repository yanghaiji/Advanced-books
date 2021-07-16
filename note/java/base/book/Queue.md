## Queue 源码分析

队列是一种特殊的线性表，它只允许在表的前端进行删除操作，而在表的后端进行插入操作。

LinkedList类实现了Queue接口，因此我们可以把LinkedList当成Queue来用。

本文也将围绕LinkedList进行分析，示例代码

```java
    Queue<Integer> queue = new LinkedList<>();
    boolean add = queue.add(1);
    Integer element = queue.element();
    boolean offer = queue.offer(2);
    Integer peek1 = queue.peek();
    Integer poll = queue.poll();
    Integer remove = queue.remove();
```

### ⭐ add

```java
   // 添加元素
	public boolean add(E e) {
        linkLast(e);
        return true;
    }
	// 往链表最后添加
    void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        // 当链表为空时，将新创建的node 作为第一个节点，否则做为其后驱节点，并将size ++
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;
    }
```

---

### ⭐ element

```java
   // 返回元素的第一个节点
	public E element() {
        return getFirst();
    }

    public E getFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return f.item;
    }
```

### ⭐ offer

其底层还是调用的add方法

```java
    public boolean offer(E e) {
        return add(e);
    }
```

---

### ⭐ peek

返回第一个元素但是不删除

```java
    public E peek() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }
```

---

### ⭐ poll

返回第一个元素并删除(不是空队列时删除)

```java
    public E poll() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }
	// 将第一个元素置为null，当first的next 为null 将last也置为null
	// 否则将first next 的prev 置为null 即将next 做为第一个节点
    private E unlinkFirst(Node<E> f) {
        // assert f == first && f != null;
        final E element = f.item;
        final Node<E> next = f.next;
        f.item = null;
        f.next = null; // help GC
        first = next;
        if (next == null)
            last = null;
        else
            next.prev = null;
        size--;
        modCount++;
        return element;
    }
```

---

### ⭐ remove

```java
    // 其实底部和poll 类似
	public E remove() {
        return removeFirst();
    }

    public E removeFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return unlinkFirst(f);
    }
```

这里我可以发现 LinkeList 不是线程安全的，当我们需要使用线程安全的队列该如何做呢？当然JDK 自带了我们可以使用`ArrayBlockingQueue`, 下一篇我们将讲解`ArrayBlockingQueue`