package com.hackhu.seckill.controller;

import com.hackhu.seckill.error.BusinessErrorEnum;
import com.hackhu.seckill.error.BusinessException;
import com.hackhu.seckill.response.CommonReturnType;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hackhu
 * @date 2020/3/11
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonReturnType doError(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Exception ex) {
        ex.printStackTrace();
        Map<String,Object> responseData = new HashMap<>();
        if( ex instanceof BusinessException){
            BusinessException businessException = (BusinessException)ex;
            responseData.put("errCode",businessException.getErrorCode());
            responseData.put("errMsg",businessException.getErrorMsg());
        }else if(ex instanceof ServletRequestBindingException){
            responseData.put("errCode", BusinessErrorEnum.UNKNOWN_ERROR.getErrorCode());
            responseData.put("errMsg","url绑定路由问题");
        }else if(ex instanceof NoHandlerFoundException){
            responseData.put("errCode",BusinessErrorEnum.UNKNOWN_ERROR.getErrorCode());
            responseData.put("errMsg","没有找到对应的访问路径");
        }else{
            responseData.put("errCode", BusinessErrorEnum.UNKNOWN_ERROR.getErrorCode());
            responseData.put("errMsg",BusinessErrorEnum.UNKNOWN_ERROR.getErrorMsg());
        }
        return CommonReturnType.create(responseData,"fail");
    }
}
