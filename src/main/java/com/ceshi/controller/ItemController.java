package com.ceshi.controller;


import com.ceshi.ResponseBody.CommonReturnType;
import com.ceshi.controller.viewobject.ItemVo;
import com.ceshi.error.BusinessException;
import com.ceshi.service.ItemService;
import com.ceshi.service.model.ItemModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller("item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class ItemController extends BaseController{


    @Resource
    private ItemService itemService;


    //创建商品的controller
    @RequestMapping(value = "/createItem",method = RequestMethod.POST,consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title")String title,
                                      @RequestParam(name = "description")String description,
                                      @RequestParam(name = "price") BigDecimal price,
                                      @RequestParam(name = "stock")Integer stock,
                                      @RequestParam(name = "imgUrl")String imgUrl) throws BusinessException {

        //封装service请求用来创建商品
        ItemModel itemModel=new ItemModel();
        itemModel.setTitle(title);
        itemModel.setStock(stock);
        itemModel.setPrice(price);
        itemModel.setDescription(description);
        itemModel.setImgUrl(imgUrl);

        ItemModel itemModelForReturn= itemService.creatItem(itemModel);

        ItemVo itemVo=convertFromModel(itemModelForReturn);

        return CommonReturnType.create(itemVo);

    }

    @RequestMapping(value = "/to",method = RequestMethod.POST,consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public String to(@RequestParam(name = "id")Integer id, HttpServletRequest request){

        request.setAttribute("id",id);

        return "getitem";
    }

    //商品详情页面浏览
    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id")Integer id){

        ItemModel itemModel=itemService.getItemById(id);
        ItemVo itemVo=convertFromModel(itemModel);

        return CommonReturnType.create(itemVo);
    }


    //商品列表页面浏览
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType listItem(){
        List<ItemModel> itemModelList=itemService.listItem();

        //使用stream api将list内的itemModel转化为ITEMVO
        List<ItemVo> itemVoList= itemModelList.stream().map(itemModel -> {
           ItemVo itemVo=this.convertFromModel(itemModel);
           return itemVo;
        }).collect(Collectors.toList());

        return CommonReturnType.create(itemModelList);
    }





    private ItemVo convertFromModel(ItemModel itemModel){
        if (itemModel == null){
            return null;
        }
        ItemVo itemVO = new ItemVo();
        BeanUtils.copyProperties(itemModel, itemVO);

        if(itemModel.getPromoModel()!=null){

            //表示有正在进行或即将进行的秒杀活动
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else {
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }


}
