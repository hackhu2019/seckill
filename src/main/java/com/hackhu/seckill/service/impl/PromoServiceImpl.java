package com.hackhu.seckill.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.hackhu.seckill.dao.ItemDTOMapper;
import com.hackhu.seckill.dao.PromoDTOMapper;
import com.hackhu.seckill.dto.ItemDTO;
import com.hackhu.seckill.dto.PromoDTO;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.service.ItemService;
import com.hackhu.seckill.service.PromoService;
import com.hackhu.seckill.service.UserService;
import com.hackhu.seckill.service.model.ItemModel;
import com.hackhu.seckill.service.model.PromoModel;
import com.hackhu.seckill.service.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    @Resource
    private ItemDTOMapper itemDTOMapper;
    @Resource
    private UserService userService;
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
        // 限制秒杀令牌生成数
        redisTemplate.opsForValue().set("promo_door_count" + promoId, itemModel.getStock() * 5);
    }

    @Override
    public String generateSeckillToken(Integer promId, Integer itemId, Integer userId) {
        // 判断库存是否售罄
        if (redisTemplate.hasKey("promo_item_stock_invalid_" + itemId)) {
            return null;
        }
        PromoDTO promoDTO = promoDTOMapper.selectByPrimaryKey(promId);
        PromoModel promoModel = convertFromPromoDTO(promoDTO);
        if (promId == null) {
            return null;
        }
        // 判断当前时间秒杀活动是否有效
        if (promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(1);
        } else if (promoModel.getStartDate().isBeforeNow()) {
            promoModel.setStatus(3);
        } else {
            promoModel.setStatus(2);
        }
        if (promoModel.getStatus().intValue() != 2) {
            return null;
        }
        // 判断 item 信息是否正确
        ItemDTO itemDTO = itemDTOMapper.selectByPrimaryKey(itemId);
        if (!promoModel.getItemId().equals(itemId) || itemDTO == null) {
            return null;
        }
        // 判断用户信息是否存在
        UserModel userModel = userService.getUserById(userId);
        if (userModel == null) {
            return null;
        }
        // 判断当前令牌数是否已全部消耗
        Integer tokenCount = (Integer) redisTemplate.opsForValue().get("promo_door_count" + promId);
        if (tokenCount < 1) {
            return null;
        }
        // 生成秒杀令牌
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisTemplate.opsForValue().set("promo_token_" + promId + "_userid_" + userId + "_itemid_" + itemId, token);
        redisTemplate.expire("promo_token_" + promId + "_userid_" + userId + "_itemid_" + itemId, 5, TimeUnit.MINUTES);
        return token;
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
