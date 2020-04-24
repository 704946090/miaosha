package com.ceshi.config;


import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {
	
//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
	public static String app_id = "2016092900624277";
	
	// 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCGj1S12osBWYqPt08fkjx5z0F5bEHQ+mlJd+U3CcxEf56wDnlC92fz4MVOlpi2DaBoxnhQ7bENPWOAIb9zzm6lhTO6Y+8BRdQ+nNlUcJVlEHhTzTgJm+GRGSP41VE1amV6pmRBb/TLw0qePih1pHvj6vK0pIZ6rnBlWjbO6idPedZD3QQHY2FArtMjMC7APdMGarPXsR3gkff5JBhH0x5Hb1HtLmcvJKs+CZn65EktbgbM3wKqlHgMyPSBwNoBiZoiBWTTKkidUCVmLbY0wSeCfQhxcYt44cmR8JPojw0O+/4bHyeu4z6MMfFaBM7dEQufB28vncqcpj8Zf9LIsa+NAgMBAAECggEARECLfTp+645ZhBA86Ayq9DY2Rbqtn6yEnylbcJFdtRjuiuqsg1Uuuzs1mWk1yJIc3UheWt5VwhtzTtjPXZRF91sbI93wqzqaL7ArZHM+s+aowgkD9YvF3G1eG6dH68ot8wpXLAH3rys9vApXR2TJGpBfz1TsiWnVxPL/ffCzQ9IO15Mr74U8nZdVZXSm6SxdN1rBYigvu2Pua89OvvC97iJrrKG13OV21n5xbY274sAt44G/Nk6nxWrl/OUstWZRAi4wuIISCNsBoMoRML1kjBiqb/lIXq9Zd1TyRQTbZ5YyApYvnUph6gtwjoM8bldNex18lYrt3nIOtiQhIXhTmQKBgQDiqrFI7a10kioVCUQIBWDmPeVCQqhiJ5IssaOA/dXesomwRolsJaCkj7ffg596FX2m6ZPoTCSpbuapiuyFNHWxs7qMr9ji1Pyr9opVTMuRmJlV6X/H7qT2HDrhl6kVJNYVgId1+L4JVk4L9ylzlpzVcvRwyZ4S6VySiDsfCNh7FwKBgQCX+TQVJtrneCofsu11KMdX7FTj59rlrnd4eqPD7DWNVzFtWjVDvw0fhmb76O2XMffa6F0OcnndN6t3L3wz8ZqY2ATtP76MfxyD/PidTqwPnEDEzvtDq9EKTUKpGxMIMPxjKyf3DmZlZpTgy/KbSj60qs+8EZFZlRG5cTHLObcA+wKBgQCEdS9o6MwGZSwGYwXIkiwQIHeLAx0XTvyS9bQQu0VyAG+J/Y5tDogT4pCXI3nchBaLpc/zXhvGj7JnvXF+XiuxDtCjSle6JrEJZOdt9ZS4nq5skClGEuV/lPurvU7ntkENC7EddaDIIBE32NX2YDuCWM0T5B5l+s1ILCgweKDZPQKBgGz88zUYIHVP4CXLlDAphoyvmFG5vWpqrJNioH+prt4mYfQidYCYSzkIkmKmsX11AGOlsNbjkjHr8rQPAeveoRhsE1xyEp1/q1uc2E84VHz9ehWqutfVKJ1SuzO7jvKY7ufP02GCmjyuTKbKnLpxjHbU6RXfI3YzSfrMCwHL6kpxAoGAQgJygqF191aBK3aSOd4Mh9IdL0Uv17k3tviCjKp9kTPA8Jz19OnMxgQN39QO3JDjZmUsO7WEZAywrHSu49VoxuMHNAzEJF1cx0AevAEV3ON6Xey6cHTrgUXGGzP+7ZPws93DYyQIh35x+lskwKvO2u/mwbbg+3odwFV7oZiZM5o=";
	
	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4hDXilK2SjDLiACOPEqzG5YHRMRf6JqhVCHhHa3e4bfW/bfGL1FT1HGIuVZ4P6k9jdkujmYzRGEMVIUR12uk92DEX/pqOxQv1D1prVDQ8pRyEIZWLVrNnkAlJSvPKbZli2KnyhVfzCoJRdIMbMDkgAyDdt8GGxPEPelj2LxM0iUTRWuNb4DO99TD1kX2WFD5pD+qfVg5EdVSvt4rgDcIMkuKNZAtMB6yRtOtu6tNEE6N/boPS2xvdV8h6SWOXmlB5rOQ0m/AR11td0mdItCahNlWNHrpomxcEB4l93TR/+tyMahFBNyfQa4RmDdBaTuXUP2oR2P5cgm7/rR+9PosJQIDAQAB";

	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "http://25b0y99147.qicp.vip:43182/notify";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
//	public static String return_url = "http://工程公网访问地址/alipay.trade.page.pay-JAVA-UTF-8/return_url.jsp";
    public static String return_url = "http://25b0y99147.qicp.vip:43182/success";

	// 签名方式
	public static String sign_type = "RSA2";
	
	// 字符编码格式
	public static String charset = "utf-8";
	
	// 支付宝网关
//	public static String gatewayUrl = "https://openapi.alipay.com/gateway.do";
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
	
	// 支付宝网关
	public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /** 
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

