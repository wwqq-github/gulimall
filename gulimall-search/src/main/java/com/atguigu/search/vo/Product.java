package com.atguigu.search.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Product {

    private Long skuId;
    private String skuImg;
    private BigDecimal skuPrice;
    private String skuTitle;
}
