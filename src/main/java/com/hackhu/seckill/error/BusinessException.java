package com.hackhu.seckill.error;

/**
 * @author hackhu
 * @date 2020/2/24
 */
public class BusinessException extends Exception implements CommonError {
    private CommonError commonError;

    /**
     * 直接接收 CommonError 的传参以用于构造业务异常
     * @param commonError
     */
    public BusinessException(CommonError commonError) {
        super();
        this.commonError = commonError;
    }

    public BusinessException(CommonError commonError,String msg) {
        super();
        this.commonError = commonError;
        this.commonError.setErrorMsg(msg);
    }
    @Override
    public int getErrorCode() {
        return this.commonError.getErrorCode();
    }

    @Override
    public String getErrorMsg() {
        return this.commonError.getErrorMsg();
    }

    @Override
    public CommonError setErrorMsg(String errorMsg) {
        this.commonError.setErrorMsg(errorMsg);
        return this.commonError;
    }

    public CommonError getCommonError() {
        return commonError;
    }
}
