package com.hackhu.seckill.service.model;

import lombok.Data;

/**
 * @author hackhu
 * @date 2020/2/24
 */
@Data
public class UserModel {
    private Integer id;
    private String name;
    private Byte gender;
    private Integer age;
    private String telephone;
    private String registerMode;
    private String thirdPartyId;
    private String password;
}
