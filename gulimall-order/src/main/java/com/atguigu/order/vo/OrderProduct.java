package com.atguigu.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderProduct {

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
    private Boolean check;
    //是否有货
    private Boolean  stock;
    //销售属性值
    private String skuAttrsVals;
}
