package com.atguigu.ware.service;

import com.atguigu.ware.dto.SkuUpDto;
import com.atguigu.ware.vo.LockStockVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:22:49
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    public List<SkuUpDto> getStockAndAddr(List<Long> skuIds);

    Map<Long, Boolean> getStock(List<Long> skuIds);

    void lockStock(List<LockStockVO> stockVOS);

    void unlockStock(List<LockStockVO> stockVOS);
}

