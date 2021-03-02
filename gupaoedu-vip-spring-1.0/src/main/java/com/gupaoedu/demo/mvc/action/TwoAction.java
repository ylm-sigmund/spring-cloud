package com.gupaoedu.demo.mvc.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gupaoedu.demo.service.IDemoService;

//没加注解，控制权不反转，自己管自己
public class TwoAction {
	
	private IDemoService demoService;

	public void edit(HttpServletRequest req,HttpServletResponse resp,
					 String name){
		String result = demoService.get(name);
		try {
			resp.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}