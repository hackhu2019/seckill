package com.hackhu.seckill.service.impl;

import com.hackhu.seckill.dao.UserDTOMapper;
import com.hackhu.seckill.dao.UserPasswordDTOMapper;
import com.hackhu.seckill.dto.UserDTO;
import com.hackhu.seckill.dto.UserPasswordDTO;
import com.hackhu.seckill.service.UserService;
import com.hackhu.seckill.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author hackhu
 * @date 2020/2/24
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDTOMapper userDTOMapper;
    @Resource
    private UserPasswordDTOMapper userPasswordDTOMapper;
    @Override
    public UserModel getUserById(Integer id) {
        UserDTO userDTO = userDTOMapper.selectByPrimaryKey(id);
        if (userDTO == null) {
            return null;
        }
        UserPasswordDTO userPasswordDTO = userPasswordDTOMapper.selectByUserId(id);
        return convertFromUserModel(userDTO,userPasswordDTO);
    }

    // 创建 UserDTO+UserPasswordDTO 转换为 UserModel 方法
    private UserModel convertFromUserModel(UserDTO userDTO, UserPasswordDTO userPasswordDTO) {
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDTO, userModel);
        if (userPasswordDTO == null) {
            userModel.setPassword(userPasswordDTO.getEncrptPassword());
        }
        return userModel;
    }
}
