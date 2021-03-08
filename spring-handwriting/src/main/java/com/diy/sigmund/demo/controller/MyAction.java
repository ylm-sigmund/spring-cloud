package com.diy.sigmund.demo.controller;

import com.diy.sigmund.demo.service.IModifyService;
import com.diy.sigmund.demo.service.IQueryService;
import com.diy.sigmund.mvcframework.annotation.SAutowired;
import com.diy.sigmund.mvcframework.annotation.SController;
import com.diy.sigmund.mvcframework.annotation.SRequestMapping;
import com.diy.sigmund.mvcframework.annotation.SRequestParam;
import com.diy.sigmund.mvcframework.v4.webmvc.servlet.SModelAndView;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 公布接口url
 * @author Tom
 *
 */
@SController
@SRequestMapping("/web")
public class MyAction {

	@SAutowired
	IQueryService queryService;
	@SAutowired
	IModifyService modifyService;

	@SRequestMapping("/query.json")
	public SModelAndView query(HttpServletRequest request, HttpServletResponse response,
							   @SRequestParam("name") String name){
		String result = queryService.query(name);
		return out(response,result);
	}
	
	@SRequestMapping("/add*.json")
	public SModelAndView add(HttpServletRequest request,HttpServletResponse response,
			   @SRequestParam("name") String name,@SRequestParam("addr") String addr){
		try {
			String result = modifyService.add(name, addr);
			return out(response,result);
		}catch (Throwable e){
			Map<String,String> model = new HashMap<>();
			model.put("detail",e.getMessage());
			model.put("stackTrace", Arrays.toString(e.getStackTrace()));
			return new SModelAndView("500",model);
		}
	}
	
	@SRequestMapping("/remove.json")
	public SModelAndView remove(HttpServletRequest request, HttpServletResponse response,
								 @SRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		return out(response,result);
	}
	
	@SRequestMapping("/edit.json")
	public SModelAndView edit(HttpServletRequest request,HttpServletResponse response,
			@SRequestParam("id") Integer id,
			@SRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		return out(response,result);
	}
	
	
	
	private SModelAndView out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
