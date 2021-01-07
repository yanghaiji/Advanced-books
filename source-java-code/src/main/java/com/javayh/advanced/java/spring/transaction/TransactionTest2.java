package com.javayh.advanced.java.spring.transaction;

import com.javayh.advanced.java.mybatis.mapper.TestMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-25
 */
@Service
public class TransactionTest2 {
    @Resource
    private TestMapper testMapper;

    @Transactional(rollbackFor = Exception.class)
    public void test01() {
        testMapper.insert("789");
        test02();
    }

    //@Transactional(rollbackFor = Exception.class)
    public void test02() {
        testMapper.insert("12345");
        Integer.valueOf("1245i");
    }

}
