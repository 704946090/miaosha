package com.ceshi.controller;

import org.springframework.stereotype.Controller;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;



@Controller
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class QQController {


    /**
     * 根目录请求
     * @return
     */
//    @RequestMapping("/log")
//    public ModelAndView root(){
//        ModelAndView mav=new ModelAndView();
//        mav.setViewName("index");
//        mav.addObject("title","QQ登录测试");
//        return mav;
//    }

    /**
     * qq登录 带参数发送服务器
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/qqLogin")
    public void qqLogin(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html;charset=utf-8");
        try {
            response.sendRedirect(new Oauth().getAuthorizeURL(request));
        } catch (QQConnectException e) {
            e.printStackTrace();
        }
    }

    /**
     * 回调
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/connect")
    public void connect(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();
        try {
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);
            String accessToken   = null,
                    openID        = null;
            long tokenExpireIn = 0L;

            if (accessTokenObj.getAccessToken().equals("")) {
//                我们的网站被CSRF攻击了或者用户取消了授权
//                做一些数据统计工作
                System.out.print("没有获取到响应参数");
            } else {
                accessToken = accessTokenObj.getAccessToken();
                tokenExpireIn = accessTokenObj.getExpireIn();

                request.getSession().setAttribute("demo_access_token", accessToken);
                request.getSession().setAttribute("demo_token_expirein", String.valueOf(tokenExpireIn));

                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj =  new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();
                /*
                用openID去数据库查用户user
                if(user=null){
                       //新用户注册
                }else{
                       //老用户直接登陆
                }
                 */

                out.println("欢迎你，代号为 " + openID + " 的用户!");
                request.getSession().setAttribute("demo_openid", openID);




                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                out.println("<br/>");
                if (userInfoBean.getRet() == 0) {
                    out.println(userInfoBean.getNickname() + "<br/>");
                    out.println(userInfoBean.getGender() + "<br/>");
                    out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL30() + "><br/>");
                    out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL50() + "><br/>");
                    out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL100() + "><br/>");
                } else {
                    out.println("很抱歉，我们没能正确获取到您的信息，原因是： " + userInfoBean.getMsg());
                }

            }
        } catch (QQConnectException e) {
        }
    }

}
