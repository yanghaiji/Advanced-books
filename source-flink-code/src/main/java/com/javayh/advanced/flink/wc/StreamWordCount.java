package com.javayh.advanced.flink.wc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * <p>
 * 流处理
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-07
 */
public class StreamWordCount {

    public static void main(String[] args) throws Exception{
        //创建流处理执行环境
        StreamExecutionEnvironment env=StreamExecutionEnvironment.getExecutionEnvironment();
        //设置并行度,默认CPU核数
        env.setMaxParallelism(8);
        //读取数据
        DataStream<String> input =
                env.readTextFile("C:\\Dylan\\Advanced-books\\source-flink-code\\src\\main\\resources\\flink.txt");
//        ParameterTool tool = ParameterTool.fromArgs(args);
//        String host = tool.get("host");
//        int port = tool.getInt("port");
//        DataStream<String> input = env.socketTextStream(host, port);
        //统计数据
        DataStream<Tuple2<String,Integer>> res= input.flatMap(new BatchWordCount.MyFlatMapper())
                .keyBy(0).sum(1);
        //输出打印
        res.print().setParallelism(1);
        //执行
        env.execute();
    }
}
