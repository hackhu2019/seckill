package com.hackhu.seckill.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.hackhu.seckill.dao.*;
import com.hackhu.seckill.dto.*;
import com.hackhu.seckill.error.BusinessErrorEnum;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.service.OrderService;
import com.hackhu.seckill.service.model.OrderModel;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author hackhu
 * @date 2020/3/1
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderInfoDTOMapper orderInfoDTOMapper;
    @Resource
    private UserDTOMapper userDTOMapper;
    @Resource
    private ItemDTOMapper itemDTOMapper;
    @Resource
    private ItemStockDTOMapper itemStockDTOMapper;
    @Resource
    private SequenceInfoDTOMapper sequenceInfoDTOMapper;
    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException {
        // 校验订单合法性，商品是否存在，用户是否合法、购买数量是否超出库存
        UserDTO userDTO= userDTOMapper.selectByPrimaryKey(userId) ;
        if (userDTO==null) {
            throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR,"用户不存在");
        }
        ItemDTO itemDTO = itemDTOMapper.selectByPrimaryKey(itemId);
        if (itemDTO == null) {
            throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR,"商品不存在");
        }
        ItemStockDTO itemStockDTO = itemStockDTOMapper.selectByItemId(itemId);
        if (itemStockDTO.getStock()<amount){
            throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR,"商品库存不足");
        }
        // 校验通过，落单减库存
        int result = itemStockDTOMapper.decreaseStock(itemId, amount);
        if (result<1){
            throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR, "库存减少失败");
        }
        // 订单入库
        OrderInfoDTO orderInfoDTO = new OrderInfoDTO();
        orderInfoDTO.setItemId(itemId);
        orderInfoDTO.setItemPrice(itemDTO.getPrice().doubleValue());
        orderInfoDTO.setOrderPrice(itemDTO.getPrice().multiply(new BigDecimal(amount)).doubleValue());
        // 创建订单流水号
        
        return convertOrderModerFromOrderInfo(orderInfoDTO);
    }

    /**
     * 将 orderInfo 转换为 orderModel
     */
    private OrderModel convertOrderModerFromOrderInfo(OrderInfoDTO orderInfoDTO) {
        OrderModel orderModel = new OrderModel();
        BeanUtils.copyProperties(orderInfoDTO, orderModel);
        return orderModel;
    }
    /**
     * 订单流水号创建
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    String generateOrderNO(){
         // 订单号为 16 位
        StringBuilder orderNO=new StringBuilder();
        // 订单号前 8 位为年月日
        LocalDateTime now=LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        orderNO.append(nowDate);
        // 中间 6 位为自增序列
        SequenceInfoDTO sequenceInfoDTO = sequenceInfoDTOMapper.getSequenceByName("order_info");
        Integer sequence = sequenceInfoDTO.getCurrentValue();
        // 更新序列
        sequenceInfoDTO.setCurrentValue(sequence + sequenceInfoDTO.getStep());
        // 当前自增序列不足 6 位则补齐
        String fill = sequence.toString();
        for (int i = 0; i < 6-fill.length(); i++) {
            fill+="0";
        }
        orderNO.append(fill);
        // 分库分表位
        orderNO.append("00");
        return orderNO.toString();
    }
}