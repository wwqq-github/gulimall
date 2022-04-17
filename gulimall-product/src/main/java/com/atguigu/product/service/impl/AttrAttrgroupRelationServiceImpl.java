package com.atguigu.product.service.impl;

import com.atguigu.product.vo.AttrRelationVO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void findByttrAndGroupdelete(List<AttrAttrgroupRelationEntity>  relations) {
        for (AttrAttrgroupRelationEntity relation: relations) {
            Map<String, Object> columnMap=new HashMap<>();
            columnMap.put("attr_id",relation.getAttrId());
            columnMap.put("attr_group_id",relation.getAttrGroupId());
            this.removeByMap(columnMap);
        }

    }
}