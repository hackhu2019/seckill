package com.hackhu.seckill.error;

/**
 * @author hackhu
 * @date 2020/2/24
 */
public enum BusinessErrorEnum implements CommonError {
    // 0000 开头为通用错误类型
    PARAMETER_VALIDATION_ERROR(00001,"参数不合法"),
    UNKNOW_ERROR(00002,"未知错误"),
    // 1000 开头为用户信息相关错误码
    USER_NOT_EXIST(10001, "用户不存在"),;

    private int errorCode;
    private String errorMsg;

    private BusinessErrorEnum(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg; 
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    @Override
    public CommonError setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }}
