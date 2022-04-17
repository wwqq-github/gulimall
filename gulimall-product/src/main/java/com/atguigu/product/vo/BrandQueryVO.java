package com.atguigu.product.vo;

import lombok.Data;

@Data
public class BrandQueryVO {
    private String page;
    private String limit;
    private String name;
    private String brandId;
}
