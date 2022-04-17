package com.atguigu.ware.service.impl;

import com.atguigu.ware.vo.WareQueryVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.ware.dao.WareInfoDao;
import com.atguigu.ware.entity.WareInfoEntity;
import com.atguigu.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(WareQueryVO wareQueryVO) {
        Map<String, Object> params=new HashMap<>();
        params.put("page",wareQueryVO.getPage());
        params.put("limit",wareQueryVO.getLimit());
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(wareQueryVO.getName())){
            wrapper.eq("name",wareQueryVO.getName());
        }
        if (StringUtils.isNotEmpty(wareQueryVO.getAreacode())){
            wrapper.eq("areacode",wareQueryVO.getAreacode());
        }
        if (StringUtils.isNotEmpty(wareQueryVO.getAddress())){
            wrapper.like("address",wareQueryVO.getAddress());
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),wrapper
        );

        return new PageUtils(page);
    }

}