package com.diy.sigmund.demo.service.impl;

import java.util.Optional;

import com.diy.sigmund.demo.service.IDemoServcice;
import com.diy.sigmund.mvcframework.annotation.SService;

/**
 * @author ylm-sigmund
 * @since 2021/3/3 20:57
 */
@SService
public class DemoServcice implements IDemoServcice {

    @Override
    public String get(String name) {
        return "my name is " + Optional.ofNullable(name).orElse(null);
    }
}
