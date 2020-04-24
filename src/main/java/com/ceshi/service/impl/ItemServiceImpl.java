package com.ceshi.service.impl;

import com.ceshi.dao.ItemDOMapper;
import com.ceshi.dao.ItemStockDOMapper;
import com.ceshi.dataobject.ItemDO;
import com.ceshi.dataobject.ItemStockDO;
import com.ceshi.error.BusinessException;
import com.ceshi.error.EmBusinessError;
import com.ceshi.service.ItemService;
import com.ceshi.service.PromoService;
import com.ceshi.service.model.ItemModel;
import com.ceshi.service.model.PromoModel;
import com.ceshi.validator.ValidationResult;
import com.ceshi.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {


    @Resource
    private ValidatorImpl validator;

    @Resource
    private ItemDOMapper itemDOMapper;

    @Resource
    private ItemStockDOMapper itemStockDOMapper;

    @Resource
    private PromoService promoService;


    private ItemDO convertItemDoFromiTemModel(ItemModel itemModel){

        if(itemModel==null){
            return null;
        }

        ItemDO itemDO=new ItemDO();
        BeanUtils.copyProperties(itemModel,itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());

        return itemDO;
    }

    private ItemStockDO convertItemStockDOFromiTemModel(ItemModel itemModel){
        if(itemModel==null){
            return null;
        }
        ItemStockDO itemStockDO=new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());

        return itemStockDO;

    }


    @Override
    @Transactional
    public ItemModel creatItem(ItemModel itemModel) throws BusinessException {
        //校验入参
        ValidationResult result=validator.validae(itemModel);
        if(result.isHasError()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }


        //转化itemmodel->dataobject
        ItemDO itemDO=this.convertItemDoFromiTemModel(itemModel);

        //写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());

        ItemStockDO itemStockDO=this.convertItemStockDOFromiTemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);

        //返回创建完成的对象

        return this.getItemById(itemModel.getId());
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOList=itemDOMapper.listItem();
        List<ItemModel> itemModelList = itemDOList.stream().map(itemDO -> {
            ItemStockDO itemStockDO=itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel=convertItemDoFromDataobject(itemDO,itemStockDO);
            return itemModel;
        }).collect(Collectors.toList());

        return itemModelList;
    }


    @Override
    public ItemModel getItemById(Integer id) {

        ItemDO itemDO=itemDOMapper.selectByPrimaryKey(id);
        if(itemDO==null){
            return null;
        }

        //操作获得库存数量
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());


        //将dataobject->model
        ItemModel itemModel=convertItemDoFromDataobject(itemDO,itemStockDO);


        //获取活动商品信息
        PromoModel promoModel = promoService.getPromoById(id);
        if(promoModel !=null && promoModel.getStatus().intValue()!=3){
            itemModel.setPromoModel(promoModel);
        }

        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
       int affectedRow = itemStockDOMapper.decreaseStock(itemId,amount);
       if(affectedRow>0){

           //更新库存成功
           return true;

       }else {

           //更新库存失败
           return false;

       }
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
        itemDOMapper.increaseSales(itemId,amount);
    }

    private ItemModel convertItemDoFromDataobject(ItemDO itemDO,ItemStockDO itemStockDO){

        ItemModel itemModel=new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());

        return itemModel;
    }
}
