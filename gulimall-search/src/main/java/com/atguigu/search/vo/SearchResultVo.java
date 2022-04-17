package com.atguigu.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchResultVo {

    private List<Brand> brands;

    private List<Catelog> catelogs;

    private List<Attr>  attrs;

    private List<Product> products;

    //页码
    private Long pageNum;

    //总页数
    private Long totalPages;

    //总记录数
    private Long total;

}
