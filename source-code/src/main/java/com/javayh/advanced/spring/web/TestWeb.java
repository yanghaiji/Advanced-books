package com.javayh.advanced.spring.web;

import com.javayh.advanced.spring.aop.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haiyang
 */
@RestController
@RequestMapping(value = "/test/")
public class TestWeb {

    @SysLog(value = "测试Aop注解",limit = 3)
    @RequestMapping(value = "syslog")
    public String test(){
        return "test";
    }
}
