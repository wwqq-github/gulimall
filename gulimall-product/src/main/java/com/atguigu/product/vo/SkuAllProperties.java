package com.atguigu.product.vo;

import com.atguigu.product.entity.AttrEntity;
import com.atguigu.product.entity.SkuSaleAttrValueEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuAllProperties{
    //属性
    private List<SkuSaleAttrValueEntity> attr;
    //计数优惠是否叠加
    private Integer countStatus;
    //商品信息
    private List<String> descar;
    //sku图片
    private List<SkuImage> images;
    //sku的价格
    private BigDecimal skuPrice;
    //打折信息
    private BigDecimal discount;
    //会员价
    private List<MemberPriceVO> memberPrice;
    //打折要求
    private Integer fullCount;
    //满减的要求
    private BigDecimal fullPrice;
    //满减价格
    private BigDecimal reducePrice;
    //满减优惠是否叠加
    private Integer priceStatus;
    private String skuName;
    private String skuSubtitle;
    private String skuTitle;
}