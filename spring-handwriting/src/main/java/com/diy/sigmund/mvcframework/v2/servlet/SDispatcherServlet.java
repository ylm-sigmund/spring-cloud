package com.diy.sigmund.mvcframework.v2.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.diy.sigmund.mvcframework.annotation.SAutowired;
import com.diy.sigmund.mvcframework.annotation.SController;
import com.diy.sigmund.mvcframework.annotation.SRequestMapping;
import com.diy.sigmund.mvcframework.annotation.SRequestParam;
import com.diy.sigmund.mvcframework.annotation.SService;

/**
 * @author ylm-sigmund
 * @since 2021/3/3 21:07
 */
public class SDispatcherServlet extends HttpServlet {
    /**
     * application.properties配置文件数据
     */
    private Properties contextConfig = new Properties();
    /**
     * 享元模式，缓存
     */
    private List<String> classNames = new ArrayList<>();
    /**
     * IoC容器，key默认是类名首字母小写，value就是对应的实例对象
     */
    private Map<String, Object> ioc = new HashMap<String, Object>();

    /**
     * 存储url和方法的映射关系
     */
    private Map<String, Method> handlerMapping = new HashMap<String, Method>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Exception,Detail : " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String url = req.getRequestURI();// /demo/query
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");
        if (!handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 Not found");
            return;
        }
        final Map<String, String[]> parameterMap = req.getParameterMap();
        final Method method = this.handlerMapping.get(url);

        // 获取形参列表
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Object[] paramValues = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            final Class<?> parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class) {
                paramValues[i] = req;
            } else if (parameterType == HttpServletResponse.class) {
                paramValues[i] = resp;
            } else if (parameterType == String.class) {
                final Annotation[][] pa = method.getParameterAnnotations();
                for (int j = 0; j < pa.length; j++) {
                    for (Annotation annotation : pa[i]) {
                        if (annotation instanceof SRequestParam) {
                            String paramName = ((SRequestParam)annotation).value();
                            if (!Objects.equals("", paramName.trim())) {
                                String value = Arrays.toString(parameterMap.get(paramName)).replaceAll("\\[|\\]", "")
                                    .replaceAll("\\s+", ",");
                                paramValues[i] = value;
                            }
                        }
                    }
                }
            }
        }
        final String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
        method.invoke(ioc.get(beanName), paramValues);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 1、加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        // 2、扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));
        // ==============IoC部分==============
        // 3、初始化IoC容器，将扫描到的相关的类实例化，保存到IOC容器中
        doInstance();
        // AOP，新生成的代理对象
        // ==============DI部分==============
        // 4、完成依赖注入
        doAutowired();
        // ==============MVC部分==============
        // 5、初始化HandlerMapping
        doInitHandlerMapping();
        System.out.println("Sigmund Spring framework is init.");
    }

    private void doInitHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            final Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(SController.class)) {
                continue;
            }
            // 相当于提取 class上配置的url
            String baseUrl = "";
            if (clazz.isAnnotationPresent(SRequestMapping.class)) {
                baseUrl = clazz.getAnnotation(SRequestMapping.class).value();
            }

            // 只获取public的方法
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(SRequestMapping.class)) {
                    continue;
                }
                // 提取每个方法上面配置的url
                final SRequestMapping sRequestMapping = method.getAnnotation(SRequestMapping.class);
                String url = ("/" + baseUrl + sRequestMapping.value()).replaceAll("/+", "/");
                handlerMapping.put(url, method);
                System.out.println("HandlerMapping " + url + "=" + method);
            }

        }
    }

    private void doAutowired() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            final Object instance = entry.getValue();
            // 把所有的包括private/protected/default/public 修饰字段都取出来
            for (Field field : instance.getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(SAutowired.class)) {
                    continue;
                }
                final SAutowired sAutowired = field.getAnnotation(SAutowired.class);
                String beanName = sAutowired.value().trim();
                if (Objects.equals("", beanName)) {
                    // com.diy.sigmund.demo.service.IDemoServcice
                    beanName = field.getType().getName();
                }
                // 暴力访问
                field.setAccessible(true);

                try {
                    // 给该实例设置值，ioc.get(beanName) 相当于通过接口的全名拿到接口的实现的实例
                    field.set(instance, ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void doInstance() {
        if (classNames.isEmpty()) {
            return;
        }
        try {
            for (String className : classNames) {
                final Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(SController.class)) {
                    String beanName = toLowerFirstCase(clazz.getSimpleName());
                    ioc.put(beanName, clazz.newInstance());
                } else if (clazz.isAnnotationPresent(SService.class)) {
                    String beanName = clazz.getAnnotation(SService.class).value();
                    if (Objects.equals("", beanName.trim())) {
                        beanName = toLowerFirstCase(clazz.getSimpleName());
                    }
                    final Object instance = clazz.newInstance();
                    ioc.put(beanName, instance);

                    // 如果是接口
                    // 判断有多少个实现类，如果只有一个，默认就选择这个实现类
                    // 如果有多个，只能抛异常
                    for (Class<?> i : clazz.getInterfaces()) {
                        if (ioc.containsKey(i.getName())) {
                            throw new Exception(i.getName() + "is exist!");
                        }
                        // 该行代码会报错InstantiationException
                        // ioc.put(i.getName(), i.newInstance());
                        ioc.put(i.getName(), instance);
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String toLowerFirstCase(String name) {
        final char[] chars = name.toCharArray();
        // 之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        // 在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }

    // scanPackage=com.diy.sigmund
    private void doScanner(String scanPackage) {
        final URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        final File classPath = new File(url.getFile());
        // 当成是一个classPath文件夹
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            }
            if (!file.getName().endsWith("class")) {
                continue;
            }
            // 全类名 = 包名.类名
            String className = scanPackage + "." + file.getName().replace(".class", "");
            classNames.add(className);
        }
    }

    private void doLoadConfig(String contextConfigLocation) {
        try (final InputStream inputStream =
            this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);) {
            contextConfig.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
