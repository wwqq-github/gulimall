package com.atguigu.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.ware.dto.SkuInfoDo;
import com.atguigu.ware.entity.PurchaseDetailEntity;
import com.atguigu.ware.entity.WareSkuEntity;
import com.atguigu.ware.feign.ProductFeign;
import com.atguigu.ware.service.PurchaseDetailService;
import com.atguigu.ware.service.WareSkuService;
import com.atguigu.ware.vo.MergeVO;
import com.atguigu.ware.vo.PurchaseDoneVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.ware.dao.PurchaseDao;
import com.atguigu.ware.entity.PurchaseEntity;
import com.atguigu.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private  WareSkuService wareSkuService;
    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private ProductFeign productFeign;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<PurchaseEntity> getUnreceiveList() {
        QueryWrapper<PurchaseEntity> wrapper=new QueryWrapper<PurchaseEntity>();
        wrapper.eq("status",0);
        List<PurchaseEntity> list = this.list(wrapper);
        return list;
    }

    /**
     * 分配采购单
     * @param mergeVO
     */
    @Transactional
    @Override
    public void updateMerge(MergeVO mergeVO) {
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setStatus(1);
        purchaseEntity.setId(mergeVO.getPurchaseId());
        this.updateById(purchaseEntity);
        List<PurchaseDetailEntity> collect = mergeVO.getItems().stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setPurchaseId(mergeVO.getPurchaseId());;
            purchaseDetailEntity.setStatus(1);
            purchaseDetailEntity.setId(item);
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
    }

    /**
     * 完成采购单
     * @param purchaseDoneVO
     */
    @Override
    public void done(PurchaseDoneVO purchaseDoneVO) {
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setStatus(3);
        purchaseEntity.setId(purchaseDoneVO.getId());
        this.updateById(purchaseEntity);
        List<WareSkuEntity> wareSkus=new ArrayList<>();
        List<PurchaseDetailEntity> collect = purchaseDoneVO.getItems().stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = purchaseDetailService.getById(item);
            System.err.println(purchaseDetailEntity.toString());
            purchaseDetailEntity.setStatus(3);
            //添加库存
            try {
                WareSkuEntity wareSkuEntity=new WareSkuEntity();
                wareSkuEntity.setSkuId(purchaseDetailEntity.getSkuId());
                wareSkuEntity.setId(purchaseDetailEntity.getSkuId());
                wareSkuEntity.setWareId(purchaseDetailEntity.getWareId());
                wareSkuEntity.setStock(purchaseDetailEntity.getSkuNum());
                wareSkuEntity.setStockLocked(0);
                R info = productFeign.info(wareSkuEntity.getSkuId());
                SkuInfoDo data = (SkuInfoDo) info.getData("skuInfo", new TypeReference<SkuInfoDo>() {
                });
                wareSkuEntity.setSkuName(data.getSkuName());
                wareSkus.add(wareSkuEntity);
            }catch (Exception e){
                purchaseDetailEntity.setStatus(4);
                purchaseEntity.setStatus(4);
                this.updateById(purchaseEntity);
            }


            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
        wareSkuService.saveBatch(wareSkus);
    }


    @Override
    public void updateStatus(PurchaseEntity purchase) {
        purchase.setStatus(2);
        this.updateById(purchase);
        List<PurchaseDetailEntity> list = purchaseDetailService.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id",purchase.getId()));
        List<PurchaseDetailEntity> collect = list.stream().map(item -> {
            item.setStatus(2);
            return item;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
    }


}