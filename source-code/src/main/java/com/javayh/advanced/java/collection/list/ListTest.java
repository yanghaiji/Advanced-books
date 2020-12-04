package com.javayh.advanced.java.collection.list;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * list test case
 * </p>
 *
 * @author Dylan-haiji
 * @version 1.0.0
 * @since 2020-07-22
 */
public class ListTest {

    public static void main(String[] args) {

        List<String> list = new ArrayList<>();
        list.add("Java");
        list.add("1234");
        list.add("123422");
        list.add("123422");
        list.add("1233334");

        list.remove("123422");
    }
}
