package com.diy.sigmund.mvcframework.v4.webmvc.servlet;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ylm-sigmund
 * @since 2021/3/8 11:49
 */
public class SView {
    private File viewFile;

    public SView(File templateFile) {
        this.viewFile = templateFile;
    }

    public void render(Map<String, ?> model, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        final StringBuffer sb = new StringBuffer();
        final RandomAccessFile ra = new RandomAccessFile(this.viewFile, "r");

        String line = null;
        while (Objects.nonNull(line = ra.readLine())) {
            line = new String(line.getBytes("ISO-8859-1"), "utf-8");
            final Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                String paramName = matcher.group();
                paramName = paramName.replaceAll("￥\\{|\\}", "");
                final Object paramValue = model.get(paramName);
                line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                // matcher = pattern.matcher(line);
            }
            sb.append(line);
        }
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write(sb.toString());
    }

    private String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*").replace("+", "\\+").replace("|", "\\|").replace("{", "\\{")
            .replace("}", "\\}").replace("(", "\\(").replace(")", "\\)").replace("^", "\\^").replace("$", "\\$")
            .replace("[", "\\[").replace("]", "\\]").replace("?", "\\?").replace(",", "\\,").replace(".", "\\.")
            .replace("&", "\\&");
    }
}
