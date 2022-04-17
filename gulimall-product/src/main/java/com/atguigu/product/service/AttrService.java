package com.atguigu.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:21:09
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils getAttrList(String type, Long attrId,Map<String, Object> params);
}

