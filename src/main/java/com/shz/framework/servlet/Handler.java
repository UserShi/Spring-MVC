package com.shz.framework.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;
/*保存url与方法得关系映射
* */
public class Handler {
    //对应得类
    private Object controller;
    //对应得方法
    private Method method;
    //url校验
    private Pattern pattern;

    public Handler(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
