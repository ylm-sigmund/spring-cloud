package com.diy.sigmund.client.kafka;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ylm-sigmund
 * @since 2020/9/19 14:28
 */
public class ObjectSerializer implements Serializer<Serializable> {
    private Logger logger = LoggerFactory.getLogger(ObjectSerializer.class);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String s, Serializable serializable) {
        return new byte[0];
    }

    @Override
    public byte[] serialize(String topic, Headers headers, Serializable data) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] dataArray = null;
        try (ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
            // 将对象写入到ObjectOutputStream
            oos.writeObject(data);
            // 将写入后的数据，通过字节数组方式获取
            dataArray = outputStream.toByteArray();
        } catch (IOException e) {
            logger.error("IOException", e);
        }

        logger.info("当前 Topic ：{},序列化对象：{}", topic, data);
        return dataArray;
    }

    @Override
    public void close() {

    }
}
