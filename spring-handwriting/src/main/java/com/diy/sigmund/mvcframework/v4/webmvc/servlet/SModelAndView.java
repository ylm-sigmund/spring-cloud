package com.diy.sigmund.mvcframework.v4.webmvc.servlet;

import java.util.Map;

/**
 * @author ylm-sigmund
 * @since 2021/3/8 11:49
 */
public class SModelAndView {
    private String viewName;
    private Map<String,?> model;

    public SModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public SModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }


    public Map<String, ?> getModel() {
        return model;
    }

}
