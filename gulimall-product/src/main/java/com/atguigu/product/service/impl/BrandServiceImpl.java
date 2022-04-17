package com.atguigu.product.service.impl;

import com.atguigu.product.vo.BrandQueryVO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.product.dao.BrandDao;
import com.atguigu.product.entity.BrandEntity;
import com.atguigu.product.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Override
    public PageUtils queryPage(BrandQueryVO brandQueryVO) {
        Map<String,Object> params=new HashMap<String,Object>();
        params.put("page",brandQueryVO.getPage());
        params.put("limit",brandQueryVO.getLimit());
        QueryWrapper<BrandEntity> brandEntityQueryWrapper = new QueryWrapper<>();
        if (brandQueryVO.getBrandId()!=null&&!"".equals(brandQueryVO.getBrandId())){
            brandEntityQueryWrapper.eq("brand_id",brandQueryVO.getBrandId());
        }
        if (brandQueryVO.getName()!=null&&!"".equals(brandQueryVO.getName())){
           brandEntityQueryWrapper.eq("name",brandQueryVO.getName());
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),brandEntityQueryWrapper

        );

        return new PageUtils(page);
    }

}