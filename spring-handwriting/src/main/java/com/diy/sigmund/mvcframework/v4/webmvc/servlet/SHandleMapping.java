package com.diy.sigmund.mvcframework.v4.webmvc.servlet;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author ylm-sigmund
 * @since 2021/3/8 11:48
 */
public class SHandleMapping {
    /**
     * URL
     */
    private Pattern pattern;
    /**
     * 对应的Method
     */
    private Method method;
    /**
     * Method对应的实例对象
     */
    private Object controller;

    public SHandleMapping(Pattern pattern, Method method, Object controller) {
        this.pattern = pattern;
        this.method = method;
        this.controller = controller;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SHandleMapping that = (SHandleMapping)o;
        return Objects.equals(pattern, that.pattern) && Objects.equals(method, that.method)
            && Objects.equals(controller, that.controller);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, method, controller);
    }
}
