package com.diy.sigmund.client.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author ylm-sigmund
 * @since 2020/9/19 16:50
 */
public interface PersonSink {
    /**
     * Input channel name.
     */
    String TOPIC = "person-sink";
//    String TOPIC = "gupao";

    /**
     * @return input channel.
     */
    @Input(TOPIC)
    SubscribableChannel channel();
}
