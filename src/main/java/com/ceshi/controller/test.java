package com.ceshi.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class test {

    //继承,就无法使用shiro的未知页面跳转
    @RequiresPermissions("user:list")
    @ResponseBody
    @RequestMapping("/show")
    public String showUser() {
        return "这是学生信息";
    }
}
