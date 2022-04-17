package com.atguigu.product.vo;

import com.atguigu.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

@Data
public class BaseAttrVO {
    private String attrGroupName;
    private List<AttrEntity> attrs;
}
