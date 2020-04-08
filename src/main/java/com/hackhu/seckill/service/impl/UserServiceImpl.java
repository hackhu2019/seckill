package com.hackhu.seckill.service.impl;

import com.hackhu.seckill.dao.UserDTOMapper;
import com.hackhu.seckill.dao.UserPasswordDTOMapper;
import com.hackhu.seckill.dto.UserDTO;
import com.hackhu.seckill.dto.UserPasswordDTO;
import com.hackhu.seckill.error.BusinessErrorEnum;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.service.UserService;
import com.hackhu.seckill.service.model.UserModel;
import com.hackhu.seckill.validator.ValidatorImpl;
import com.hackhu.seckill.validator.ValidatorResult;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

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
    @Resource
    private ValidatorImpl validator;
    @Resource
    private RedisTemplate redisTemplate;
    private String cachePrefix = "user_validate_";
    @Override
    public UserModel getUserById(Integer id) {
        return getUserByIdInCache(id);
    }

    @Override
    @Transactional
    public boolean register(UserModel userModel) throws BusinessException {
        // 校验数据有效性
        ValidatorResult validatorResult = validator.validate(userModel);
        if (validatorResult.isHasErrors()) {
            throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR,validatorResult.getErrorMsg());
        }
        // 数据库中添加数据
        UserDTO userDTO = convertUserDTOFromUserModel(userModel);

        int result = 0;
        try {
            result = userDTOMapper.insertSelective(userDTO);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR, "手机号不能重复");
        }
        if (result < 1) {
            return false;
        }
        UserPasswordDTO userPasswordDTO = convertUserPasswordDTOFromUserModel(userModel);
        result = userPasswordDTOMapper.insert(userPasswordDTO);
        return result>0;
    }

    @Override
    public UserModel login(String telephone, String password) throws BusinessException {
        UserDTO userDTO = userDTOMapper.selectByTelephone(telephone);
        if (userDTO == null) {
            return null;
        }
        UserPasswordDTO userPasswordDTO = userPasswordDTOMapper.selectByUserId(userDTO.getId());
        return userPasswordDTO != null && password.equals(userPasswordDTO.getEncrptPassword())?convertFromUserModel(userDTO,userPasswordDTO):null;
    }

    @Override
    public UserModel getUserByIdInCache(Integer userId) {
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(cachePrefix + userId);
        if (userModel == null) {
            userModel = convertFromUserModel(userDTOMapper.selectByPrimaryKey(userId), userPasswordDTOMapper.selectByUserId(userId));
            redisTemplate.opsForValue().set(cachePrefix + userId, userModel);
            redisTemplate.expire(cachePrefix + userId, 10, TimeUnit.MINUTES);
        }
        return userModel;
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
