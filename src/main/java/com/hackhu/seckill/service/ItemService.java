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

    List<ItemModel> getItemList(Integer index, Integer pageSize);

    /**
     * 商品详情展示
     */
    ItemModel getItemDetailById(Integer itemId) throws BusinessException;

    /**
     * 减少商品库存
     */
    boolean decreaseStock(Integer itemId,Integer amount) throws BusinessException;
    /**
     * 异步减少商品库存
     */
    boolean asyncDecreaseStock(Integer itemId,Integer amount) throws BusinessException;

    /**
     * 增加库存
     * @param itemId
     * @param amount
     * @return
     * @throws BusinessException
     */
    boolean increaseStock(Integer itemId, Integer amount) throws BusinessException;
    boolean increaseSale(Integer itemId, Integer amount) throws BusinessException;

    /**
     * 初始化库存流水
     */
    String initStockLog(Integer itemId, Integer amount);
    /**
     * 从缓存中获取 itemModel
     */
    ItemModel getItemByIdInCache(Integer itemId) throws BusinessException;

    void deleteById(Integer id) throws BusinessException;
}
