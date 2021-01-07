package com.javayh.advanced.java.redis;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * <p>
 *    自定redis 序列化工具
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-17 4:19 PM
 */
public class RedisObjectSerializer implements RedisSerializer {

    /** 为了方便进行对象与字节数组的转换，所以应该首先准备出两个转换器 */
    private Converter<Object, byte[]> serializingConverter = new SerializingConverter();

    private Converter<byte[], Object> deserializingConverter = new DeserializingConverter();

    /** 初始化数组 */
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /**
     * <p>
     * 序列化
     * </p>
     * @version 1.0.0
     * @author yang-haiji
     * @since 2020/3/4
     * @param obj
     * @return byte[]
     */
    @Override
    public byte[] serialize(Object obj) throws SerializationException {
        /* 这个时候没有要序列化的对象出现，所以返回的字节数组应该就是一个空数组 */
        if (obj == null) {
            return EMPTY_BYTE_ARRAY;
        }
        /* 将对象变为字节数组 */
        return this.serializingConverter.convert(obj);
    }

    /**
     * <p>
     * 反序列化
     * </p>
     * @version 1.0.0
     * @author Yang-haiji
     * @param data
     * @return java.lang.Object
     */
    @Override
    public Object deserialize(byte[] data) throws SerializationException {
        /* 此时没有对象的内容信息 */
        if (data == null || data.length == 0) {
            return null;
        }
        return this.deserializingConverter.convert(data);
    }

}
