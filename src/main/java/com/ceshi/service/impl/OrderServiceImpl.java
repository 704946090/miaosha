package com.ceshi.service.impl;

import com.ceshi.dao.OrderDOMapper;
import com.ceshi.dao.SequenceDOMapper;
import com.ceshi.dataobject.OrderDO;
import com.ceshi.dataobject.SequenceDO;
import com.ceshi.error.BusinessException;
import com.ceshi.error.EmBusinessError;
import com.ceshi.service.ItemService;
import com.ceshi.service.OrderService;
import com.ceshi.service.UserService;
import com.ceshi.service.model.ItemModel;
import com.ceshi.service.model.OrderModel;
import com.ceshi.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private ItemService itemService;

    @Resource
    private UserService userService;

    @Resource
    private OrderDOMapper orderDOMapper;

    @Resource
    private SequenceDOMapper sequenceDOMapper;



    @Override
    @Transactional
    public OrderModel createOder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException {

        //1.校验下单状态,下单的商品是否存在,用户是否合法,购买的数量是否正确
        ItemModel itemModel=itemService.getItemById(itemId);
        if(itemModel==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品信息不存在");
        }

        UserModel userModel=userService.getUserById(userId);
        if(userModel==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户信息不存在");

        }
        if(amount<=0||amount>99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"数量信息不正确");

        }

        //校验活动信息
        if(promoId != null){
            //(1)校验对应活动是否存在这个适用商品
            if(promoId.intValue() != itemModel.getPromoModel().getId()){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动信息不正确");
            //(2)校验活动是否在进行中
            }else if(itemModel.getPromoModel().getStatus().intValue()!=2){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动还未开始");
            }
        }

        //2.落单减库存
        boolean result = itemService.decreaseStock(itemId,amount);
        if(!result){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }


        //3.订单入库
        OrderModel orderModel=new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        if(promoId != null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else {
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));

        //生成交易流水号,交易订单号
        String io=generateOrderNo();
        orderModel.setId(io);
        OrderDO orderDO=convertFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);

        //加上商品销量
        itemService.increaseSales(itemId,amount);

        //4.返回前端
        return orderModel;
    }


    //获取订单号
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    String generateOrderNo(){
        //1.订单号有16位
        StringBuilder stringBuilder = new StringBuilder();
        //前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);

        //2.中间六位为自增序列
        //获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        //按步长更新数据库
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);

        //凑足六位
        String sequenceStr = String.valueOf(sequence);
        for (int i = 0; i < 6-sequenceStr.length(); i++){
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);


        //3.最后两位分库分表位 00~99, 暂时写死
        stringBuilder.append("00");

        return stringBuilder.toString();
    }


    private OrderDO convertFromOrderModel(OrderModel orderModel){

        if(orderModel==null){
            return null;
        }
        OrderDO orderDO=new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderDO;
    }
}
