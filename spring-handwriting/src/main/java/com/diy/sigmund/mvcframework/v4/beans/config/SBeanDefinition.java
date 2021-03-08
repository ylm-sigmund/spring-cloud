package com.diy.sigmund.mvcframework.v4.beans.config;

/**
 * @author ylm-sigmund
 * @since 2021/3/7 22:06
 */
public class SBeanDefinition {

    private String factoryBeanName;
    /**
     * 全限定名
     */
    private String beanClassName;

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }
}
