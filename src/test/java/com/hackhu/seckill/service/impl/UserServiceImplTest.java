package com.hackhu.seckill.service.impl;

import com.hackhu.seckill.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author hackhu
 * @date 2020/3/13
 */
@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserService userService;
    @Test
    void getUserById() {
        System.out.println(userService.getUserById(1));
    }
}