package com.hackhu.seckill.service;

import com.hackhu.seckill.dto.UserDTO;
import com.hackhu.seckill.service.model.UserModel;

/**
 * @author hackhu
 * @date 2020/2/24
 */
public interface UserService {
    UserModel getUserById(Integer id);
}
