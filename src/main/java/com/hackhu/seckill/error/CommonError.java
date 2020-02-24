package com.hackhu.seckill.error;

/**
 * @author hackhu
 * @date 2020/2/24
 */
public interface CommonError {
    int getErrorCode();

    String getErrorMsg();

    CommonError setErrorMsg(String errorMsg);
    
}
