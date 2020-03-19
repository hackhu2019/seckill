package com.hackhu.seckill.service;

import com.hackhu.seckill.dto.UserDTO;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.service.model.UserModel;

/**
 * @author hackhu
 * @date 2020/2/24
 */
public interface UserService {
    UserModel getUserById(Integer id);

    boolean register(UserModel userModel) throws BusinessException;

    UserModel login(String telephone, String password) throws BusinessException;

    /**
     * 从缓存中获取用户对象
     */
    UserModel getUserByIdInCache(Integer userId);
}
