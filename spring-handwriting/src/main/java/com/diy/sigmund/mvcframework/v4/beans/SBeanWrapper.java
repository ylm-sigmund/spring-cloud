package com.diy.sigmund.mvcframework.v4.beans;

/**
 * @author ylm-sigmund
 * @since 2021/3/7 22:06
 */
public class SBeanWrapper {

    private Object wrapperInstance;
    private Class<?> wrappedClass;

    public SBeanWrapper(Object instance) {
        this.wrapperInstance = instance;
        this.wrappedClass = instance.getClass();
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public Class<?> getWrappedClass() {
        return wrappedClass;
    }
}
