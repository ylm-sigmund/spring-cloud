package com.diy.sigmund.client.kafka;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ylm-sigmund
 * @since 2020/9/19 15:30
 */
public class ObjectDeserializer implements Deserializer<Serializable> {
    private Logger logger = LoggerFactory.getLogger(ObjectDeserializer.class);

    /**
     * Configure this class.
     *
     * @param configs
     *            configs in key/value pairs
     * @param isKey
     *            whether is for key or value
     */
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        logger.info(configs.toString());
    }

    /**
     * Deserialize a record value from a byte array into a value or object.
     *
     * @param topic
     *            topic associated with the data
     * @param data
     *            serialized bytes; may be null; implementations are recommended to handle null by returning a value or
     *            null rather than throwing an exception.
     * @return deserialized typed data; may be null
     */
    @Override
    public Serializable deserialize(String topic, byte[] data) {
        return null;
    }

    /**
     * Deserialize a record value from a byte array into a value or object.
     *
     * @param topic
     *            topic associated with the data
     * @param headers
     *            headers associated with the record; may be empty.
     * @param data
     *            serialized bytes; may be null; implementations are recommended to handle null by returning a value or
     *            null rather than throwing an exception.
     * @return deserialized typed data; may be null
     */
    @Override
    public Serializable deserialize(String topic, Headers headers, byte[] data) {
        Serializable object = null;

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        try (final ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            object = (Serializable)objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Exception", e);
        }
        logger.info("当前 Topic ：{},反序列化对象：{}", topic, object);
        return object;
    }

    /**
     * Close this deserializer.
     * <p>
     * This method must be idempotent as it may be called multiple times.
     */
    @Override
    public void close() {

    }
}
