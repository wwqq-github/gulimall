package com.atguigu.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class Attr {

    private String attrName;
    private List<AttrValue> attrValues;
}
