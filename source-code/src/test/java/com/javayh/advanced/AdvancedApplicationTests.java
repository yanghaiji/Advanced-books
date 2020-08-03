package com.javayh.advanced;

import com.javayh.advanced.spring.web.TestWeb;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdvancedApplicationTests {

    @Autowired
    private TestWeb testWeb;


    @Test
    void sysLog() {
        testWeb.test();
    }

}
