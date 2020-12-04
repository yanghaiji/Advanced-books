package com.javayh.advanced.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-11
 */
public class UnsafeUtils {

    private static final Logger logger = LoggerFactory.getLogger(UnsafeUtils.class);

    /**
     * <p>
     * 通过反射获取 Unsafe
     * </p>
     *
     * @return sun.misc.Unsafe
     * @version 1.0.0
     * @author hai ji
     * @since 2020/8/11
     */
    public static Unsafe getUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
