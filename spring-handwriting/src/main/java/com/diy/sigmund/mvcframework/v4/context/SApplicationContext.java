package com.diy.sigmund.mvcframework.v4.context;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import com.diy.sigmund.mvcframework.annotation.SAutowired;
import com.diy.sigmund.mvcframework.annotation.SController;
import com.diy.sigmund.mvcframework.annotation.SService;
import com.diy.sigmund.mvcframework.v4.beans.SBeanWrapper;
import com.diy.sigmund.mvcframework.v4.beans.config.SBeanDefinition;
import com.diy.sigmund.mvcframework.v4.beans.support.SBeanDefinitionReader;

/**
 * @author ylm-sigmund
 * @since 2021/3/7 22:07
 */
public class SApplicationContext {

    private SBeanDefinitionReader reader;

    private Map<String, SBeanDefinition> beanDefinitionMap = new HashMap<>();
    private Map<String, SBeanWrapper> factoryBeanInstanceCache = new HashMap<>();
    private Map<String, Object> factoryBeanObjectCache = new HashMap<>();

    public SApplicationContext(String... contextConfigLocation) {
        // 1、加载配置文件
        reader = new SBeanDefinitionReader(contextConfigLocation);
        try {
            // 2、解析配置文件，封装成BeanDefinition
            final List<SBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

            // 3、把BeanDefintion缓存起来
            doRegisterBeanDefinition(beanDefinitions);

            doAutowired();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doRegisterBeanDefinition(List<SBeanDefinition> beanDefinitions) throws Exception {
        for (SBeanDefinition beanDefinition : beanDefinitions) {
            if (this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception(beanDefinition.getFactoryBeanName() + " is exist");
            }
            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
            beanDefinitionMap.put(beanDefinition.getBeanClassName(), beanDefinition);
        }
    }

    private void doAutowired() {
        // 调用getBean()
        // 这一步，所有的Bean并没有真正的实例化，还只是配置阶段
        for (Map.Entry<String, SBeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
            final String className = beanDefinitionEntry.getKey();
            getBean(className);
        }
    }

    /**
     * Bean的实例化，DI是从而这个方法开始的
     * 
     * @param beanName
     *            beanName
     */
    public Object getBean(String beanName) {
        // 1、先拿到BeanDefinition配置信息
        final SBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        // 2、反射实例化newInstance();
        Object instance = instantiateBean(beanName, beanDefinition);
        // 3、封装成一个BeanWrapper
        final SBeanWrapper beanWrapper = new SBeanWrapper(instance);
        // 4、保存到IoC容器
        factoryBeanInstanceCache.put(beanName, beanWrapper);
        // 5、执行依赖注入
        populateBean(beanName, beanDefinition, beanWrapper);
        return beanWrapper.getWrapperInstance();

    }

    private void populateBean(String beanName, SBeanDefinition beanDefinition, SBeanWrapper beanWrapper) {
        // 可能涉及到循环依赖？
        // A{ B b}
        // B{ A b}
        // 用两个缓存，循环两次
        // 1、把第一次读取结果为空的BeanDefinition存到第一个缓存
        // 2、等第一次循环之后，第二次循环再检查第一次的缓存，再进行赋值
        final Object instance = beanWrapper.getWrapperInstance();
        final Class<?> clazz = beanWrapper.getWrappedClass();

        // 在Spring中@Component
        if (!(clazz.isAnnotationPresent(SController.class) || clazz.isAnnotationPresent(SService.class))) {
            return;
        }

        // 把所有的包括private/protected/default/public 修饰字段都取出来
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(SAutowired.class)) {
                continue;
            }
            final SAutowired sAutowired = field.getAnnotation(SAutowired.class);
            // 如果用户没有自定义的beanName，就默认根据类型注入
            String autowiredBeanName = sAutowired.value().trim();
            if (Objects.equals("", autowiredBeanName)) {
                // com.diy.sigmund.demo.service.IDemoServcice
                autowiredBeanName = field.getType().getName();
            }
            // 暴力访问
            field.setAccessible(true);

            try {
                if (Objects.isNull(this.factoryBeanInstanceCache.get(autowiredBeanName))) {
                    continue;
                }
                // 给该实例设置值，ioc.get(beanName) 相当于通过接口的全名拿到接口的实现的实例
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Object instantiateBean(String beanName, SBeanDefinition beanDefinition) {
        final String className = beanDefinition.getBeanClassName();
        Object instance = null;
        try {
            if (this.factoryBeanObjectCache.containsKey(beanName)) {
                instance = this.factoryBeanObjectCache.get(beanName);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                // ==================AOP开始=========================

                // ==================AOP结束=========================

                this.factoryBeanObjectCache.put(beanName, instance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    private String toLowerFirstCase(String name) {
        final char[] chars = name.toCharArray();
        // 之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        // 在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public Object geBean(Class beanClass) {
        return getBean(beanClass.getName());
    }

    public Properties getContextConfig(){
        return this.reader.getContextConfig();
    }
}
