package com.ceshi.config;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class exceptionController {

    //shiro无权限异常页面跳转

    @ExceptionHandler(value = UnauthorizedException.class)//处理访问方法时权限不足问题
    public String defaultErrorHandler(HttpServletRequest req, Exception e)  {
        return "404page";
    }

    @ExceptionHandler(AuthorizationException.class)
    public String AuthorizationException(Exception ex) {
        return "log2";
    }
}
