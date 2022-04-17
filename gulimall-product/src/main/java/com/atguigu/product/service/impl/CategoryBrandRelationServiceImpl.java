package com.atguigu.product.service.impl;

import com.atguigu.product.entity.AttrGroupEntity;
import com.atguigu.product.entity.BrandEntity;
import com.atguigu.product.entity.CategoryEntity;
import com.atguigu.product.service.BrandService;
import com.atguigu.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.product.dao.CategoryBrandRelationDao;
import com.atguigu.product.entity.CategoryBrandRelationEntity;
import com.atguigu.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),new QueryWrapper<CategoryBrandRelationEntity>()

        );

        return new PageUtils(page);
    }

    @Override
    public void saveRelation(CategoryBrandRelationEntity categoryBrandRelation) {
        CategoryEntity categoryEntity = categoryService.getById(categoryBrandRelation.getCatelogId());
        BrandEntity brandEntity = brandService.getById(categoryBrandRelation.getBrandId());
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        this.save(categoryBrandRelation);
    }

    @Override
    public List<CategoryBrandRelationEntity> findByCatId(Map<String, Object> params) {
        return this.baseMapper.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", params.get("catId")));
    }

    @Override
    public List<CategoryBrandRelationEntity> queryList(Long brandId) {

        return this.baseMapper.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
    }

}