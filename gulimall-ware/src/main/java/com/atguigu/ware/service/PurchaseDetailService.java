package com.atguigu.ware.service;

import com.atguigu.ware.dto.SkuUpDto;
import com.atguigu.ware.vo.WarePurchaseitemVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:22:49
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(WarePurchaseitemVO warePurchaseitemVO);

    void savePurchaseDetail(PurchaseDetailEntity purchaseDetail);
}

