package com.hackhu.seckill.controller;

import com.hackhu.seckill.dto.AdminDO;
import com.hackhu.seckill.error.BusinessErrorEnum;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.error.CommonError;
import com.hackhu.seckill.response.CommonReturnType;
import com.hackhu.seckill.service.AdminService;
import com.hackhu.seckill.utils.EncodeUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author hackhu
 * @date 2020/5/25
 */
@RestController
@RequestMapping("/admin")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class AdminController extends BaseController {
    @Resource
    private AdminService adminService;
    @Resource
    private RedisTemplate redisTemplate;
    @RequestMapping(value = "/login",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name="name")String name,
                             @RequestParam(name="password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //入参校验
        if(org.apache.commons.lang3.StringUtils.isEmpty(name)||
                StringUtils.isEmpty(password)){
            throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR);
        }

        //用户登陆服务,用来校验用户登陆是否合法
        AdminDO adminDO = adminService.login(name, password);
        if (!adminDO.getPassword().equals(EncodeUtil.EncodeByMd5(password))) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_EXIST,"用户名密码不一致");
        }
        //将登陆凭证加入到用户登陆成功的session内

        //修改成若用户登录验证成功后将对应的登录信息和登录凭证一起存入redis中

        //生成登录凭证token，UUID
        String uuidToken = UUID.randomUUID().toString();
        uuidToken = uuidToken.replace("-","");
        //建议token和用户登陆态之间的联系
        redisTemplate.opsForValue().set(uuidToken,adminDO);
        redisTemplate.expire(uuidToken,1, TimeUnit.HOURS);

//        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
//        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);
        //下发了token
        return CommonReturnType.create(uuidToken);
    } 
}
