package com.atguigu.product.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {

    /**
     * spu_id
     */
    private Long spuId;
    /**
     * spu_name
     */
    private String spuName;
    /**
     * 品牌
     */
    private String spuBrand;
    /**
     * 商品分类id
     */
    private Long categoryId;
    /**
     * 商品sku编号
     */
    private Long skuId;
    /**
     * 商品sku名字
     */
    private String skuName;
    /**
     * 商品sku图片
     */
    private String skuPic;
    /**
     * 商品sku价格
     */
    private BigDecimal skuPrice;
}
