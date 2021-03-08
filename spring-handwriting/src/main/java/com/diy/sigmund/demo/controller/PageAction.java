package com.diy.sigmund.demo.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.diy.sigmund.demo.service.IQueryService;
import com.diy.sigmund.mvcframework.annotation.SAutowired;
import com.diy.sigmund.mvcframework.annotation.SController;
import com.diy.sigmund.mvcframework.annotation.SRequestMapping;
import com.diy.sigmund.mvcframework.annotation.SRequestParam;
import com.diy.sigmund.mvcframework.v4.webmvc.servlet.SModelAndView;

/**
 * 公布接口url
 * 
 * @author Tom
 *
 */
@SController
@SRequestMapping("/")
public class PageAction {

    @SAutowired
    IQueryService queryService;

    @SRequestMapping("/first.html")
    public SModelAndView query(@SRequestParam("teacher") String teacher) {
        teacher = Optional.ofNullable(teacher).orElse(" 请填写teacher字段 ");
        String result = queryService.query(teacher);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new SModelAndView("first.html", model);
    }

}
