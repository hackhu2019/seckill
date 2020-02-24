package com.hackhu.seckill.controller.viewobject;

import lombok.Data;

/**
 * @author hackhu
 * @date 2020/2/24
 */
@Data
public class UserVO {
    private Integer id;
    private String name;
    private Byte gender;
    private Integer age;
    private String telephone;
}
