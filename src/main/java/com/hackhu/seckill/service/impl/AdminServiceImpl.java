package com.hackhu.seckill.service.impl;

import com.hackhu.seckill.dao.AdminDOMapper;
import com.hackhu.seckill.dto.AdminDO;
import com.hackhu.seckill.error.BusinessErrorEnum;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.service.AdminService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author hackhu
 * @date 2020/5/25
 */
@Service
public class AdminServiceImpl implements AdminService {
    @Resource
    private AdminDOMapper adminDOMapper;

    @Override
    public AdminDO login(String name, String password) throws BusinessException {
        AdminDO adminDO = adminDOMapper.selectByName(name);
        if (adminDO == null) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_EXIST);
        }
        return adminDO;
    }
}
