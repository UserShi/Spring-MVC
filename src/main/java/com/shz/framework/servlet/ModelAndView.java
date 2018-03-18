package com.shz.framework.servlet;

import java.util.Map;

/**
 * 返回处理结果
 */
public class ModelAndView {
    //页面
    private  String view;
    //返回值
    private Map<String,Object> model;

    public ModelAndView(String view, Map<String, Object> model) {
        this.view = view;
        this.model = model;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
}
