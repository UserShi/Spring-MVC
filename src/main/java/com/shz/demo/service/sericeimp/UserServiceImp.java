package com.shz.demo.service.sericeimp;

import com.shz.demo.entity.User;
import com.shz.demo.service.UserService;
import com.shz.framework.annotation.ShzService;

@ShzService
public class UserServiceImp implements UserService {
    public void login(User user) {
        System.out.println("登录成功");
    }
}
