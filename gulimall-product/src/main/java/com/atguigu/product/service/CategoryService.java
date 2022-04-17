package com.atguigu.product.service;

import com.atguigu.product.vo.CategoryTreeVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:21:07
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryTreeVO> getListTree();

    List<CategoryEntity> getOneLevel();

    Map<String, List<CategoryTreeVO>> getCategoryJson();
}

