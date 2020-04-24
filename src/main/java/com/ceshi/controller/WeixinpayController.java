package com.ceshi.controller;

import com.ceshi.ResponseBody.CommonReturnType;
import com.ceshi.entity.Pay;
import com.ceshi.error.BusinessException;
import com.ceshi.error.EmBusinessError;
import com.ceshi.util.HttpsRequest;
import com.ceshi.util.Sign;
import com.ceshi.util.XStream;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

@Controller
@RequestMapping("/weixinpay")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class WeixinpayController {

    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse response;

    public static String code ="false";


    @RequestMapping(value = "/getQ", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public void getqcode() throws IOException {

        String url = (String) request.getSession().getAttribute("code_url");
        if (url != null && !"".equals(url)) {
            ServletOutputStream stream = null;
            try {

                int width = 300;//图片的宽度
                int height = 300;//高度
                stream = response.getOutputStream();
                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix m = writer.encode(url, BarcodeFormat.QR_CODE, height, width);
                MatrixToImageWriter.writeToStream(m, "png", stream);
            } catch (WriterException e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    stream.flush();
                    stream.close();
                }
            }
        }
    }


    @RequestMapping(value = "/check",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType check(){
//        System.out.println("123");
        if(code.equals("SUCCESS")){
            code ="false";
            return CommonReturnType.create(null);
        }else {
            return CommonReturnType.create("123","false");
        }
    }



    @RequestMapping(value = "/pay",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType pay() throws BusinessException {


        String Key="javastruts2springhibernate2016tr";//填自己的
        String appid="wx2537437d11cdec0b";//填自己的
        String mch_id="1381483602";//填自己的
        String spbill_create_ip="49.221.62.131";
        String body="测试微商场";
        String trade_type="NATIVE";
        String notify_url="http://25b0y99147.qicp.vip:43182/weixinpay/huidao";//填自己的
        String total_fee="1";

        String nonce_str= (String) request.getSession().getAttribute("title");
        String out_trade_no=(String) request.getSession().getAttribute("PayId");

        SortedMap<Object,Object> parameters=new TreeMap<Object, Object>();

        parameters.put("appid",appid);
        parameters.put("mch_id",mch_id);
        parameters.put("spbill_create_ip",spbill_create_ip);
        parameters.put("body",body);
        parameters.put("trade_type",trade_type);
        parameters.put("notify_url",notify_url);
        parameters.put("total_fee",total_fee);
        parameters.put("nonce_str",nonce_str);
        parameters.put("out_trade_no",out_trade_no);

        String sign= Sign.sign(parameters,Key);

        System.out.println("sign:"+sign);

        Pay pay=new Pay();
        pay.setAppid(appid);
        pay.setMch_id(mch_id);
        pay.setBody(body);
        pay.setNonce_str(nonce_str);
        pay.setNotify_url(notify_url);
        pay.setOut_trade_no(out_trade_no);
        pay.setSign(sign);
        pay.setSpbill_create_ip(spbill_create_ip);
        pay.setTotal_fee(total_fee);
        pay.setTrade_type(trade_type);

        XStream.xstream.alias("xml",Pay.class);
        String reqXml=XStream.xstream.toXML(pay);
        reqXml=reqXml.replaceAll("__","_");

        System.out.println(reqXml);

        String respxml=null;
        String requestUrl="https://api.mch.weixin.qq.com/pay/unifiedorder";

        try {
            respxml= HttpsRequest.httpsRequest(requestUrl,"POST",reqXml);
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("---------------------"+respxml);


        String code_url=null;
        try{
            Document doc= DocumentHelper.parseText(respxml);
            Element root=doc.getRootElement();
            List<Element> elementList=root.elements();
            for (Element e:elementList){
                if(e.getName().equals("code_url")){
                    code_url=e.getTextTrim();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("code_url:"+code_url);

        if(code_url!=null&&!"".equals(code_url)){
            request.getSession().setAttribute("code_url",code_url);
            request.getSession().setAttribute("code","false");
            return CommonReturnType.create(null);
        }else {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"code为null");
        }

    }

    @RequestMapping("/huidao")
    @ResponseBody
    public void huidiao(HttpServletRequest request, HttpServletResponse response) throws Exception{

        System.out.println("hahahaha");

        InputStream inputStream=null;
        inputStream=request.getInputStream();
        StringBuffer sb=new StringBuffer();
        BufferedReader in=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
        String s;
        while ((s = in.readLine())!= null){

            sb.append(s);
        }
        in.close();
        inputStream.close();
        String respxml=sb.toString();

        System.out.println("respxml:"+respxml);

        String code1=null;
        Document doc=DocumentHelper.parseText(respxml);
        Element root=doc.getRootElement();
        List<Element> elementList=root.elements();
        for(Element e:elementList){
            if(e.getName().equals("result_code")){
                code1=e.getTextTrim();
                System.out.println(code1);
            }
        }
        if(code1.equals("SUCCESS")){
            code=code1;

            String resXml = "<xml>" +"<return_code><![CDATA[SUCCESS]]></return_code>"
                    + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());

            out.write(resXml.getBytes());
            out.flush();
            out.close();
            System.out.println("回复成功");

        }
//        request.getRequestDispatcher("/weixinpay/in").include(request, response);

    }


}
