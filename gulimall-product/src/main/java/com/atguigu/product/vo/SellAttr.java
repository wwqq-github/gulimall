package com.atguigu.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class SellAttr {

    private Long attrId;
    private String attrName;
    private List<SellAndSkuVO> values;
}
