package com.hackhu.seckill.service.impl;

import com.hackhu.seckill.dao.UserDTOMapper;
import com.hackhu.seckill.dao.UserPasswordDTOMapper;
import com.hackhu.seckill.dto.UserDTO;
import com.hackhu.seckill.dto.UserPasswordDTO;
import com.hackhu.seckill.error.BusinessErrorEnum;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.service.UserService;
import com.hackhu.seckill.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
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

    @Override
    public boolean register(UserModel userModel) throws BusinessException {
        // 校验数据有效性
        if (userModel == null ||
                StringUtils.isEmpty(userModel.getName())||
                userModel.getGender()==null||
                userModel.getAge()==null||
                StringUtils.isEmpty(userModel.getTelephone())||
                StringUtils.isEmpty(userModel.getPassword())) {
            throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR);
        }
        // 数据库中添加数据
        UserDTO userDTO = convertUserDTOFromUserModel(userModel);
        int result = userDTOMapper.insertSelective(userDTO);
        if (result < 1) {
            return false;
        }
        UserPasswordDTO userPasswordDTO = convertUserPasswordDTOFromUserModel(userModel);
        result = userPasswordDTOMapper.insert(userPasswordDTO);
        return result>0;
    }

    /**
     * 创建 UserModel to UserDTO 转换方法
     */
    private UserDTO convertUserDTOFromUserModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userModel, userDTO);
        return userDTO;
    }
    /**
     * 创建 UserModel to UserPasswordDTO 转换方法
     */
    private UserPasswordDTO convertUserPasswordDTOFromUserModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserPasswordDTO userPasswordDTO = new UserPasswordDTO();
        userPasswordDTO.setEncrptPassword(userModel.getPassword());
        userPasswordDTO.setUserId(userModel.getId());
        return userPasswordDTO;
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
