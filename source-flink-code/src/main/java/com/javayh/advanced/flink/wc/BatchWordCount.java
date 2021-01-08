package com.javayh.advanced.flink.wc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * <p>
 * 批处理的demo
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-07
 */
public class BatchWordCount {

    public static void main(String[] args) throws Exception {
        //创建执行环境
        ExecutionEnvironment env = ExecutionEnvironment.createCollectionsEnvironment();
        //从文件中读取数据
        // 这里写成项目的根路径 如： C:\Advanced-books\source-flink-code\src\main\resources\flink.txt
        String inputPath = "\\Advanced-books\\source-flink-code\\src\\main\\resources\\flink.txt";
        DataSource<String> input = env.readTextFile(inputPath);
        //进行数据统计
        FlatMapOperator<String, Tuple2<String, Integer>> map = input.flatMap(new MyFlatMapper());
        //进行输出
        map.print();


    }

    public static class MyFlatMapper implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            String[] words = value.split(" ");
            for (String word : words) {
                out.collect(new Tuple2<>(word, 1));
            }
        }
    }

}
