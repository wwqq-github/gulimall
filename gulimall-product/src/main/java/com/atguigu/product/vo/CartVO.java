package com.atguigu.product.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartVO {

    //skuId
    private Long skuId;
    //sku标题
    private String skuTitle;
    //商品价格
    private BigDecimal skuPrice;
    //商品数量
    private Integer skuNum;
    //默认照片
    private String skuDefaultImg;
    //是否选中
    private Boolean check=false;
    //销售属性值
    private String skuAttrsVals;

}
