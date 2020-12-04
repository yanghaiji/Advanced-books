package com.javayh.advanced.java.collection.map;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author Dylan-haiji
 * @version 1.0.0
 * @since 2020-07-27
 */
public class MapTest {

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>(8, 0.75f);
        for (int i = 0; i < 15; i++) {
            map.put(i + "java", i + "java");
        }
    }
}
