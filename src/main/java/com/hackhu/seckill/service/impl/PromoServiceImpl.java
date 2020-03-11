package com.hackhu.seckill.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.hackhu.seckill.dao.PromoDTOMapper;
import com.hackhu.seckill.dto.PromoDTO;
import com.hackhu.seckill.service.PromoService;
import com.hackhu.seckill.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author hackhu
 * @date 2020/3/11
 */
public class PromoServiceImpl implements PromoService {
    @Resource
    private PromoDTOMapper promoDTOMapper;

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
