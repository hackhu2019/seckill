package com.hackhu.seckill.service;

import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.service.model.PromoModel;

/**
 * @author hackhu
 * @date 2020/3/11
 */
public interface PromoService {
    PromoModel getPromoByItemId(Integer itemId);

    /**
     * 活动发布
     */
    void publicPromo(Integer promoId) throws BusinessException;

    /**
     * 秒杀令牌生成
     */
    String generateSeckillToken(Integer promId, Integer itemId, Integer userId);
}
