package com.hackhu.seckill.controller.viewobject;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author hackhu
 * @date 2020/2/28
 */
@Data
public class ItemVO {
    private Integer id;
    // 商品名称
    private String title;
    // 商品价格
    private BigDecimal price;
    // 商品库存
    private Integer stock;
    // 商品描述
    private String description;
    // 商品销量
    private Integer sales;
    // 商品图片地址
    private String imgUrl;
}
