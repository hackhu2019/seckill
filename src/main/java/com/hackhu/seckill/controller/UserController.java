package com.hackhu.seckill.controller;

import com.alibaba.druid.util.StringUtils;
import com.hackhu.seckill.controller.viewobject.UserVO;
import com.hackhu.seckill.error.BusinessErrorEnum;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.error.CommonError;
import com.hackhu.seckill.response.CommonReturnType;
import com.hackhu.seckill.service.UserService;
import com.hackhu.seckill.service.model.UserModel;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author hackhu
 * @date 2020/2/24
 */
@RestController
@RequestMapping("/user")
// 处理跨域请求问题
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class UserController extends BaseController{
    @Resource
    private UserService userService;
    @Resource
    private HttpServletRequest httpServletRequest;
    @Resource
    private RedisTemplate redisTemplate;
    private String salt = "hack-hu";

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        UserModel userModel = userService.getUserById(id);
        if (userModel == null) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_EXIST);
        }
        UserVO userVO = convertFromModel(userModel);
        return CommonReturnType.create(userVO);
    }

    /**
     * 用户注册接口
     */
    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType regiseter(@RequestParam(name = "telephone") String telephone,
                                      @RequestParam(name = "otpCode") String otpCode,
                                      @RequestParam(name = "name") String name,
                                      @RequestParam(name = "gender") Integer gender,
                                      @RequestParam(name = "age") Integer age,
                                      @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 验证短信验证码是否正确
        String sessionOTPCode = (String) httpServletRequest.getSession().getAttribute(telephone);
        if (!StringUtils.equals(otpCode,sessionOTPCode)){
            throw new BusinessException(BusinessErrorEnum.PARAMETER_VALIDATION_ERROR, "短信验证码不一致");
        }
        // 注册用户
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setTelephone(telephone);
        userModel.setAge(age);
        userModel.setGender(gender);
        // 对用户密码加密存储
        userModel.setPassword(encodeByMD5(password));
        boolean result = userService.register(userModel);
        return result?CommonReturnType.create("注册成功"):CommonReturnType.create("注册失败");
    }

    /**
     * 用户登录接口
     */
    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType login(@RequestParam(name = "telephone") String telephone,
                                  @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 对用户密码加密存储
        UserModel result = userService.login(telephone, password);
        // 登录成功将凭证加入session
        if (result != null) {
            this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        }
        return CommonReturnType.create(result);
    }

    /**
     * 用户密码加密算法
     */
    private String encodeByMD5(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String encodedPassword = Base64.getEncoder().encodeToString(md5.digest((password + salt).getBytes("utf-8")));
        return encodedPassword;
    }
    /**
     * 用户获取otp短信接口
     */
    @RequestMapping(value = "/getotp",method = RequestMethod.POST,consumes = CONTENT_TYPE_FORMED)
    public CommonReturnType getOTP(@RequestParam(name = "telephone")String telephone) {
         // 按照规则生成OTP验证码
        Random rd = new Random();
        int randomInt = rd.nextInt(10000);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);
        // 将OTP验证码与用户手机号关联
        httpServletRequest.getSession().setAttribute(telephone, otpCode);
        // 将OTP验证码发送至用户
        System.out.println("telephone:" + telephone + ",otpCode:" + otpCode);
        return CommonReturnType.create(null);
    }
    /**
     * 将领域模型 UserModel 转换为 UserVO
     */
    private UserVO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }
}
