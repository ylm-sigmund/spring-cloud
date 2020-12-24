package com.diy.sigmund;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

/**
 * @author ylm-sigmund
 * @since 2020/9/14 20:18
 */
@EnableAutoConfiguration
public class DemoBootstrap {
    /**
     * 不通过 @Qualifier value() 属性来依赖查找
     */
    @Autowired
    private Map<String, String> allStringBeans = Collections.EMPTY_MAP;
    /**
     * 通过 @Qualifier value() 属性来依赖查找
     */
    @Autowired
    // @Qualifier
    @Group
    private Map<String, String> groupStringBeans = Collections.EMPTY_MAP;

    @Autowired
    @Qualifier("a")
    private String aBean;

    @Autowired
    @Qualifier("b")
    private String bBean;

    @Autowired
    @Qualifier("c")
    private String cBean;

    @Bean
    public ApplicationRunner runner() {
        return args -> {
            System.out.println("aBean" + aBean);
            System.out.println("bBean" + bBean);
            System.out.println("cBean" + cBean);
            System.out.println("allStringBeans " + allStringBeans);
            System.out.println("groupStringBeans " + groupStringBeans);
        };
    }

    @Bean
    public String a() {
        return "String-a";
    }

    /**
     * b和c 分组 @Qualifier
     */
    @Bean
    // @Qualifier
    @Group
    public String b() {
        return "String-b";
    }

    @Bean
    // @Qualifier
    @Group
    public String c() {
        return "String-c";
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(DemoBootstrap.class).web(WebApplicationType.NONE).run(args);
        // SpringApplication.run(DemoBootstrap.class, args);
    }
}

/**
 * 自定义注解，元注解 @Qualifier
 * 
 * @author ylm
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Qualifier
@interface Group {

}
