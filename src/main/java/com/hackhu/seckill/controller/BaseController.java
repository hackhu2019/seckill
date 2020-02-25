package com.hackhu.seckill.controller;

import com.hackhu.seckill.error.BusinessErrorEnum;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hackhu
 * @date 2020/2/25
 */
public class BaseController {
    public static final String CONTENT_TYPE_FORMED = "application/x-www-form-urlencoded";
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
