package com.diy.sigmund.demo.controller;

import com.diy.sigmund.demo.service.IDemoServcice;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



//没加注解，控制权不反转，自己管自己
public class TwoAction {
	
	private IDemoServcice demoService;

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
