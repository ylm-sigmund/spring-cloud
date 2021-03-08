package com.diy.sigmund.mvcframework.v4.beans.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.diy.sigmund.mvcframework.v4.beans.config.SBeanDefinition;

/**
 * @author ylm-sigmund
 * @since 2021/3/7 22:06
 */
public class SBeanDefinitionReader {

    private Properties contextConfig = new Properties();
    private List<String> registerBeanClass = new ArrayList<>();

    public Properties getContextConfig() {
        return contextConfig;
    }

    public SBeanDefinitionReader(String[] contextConfigLocation) {
        doLoadConfig(contextConfigLocation[0]);
        // 扫描配置文件中的配置的相关的类
        doScanner(contextConfig.getProperty("scanPackage"));
    }

    public List<SBeanDefinition> loadBeanDefinitions() {
        List<SBeanDefinition> result = new ArrayList<>();
        try {
            for (String className : registerBeanClass) {
                final Class<?> clazz = Class.forName(className);
                if (clazz.isInterface()) {
                    continue;
                }
                //保存类对应的ClassName（全类名）
                //还有beanName
                //1、默认是类名首字母小写
                result.add(buildBeanDefinition(toLowerFirstCase(clazz.getSimpleName()), clazz.getName()));
                //2、自定义
                //3、接口注入
                for (Class<?> i : clazz.getInterfaces()) {
                    result.add(buildBeanDefinition(i.getName(), clazz.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private SBeanDefinition buildBeanDefinition(String beanName, String beanClassName) {
        final SBeanDefinition beanDefinition = new SBeanDefinition();
        beanDefinition.setFactoryBeanName(beanName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }

    private void doLoadConfig(String contextConfigLocation) {
        try (final InputStream inputStream =
            this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.replaceAll("classpath:", ""));) {
            contextConfig.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // scanPackage=com.diy.sigmund
    private void doScanner(String scanPackage) {
        // jar 、 war 、zip 、rar
        final URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        final File classPath = new File(url.getFile());
        // 当成是一个classPath文件夹
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            }else {
                if (!file.getName().endsWith("class")) {
                    continue;
                }
                // 全类名 = 包名.类名
                String className = (scanPackage + "." + file.getName()).replace(".class", "");
                registerBeanClass.add(className);
            }
        }
    }
    //自己写，自己用
    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
//        if(chars[0] > )
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
