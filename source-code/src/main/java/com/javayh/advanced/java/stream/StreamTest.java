package com.javayh.advanced.java.stream;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 *
 * </p>
 *
 * @author Dylan-haiji
 * @version 1.0.0
 * @since 2020-07-17
 */
public class StreamTest
{
    public static void main(String[] args) {

        //java Stream

        // 获取指定元素的流
        Stream<String> stream = Stream.of("S0337", "S0ZOM", "S0ZP1","S0ZOM");

        // 将指定的流转换成集合 Collectors 详细解说请看这里
        List<String> collect = stream.collect(Collectors.toList());
        System.out.println("集合为:"+collect);

        //数据过滤,同if(){}相同
        List<String> s0337 = collect.stream().
                filter("S0337"::equalsIgnoreCase)
                .collect(Collectors.toList());
        System.out.println("数据过滤:"+s0337);

        //去重
        List<String> distinct = collect.stream().distinct().collect(Collectors.toList());
        System.out.println("数据去重:"+distinct);

        //排序
        List<String> sorted = collect.stream().sorted().collect(Collectors.toList());
        System.out.println("数据排序一:"+distinct);

        //根据指定字段排序
        List<StreamData> entity = Stream.of(new StreamData("Yang", "28"),
                new StreamData("Yang", "29"),
                new StreamData("Yang", "21")).collect(Collectors.toList());
        List<StreamData> entitySorted = entity.stream()
                .sorted(Comparator.comparing(StreamData::getCode))
                .collect(Collectors.toList());
        System.out.println("数据排序二:"+entitySorted.toString());

        //map
        List<String> map = collect.stream().map(s -> s.replaceAll("S", "Y")).collect(Collectors.toList());
        List<String> mapEntity = entitySorted.stream().map(StreamData::getCode).collect(Collectors.toList());
        System.out.println("map:"+map);
        System.out.println("mapEntity:"+mapEntity);

        List<String> flatMap = collect.stream().flatMap(s -> Arrays.stream(s.split("0"))).collect(Collectors.toList());
        System.out.println("flatMap:"+flatMap);

        //综合示例
        List<String> stringList = collect
                .stream()
                .distinct()
                .filter("S0337"::equalsIgnoreCase)
                .sorted(Comparator.comparing(String::hashCode))
                .collect(Collectors.toList());
        System.out.println("综合示例:"+stringList);

        //获取集合第一个元素
        Optional<String> first = collect.stream().findFirst();
        System.out.println("第一个元素"+first.get());
        Optional<String> any = collect.stream().findAny();
        System.out.println("第一个元素"+any.get());

        //提前查看数据
        List<String> peek = collect.stream().peek(System.out::println).collect(Collectors.toList());

        //取指定的条数
        List<String> limit = collect.stream().limit(2).collect(Collectors.toList());
        System.out.println("limit:"+limit);

        //返回指定下标后的元素
        List<String> skip = collect.stream().skip(1).collect(Collectors.toList());
        System.out.println("skip:"+skip);

        //获取 size
        //以下代码 等同于 collect.size()
        long count = collect.stream().count();
        System.out.println("size:"+count);

        //最大值 最小值同理
        Optional<String> max = collect.stream().max(Comparator.comparing(s -> s));
        Optional<StreamData> maxEntity = entitySorted.stream().max(Comparator.comparing(StreamData::getCode));
        System.out.println("max值:"+max);
        System.out.println("maxEntity值:"+maxEntity);

    }

}

class StreamData{
    private String name;
    private String code;

    public StreamData(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "{" +
                "name:'" + name + '\'' +
                ", code:'" + code + '\'' +
                '}';
    }
}
