package com.hackhu.seckill.service.impl;

import com.hackhu.seckill.dao.ItemMapper;
import com.hackhu.seckill.dao.ItemStockMapper;
import com.hackhu.seckill.dto.ItemDTO;
import com.hackhu.seckill.dto.ItemStockDTO;
import com.hackhu.seckill.error.BusinessErrorEnum;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.service.ItemService;
import com.hackhu.seckill.service.model.ItemModel;
import com.hackhu.seckill.validator.ValidatorImpl;
import com.hackhu.seckill.validator.ValidatorResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hackhu
 * @date 2020/2/27
 */
@Service
@Transactional
public class ItemServiceImpl implements ItemService {
    @Resource
    private ItemMapper itemMapper;
    @Resource
    private ItemStockMapper itemStockMapper;
    @Resource
    private ValidatorImpl validator;
    @Override
    public boolean createItem(ItemModel itemModel) throws BusinessException {
        // 校验 itemModel 参数合法性
        ValidatorResult validatorResult = validator.validate(itemModel);
        if (validatorResult.isHasErrors()) {
            throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR, validatorResult.getErrorMsg());
        }
        // 写入数据库
        ItemDTO itemDTO = convertItemDTOFromItemModel(itemModel);
        int result = itemMapper.insert(itemDTO);
        if (result < 1) {
            return false;
        }
        ItemStockDTO itemStockDTO = convertItemStockDTOFromItemModel(itemModel);
        result = itemStockMapper.insert(itemStockDTO);
        return result > 0;
    }

    /**
     * 将 itemModel 转换为 itemDTO
     */
    private ItemDTO convertItemDTOFromItemModel(ItemModel itemModel) {
        ItemDTO itemDTO = new ItemDTO();
        BeanUtils.copyProperties(itemModel, itemDTO);
        // 处理浮点型数据精度丢失问题
        itemDTO.setPrice(itemModel.getPrice().doubleValue());
        
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
        return null;
    }

    @Override
    public ItemModel getItemDetailById(Integer itemId) {
        return null;
    }
}
