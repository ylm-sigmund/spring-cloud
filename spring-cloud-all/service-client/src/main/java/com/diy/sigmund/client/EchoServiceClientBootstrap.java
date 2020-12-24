package com.diy.sigmund.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.diy.sigmund.client.domain.Person;
import com.diy.sigmund.client.kafka.ObjectSerializer;
import com.diy.sigmund.client.messaging.PersonSink;
import com.diy.sigmund.client.messaging.PersonSource;

/**
 * @author ylm-sigmund
 * @since 2020/9/5 14:22
 */
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableFeignClients
@RestController
@EnableBinding({Source.class, PersonSource.class, PersonSink.class})
public class EchoServiceClientBootstrap {
    private Logger logger = LoggerFactory.getLogger(ObjectSerializer.class);
    private final EchoServiceClient echoServiceClient;

    // @LoadBalanced
    // private final RestTemplate restTemplate;

    private KafkaTemplate<String, Object> kafkaTemplate;

    private final Source source;
    private final PersonSource personSource;
    private final PersonSink personSink;

    @Bean
    public ObjectSerializer objectSerializer(){
        return new ObjectSerializer();
    }


    public EchoServiceClientBootstrap(EchoServiceClient echoServiceClient, // RestTemplate restTemplate,
        KafkaTemplate<String, Object> kafkaTemplate, Source source, PersonSource personSource, PersonSink personSink) {
        this.echoServiceClient = echoServiceClient;
        // this.restTemplate = restTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.source = source;
        this.personSource = personSource;
        this.personSink = personSink;
    }

    /**
     * 发送 Kafka 消息
     * 
     * @param name
     * @return
     */
    @GetMapping("person")
    public Person person(String name) {
        Person person = getPerson(name);
        // kafka发送的是原始对象，是什么就是什么
        kafkaTemplate.send("gupao", person);
        return person;
    }

    /**
     * 当前 Topic ：output,序列化对象：[123, 34, 105,
     * 
     * @param name
     * @return
     */
    @GetMapping("stream/person")
    public Person streamPerson(String name) {
        Person person = getPerson(name);
        final MessageChannel output = source.output();
        // 发送的是对象变成的json
        output.send(MessageBuilder.withPayload(person).build());
        return person;
    }

    @GetMapping("stream/person/source")
    public Person streamPersonSource(String name) {
        Person person = getPerson(name);
        final MessageChannel output = personSource.output();
        final MessageBuilder<Person> messageBuilder =
            MessageBuilder.withPayload(person).setHeader("Content-Type", "java/pojo");
        output.send(messageBuilder.build());
        return person;
    }

    /**
     * 通过 org.springframework.messaging API 监听数据
     * 
     * @return
     */
    @Bean
    public ApplicationRunner runner() {
        return args -> {
            personSink.channel().subscribe(new MessageHandler() {
                @Override
                public void handleMessage(Message<?> message) throws MessagingException {
                    final MessageHeaders headers = message.getHeaders();
                    final String contentType = headers.get("Content-Type", String.class);
                    final Object object = message.getPayload();

                    logger.info("消息主体[{}]，头信息：{}", object, headers);
                }
            });
        };
    }

    /**
     * 通过注解方式监听数据
     * 
     * @param person
     */

    @StreamListener("gupao")
    public void listenFromStream(Person person) {
        logger.info("listen topic: gupao data {}", person);
    }

    // @KafkaListener(topics = "gupao")
    // public void listen(Person person) {
    // logger.info("listen topic: gupao data {}", person);
    // }

    @GetMapping("call/echo/{message}")
    public String callEcho(@PathVariable String message) {
        return echoServiceClient.echo(message);
    }

    private Person getPerson(String name) {
        Person person = new Person();
        person.setId(System.currentTimeMillis());
        person.setName(name);
        return person;
    }

    // @LoadBalanced
    // @Bean
    // public RestTemplate restTemplate() {
    // return new RestTemplate();
    // }

    public static void main(String[] args) {
        SpringApplication.run(EchoServiceClientBootstrap.class, args);
    }
}
