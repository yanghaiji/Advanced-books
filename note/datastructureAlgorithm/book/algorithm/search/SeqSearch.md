## 线性查找算法

### 介绍

线性查找又称顺序查找，是一种最简单的查找方法，它的基本思想是从第一个记录开始，逐个比较记录的关键字，直到和给定的K值相等，则查找成功；若比较结果与文件中n个记录的关键字都不等，则查找失败。

查找是对具有相同属性的数据元素（记录）的集合（数据对象）进行的，称之为表或文件，也称字典。对表的查找，若仅对表进行查找操作，而不能改变表中的数据元素，为静态查找；对表除了进行查找操作外，还可能对表进行插入或删除操作，则为动态查找。

由此也可以看出其时间复杂度为 Ｏ(N)

### 示例

有一个数列： {1,8, 10, 89, 1000, 1234} ，判断数列中是否包含此名称【顺序查找】 要求:  如果找到了，就提示找到，并给出下标值。

```java
public class SeqSearch {
    public static void main(String[] args) {
        int[] array =  {1,8, 10, 89, 1000, 1234};

        int i = seqSearch(array, 89);
        if(i == 1){
            System.out.println("array 中查到目标数据");
        }else {
            System.out.println("array 中未查到目标数据");
        }

    }

    /**
     *
     * @param array 目标数据
     * @param num 需要查询的数据
     */
    private static int seqSearch(int[] array, int num) {
        // flag = 1 表示查找到 ,否则为为查找到
        int flag = 0;
        for (int i = 0; i < array.length-1; i++) {
            if (num == array[i]) {
                flag = 1;
                break;
            }
        }
        return flag;
    }
}
```
