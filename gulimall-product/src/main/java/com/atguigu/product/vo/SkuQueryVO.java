package com.atguigu.product.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuQueryVO {

    private String page;
    private String limit;
    private String key;
    private Long catelogId;
    private Long brandId;
    private BigDecimal min;
    private BigDecimal max;
}
