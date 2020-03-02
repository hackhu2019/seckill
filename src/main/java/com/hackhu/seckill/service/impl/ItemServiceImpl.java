package com.hackhu.seckill.service.impl;

import com.hackhu.seckill.dao.ItemDTOMapper;
import com.hackhu.seckill.dao.ItemStockDTOMapper;
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
import java.util.ArrayList;
import java.util.List;

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
        ItemDTO itemDTO = itemDTOMapper.selectByPrimaryKey(itemId);
        if (itemDTO == null) {
            throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR, "商品id错误");
        }
        ItemStockDTO itemStockDTO = itemStockDTOMapper.selectByItemId(itemId);
        ItemModel itemModel = convertItemModelFromItemDTOAndItemStockDTO(itemDTO, itemStockDTO);
        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
        int affectedRow =  itemStockDTOMapper.decreaseStock(itemId,amount);
        if(affectedRow > 0){
            //更新库存成功
            return true;
        }else{
            //更新库存失败
            return false;
        }
    }

    @Override
    public boolean increaseSale(Integer itemId, Integer amount) throws BusinessException {
        boolean result = itemDTOMapper.increaseSales(itemId, amount);
        return result;
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
