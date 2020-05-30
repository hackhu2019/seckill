package com.hackhu.seckill.service;

import com.hackhu.seckill.controller.viewobject.Page;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.service.model.OrderModel;

/**
 * @author hackhu
 * @date 2020/3/1
 */
public interface OrderService {
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLogId) throws BusinessException;

    Page getAll(Integer index, Integer pageSize);
}
