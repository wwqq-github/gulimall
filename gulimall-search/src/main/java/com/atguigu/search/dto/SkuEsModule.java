package com.atguigu.search.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuEsModule {

    private Long skuId;
    /**
     * spuId
     */
    private Long spuId;
    /**
     * 标题
     */
    private String skuTitle;

    /**
     * 价格
     */
    private BigDecimal skuPrice;
    /**
     * 销量
     */
    private Long saleCount;

    /**
     * 默认图片
     */
    private String skuDefaultImg;

    /**
    *是否有货
     */
    private  Boolean hasStock;

    /**
     * 热度评分
     */
    private Integer hotScore;

    /**
     * 所属分类id
     */
    private Long catalogId;
    /**
     * 品牌id
     */
    private Long brandId;


    /**
     * 所属分类名字
     */
    private String catalogName;
    /**
     * 品牌id名字
     */
    private String brandName;

    /**
     * 品牌logo地址
     */
    private String brandImg;

    /**
     * id
     */
    private Long wareId;
    /**
     * 仓库地址
     */
    private String wareAddress;

    private List<ProductAttrValueVO> attrs;

}
