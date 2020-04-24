package com.ceshi.controller;

import com.ceshi.ResponseBody.CommonReturnType;
import com.ceshi.error.BusinessException;
import com.ceshi.error.EmBusinessError;
import com.ceshi.service.ItemService;
import com.ceshi.service.OrderService;
import com.ceshi.service.model.ItemModel;
import com.ceshi.service.model.OrderModel;
import com.ceshi.service.model.PayMsgModel;
import com.ceshi.service.model.UserModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Controller("order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class OrderController extends BaseController{


    @Resource
    private OrderService orderService;

    @Resource
    private HttpServletRequest httpServletRequest;

    @Resource
    private ItemService itemService;

    //封装下单请求
    @RequestMapping(value = "/createorder",method = RequestMethod.POST,consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId")Integer itemId,
                                        @RequestParam(name = "amount")Integer amount,
                                        @RequestParam(name = "promoId" ,required = false)Integer promoId) throws BusinessException {

        //获取用户登录信息
        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(isLogin == null || !isLogin.booleanValue()){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户还未登录,不能下单");
        }
        //获取用户的登录信息
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");

        OrderModel orderModel = orderService.createOder(userModel.getId(),itemId,promoId,amount);

        ItemModel itemModel = itemService.getItemById(itemId);


        httpServletRequest.getSession().setAttribute("orderprice",orderModel.getOrderPrice());
        httpServletRequest.getSession().setAttribute("amount",amount);
        httpServletRequest.getSession().setAttribute("PayId",orderModel.getId());
        httpServletRequest.getSession().setAttribute("title",itemModel.getTitle());
        httpServletRequest.getSession().setAttribute("imgUrl",itemModel.getImgUrl());
        httpServletRequest.getSession().setAttribute("dscription",itemModel.getDescription());
        httpServletRequest.getSession().setAttribute("price",itemModel.getPrice());


        return CommonReturnType.create(null);

    }


    @RequestMapping("/paym")
    public String topaymsg(){
        return "paymsg";
    }


    @RequestMapping(value = "/paymsg",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType payMsg(){

        PayMsgModel payMsgModel = new PayMsgModel();

        payMsgModel.setId((String) httpServletRequest.getSession().getAttribute("PayId"));

        payMsgModel.setPrice((BigDecimal) httpServletRequest.getSession().getAttribute("price"));

        payMsgModel.setTitle((String) httpServletRequest.getSession().getAttribute("title"));
        payMsgModel.setImgUrl((String) httpServletRequest.getSession().getAttribute("imgUrl"));
        payMsgModel.setDescription((String) httpServletRequest.getSession().getAttribute("dscription"));
        payMsgModel.setAmount((Integer) httpServletRequest.getSession().getAttribute("amount"));
        payMsgModel.setOrderPrices((BigDecimal) httpServletRequest.getSession().getAttribute("orderprice"));



        return CommonReturnType.create(payMsgModel);

    }


}
