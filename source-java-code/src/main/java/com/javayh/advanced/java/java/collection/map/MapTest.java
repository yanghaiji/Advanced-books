package com.javayh.advanced.java.java.collection.map;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        Map<String, String> concurrentHashMap = new ConcurrentHashMap<>(16);
        for (int i = 0; i < 15; i++) {
            concurrentHashMap.put(i + "java", i + "java");
        }
    }
}
