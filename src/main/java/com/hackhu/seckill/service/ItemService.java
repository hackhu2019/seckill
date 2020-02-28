package com.hackhu.seckill.service;

import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.service.model.ItemModel;

import java.util.List;

/**
 * @author hackhu
 * @date 2020/2/27
 */
public interface ItemService {
    /**
     * 创建商品
     */
    boolean createItem(ItemModel itemModel) throws BusinessException;

    /**
     * 商品列表浏览
     */
    List<ItemModel> getItemList();

    /**
     * 商品详情展示
     */
    ItemModel getItemDetailById(Integer itemId) throws BusinessException;
}
