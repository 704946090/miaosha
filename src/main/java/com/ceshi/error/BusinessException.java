package com.ceshi.error;

//包装器业务异常类实现
public class BusinessException extends Exception implements CommonError {


    private CommonError commonError;


    //直接接受EmBusinessException的传参用于构造业务异常
    public BusinessException(CommonError commonError){
        super();
        this.commonError=commonError;
    }

    //接收自定义errMsg的方式构造业务异常
    public BusinessException(CommonError commonError,String errMsg){
        super();
        this.commonError=commonError;
        this.commonError.setErrorMsg(errMsg);
    }


    @Override
    public int getErrorCode() {
        return this.commonError.getErrorCode();
    }

    @Override
    public String getErrorMsg() {
        return this.commonError.getErrorMsg();
    }

    @Override
    public CommonError setErrorMsg(String errMsg) {
        this.commonError.setErrorMsg(errMsg);
        return this;
    }
}
