package com.atguigu.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:21:09
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    List<CategoryBrandRelationEntity> queryList(Long brandId);

    PageUtils queryPage(Map<String, Object> params);

    void saveRelation(CategoryBrandRelationEntity categoryBrandRelation);

    List<CategoryBrandRelationEntity> findByCatId(Map<String, Object> params);
}

