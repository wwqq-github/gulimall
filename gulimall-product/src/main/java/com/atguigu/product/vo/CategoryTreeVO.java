package com.atguigu.product.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class CategoryTreeVO {

    private String name;
    private Long parentCid;
    private Long catId;
    private List<CategoryTreeVO> children;
}
