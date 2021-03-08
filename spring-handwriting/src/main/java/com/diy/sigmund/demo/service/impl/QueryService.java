package com.diy.sigmund.demo.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.diy.sigmund.demo.service.IQueryService;
import com.diy.sigmund.mvcframework.annotation.SService;

/**
 * 查询业务
 * 
 * @author Tom
 *
 */
@SService
// @Slf4j
public class QueryService implements IQueryService {

    /**
     * 查询
     */
    @Override
	public String query(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
        System.out.println("这是在业务方法中打印的：" + json);
        return json;
    }

}
