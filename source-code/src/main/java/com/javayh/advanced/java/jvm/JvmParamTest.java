package com.javayh.advanced.java.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * jvm 参数
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-11-23 1:41 PM
 */
public class JvmParamTest {

    /**
     * 1.设置最大/小 heap
     * -Xmx10m/-Xms10m
     * 2.添加gc log
     * -XX:+PrintGCDetails
     * 3.添加gc记录文件
     * -Xloggc:文件名
     * 4.选择垃圾回收类型
     * -XX:+UseG1GC
     * 5.指定错误日日志的记录
     * -XX:ErrorFile=./hs_err_pid%p.log
     * -XX:ErrorFile=./hs_err_pid.log
     * 示例:
     * -Xms10m
     * -Xmx10m
     * -XX:+UseG1GC
     * -XX:+PrintGCDetails
     * -Xloggc:var\log\gclog.log
     * -XX:+HeapDumpOnOutOfMemoryError
     * -XX:HeapDumpPath=var\log\
     * <p>
     * <p>
     * -verbose:gc
     * -XX:+PrintGCDetails
     * -XX:+PrintGCDateStamps
     * -XX:+PrintGCTimeStamps
     * -XX:+UseGCLogFileRotation
     * -XX:NumberOfGCLogFiles=10
     * -XX:GCLogFileSize=100M
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("start......");
        //Thread.sleep(1000000);
        List<String> list = new ArrayList<>();
        while (true) {
            list.add("Jvm");
        }
    }
}
