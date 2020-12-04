package com.javayh.advanced.spring.transaction;

import com.javayh.advanced.mybatis.mapper.TestMapper;
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
 * @since 2020-08-18
 */
@Service
public class TransactionTest {

    @Resource
    private TestMapper testMapper;

    @Resource
    private TransactionTest2 transactionTest2;

    @Transactional(rollbackFor = Exception.class)
    public void test01() {
        testMapper.insert("789");
        transactionTest2.test02();
        //test02();
    }

    @Transactional(rollbackFor = Exception.class)
    public void test02() {
        testMapper.insert("12345");
        Integer.valueOf("1245i");
    }

    /**
     * 1. @Transactional注解 不能作用在private 方法上
     * 2. 带有事务的方法，调用本类中不带有事务的方法，会将本类不带有事务的方法纳入到事务内，
     *    这是调用的方法，可以是私有方法,即走的是最外层方法的事务
     * 3. A类 无事务的方法，调用 B类有事务的方法时， B类异常可以进行事务回滚，但是A类无法回滚
     * 4. A类有事务的方法，调用B类无事务的方法，当B类发生异常，可以正常进行事务回滚
     * 5. 设置事务回滚的两种方式
     *    5.1 进行 throw new Exception
     *    5.2 可以在需要设置手动回滚TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
     */

}
