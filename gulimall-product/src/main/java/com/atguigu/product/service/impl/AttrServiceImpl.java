package com.atguigu.product.service.impl;

import com.atguigu.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.enums.SqlLike;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.product.dao.AttrDao;
import com.atguigu.product.entity.AttrEntity;
import com.atguigu.product.service.AttrService;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils getAttrList(String type, Long attrId,Map<String, Object> params) {
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<AttrEntity>();
        attrEntityQueryWrapper.eq("attr_type","base".equals(type)?1:0);
        if (!"".equals(params.get("key"))&&params.get("key")!=null){
            attrEntityQueryWrapper.eq("attr_name",params.get("key")).or().like("value_select",params.get("key"));
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                attrEntityQueryWrapper
        );

        return new PageUtils(page);
    }

}