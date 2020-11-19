package com.javayh.advanced.java.collection.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * Collections code
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-11-18 3:46 PM
 */
public class CollectionsTest {
    public static void main(String[] args) {
        List<String> listOne = new ArrayList<>();
        String[] strings = new String[]{"List","Set","Map"};
        Collections.addAll(listOne,"Yang","Hai","ji");
        Collections.addAll(listOne,strings);
        System.out.println(listOne);

        List<String> listTwo = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            listTwo.add("Yang"+i);
        }
        int yang999 = Collections.binarySearch(listTwo, "Yang999");
        System.out.println(yang999);

        List<String> linkedList = new LinkedList<>();
        for (int i = 0; i < 5050; i++) {
            linkedList.add("Hai"+i);
        }
        int hai = Collections.binarySearch(linkedList, "Hai999");
        System.out.println(hai);

        List<String> syncList = Collections.synchronizedList(new ArrayList<>());


        List<String> disjointList1 = new ArrayList<>();
        List<String> disjointList2 = new ArrayList<>();
        boolean disjoint = Collections.disjoint(disjointList1, disjointList2);

        System.out.println(Collections.max(listOne));

    }
}

