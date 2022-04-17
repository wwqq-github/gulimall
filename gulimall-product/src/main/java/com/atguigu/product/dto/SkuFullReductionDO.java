package com.atguigu.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuFullReductionDO {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;
    /**
     * spu_id
     */
    private Long skuId;
    /**
     * 满多少
     */
    private BigDecimal fullPrice;
    /**
     * 减多少
     */
    private BigDecimal reducePrice;
    /**
     * 是否参与其他优惠
     */
    private Integer addOther;
}
