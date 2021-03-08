package com.diy.sigmund.mvcframework.v4.webmvc.servlet;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.diy.sigmund.mvcframework.annotation.SRequestParam;

/**
 * @author ylm-sigmund
 * @since 2021/3/8 11:48
 */
public class SHandlerAdapter {
    public SModelAndView handler(HttpServletRequest req, HttpServletResponse resp, SHandleMapping handleMapping)
        throws Exception {
        // 保存形参列表
        // 将参数名称和参数的位置，这种关系保存起来
        Map<String, Integer> paramIndexMapping = new HashMap<>();

        // 通过运行时的状态去拿到
        final Annotation[][] pa = handleMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation annotation : pa[i]) {
                if (annotation instanceof SRequestParam) {
                    String paramName = ((SRequestParam)annotation).value();
                    if (!Objects.equals("", paramName.trim())) {
                        // String value = Arrays.toString(parameterMap.get(paramName)).replaceAll("\\[|\\]", "")
                        // .replaceAll("\\s+", ",");
                        // paramValues[i] = value;
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        // 初始化
        final Class<?>[] paramTypes = handleMapping.getMethod().getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            final Class<?> parameterType = paramTypes[i];
            if (parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class) {
                paramIndexMapping.put(parameterType.getName(), i);
            }
        }

        // 去拼接实参列表
        // http://localhost/web/query?name=Tom&Cat
        final Map<String, String[]> parameterMap = req.getParameterMap();
        final Object[] paramValues = new Object[paramTypes.length];

        for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
            final String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s+", "");
            if (!paramIndexMapping.containsKey(param.getKey())) {
                continue;
            }
            int index = paramIndexMapping.get(param.getKey());

            paramValues[index] = castStringValue(value, paramTypes[index]);
        }
        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            paramValues[paramIndexMapping.get(HttpServletRequest.class.getName())] = req;
        }
        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            paramValues[paramIndexMapping.get(HttpServletResponse.class.getName())] = resp;
        }
        final Object result = handleMapping.getMethod().invoke(handleMapping.getController(), paramValues);
        if (Objects.isNull(result) || result instanceof Void) {
            return null;
        }
        boolean modelAndView = handleMapping.getMethod().getReturnType() == SModelAndView.class;
        if (modelAndView) {
            return (SModelAndView)result;
        }
        return null;

    }

    private Object castStringValue(String value, Class<?> paramType) {
        if (Objects.isNull(value)) {
            return null;
        }
        if (String.class == paramType) {
            return value;
        } else if (Integer.class == paramType) {
            return Integer.parseInt(value);
        } else if (Double.class == paramType) {
            return Double.parseDouble(value);
        } else {
            return value;
        }
    }
}
