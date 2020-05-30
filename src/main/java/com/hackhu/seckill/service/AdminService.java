package com.hackhu.seckill.service;


import com.hackhu.seckill.dto.AdminDO;
import com.hackhu.seckill.error.BusinessException;

/**
 * @author hackhu
 * @date 2020/5/25
 */
public interface AdminService {
    AdminDO login(String name, String password) throws BusinessException;
}
