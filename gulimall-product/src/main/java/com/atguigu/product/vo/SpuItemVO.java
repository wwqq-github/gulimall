package com.atguigu.product.vo;

import com.atguigu.product.entity.SkuInfoEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class SpuItemVO {
    private SkuInfoEntity skuInfo;

    private List<AttrAndGroupVO> attrAndGroupVOS;

    private List<String> spuDecriptImage;

    private List<String> skuImage;

    private List<SellAttr> sellAttrs;
}
