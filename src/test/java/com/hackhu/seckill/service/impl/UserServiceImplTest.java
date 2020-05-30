package com.hackhu.seckill.service.impl;

import com.hackhu.seckill.dao.ItemDTOMapper;
import com.hackhu.seckill.dto.ItemDTO;
import com.hackhu.seckill.service.ItemService;
import com.hackhu.seckill.service.UserService;
import com.hackhu.seckill.service.model.ItemModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author hackhu
 * @date 2020/3/13
 */
@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserService userService;
    @Resource
    private ItemDTOMapper itemDTOMapper;
    @Resource
    private ItemService itemService;
    @Test
    void getUserById() {
        System.out.println(userService.getUserById(1));
    }

    @Test
    void getALl() {
        List<ItemDTO> itemDTOS = itemDTOMapper.find(0, 10);
        List<ItemDTO> itemDTOSS = itemDTOMapper.selectAll();
        List<ItemModel> itemList = itemService.getItemList(0, 10);
        System.out.printf(itemList.toString());
    }
}