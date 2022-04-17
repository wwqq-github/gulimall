package com.atguigu.product.dto;

import lombok.Data;

@Data
public class SkuUpDto {
    /**
     * skuId
     */
    private Long SkuId;

    /**
     * 是否有库存
     */
    private boolean hasStock;

    /**
     * 仓库id
     */
    private Long wareId;
    /**
     * 仓库地址
     */
    private String wareAddress;
}
