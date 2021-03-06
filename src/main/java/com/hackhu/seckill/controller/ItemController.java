package com.hackhu.seckill.controller;

import com.hackhu.seckill.controller.viewobject.ItemVO;
import com.hackhu.seckill.controller.viewobject.Page;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.response.CommonReturnType;
import com.hackhu.seckill.service.CacheService;
import com.hackhu.seckill.service.ItemService;
import com.hackhu.seckill.service.PromoService;
import com.hackhu.seckill.service.model.ItemModel;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author hackhu
 * @date 2020/2/27
 */
@RestController
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class ItemController extends BaseController {
    @Resource
    private ItemService itemService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private CacheService cacheService;
    @Resource
    private PromoService promoService;


    @RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType create(ItemModel itemModel) throws BusinessException {
        boolean result = itemService.createItem(itemModel);
        return CommonReturnType.create(result);
    }

    @RequestMapping(value = "/detail", method = {RequestMethod.POST, RequestMethod.GET}, consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType detail(@RequestParam(name = "id") Integer itemId) throws BusinessException {
        ItemModel result = null;
        String itemKey = "item_" + itemId;
        // 根据商品 id 在本地缓存中获取 item
        result = (ItemModel) cacheService.getFromCommonCache(itemKey);
        // 本地缓存不存在则在 redis 中获取
        if (result == null) {
            result = (ItemModel) redisTemplate.opsForValue().get(itemKey);
            // redis 不存在则在数据库中查找
            if (result == null) {
                result = itemService.getItemDetailById(itemId);
                redisTemplate.opsForValue().set(itemKey, result);
                redisTemplate.expire(itemKey, 10, TimeUnit.MINUTES);
            }
            cacheService.setCommonCache(itemKey, result);
        }
        ItemVO itemVO = convertItemVOFromItemModel(result);
        return CommonReturnType.create(itemVO);
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.GET}, consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType delete(@RequestParam(name = "id") Integer itemId) throws BusinessException {
        itemService.deleteById(itemId);
        return CommonReturnType.create(null);
    }

    @RequestMapping(value = "/getAll", method = {RequestMethod.POST, RequestMethod.GET}, consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType getAll() throws BusinessException {
        List<ItemModel> itemList = itemService.getItemList();
        List<ItemVO> itemVOS = itemList.stream().map(itemModel -> {
            ItemVO itemVO = convertItemVOFromItemModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOS);
    }

    @RequestMapping(value = "/find", method = {RequestMethod.GET})
    public CommonReturnType find(@RequestParam(name = "page") Integer page, @RequestParam(name = "pageSize") Integer pageSize) throws BusinessException {
        Page pageInfo = new Page();
        pageInfo.setSize((long) itemService.getItemList().size());
        pageInfo.setObject(itemService.getItemList(page, pageSize));
        return CommonReturnType.create(pageInfo);
    }

    private ItemVO convertItemVOFromItemModel(ItemModel itemModel) {
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        return itemVO;
    }
}
