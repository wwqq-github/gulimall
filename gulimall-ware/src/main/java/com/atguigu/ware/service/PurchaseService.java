package com.atguigu.ware.service;

import com.atguigu.ware.vo.MergeVO;
import com.atguigu.ware.vo.PurchaseDoneVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:22:50
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<PurchaseEntity> getUnreceiveList();

    void updateMerge(MergeVO mergeVO);

    void done(PurchaseDoneVO purchaseDoneVO);

    void updateStatus(PurchaseEntity purchase);
}

