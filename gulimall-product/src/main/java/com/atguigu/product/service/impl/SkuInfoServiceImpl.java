package com.atguigu.product.service.impl;

import com.atguigu.product.entity.SkuSaleAttrValueEntity;
import com.atguigu.product.entity.SpuInfoEntity;
import com.atguigu.product.service.SkuSaleAttrValueService;
import com.atguigu.product.service.SpuInfoService;
import com.atguigu.product.vo.CartVO;
import com.atguigu.product.vo.OrderItemVO;
import com.atguigu.product.vo.SkuQueryVO;
import org.springframework.beans.BeanUtils;
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

import com.atguigu.product.dao.SkuInfoDao;
import com.atguigu.product.entity.SkuInfoEntity;
import com.atguigu.product.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SpuInfoService spuInfoService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Override
    public PageUtils queryPage(SkuQueryVO spuQueryVO) {
        Map<String,Object> params=new HashMap<String,Object>();
        params.put("page",spuQueryVO.getPage());
        params.put("limit",spuQueryVO.getLimit());
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        if (spuQueryVO.getKey()!=null&&!"".equals(spuQueryVO.getKey())){
            wrapper.eq("sku_name",spuQueryVO.getKey());
        }
        if (spuQueryVO.getBrandId()!=null&&spuQueryVO.getBrandId()!=0){
            wrapper.eq("brand_id",spuQueryVO.getBrandId());
        }
        if (spuQueryVO.getCatelogId()!=null&&spuQueryVO.getCatelogId()!=0){
            wrapper.eq("catalog_id",spuQueryVO.getCatelogId());
        }
        if (spuQueryVO.getMin()!=null&&spuQueryVO.getMin().equals("0")){
            wrapper.ge("price",spuQueryVO.getMin());
        }
        if (spuQueryVO.getMax()!=null&&spuQueryVO.getMin().equals("0")){
            wrapper.le("price",spuQueryVO.getMax());
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<OrderItemVO> orderProduct(List<Long> skuIds) {
        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().in("sku_id",skuIds));
        List<OrderItemVO> orderItems = list.stream().map(item -> {
            OrderItemVO orderItem = new OrderItemVO();
            BeanUtils.copyProperties(item,orderItem);
            orderItem.setSkuPic(item.getSkuDefaultImg());
            orderItem.setCategoryId(item.getCatalogId());
            return orderItem;

        }).collect(Collectors.toList());

        return orderItems;
    }

    @Override
    public CartVO getCart(Long skuId) {
        CartVO cart=new CartVO();
        SkuInfoEntity byId = this.getById(skuId);
        cart.setSkuDefaultImg(byId.getSkuDefaultImg());
        cart.setSkuId(skuId);
        cart.setSkuTitle(byId.getSkuTitle());
        cart.setSkuPrice(byId.getSkuPrice());
        List<SkuSaleAttrValueEntity> list = skuSaleAttrValueService.list(new QueryWrapper<SkuSaleAttrValueEntity>().eq("sku_id",skuId));
        StringBuffer sb=new StringBuffer();
        for (SkuSaleAttrValueEntity v:list){
            sb.append(v.getAttrName()+":").append(v.getAttrValue()+" ");
        }
        cart.setSkuAttrsVals(sb.toString().trim());
        return cart;
    }

}