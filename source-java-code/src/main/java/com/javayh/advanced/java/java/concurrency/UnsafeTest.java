package com.javayh.advanced.java.java.concurrency;


import com.javayh.advanced.java.util.UnsafeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

/**
 * <p>
 * 测试 Unsafe
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-11
 */
public class UnsafeTest {

    static Logger logger = LoggerFactory.getLogger(UnsafeTest.class);

    public static void main(String[] args) {
        //获取 unsafe
        Unsafe unsafe = UnsafeUtils.getUnsafe();
        assert unsafe != null;
        logger.info(unsafe.toString());
    }
}
