package com.hackhu.seckill.response;

import lombok.Data;

/**
 * @author hackhu
 * @date 2020/2/24
 */
public class CommonReturnType {
    // 表明对应请求返回处理结果"success" 或 "fail"
    private String status;
    // 若 status = success 则返回前端需要的数据，否则对应的错误码
    private Object data;

    public static CommonReturnType create(Object data) {
        return CommonReturnType.create(data, "success");
    }

    public static CommonReturnType create(Object data,String status) {
        CommonReturnType commonReturnType = new CommonReturnType();
        commonReturnType.setData(data);
        commonReturnType.setStatus(status);
        return commonReturnType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
