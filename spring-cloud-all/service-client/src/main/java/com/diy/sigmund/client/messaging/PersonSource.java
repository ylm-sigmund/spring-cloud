package com.diy.sigmund.client.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;

/**
 * @author ylm-sigmund
 * @since 2020/9/19 16:09
 */
public interface PersonSource {
    /**
     * Name of the output channel.
     */
    String TOPIC = "person-source";
//    String TOPIC = "gupao";

    /**
     * @return output channel
     */
    @Output(TOPIC)
    MessageChannel output();
}
