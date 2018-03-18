package com.shz.framework.context;

import com.shz.framework.annotation.ShzAutowired;
import com.shz.framework.annotation.ShzController;
import com.shz.framework.annotation.ShzService;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ShzApplicationContext {

    private Map<String,Object> instanceMap = new ConcurrentHashMap<String,Object>();
    private List<String> classCache = new ArrayList<String>();
    //这里暂时简单做成解析properties
    private Properties config = new Properties();
    public  ShzApplicationContext(String location){
        InputStream is=null;
        try {
        //1.定位配置文件
        is = this.getClass().getClassLoader().getResourceAsStream(location);
        //2.加载配置文件
          config.load(is);
           //3.注册 扫描 class
            String packageName = config.getProperty("scanPackage");
            doRegister(packageName);
            //4.实例化对象  扫描添加注解得service controller得calss类
            doCreateBean();
            System.out.println("这里是application");
            //5.依赖注入
            populate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //扫描注册 class
    private void doRegister(String packageName){
        URL url = this.getClass().getClassLoader().
                getResource("/"+packageName.replaceAll("\\.","/"));
        File  dir = new File(url.getFile());
        for (File file:
             dir.listFiles()) {
            if(file.isDirectory()){
                //扫描class文件  如果当前还是文件夹  递归扫描
                doRegister(packageName+"."+file.getName());
            }else{
                //如果是文件就 添加到缓存里
                classCache.add(packageName+"."+file.getName().replace(".class","").trim());
            }
        }


    }
    //实例化bean
    private  void  doCreateBean (){
        //检查是否有class类得注册信息 没有就返回
        if(classCache.size()==0){   return;     }
        for (String className:
             classCache) {
            try {
                Class<?> clazz = Class.forName(className);
                //注解为 service 或者 controller
                if(clazz.isAnnotationPresent(ShzController.class)){
                    ShzController controller = clazz.getAnnotation(ShzController.class);
                    if("".equals(controller.value().trim())){
                        //如果为空默认 类名小写
                        instanceMap.put(lowerFirstChar(clazz.getSimpleName()),clazz.newInstance());
                    }else{
                        instanceMap.put(controller.value().trim(),clazz.newInstance());
                    }
                }else if(clazz.isAnnotationPresent(ShzService.class)){
                    ShzService shzService = clazz.getAnnotation(ShzService.class);
                    if("".equals(shzService.value().trim())){
                        //service 一般都实现接口  默认用接口
                        Class<?> [] interfaces = clazz.getInterfaces();
                        for (Class<?> interfaceclazz:
                             interfaces) {
                            instanceMap.put(lowerFirstChar(interfaceclazz.getName()),clazz.newInstance());
                        }

                    }else{
                        //不为空
                        instanceMap.put(shzService.value().trim(),clazz.newInstance());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        
    }
    //依赖注入
    private void populate(){
        //判断容器中是否有实例化的bean
        if(instanceMap.isEmpty()){
            return;
        }
        for (Map.Entry<String,Object> entry:
             instanceMap.entrySet()) {
            //获取实例里所有得属性 包括私有属性
            Field [] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field:
                 fields) {
                if (!field.isAnnotationPresent(ShzAutowired.class)){
                    continue;
                }
                //如果是autowired
                ShzAutowired autowired = field.getAnnotation(ShzAutowired.class);
                String autoid = autowired.value().trim();
                if ("".equals(autoid)){
                        autoid =  lowerFirstChar(field.getType().getName());
                }
                //私有属性权限开放
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(),instanceMap.get(autoid));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //类名首字母小写转换
    private String lowerFirstChar(String str){
            char [] chars  = str.toCharArray();
            //大写转小写
            chars[0] +=32;
            return chars.toString();
    }
    public Map<String,Object> getAll(){
        return instanceMap;
    }
}
