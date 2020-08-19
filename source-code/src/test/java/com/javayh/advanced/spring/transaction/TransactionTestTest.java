package com.javayh.advanced.spring.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-18
 */
@SpringBootTest
public class TransactionTestTest {

    @Resource
    private TransactionTest transactionTest;

    @Test
    void transactionTest() {
        transactionTest.test01();
    }
}