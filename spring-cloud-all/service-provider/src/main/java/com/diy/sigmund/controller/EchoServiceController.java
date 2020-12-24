package com.diy.sigmund.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ylm-sigmund
 * @since 2020/9/5 14:10
 */
@RestController
public class EchoServiceController {
    /**
     * 采用随机端口的时候${server.port}获取的是0,无法满足要求 @Value("${server.port}")
     * 
     * @LocalServerPort 外部化配置其实是有点不靠谱的,它并非完全静态,也不一定及时返回
     */
    // @LocalServerPort
    // private int port;

    @Autowired
    private Environment environment;

    /*private final Environment environment;
    
    public EchoServiceController(Environment environment) {
        this.environment = environment;FeignClientsRegistrar
    }*/

    /**
     * 技巧以解决 Caused by: java.lang.IllegalArgumentException: Could not resolve placeholder 'local.server.port' in value
     * "${local.server.port}"
     * 
     * @return
     */
    String getPort() {
        return environment.getProperty("local.server.port");
    }

    @GetMapping("echo/{message}")
    public String echo(@PathVariable String message) {
        return "[ECHO " + getPort() + "] " + message;
    }
}
