package com.atguigu.product.vo;

import com.atguigu.product.entity.ProductAttrValueEntity;
import lombok.Data;

import java.util.List;

@Data
public class AttrAndGroupVO {

    private String attrGroupName;
    private List<ProductAttrValueEntity> attrs;
}
