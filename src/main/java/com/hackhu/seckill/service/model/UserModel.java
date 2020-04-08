package com.hackhu.seckill.service.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author hackhu
 * @date 2020/2/24
 */
@Data
public class UserModel implements Serializable {
    private Integer id;
    @NotBlank(message = "用户名不能为空")
    private String name;
    @NotNull(message = "性别为必填项")
    private Integer gender;
    @NotNull(message = "年龄为必填项")
    @Max(value = 150,message = "请输入有效年龄")
    @Min(value = 0,message = "请输入有效年龄")
    private Integer age;
    @NotNull(message = "电话为必填项")
    private String telephone;
    private String registerMode;
    private String thirdPartyId;
    @NotNull(message = "密码为必填项")
    private String password;
}
