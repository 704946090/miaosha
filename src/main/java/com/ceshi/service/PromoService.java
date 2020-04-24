package com.ceshi.service;

import com.ceshi.service.model.PromoModel;

public interface PromoService {

    //根据itemId获取即将进行的或正在进行的秒杀活动信息
    PromoModel getPromoById(Integer itemId);
}
