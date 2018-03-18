package com.shz.framework.servlet;

import com.shz.framework.annotation.ShzController;
import com.shz.framework.annotation.ShzRequestMapping;
import com.shz.framework.annotation.ShzRequestParam;
import com.shz.framework.context.ShzApplicationContext;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javafx.scene.input.KeyCode.O;

public class ShzDispatcherServlet extends HttpServlet{

    private static  final  String Location ="contextConfigLocation";
    //保存url与方法的映射
    private List<Handler>  handlerMapping = new ArrayList<Handler>();
    //
    private List<HandlerAdapter>  adapterMapping = new ArrayList<HandlerAdapter>();
    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化iIOC容器
        ShzApplicationContext context = new ShzApplicationContext(config.getInitParameter(Location));
        //下面初始化url与方法映射  解析url与 method得关系

        //流程按照spring得流程来
        //请求解析
        initMultipartResolver(context);
        //多语言、国际化
        initLocaleResolver(context);
        //主题View层的
        initThemeResolver(context);

        //============== 重要   暂时先实现这两个重要得方法 先让造得轮子能跑起来================
        //解析url和Method的关联关系
        initHandlerMappings(context);
        //适配器（匹配的过程） 方法参数匹配
        initHandlerAdapters(context);
        //============== 重要 ================


        //异常解析
        initHandlerExceptionResolvers(context);
        //视图转发（根据视图名字匹配到一个具体模板）
        initRequestToViewNameTranslator(context);

        //解析模板中的内容（拿到服务器传过来的数据，生成HTML代码）
        initViewResolvers(context);

        initFlashMapManager(context);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            resp.getWriter().write("请求有误");
            e.printStackTrace();
        }
    }

    //请求解析
    private void initMultipartResolver(ShzApplicationContext context){   }
    //多语言、国际化
    private void  initLocaleResolver(ShzApplicationContext context){};
    //主题View层的
    private void  initThemeResolver(ShzApplicationContext context){};

    //============== 重要 ================
    //解析url和Method的关联关系
    private void initHandlerMappings(ShzApplicationContext  context){
            Map<String,Object> ioc = context.getAll();
            //如果ioc容器中没有实例就返回
            if(ioc.isEmpty()){
                return;
            }
            /*遍历查找Controller修饰的类，并解析方法*/
           /* for (Map.Entry<String,Object> entry:
              ioc.entrySet()) {
           }*/
             ioc.forEach((name,value)->{
                    Class<?> clazz = value.getClass();
                    //判断是controller
                    if (clazz.isAnnotationPresent(ShzController.class)){
                        String url="";
                        //controller上得url
                        if(clazz.isAnnotationPresent(ShzRequestMapping.class)){
                                ShzRequestMapping requestMapping = clazz.getAnnotation(ShzRequestMapping.class);
                                url=requestMapping.value();
                        }
                        //获取当前类得所有方法
                        Method[] methods = clazz.getMethods();
                        for (Method method:methods) {
                            if (method.isAnnotationPresent(ShzRequestMapping.class)){
                                ShzRequestMapping requestMapping =method.getAnnotation(ShzRequestMapping.class);
                                //正则匹配url
                                String regex = url +requestMapping.value().replaceAll("/+","/");
                                Pattern pattern =Pattern.compile(regex);
                                handlerMapping.add(new Handler(value,method,pattern));
                            }
                        }
                    }
             });

    };
    //适配器（匹配的过程）
    //匹配方法参数
    private void initHandlerAdapters(ShzApplicationContext context){
        if(handlerMapping.isEmpty()){ return;}

        //遍历handler
        handlerMapping.forEach(handler -> {
            //方法参数类型，参数索引位置
            Map<String,Integer> paramMapping = new HashMap<String,Integer>();
            //获取方法参数
            Class<?>[] paramstype = handler.getMethod().getParameterTypes();
            System.out.println("this is param"+paramstype.length);
            //有顺序，当时无法拿到方法参数名字
            //所以按顺序匹配
           // if(paramstype.length>0){
                for (int i = 0; i < paramstype.length; i++) {
                    //匹配request response
                    if (paramstype[i]== HttpServletRequest.class||
                            paramstype[i]== HttpServletResponse.class){
                        paramMapping.put(paramstype[i].getName(),i);
                    }

                }
                //匹配其他RequestParam
                Annotation [][] pa = handler.getMethod().getParameterAnnotations();
                for (int i = 0; i < pa.length; i++) {
                    for (Annotation annotation : pa[i]) {
                        if(annotation instanceof ShzRequestParam){
                            String paramName = ((ShzRequestParam) annotation).value();
                            if(!"".equals(paramName.trim())){
                                paramMapping.put(paramName,i);
                            }
                        }
                    }
                }
                adapterMapping.add(new HandlerAdapter(paramMapping,handler));
          //  }
        });
    };
    //============== 重要 ================


    //异常解析
    private void initHandlerExceptionResolvers(ShzApplicationContext context){};
    //视图转发（根据视图名字匹配到一个具体模板）
    private void initRequestToViewNameTranslator(ShzApplicationContext context){};

    //解析模板中的内容（拿到服务器传过来的数据，生成HTML代码）
    private void initViewResolvers(ShzApplicationContext context){};

    private void initFlashMapManager(ShzApplicationContext context){};
    //请求转发
    private void  doDispatch(HttpServletRequest request,HttpServletResponse response) throws  Exception{
        //根据request 得url获取hander
        Handler handler =getHanderByRequest(request);
        if(handler==null){
            response.getWriter().write("404 Not Found");
        }
        //再由hander获取一个方法适配器
        HandlerAdapter handlerAdapter =getHandlerAdapterByHander(handler);
        ModelAndView mv = handlerAdapter.handle(request,response);
     }
    //根据请求url获取handler
    private Handler getHanderByRequest(HttpServletRequest request){
        if(handlerMapping.isEmpty()){return null;}
        String url =request.getRequestURI();
        String contextpath = request.getContextPath();
        url = url.replace(contextpath,"").replaceAll("/+","/");
        for (Handler handler:
             handlerMapping) {
            Matcher matcher =handler.getPattern().matcher(url);
            if(matcher.matches()){
                return handler;
            }
        }
        return null;
    }
    //根据Handler获取方法适配器
    private HandlerAdapter getHandlerAdapterByHander(Handler handler){
        if(!adapterMapping.isEmpty()){
            for (HandlerAdapter handlerAdapter : adapterMapping) {
                if(handlerAdapter.getHandler()==handler){
                    return  handlerAdapter;
                }
            }
        }
        return null;
    }

}
