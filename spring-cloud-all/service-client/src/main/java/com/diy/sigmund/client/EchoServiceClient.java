package com.diy.sigmund.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author ylm-sigmund
 * @since 2020/9/5 14:25
 */
@FeignClient("service-provider")
public interface EchoServiceClient {

    @GetMapping("echo/{message}")
    String echo(@PathVariable String message);
}

