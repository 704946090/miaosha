package com.ceshi.controller;

import com.ceshi.ResponseBody.CommonReturnType;
import com.ceshi.error.BusinessException;
import com.ceshi.error.EmBusinessError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseController {
    //定义exceptionhandler解决未被controller层吸收的exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception ex){

        Map<String,Object> responseData=new HashMap<String, Object>();
        if(ex instanceof BusinessException){
            BusinessException businessException=(BusinessException)ex;
            responseData.put("errCode",businessException.getErrorCode());
            responseData.put("errMsg",businessException.getErrorMsg());
        }else {
            responseData.put("errCode", EmBusinessError.UNKNOW_ERRO.getErrorCode());
            responseData.put("errMsg",EmBusinessError.UNKNOW_ERRO.getErrorMsg());
        }
        return CommonReturnType.create(responseData,"fail");

    }
}
