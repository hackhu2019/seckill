package com.hackhu.seckill.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.hackhu.seckill.dao.PromoDTOMapper;
import com.hackhu.seckill.dto.PromoDTO;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.service.ItemService;
import com.hackhu.seckill.service.PromoService;
import com.hackhu.seckill.service.model.ItemModel;
import com.hackhu.seckill.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author hackhu
 * @date 2020/3/11
 */
public class PromoServiceImpl implements PromoService {
    @Resource
    private PromoDTOMapper promoDTOMapper;
    private String cachePrefix = "promo_item_stock_";
    @Resource
    private ItemService itemService;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        // 获取秒杀商品信息
        PromoDTO promoDTO = promoDTOMapper.selectByItemId(itemId);
        PromoModel promoModel = convertFromPromoDTO(promoDTO);
        if (promoModel == null) {
            return null;
        }
        // 判断当前秒杀活动是否正在进行
        if (promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(1);
        } else if (promoModel.getStartDate().isBeforeNow()) {
            promoModel.setStatus(3);
        } else {
            promoModel.setStatus(2);
        } 
        return null;
    }

    @Override
    public void publicPromo(Integer promoId) throws BusinessException {
        PromoDTO promoDTO = promoDTOMapper.selectByItemId(promoId);
        if (promoDTO.getItemId() == null || promoDTO.getItemId().intValue() == 0) {
            return;
        }
        ItemModel itemModel = itemService.getItemDetailById(promoDTO.getItemId());
        // 同步至缓存
        redisTemplate.opsForValue().set(cachePrefix + itemModel.getId(), itemModel.getStock());
    }

    private PromoModel convertFromPromoDTO(PromoDTO promoDTO) {
        if (promoDTO == null) {
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDTO, promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDTO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDTO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDTO.getEndDate()));
        return promoModel;
    }
}
