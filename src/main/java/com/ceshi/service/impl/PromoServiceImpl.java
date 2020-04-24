package com.ceshi.service.impl;

import com.ceshi.dao.PromoDOMapper;
import com.ceshi.dataobject.PromoDO;
import com.ceshi.service.PromoService;
import com.ceshi.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
public class PromoServiceImpl implements PromoService {

    @Resource
    private PromoDOMapper promoDOMapper;



    @Override
    public PromoModel getPromoById(Integer itemId) {

        //获取对应商品的秒杀活动信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);


        //dataobject->model
        PromoModel promoModel = convertFromPromoDo(promoDO);
        if(promoModel == null){
            return null;
        }

        //判断当前时间是否秒杀活动即将开始或正在进行
        if(promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);
        }else if(promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);
        }else {
            promoModel.setStatus(2);
        }


        return promoModel;
    }

    private PromoModel convertFromPromoDo(PromoDO promoDO){

        if(promoDO == null){
            return null;
        }

        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO,promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;

    }



}
