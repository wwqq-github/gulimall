package com.atguigu.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.ware.dto.SkuInfoDo;
import com.atguigu.ware.dto.SkuUpDto;
import com.atguigu.ware.feign.ProductFeign;
import com.atguigu.ware.vo.WarePurchaseitemVO;
import org.apache.commons.lang.StringUtils;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.ware.dao.PurchaseDetailDao;
import com.atguigu.ware.entity.PurchaseDetailEntity;
import com.atguigu.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {
    @Autowired
    private ProductFeign productFeign;
    @Override
    public PageUtils queryPage(WarePurchaseitemVO warePurchaseitemVO) {

        Map<String, Object> params=new HashMap<>();
        params.put("page",warePurchaseitemVO.getPage());
        params.put("limit",warePurchaseitemVO.getLimit());
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(warePurchaseitemVO.getKey())){
            wrapper.eq("sku_id",warePurchaseitemVO.getKey());
        }
        if (StringUtils.isNotEmpty(warePurchaseitemVO.getStatus())){
            wrapper.eq("status",warePurchaseitemVO.getStatus());
        }
        if (StringUtils.isNotEmpty(warePurchaseitemVO.getWareId())){
            wrapper.eq("ware_id",warePurchaseitemVO.getWareId());
        }
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),wrapper

        );

        return new PageUtils(page);
    }

    @Override
    public void savePurchaseDetail(PurchaseDetailEntity purchaseDetail) {
        R info = productFeign.info(purchaseDetail.getSkuId());
        SkuInfoDo data = (SkuInfoDo) info.getData("skuInfo", new TypeReference<SkuInfoDo>() {
        });
        PurchaseDetailEntity purchaseDetailEntity = this.baseMapper.selectOne(new QueryWrapper<PurchaseDetailEntity>().eq("sku_id", purchaseDetail.getSkuId()));
        BigDecimal pricet = data.getSkuPrice().multiply(new BigDecimal(purchaseDetail.getSkuNum() + ""));
        if (purchaseDetailEntity==null){
            purchaseDetail.setSkuPrice(pricet);
            this.save(purchaseDetail);
        }else {
            purchaseDetailEntity.setSkuPrice(pricet.add(purchaseDetailEntity.getSkuPrice()));
            this.updateById(purchaseDetailEntity);
        }


    }

}