package com.atguigu.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchVO {

    //页面
    private Long pageNum;

    private Long catelogId;
    private String keyword;
    private Long brandId;
    private List<String> attrs;
    //排序  hotScore_desc hotScore_asc
    //saleCount_desc  saleCount_asc
    //skuPrice_desc  skuPrice_asc
    private String sort;
    //价格筛选  skuPrice=100_1000
    private String skuPrice;
}
