package com.atguigu.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AddSpuVO {

    private Bounds bounds;
    //分组id
    private Long catalogId;

    //基本属性集合
    private List<SpuAttrValueVO> baseAttrs;
    //品牌id
    private Long brandId;
    //商品描述照片
    private List<String> decript;
    //商品照片
    private List<String> images;
    //发布状态
    private Integer publishStatus;
    //sku信息
    private List<SkuAllProperties> skus;
    //商品标题
    private String spuDescription;
    //商品名字
    private String spuName;
    //商品重量
    private BigDecimal weight;

}

