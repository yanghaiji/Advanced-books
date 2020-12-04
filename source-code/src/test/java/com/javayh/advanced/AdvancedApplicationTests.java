package com.javayh.advanced;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javayh.advanced.mybatis.mapper.TestMapper;
import com.javayh.advanced.mybatis.vo.LogisticsVO;
import com.javayh.advanced.spring.web.TestWeb;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class AdvancedApplicationTests {

    @Autowired
    private TestWeb testWeb;
    @Autowired
    private TestMapper testMapper;

    @Test
    void sysLog() {
        testWeb.test();
    }

    @Test
    void mapperMap() throws JsonProcessingException {
        List<LogisticsVO> all = testMapper.findAll();
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(all);
        System.out.println(s);
    }


    @Test
    void mapperIf() throws JsonProcessingException {
        LogisticsVO yanghaiji = LogisticsVO.builder().addressee_name("yanghaiji").addressee_phone("1234565432").build();
        List<LogisticsVO> logisticsVOS = new ArrayList<>();
        logisticsVOS.add(yanghaiji);
        List<LogisticsVO> all = testMapper.findListAndIf(logisticsVOS);
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(all);
        System.out.println(s);
    }

    @Test
    void limit() {
        for (int i = 0; i < 20; i++) {
            testWeb.test();
        }

    }

}
