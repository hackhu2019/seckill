package com.hackhu.seckill.service.impl;

import com.hackhu.seckill.dao.ItemDTOMapper;
import com.hackhu.seckill.dao.ItemStockDTOMapper;
import com.hackhu.seckill.dao.StockLogDTOMapper;
import com.hackhu.seckill.dto.ItemDTO;
import com.hackhu.seckill.dto.ItemStockDTO;
import com.hackhu.seckill.dto.StockLogDTO;
import com.hackhu.seckill.error.BusinessErrorEnum;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.mq.RocketMQProducer;
import com.hackhu.seckill.service.ItemService;
import com.hackhu.seckill.service.model.ItemModel;
import com.hackhu.seckill.validator.ValidatorImpl;
import com.hackhu.seckill.validator.ValidatorResult;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author hackhu
 * @date 2020/2/27
 */
@Service
@Transactional
public class ItemServiceImpl implements ItemService {
    @Resource
    private ItemDTOMapper itemDTOMapper;
    @Resource
    private ItemStockDTOMapper itemStockDTOMapper;
    @Resource
    private ValidatorImpl validator;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private StockLogDTOMapper stockLogDTOMapper;
    @Resource
    private RocketMQProducer rocketMQProducer;
    private String cachePrefix = "item_validate_";
    @Override
    public boolean createItem(ItemModel itemModel) throws BusinessException {
        // 校验 itemModel 参数合法性
        ValidatorResult validatorResult = validator.validate(itemModel);
        if (validatorResult.isHasErrors()) {
            throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR, validatorResult.getErrorMsg());
        }
        // 写入数据库
        ItemDTO itemDTO = convertItemDTOFromItemModel(itemModel);
        int result = itemDTOMapper.insertSelective(itemDTO);
        if (result < 1) {
            return false;
        }
        ItemStockDTO itemStockDTO = convertItemStockDTOFromItemModel(itemModel);
        result = itemStockDTOMapper.insertSelective(itemStockDTO);
        return result > 0;
    }

    /**
     * 将 itemModel 转换为 itemDTO
     */
    private ItemDTO convertItemDTOFromItemModel(ItemModel itemModel) {
        ItemDTO itemDTO = new ItemDTO();
        BeanUtils.copyProperties(itemModel, itemDTO);
        return itemDTO;
    }
    /**
     * 将 itemModel 转换为 itemStockDTO
     */
    private ItemStockDTO convertItemStockDTOFromItemModel(ItemModel itemModel) {
        ItemStockDTO itemDTO = new ItemStockDTO();
        itemDTO.setItemId(itemModel.getId());
        itemDTO.setStock(itemModel.getStock());
        return itemDTO;
    }
    @Override
    public List<ItemModel> getItemList() {
        List<ItemDTO> itemDTOS = itemDTOMapper.selectAll();
        List<ItemStockDTO> itemStockDTOS = itemStockDTOMapper.selectAll();
        return convertItemModelListFromItemDTOListAndItemStockDTOList(itemDTOS, itemStockDTOS);
    }

    @Override
    public ItemModel getItemDetailById(Integer itemId) throws BusinessException {
        return getItemByIdInCache(itemId);
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
        Long resultLong = redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue() * -1);
        if (resultLong > 0) {
            // 库存更新成功
            return true;
        } else if (resultLong == 0) {
            // 库存已空
            redisTemplate.opsForValue().set("promo_item_stock_invalid_" + itemId, "true");
            return true;
        } else {
            // 更新库存异常
            increaseStock(itemId, amount);
            return false;
        }
    }

    @Override
    public boolean asyncDecreaseStock(Integer itemId, Integer amount) throws BusinessException {
        boolean result = rocketMQProducer.asyncReduceStock(itemId, amount);
        return result;
    }

    @Override
    public boolean increaseStock(Integer itemId, Integer amount) throws BusinessException {
        redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue());
        return true;
    }
    
    @Override
    @Transactional
    public boolean increaseSale(Integer itemId, Integer amount) throws BusinessException {
        boolean result = itemDTOMapper.increaseSales(itemId, amount);
        return result;
    }

    @Override
    public String initStockLog(Integer itemId, Integer amount) {
        StockLogDTO stockLogDTO = new StockLogDTO();
        stockLogDTO.setItemId(itemId);
        stockLogDTO.setAmount(amount);
        stockLogDTO.setStockLogId(UUID.randomUUID().toString().replace("-", ""));
        stockLogDTO.setStatus(1);
        stockLogDTOMapper.insertSelective(stockLogDTO);
        return stockLogDTO.getStockLogId();
    }

    @Override
    public ItemModel getItemByIdInCache(Integer itemId) throws BusinessException {
        ItemModel itemModel = (ItemModel) redisTemplate.opsForValue().get(cachePrefix + itemId);
        if (itemId == null) {
            ItemDTO itemDTO = itemDTOMapper.selectByPrimaryKey(itemId);
            if (itemDTO == null) {
                throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR, "商品id错误");
            }
            ItemStockDTO itemStockDTO = itemStockDTOMapper.selectByItemId(itemId);
            itemModel = convertItemModelFromItemDTOAndItemStockDTO(itemDTO, itemStockDTO);
            redisTemplate.opsForValue().set(cachePrefix, itemModel);
            redisTemplate.expire(cachePrefix + itemId, 10, TimeUnit.MINUTES);
        }
        return itemModel;
    }

    /**
     * 将 itemDTOList、itemStockDTOList 转换为 itemModelList
     */
    private List<ItemModel> convertItemModelListFromItemDTOListAndItemStockDTOList(List<ItemDTO> itemDTOList, List<ItemStockDTO> itemStockDTOList) {
        List<ItemModel> itemModels = new ArrayList<>();
        if (itemDTOList != null ||
                itemStockDTOList != null ||
                itemDTOList.size() > 0 ||
                itemStockDTOList.size() > 0) {
            for (int i = 0; i < itemDTOList.size(); i++) {
                itemModels.add(convertItemModelFromItemDTOAndItemStockDTO(itemDTOList.get(i), itemStockDTOList.get(i)));
            }
        }
        return itemModels;
    }
        /**
         * 将 itemDTO、itemStockDTO 转换为 itemModel
         */
    private ItemModel convertItemModelFromItemDTOAndItemStockDTO(ItemDTO itemDTO, ItemStockDTO itemStockDTO) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDTO, itemModel);
        itemModel.setStock(itemStockDTO.getStock());
        return itemModel;
    }
}
