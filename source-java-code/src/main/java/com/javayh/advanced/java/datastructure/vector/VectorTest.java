package com.javayh.advanced.java.datastructure.vector;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-22
 */
public class VectorTest {
    public static void main(String[] args) {
        Vector<String> vector = new ArrayVector<>();
        vector.insertAtRank(0, "Java");
        vector.insertAtRank(1, "Java");
        vector.insertAtRank(2, "Java");
        vector.insertAtRank(3, "Java");
        System.out.println(vector.getAtRank(2));
        System.out.println(vector.removeAtRank(2));
        System.out.println(vector.replaceAtRank(1, "Haiji"));
    }
}
