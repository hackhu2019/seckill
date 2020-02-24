package com.hackhu.seckill.controller;

import com.hackhu.seckill.controller.viewobject.UserVO;
import com.hackhu.seckill.error.BusinessErrorEnum;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.response.CommonReturnType;
import com.hackhu.seckill.service.UserService;
import com.hackhu.seckill.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hackhu
 * @date 2020/2/24
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @RequestMapping("/get")
    public CommonReturnType getUser(@RequestParam(name = "id")Integer id) throws BusinessException {
        UserModel userModel = userService.getUserById(id);
        if (userModel == null) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_EXIST);
        }
        UserVO userVO = convertFromModel(userModel);
        return CommonReturnType.create(userVO);
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

    /**
     * 定义 exceptionHandler 处理未被 controller 层吸收的 exception
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Object exceptionHandler(HttpServletRequest request, Exception e) {
        Map<String, Object> responseMap = new HashMap<>();
        if (e instanceof BusinessException) {
            BusinessException businessException = (BusinessException) e;
            responseMap.put("errorCode", businessException.getErrorCode());
            responseMap.put("errorMsg", businessException.getErrorMsg());
        } else {
            responseMap.put("errorCode", BusinessErrorEnum.UNKNOW_ERROR.getErrorCode());
            responseMap.put("errorMsg", BusinessErrorEnum.UNKNOW_ERROR.getErrorMsg());
        } 
        CommonReturnType commonReturnType = CommonReturnType.create(responseMap, "fail");
        return commonReturnType;
    }
}
