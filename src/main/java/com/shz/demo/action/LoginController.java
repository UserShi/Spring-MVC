package com.shz.demo.action;

import com.shz.framework.annotation.ShzController;
import com.shz.framework.annotation.ShzRequestMapping;
import com.shz.framework.annotation.ShzRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ShzController("testcontroller")
@ShzRequestMapping("/login")
public class LoginController {
    @ShzRequestMapping("/test.html")
    public void  TestMethod(HttpServletRequest request, HttpServletResponse response,
                            @ShzRequestParam(value = "test")String test) throws IOException {
        String parma =request.getParameter("test");
        System.out.println(parma);
        System.out.println(test+"test====");
        System.out.println("这里是login method test");
        response.getWriter().write(test);
    }
}
