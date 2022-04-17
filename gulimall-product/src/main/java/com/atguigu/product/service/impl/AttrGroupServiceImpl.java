package com.atguigu.product.service.impl;

import com.atguigu.product.dao.AttrDao;
import com.atguigu.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.product.entity.AttrEntity;
import com.atguigu.product.service.AttrAttrgroupRelationService;
import com.atguigu.product.service.AttrService;
import com.atguigu.product.vo.BaseAttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.product.dao.AttrGroupDao;
import com.atguigu.product.entity.AttrGroupEntity;
import com.atguigu.product.service.AttrGroupService;
import org.springframework.web.bind.annotation.RequestParam;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<AttrEntity> getAttrRelation(Long attrGroupId,Map<String, Object> params) {
        //先是查找分组已经关联了那些属性
        List<AttrAttrgroupRelationEntity> relations = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId));
        //如果没有关联任何属性 直接返回空
        if(relations==null||relations.size()==0){
            return null;
        }
        //如果关联了属性 先收集属性的id
        List<Long> attrIds = relations.stream().map(attrEntity -> attrEntity.getAttrId()).collect(Collectors.toList());
        //构造查询条件
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper = wrapper.in("attr_id", attrIds);
        //查询
        List<AttrEntity> attrEntities = attrService.getBaseMapper().selectList(wrapper);
        return attrEntities;
    }

    @Override
    public PageUtils getNoAttrRelation(Long attrGroupId, Map<String, Object> params) {
        //查所有与分组有关联关系的属性
        List<AttrAttrgroupRelationEntity> relations = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId));
        //遍历所有的关联关系 获取属性的id
        List<Long> attrIds = relations.stream().map(attrEntity -> attrEntity.getAttrId()).collect(Collectors.toList());
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>();
        //没有找到任何关联关系
        if (attrGroupId==null||attrIds.size()==0){
            wrapper.eq("attr_type","1");
        }else{
            wrapper.eq("attr_type","1").notIn("attr_id", attrIds);
        }
        wrapper.eq("attr_group_id",attrGroupId);
        IPage<AttrEntity> page = attrService.page(
                new Query<AttrEntity>().getPage(params),wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<BaseAttrVO> withattr(Long catelogId, Map<String, Object> params) {
        //查询分类下所属的属性分组
        List<AttrGroupEntity> groupEntities = this.baseMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        //遍历所有的属性分组
        List<BaseAttrVO> baseAttrVOS=groupEntities.stream().map(item->{
            BaseAttrVO baseAttrVO=new BaseAttrVO();
            baseAttrVO.setAttrGroupName(item.getAttrGroupName());
            //查询分组的关联关系
            List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", item.getAttrGroupId()));
            //收集关联关系的id
            List<Long> attrIds = relationEntities.stream().map(relation -> {
                return relation.getAttrId();
            }).collect(Collectors.toList());
            QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
            if (attrIds.size()!=0&&attrIds!=null){
                wrapper.in("attr_id", attrIds);
            }else{
                wrapper.eq("attr_id",-1);
            }
            List<AttrEntity> attrEntities = attrService.list(wrapper);

            baseAttrVO.setAttrs(attrEntities);

            return baseAttrVO;
        }).collect(Collectors.toList());
        return baseAttrVOS;
    }

}