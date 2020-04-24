package com.ceshi.controller;


import com.alibaba.druid.util.StringUtils;
import com.ceshi.ResponseBody.CommonReturnType;
import com.ceshi.controller.viewobject.ItemVo;
import com.ceshi.controller.viewobject.UserVo;
import com.ceshi.error.BusinessException;
import com.ceshi.error.EmBusinessError;
import com.ceshi.service.UserService;
import com.ceshi.service.model.UserModel;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;


    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping("/log")
    public String log(){
        return "login2";
    }


    @RequestMapping("/404")
    public String error(){
        return "404page";
    }


    @RequestMapping("/tolistitem")
    public String tolistitem(){
        return "listitem";
    }

    @RequestMapping("/toregister")
    public String toregister(){
        return "register2";
    }


//    @RequestMapping("/getotp")
//    public String getotp(){
//        return "createitem";
//    }


    @RequestMapping("/getcreate")
    public String getcreate(){
        return "createitem";
    }


    @RequestMapping("/payweixin")
    public String payweixin(){
        return "weixinpay";
    }


    @RequestMapping(value = "/to",method = RequestMethod.POST,consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public CommonReturnType to(@RequestParam(name = "id")Integer id, HttpServletRequest request){

        request.getSession().setAttribute("id",id);

        return CommonReturnType.create(null);
    }

    @RequestMapping("/too")
    public String too(){
        return "getitem";
    }


    @RequestMapping(value = "/gettoo",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType gettoo(){

        Integer id=(Integer) httpServletRequest.getSession().getAttribute("id");
        ItemVo itemVo=new ItemVo();
        itemVo.setId(id);
        return CommonReturnType.create(itemVo);
    }



    //用户登录接口
    @RequestMapping(value = "/login",method = RequestMethod.POST,consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public CommonReturnType loginshiro(@RequestParam(name = "telphone")String telphone,
                                  @RequestParam(name = "password")String password,
                                       @RequestParam(name = "rememberMe")Boolean rememberMe) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //入参校验
        if(org.apache.commons.lang3.StringUtils.isEmpty(telphone)
                || org.apache.commons.lang3.StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"账号信息不正确");
        }

        Subject subject = SecurityUtils.getSubject();
        // 在认证提交前准备 token（令牌）
        UsernamePasswordToken token = new UsernamePasswordToken(telphone, password,rememberMe);

        try {
            subject.login(token);
        }catch (AuthenticationException e){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"账号信息不正确");
        }

        //将登陆凭证加入到用户登陆成功的session内
        UserModel userModel = userService.validateLogin(telphone,this.EncodeByMD5(password));

        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);

        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        if(subject.isAuthenticated()){
            System.out.println("登陆成功");
            return CommonReturnType.create(null);
        }else {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }


    }


    //用户登录接口
//    @RequestMapping(value = "/login1",method = RequestMethod.POST,consumes = "application/x-www-form-urlencoded")
//    @ResponseBody
//    public CommonReturnType login(@RequestParam(name = "telphone")String telphone,
//                                  @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
//
//        //入参校验
//        if(org.apache.commons.lang3.StringUtils.isEmpty(telphone)
//                || org.apache.commons.lang3.StringUtils.isEmpty(password)){
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//        }
//
//        //用户登录服务,用来校验用户登录是否合法
//
//        UserModel userModel = userService.validateLogin(telphone,this.EncodeByMD5(password));
//
//
//        //将登陆凭证加入到用户登陆成功的session内
//
//        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
//
//        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);
//
//        return CommonReturnType.create(null);
//
//    }


    //用户注册接口
    @RequestMapping(value = "/register",method = RequestMethod.POST,consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone")String telphone,
                                     @RequestParam(name = "otpCode")String otpCode,
                                     @RequestParam(name = "name")String name,
                                     @RequestParam(name = "gender")Integer gender,
                                     @RequestParam(name = "age")Integer age,
                                     @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和对应的otpCode相符合
        String inSessionOtpcode=this.httpServletRequest.getSession().getAttribute(telphone).toString();
        if(!StringUtils.equals(inSessionOtpcode,otpCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不符合");
        }

        //用户的注册流程
        UserModel userModel=new UserModel();

        userModel.setName(name);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(this.EncodeByMD5(password));

        userService.register(userModel);

        return CommonReturnType.create(null);

    }

    public String EncodeByMD5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定一个计算方法
        MessageDigest md5=MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder=new BASE64Encoder();
        //加密字符串
        String newstr=base64Encoder.encode(md5.digest(str.getBytes("utf-8")));

        return newstr;
    }




    //用户获取otp短信接口
    @RequestMapping(value = "/getotp",method = RequestMethod.POST,consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telphone")String telphone) throws BusinessException {

        //需要按照一定的规则生成otp验证码
        Random random=new Random();
        int randomInt=random.nextInt(999999);
        randomInt+=100000;
        String otpCode=String.valueOf(randomInt);

        //将OTP验证码同对应用户的手机号关联,使用httpsession的方式绑定他的手机号与OTPCODE
        httpServletRequest.getSession().setAttribute(telphone,otpCode);

        ceshiali.sendsMs(telphone,otpCode);

        //将OTP验证码通过短信通道发送给用户
        System.out.println("telphone="+telphone+"&otpCode="+otpCode);

        return CommonReturnType.create(null);

    }




    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id")Integer id) throws BusinessException {
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel=userService.getUserById(id);

        //若获取的对应用户信息不存在
        if(userModel==null){
//            userModel.setEncrptPassword("123");
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }


        //将核心领域模型用户对象转化为可供UI使用的viewobject
        UserVo userVo= convertFromModel(userModel);

        return CommonReturnType.create(userVo);

    }

    private UserVo convertFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserVo userVo=new UserVo();
        BeanUtils.copyProperties(userModel,userVo);
        return userVo;
    }

}
