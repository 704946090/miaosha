package com.ceshi.controller;

import com.alipay.api.internal.util.AlipaySignature;
import com.ceshi.config.AlipayConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alipay.api.*;
import com.alipay.api.request.*;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class AlipayController {


    @RequestMapping("/pay")
    @ResponseBody
    public void pay(HttpServletRequest httpRequest,
                    HttpServletResponse httpResponse) throws AlipayApiException, IOException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
//        String out_trade_no = new String(request.getParameter("WIDout_trade_no").getBytes("ISO-8859-1"),"UTF-8");
        String out_trade_no = (String) httpRequest.getSession().getAttribute("PayId");

        //付款金额，必填
//        String total_amount = new String(request.getParameter("WIDtotal_amount").getBytes("ISO-8859-1"),"UTF-8");
        String total_amount = "0.01";
        //订单名称，必填
//        String subject = new String(request.getParameter("WIDsubject").getBytes("ISO-8859-1"),"UTF-8");
        String subject = (String) httpRequest.getSession().getAttribute("title");
        //商品描述，可空
//        String body = new String(request.getParameter("WIDbody").getBytes("ISO-8859-1"),"UTF-8");
        String body = (String) httpRequest.getSession().getAttribute("dscription");

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //若想给BizContent增加其他可选请求参数，以增加自定义超时时间参数timeout_express来举例说明
        //alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
        //		+ "\"total_amount\":\""+ total_amount +"\","
        //		+ "\"subject\":\""+ subject +"\","
        //		+ "\"body\":\""+ body +"\","
        //		+ "\"timeout_express\":\"10m\","
        //		+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节

        //请求
//        String result = alipayClient.pageExecute(alipayRequest).getBody();
        String form="";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=utf-8");
        httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();


    }



    @RequestMapping("/notify")
    public void notify1(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("异步");
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }

//            try{
//                //乱码解决，这段代码在出现乱码时使用
//                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);

//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            for (Map.Entry<String, String> entry : params.entrySet()) {
//                System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
//            }
        }

        try{
            //调用SDK验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);

            //——请在这里编写您的程序（以下代码仅作参考）——

            /* 实际验证过程建议商户务必添加以下校验：
            1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
            2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
            3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
            4、验证app_id是否为该商户本身。
            */
            if(signVerified) {//验证成功
                //商户订单号
                String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

                //支付宝交易号
                String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

                //交易状态
                String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

                if(trade_status.equals("TRADE_FINISHED")){
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //如果有做过处理，不执行商户的业务程序

                    //注意：
                    //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                }else if (trade_status.equals("TRADE_SUCCESS")){
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //如果有做过处理，不执行商户的业务程序

                    //注意：
                    //付款完成后，支付宝系统发送该交易状态通知
                }
                response.setContentType("text/html;charset="+AlipayConfig.charset);
                response.getWriter().write("success");
                response.getWriter().close();
                System.out.println("验证成功");
            }else {//验证失败
                response.setContentType("text/html;charset="+AlipayConfig.charset);
                response.getWriter().write("fail");
                response.getWriter().close();
                System.out.println("验证失败");
                //调试用，写文本函数记录程序运行情况是否正常
                //String sWord = AlipaySignature.getSignCheckContentV1(params);
                //AlipayConfig.logResult(sWord);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        //——请在这里编写您的程序（以上代码仅作参考）——

    }



    //页面跳转同步验证
    @RequestMapping("/success")
    public String return1(HttpServletRequest request) {

        //获取支付宝GET过来反馈信息
        Map<String,String> params = new HashMap<String, String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            try {
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            }catch (Exception e){
                e.printStackTrace();
            }
            params.put(name, valueStr);
        }

        String result=null;
        try {
            //调用SDK验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);

            //——请在这里编写您的程序（以下代码仅作参考）——
            if(signVerified) {
                //商户订单号
//                String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

                //支付宝交易号
//                String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

                //付款金额
//                String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");

                //sign
//                String sign=new String(request.getParameter("sign").getBytes("ISO-8859-1"),"UTF-8");

//                result="trade_no:"+trade_no+"<br/>out_trade_no:"+out_trade_no+"<br/>total_amount:"+total_amount+"<br/>sign:"+sign;
//                System.out.println(result);

            }else {
                result="验签失败";
                System.out.println(result);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return "listitem";
    }

}
