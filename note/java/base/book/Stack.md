## Stack 源码解析

`Stack`类代表最先进先出（LIFO）堆栈的对象。  它扩展了类别`Vector`与五个操作，允许一个向量被视为堆栈。  设置在通常的`push`和`pop`操作，以及作为一种方法来`peek`在堆栈，以测试堆栈是否为`empty`的方法，以及向`search`在栈中的项目的方法在顶部项目和发现多远它是从顶部。

### ⭐ 示例演示

```
public class Test {
    public static void main(String[] args) {
        Stack<Integer> stack = new Stack<>();
        Integer push = stack.push(1);
        boolean empty = stack.empty();
        Integer peek = stack.peek();
        Integer pop = stack.pop();
        int search = stack.search(1);
    }
}

```

---

### ⭐ push 方法

```java
    public E push(E item) {
        addElement(item);

        return item;
    }

    public synchronized void addElement(E obj) {
        modCount++;
        add(obj, elementData, elementCount);
    }
	
	// 这里是调用的 Vector 
    private void add(E e, Object[] elementData, int s) {
        // 当容量相等时进行扩充
        if (s == elementData.length)
            elementData = grow();
        // 底层是object 的数组进行维护
        elementData[s] = e;
        //将长度进行 +1 操作 ， elementCount 的默认长度为10
        // 为什么默认长度为10，当我们取看 Vector的源码就可以看出来，如下
        elementCount = s + 1;
    }

	// 构造方法 其默认长度为10
    public Vector() {
        this(10);
    }

	// 重新计算容量
    private Object[] grow() {
        return grow(elementCount + 1);
    }

    private Object[] grow(int minCapacity) {
        int oldCapacity = elementData.length;
        // 从新计算长度
        int newCapacity = ArraysSupport.newLength(oldCapacity,
                minCapacity - oldCapacity, /* minimum growth */
                capacityIncrement > 0 ? capacityIncrement : oldCapacity
                                           /* preferred growth */);
        // 进行copy
        return elementData = Arrays.copyOf(elementData, newCapacity);
    }

    public static int newLength(int oldLength, int minGrowth, int prefGrowth) {
        // assert oldLength >= 0
        // assert minGrowth > 0

        int newLength = Math.max(minGrowth, prefGrowth) + oldLength;
        if (newLength - MAX_ARRAY_LENGTH <= 0) {
            return newLength;
        }
        return hugeLength(oldLength, minGrowth);
    }

    private static int hugeLength(int oldLength, int minGrowth) {
        int minLength = oldLength + minGrowth;
        if (minLength < 0) { // overflow
            throw new OutOfMemoryError("Required array length too large");
        }
        if (minLength <= MAX_ARRAY_LENGTH) {
            return MAX_ARRAY_LENGTH;
        }
        return Integer.MAX_VALUE;
    }
```

---

### ⭐ peek

- 查看此堆栈顶部的对象，而不从堆栈中删除它。 

```java
    // 这段代码还是很简单的，根据下标进行取数，但是不删除
	public synchronized E peek() {
        int  len = size();

        if (len == 0)
            throw new EmptyStackException();
        return elementAt(len - 1);
    }
	
    public synchronized E elementAt(int index) {
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
        }

        return elementData(index);
    }
    E elementData(int index) {
        return (E) elementData[index];
    }

```

--------

### ⭐ pop

```java
    //这里是将peek 后的数据进行删除操作
	public synchronized E pop() {
        E   obj;
        int len = size();

        obj = peek();
        removeElementAt(len - 1);

        return obj;
    }
	
	// 
    public synchronized void removeElementAt(int index) {
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " +
                                                     elementCount);
        }
        else if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        int j = elementCount - index - 1;
        if (j > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, j);
        }
        modCount++;
        elementCount--;
        // 节省空间
        elementData[elementCount] = null; /* to let gc do its work */
    }

```

---

### ⭐ search

```java
    public synchronized int search(Object o) {
        int i = lastIndexOf(o);

        if (i >= 0) {
            return size() - i;
        }
        return -1;
    }

    public synchronized int lastIndexOf(Object o) {
        return lastIndexOf(o, elementCount-1);
    }
	// 这里也很简单， 循环查找与给定元素相等的下标
    public synchronized int lastIndexOf(Object o, int index) {
        if (index >= elementCount)
            throw new IndexOutOfBoundsException(index + " >= "+ elementCount);

        if (o == null) {
            for (int i = index; i >= 0; i--)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = index; i >= 0; i--)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }
```

---

### ⭐ 总结

- 从源码查看，Stack是线程安全的，由 synchronized 控制
- 后进先出的原则 last-in-first-out 即 LIFO原则

