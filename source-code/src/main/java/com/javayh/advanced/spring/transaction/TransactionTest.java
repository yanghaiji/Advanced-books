package com.javayh.advanced.spring.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-18
 */
@Service
public class TransactionTest {

    @Transactional(rollbackFor = Exception.class)
    public void test01(){
        System.out.println("test01 开始数据插入");
        Integer integer = Integer.valueOf("12a");
        System.out.println("test01 数据插入成功");
    }


    @Transactional(rollbackFor = Exception.class)
    public void test02(){
        System.out.println("test02 开始数据插入");
        Integer integer = Integer.valueOf("12a");
        System.out.println("test02 数据插入成功");
    }
}
