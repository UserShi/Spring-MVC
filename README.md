# Sping-MVC<br>
仿写一个简易的SpingMVC
<br>
##  SpringMVC执行主要流程
------------------
1.web.xml配置DispatcherServlet<br>
2.启动默认加载IOC容器<br>
3.IOC容器初始化，扫描类，实例化并依赖注入。<br>
4.IOC初始化完成后，从IOC中取出Controller类，解析url和方法，用HandlerMapping保存方法与url得对应关系。<br>
5.初始化适配器HandlerAdapter，解析Hander中的方法，解析方法参数，保存参数与handler得对应关系<br>
6.浏览器输入URL请求，DispatcherServlet统一拦截，解析HanderMapping，获取对应URL的Handler（即对应方法）<br>
7.通过HandlerAdapter适配对应方法参数，反射调用对应方法，返回ModelAndView。<br>

##  实际上，MVC做得事情更多，这里只是描述主要流程，简易实现基础功能。
--------------
##  如有错误，请指正。
