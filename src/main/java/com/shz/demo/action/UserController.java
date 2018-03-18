package com.shz.demo.action;

import com.shz.demo.entity.User;
import com.shz.demo.service.UserService;
import com.shz.framework.annotation.ShzAutowired;
import com.shz.framework.annotation.ShzController;
import com.shz.framework.annotation.ShzRequestMapping;

@ShzController
@ShzRequestMapping
public class UserController {

    @ShzAutowired
    private UserService userService;


    @ShzRequestMapping("/user/userlogin.html")
    public void  userMethod(){
        User user = new User();
        userService.login(user);
    }

}
