package com.diy.sigmund.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author ylm
 */
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerConfiguration.class, args);
    }

    @EnableAutoConfiguration
    @EnableEurekaServer
    public static class EurekaServerConfiguration{}
}
