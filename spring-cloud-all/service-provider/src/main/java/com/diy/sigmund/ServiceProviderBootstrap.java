package com.diy.sigmund;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author ylm-sigmund
 * @since 2020/9/5 14:12
 */
// @EnableAutoConfiguration
// @ComponentScan
// 以上2个注解可替换掉@SpringBootApplication
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceProviderBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(ServiceProviderBootstrap.class, args);
    }
}
