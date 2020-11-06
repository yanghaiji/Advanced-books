package com.javayh.advanced.java.ExamplesEnum;

import java.util.Objects;

/**
 * <p>
 * 枚举示例
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-11-05 3:20 PM
 */
public class ExamplesEnum {
    public static void main(String[] args) {
        /**
         * 枚举的默认且常用的方法
         *  1. name(); 获取定义的字段名
         *  2. values(); 获取枚举内定义的数据集
         *  3. toString();
         */

        //作为常量的直接引用
        Day monday = Day.MONDAY;
        System.out.println(monday);
        //
        Day2 friday = Day2.FRIDAY;
        System.out.println(friday.getDesc());
        System.out.println(friday.getCode());
        for (Day2 value : Day2.values()) {
            System.out.println(value.name());
        }
        String desc = friday.getDesc(3);
        System.out.println(desc);

        //语句
        //语句
        //你可以有任意数量的case语句
        //语句
        switch (desc) {
            //可选
            case "星期一" -> System.out.println(desc);
            //可选
            case "星期" -> System.out.println(desc);
            //可选
            default -> System.out.println("--------");
        }

    }
}

enum Day {
    MONDAY, TUESDAY, WEDNESDAY,
    THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

enum Day2 {
    MONDAY("星期一", 1),
    TUESDAY("星期二", 2),
    WEDNESDAY("星期三", 3),
    THURSDAY("星期四", 4),
    FRIDAY("星期五", 5),
    SATURDAY("星期六", 6),
    SUNDAY("星期日", 7);//记住要用分号结束

    private String desc;//文字描述
    private Integer code; //对应的代码

    /**
     * 私有构造,防止被外部调用
     */
    private Day2(String desc,Integer code) {
        this.desc = desc;
        this.code = code;
    }

    /**
     * 定义方法,返回描述,跟常规类的定义没区别
     * @return
     */
    public String getDesc() {
        return desc;
    }
    public int getCode() {
        return code;
    }

    public String getDesc(int index){
        for (Day2 day2 : Day2.values()){
            if (day2.code == index) {
                return day2.desc;
            }
        }
        return null;
    }

    public int getCode(String desc){
        for (Day2 day2 : Day2.values()){
            if (Objects.equals(day2.desc, desc)) {
                return day2.code;
            }
        }
        return -1;
    }
}