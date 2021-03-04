package com.diy.sigmund.demo.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.diy.sigmund.demo.service.IDemoServcice;
import com.diy.sigmund.mvcframework.annotation.SAutowired;
import com.diy.sigmund.mvcframework.annotation.SController;
import com.diy.sigmund.mvcframework.annotation.SRequestMapping;
import com.diy.sigmund.mvcframework.annotation.SRequestParam;

// 虽然，用法一样，但是没有功能
@SController
@SRequestMapping("/demo")
public class DemoAction {

    @SAutowired
    private IDemoServcice demoService;

    @SRequestMapping("/query")
    public void query(HttpServletRequest req, HttpServletResponse resp, @SRequestParam("name") String name) {
        String result = demoService.get(name);
        // String result = "My name is " + name;
        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SRequestMapping("/add")
    public void add(HttpServletRequest req, HttpServletResponse resp, @SRequestParam("a") Integer a,
        @SRequestParam("b") Integer b) {
        try {
            resp.getWriter().write(a + "+" + b + "=" + (a + b));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SRequestMapping("/sub")
    public void add(HttpServletRequest req, HttpServletResponse resp, @SRequestParam("a") Double a,
        @SRequestParam("b") Double b) {
        try {
            resp.getWriter().write(a + "-" + b + "=" + (a - b));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SRequestMapping("/remove")
    public String remove(@SRequestParam("id") Integer id) {
        return "" + id;
    }

}
