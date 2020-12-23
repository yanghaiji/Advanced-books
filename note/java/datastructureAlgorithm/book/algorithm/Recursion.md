## 递归

简单的说: 递归就是方法自己调用自己,每次调用时传入不同的变量.递归有助于编程者解决复杂的问题,同时可以让代码变得简洁。
这里不禁的让我想起了两部电影 `盗梦空间` `蝴蝶效应`

### 递归能解决什么样的问题

1)	各种数学问题如: 8 皇后问题 ,  汉诺塔,  阶乘问题,  迷宫问题,  球和篮子的问题(google 编程大赛)
2)	各种算法中也会使用到递归，比如快排，归并排序，二分查找，分治算法等.
3)	将用栈解决的问题-->第归代码比较简洁

### 递归需要遵守的重要规则

1)	执行一个方法时，就创建一个新的受保护的独立空间(栈空间)
2)	方法的局部变量是独立的，不会相互影响, 比如 n 变量
3)	如果方法中使用的是引用类型变量(比如数组)，就会共享该引用类型的数据.
4)	递归必须向退出递归的条件逼近，否则就是无限递归,出现 StackOverflowError，死`龟(归)`了:)
5)	当一个方法执行完毕，或者遇到 return，就会返回，遵守谁调用，就将结果返回给谁，同时当方法执行完毕或者返回时，该方法也就执行完毕

```java
public class MiGong {
    public static void main(String[] args) {
        //创建一个二维数组模拟迷宫
        int[][] map = new int[8][7];
        //使用1表示墙
        //上下全部置为1
        for (int i = 0; i < 7; i++) {
            map[0][i]= 1;
            map[7][i]= 1;
        }
        //左右全部置为1
        for (int i = 0; i < 8; i++) {
            map[i][0]= 1;
            map[i][6]= 1;
        }
        //设置挡板，1 表示
        map[3][1] = 1;
        map[3][2] = 1;
        System.out.println("地图的情况");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(map[i][j]+ " ");
            }
            System.out.println();
        }

        //使用递归回溯给小球找路
        setWay(map,1,1);
        setWay2(map,1,1);

        System.out.println("小球走过后地图的情况");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(map[i][j]+ " ");
            }
            System.out.println();
        }


    }

    //使用递归回溯来给小球找路
    //说明
    //1. map 表示地图
    //2. i,j 表示从地图的哪个位置开始出发 (1,1)
    //3. 如果小球能到 map[6][5] 位置，则说明通路找到.
    //4.  约定：  当 map[i][j]  为  0  表示该点没有走过  当为  1  表示墙	； 2 表示通路可以走 ；  3  表示该点已经走过，但是走不通
    //5. 在走迷宫时，需要确定一个策略(方法) 下->右->上->左 ,  如果该点走不通，再回溯

    private static boolean setWay(int[][] map, int i, int j) {
        if(map[6][5]==2){
            return true;
        }else {
            //此路还没有走
            if(map[i][j]== 0 ){
                //按照策略  下->右->上->左	走
                map[i][j] = 2;
                //假定该点是可以走通.
                //向下走
                if(setWay(map, i+1, j)) {
                    return true;
                }
                //向右走
                else if (setWay(map, i, j+1)) {
                    return true;
                }
                // 向上
                else if (setWay(map, i-1, j)) {
                    return true;
                }
                // 向左走
                else if (setWay(map, i, j-1)){
                    return true;
                } else {
                    //说明该点是走不通，是死路
                    map[i][j] = 3;
                    return false;
                }
            }else { // map[i][j] != 0 可能是 1，2，3
                return false;
            }
        }
    }

    //修改找路的策略，改成 上->右->下->左
    public static boolean setWay2(int[][] map, int i, int j) {
        if(map[6][5] == 2) {
            // 通路已经找到 ok
            return true;
        }
        else {
            if(map[i][j] == 0) {
                //如果当前这个点还没有走过
                //按照策略 上->右->下->左
                // 假定该点是可以走通.
                map[i][j] = 2;
                //向上走
                if(setWay2(map, i-1, j)) {
                    return true;
                }  //向右走
                else if (setWay2(map, i, j+1)) {
                    return true;
                } //向下
                else if (setWay2(map, i+1, j)) {
                    return true;
                } //  向左走
                else if (setWay2(map, i, j-1)){
                    return true;
                } else {
                    //说明该点是走不通，是死路
                    map[i][j] = 3; return false;
                }
            } else { // 如 果 map[i][j] != 0 , 可 能 是 1， 2， 3
                return false;
            }
        }
    }

}
```




