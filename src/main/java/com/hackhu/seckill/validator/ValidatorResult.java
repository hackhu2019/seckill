package com.hackhu.seckill.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author hackhu
 * @date 2020/2/26
 */
public class ValidatorResult {
    // 校验结果
    private boolean hasErrors = false;

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public Map<String, String> getErrorMsgMap() {
        return errorMsgMap;
    }

    public String getErrorMsg() {
        return StringUtils.join(errorMsgMap.values().toArray(),",");
    }

    public void setErrorMsgMap(Map<String, String> errorMsgMap) {
        this.errorMsgMap = errorMsgMap;
    }

    // 错误信息
    private Map<String,String> errorMsgMap;

}
