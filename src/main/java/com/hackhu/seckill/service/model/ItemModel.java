package com.hackhu.seckill.service.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author hackhu
 * @date 2020/2/26
 */
@Data
public class ItemModel {
    private Integer id;
    // 商品名称
    private String title;
    // 商品价格
    @NotNull(message = "商品价格不能为空")
    @Min(value = 0, message = "商品价格不能为负数")
    private BigDecimal price;
    // 商品库存
    @NotNull(message = "商品库存不能为空")
    @Min(value = 0, message = "商品库存不能为负数")
    private Integer stock;
    // 商品描述
    @NotNull(message = "商品描述不能为空")
    private String description;
    // 商品销量
    @NotNull(message = "商品销量不能为空")
    private Integer sales;
    // 商品图片地址
    @NotNull(message = "商品图片地址不能为空")
    private String imgUrl;
    
    
}
