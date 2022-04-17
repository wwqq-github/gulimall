package com.atguigu.product.dao;

import com.atguigu.product.entity.CategoryEntity;
import com.atguigu.product.vo.CategoryTreeVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品三级分类
 * 
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:21:07
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {

    List<CategoryTreeVO> selectListTree();

    List<CategoryTreeVO> getChildren(Long parentCid);
}
