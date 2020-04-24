package com.ceshi.controller;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.ceshi.error.BusinessException;
import com.ceshi.error.EmBusinessError;

import java.util.Map;

public class ceshiali {


    public static void sendsMs(String telphone,String checknumber) throws BusinessException {
        DefaultProfile profile = DefaultProfile.getProfile("default", "LTAIqIaHsftp4h5D", "OVo1JT6anEzUUaas6TESZCb3KGreUc");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("PhoneNumbers", telphone);
        request.putQueryParameter("SignName", "轻易租平台");
        request.putQueryParameter("TemplateCode", "SMS_167528458");
        request.putQueryParameter("TemplateParam", "{\"code\":\""+checknumber+"\"}");

        if(telphone.length()!=11){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号码位数不正确");
        }

        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            String result=response.getData();
            Map maps=JSON.parseObject(result);
            String code=maps.get("Code").toString();
            System.out.println("code:"+code);
            if(code.equals("OK")){
                System.out.println("已经成功发送短信");
            }else {
                System.out.println("发送短信失败");
            }

        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        sendsMs("17363642210","236521");
        System.out.println("123");
    }
}
