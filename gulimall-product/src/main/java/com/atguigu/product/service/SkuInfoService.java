package com.atguigu.product.service;

import com.atguigu.product.vo.CartVO;
import com.atguigu.product.vo.OrderItemVO;
import com.atguigu.product.vo.SkuQueryVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.SkuInfoEntity;

import java.util.List;

/**
 * sku信息
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:21:08
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(SkuQueryVO spuQueryVO);

    List<OrderItemVO> orderProduct(List<Long> skuIds);

    CartVO getCart(Long skuId);
}

