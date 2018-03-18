package com.shz.framework.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

/**
 * 方法适配器
 */
public class HandlerAdapter {
    private Map<String,Integer> paraMapping;
    private Handler handler;

    public ModelAndView handle(HttpServletRequest request,HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
        //获取参数类型
        Class<?> [] paramTypes = handler.getMethod().getParameterTypes();
        //具体参数值  根据参数索引 放值
        Object [] paramvlues = new Object[paramTypes.length];
        //获取参数
        Map<String,String []> params =request.getParameterMap();
        params.forEach((paramname,paramvalue)->{
            //获取参数值
            String value = Arrays.toString(paramvalue).
                    replaceAll("\\[|\\]","").
                    replaceAll(",\\s",",");
            if (paraMapping.containsKey(paramname)){
                int index = paraMapping.get(paramname);
                paramvlues[index] = castObject(value,paramTypes[index]);
            }
        });
        //request response 赋值
        String reqname = HttpServletRequest.class.getName();
        if(paraMapping.containsKey(reqname)){
            int index = paraMapping.get(reqname);
            paramvlues [index] = request;
        }
        String resname = HttpServletResponse.class.getName();
        if(paraMapping.containsKey(resname)){
            int index = paraMapping.get(resname);
            paramvlues [index] = response;
        }
        boolean isModelAndView =  handler.getMethod().getReturnType() ==ModelAndView.class;
        Object r = handler.getMethod().invoke(handler.getController(),paramvlues);
        if(isModelAndView){
            return  (ModelAndView) r;
        }
         return null;
    }
    //参数类型转换  这里写个简单的
    private Object  castObject(String  values,Class<?> clazz){
        if(clazz == String.class){
            return values;
        }else if(clazz == Integer.class){
            return Integer.valueOf(values);
        }
      return null;
    }
    public HandlerAdapter(Map<String, Integer> paraMapping, Handler handler) {
        this.paraMapping = paraMapping;
        this.handler = handler;
    }

    public Map<String, Integer> getParaMapping() {
        return paraMapping;
    }

    public void setParaMapping(Map<String, Integer> paraMapping) {
        this.paraMapping = paraMapping;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

}
