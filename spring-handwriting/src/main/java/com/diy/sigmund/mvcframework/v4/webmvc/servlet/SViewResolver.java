package com.diy.sigmund.mvcframework.v4.webmvc.servlet;

import java.io.File;
import java.util.Objects;

/**
 * @author ylm-sigmund
 * @since 2021/3/8 11:49
 */
public class SViewResolver {
    private static final String DEFAULT_TEMPLATE_PATH=".html";
    private File templateRootDir;

    public SViewResolver(String templateRoot) {
        final String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }

    public SView resolveViewName(String viewName){
        if (Objects.isNull(viewName) || "".equals(viewName.trim())){
            return null;
        }
        viewName=viewName.endsWith(DEFAULT_TEMPLATE_PATH)?viewName:viewName+DEFAULT_TEMPLATE_PATH;
        final File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new SView(templateFile);

    }
}
