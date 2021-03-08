package com.diy.sigmund.mvcframework.v4.webmvc.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.diy.sigmund.mvcframework.annotation.SController;
import com.diy.sigmund.mvcframework.annotation.SRequestMapping;
import com.diy.sigmund.mvcframework.v4.context.SApplicationContext;

/**
 * 委派模式
 * 
 * 职责：负责任务调度，请求分发
 * 
 * @author ylm-sigmund
 * @since 2021/3/3 21:07
 */
public class SDispatcherServletV4 extends HttpServlet {

    private SApplicationContext applicationContext;
    private List<SHandleMapping> handlerMappings = new ArrayList<>();
    private Map<SHandleMapping, SHandlerAdapter> handlerAdapters = new HashMap<>();
    private List<SViewResolver> viewResolvers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispatch(req, resp);
        } catch (Exception e) {
            try {
                processDispatchResult(req, resp, new SModelAndView("500"));
            } catch (Exception ex) {
                ex.printStackTrace();
                resp.getWriter().write("500 Exception,Detail : " + Arrays.toString(e.getStackTrace()));
            }
            e.printStackTrace();
        }
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, SModelAndView mv)
        throws Exception {
        if (Objects.isNull(mv)) {
            return;
        }
        if (this.viewResolvers.isEmpty()) {
            return;
        }
        for (SViewResolver viewResolver : this.viewResolvers) {
            final SView view = viewResolver.resolveViewName(mv.getViewName());
            view.render(mv.getModel(), req, resp);
            return;
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // 完成了对HandlerMapping的封装
        // 完成了对方法返回值的封装ModelAndView

        // 1、通过URL获得一个HandlerMapping
        SHandleMapping handleMapping = gethandler(req);
        if (Objects.isNull(handleMapping)) {
            processDispatchResult(req, resp, new SModelAndView("404"));
            return;
        }

        // 2、根据一个HandlerMaping获得一个HandlerAdapter
        SHandlerAdapter handlerAdapter = gethandlerAdapter(handleMapping);
        // 3、解析某一个方法的形参和返回值之后，统一封装为ModelAndView对象
        final SModelAndView mv = handlerAdapter.handler(req, resp, handleMapping);

        // 就把ModelAndView变成一个ViewResolver
        processDispatchResult(req, resp, mv);
    }

    private SHandlerAdapter gethandlerAdapter(SHandleMapping handleMapping) {
        if (this.handlerAdapters.isEmpty()) {
            return null;
        }
        return handlerAdapters.get(handleMapping);
    }

    private SHandleMapping gethandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }
        String requestURI = req.getRequestURI();
        final String contextPath = req.getContextPath();
        requestURI = requestURI.replaceAll(contextPath, "").replaceAll("/+", "/");
        for (SHandleMapping handlerMapping : handlerMappings) {
            if (!handlerMapping.getPattern().matcher(requestURI).matches()) {
                continue;
            }
            return handlerMapping;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 初始化Spring核心IoC容器
        applicationContext = new SApplicationContext(config.getInitParameter("contextConfigLocation"));

        // 完成了IoC、DI和MVC部分对接

        // 初始化九大组件
        initStrategies(applicationContext);

        System.out.println("Sigmund Spring framework is init.");
    }

    private void initStrategies(SApplicationContext context) {
        // //多文件上传的组件
        // initMultipartResolver(context);
        // //初始化本地语言环境
        // initLocaleResolver(context);
        // //初始化模板处理器
        // initThemeResolver(context);
        // handlerMapping
        initHandlerMappings(context);
        // 初始化参数适配器
        initHandlerAdapters(context);
        // //初始化异常拦截器
        // initHandlerExceptionResolvers(context);
        // //初始化视图预处理器
        // initRequestToViewNameTranslator(context);
        // 初始化视图转换器
        initViewResolvers(context);
        // //FlashMap管理器
        // initFlashMapManager(context);
    }

    private void initViewResolvers(SApplicationContext context) {
        final String templateRoot = context.getContextConfig().getProperty("templateRoot");
        final String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        final File templateRootDir = new File(templateRootPath);
        for (File file : Objects.requireNonNull(templateRootDir.listFiles())) {
            // todo file变量未使用
            this.viewResolvers.add(new SViewResolver(templateRoot));
        }

    }

    private void initHandlerAdapters(SApplicationContext context) {
        for (SHandleMapping handlerMapping : handlerMappings) {
            handlerAdapters.put(handlerMapping, new SHandlerAdapter());
        }
    }

    private void initHandlerMappings(SApplicationContext context) {
        if (this.applicationContext.getBeanDefinitionCount() == 0) {
            return;
        }
        for (String beanName : this.applicationContext.getBeanDefinitionNames()) {
            final Object instance = this.applicationContext.getBean(beanName);
            final Class<?> clazz = instance.getClass();
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
                String regex = ("/" + baseUrl + sRequestMapping.value().replaceAll("\\*", ".*")).replaceAll("/+", "/");
                final Pattern pattern = Pattern.compile(regex);
                // handlerMapping.put(url, method);
                handlerMappings.add(new SHandleMapping(pattern, method, instance));
                // 之所以打印2遍重复的HandlerMapping，是因为applicationContext put了接口全限定名和simpleName
                System.out.println("HandlerMapping " + pattern + "=" + method);
            }

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

}
