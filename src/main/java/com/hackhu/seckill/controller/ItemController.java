package com.hackhu.seckill.controller;

import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.response.CommonReturnType;
import com.hackhu.seckill.service.ItemService;
import com.hackhu.seckill.service.model.ItemModel;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author hackhu
 * @date 2020/2/27
 */
@RestController
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class ItemController {
    @Resource
    private ItemService itemService;

    @RequestMapping("/create")
    public CommonReturnType create(ItemModel itemModel) throws BusinessException {
        boolean result = itemService.createItem(itemModel);
        return CommonReturnType.create(result);
    }
}
