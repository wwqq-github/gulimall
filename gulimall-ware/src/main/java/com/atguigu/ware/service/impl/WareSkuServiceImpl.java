package com.atguigu.ware.service.impl;

import com.atguigu.ware.dto.SkuUpDto;
import com.atguigu.ware.entity.PurchaseDetailEntity;
import com.atguigu.ware.entity.WareInfoEntity;
import com.atguigu.ware.exception.UnderstockException;
import com.atguigu.ware.service.WareInfoService;
import com.atguigu.ware.vo.LockStockVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.ware.dao.WareSkuDao;
import com.atguigu.ware.entity.WareSkuEntity;
import com.atguigu.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    private ThreadPoolExecutor threadPool;
    @Autowired
    private WareInfoService wareInfoService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }
    @Override
    public List<SkuUpDto> getStockAndAddr(List<Long> skuIds) {
        List<WareInfoEntity> wareInfos = wareInfoService.list();
        Map<Long, String> map = wareInfos.stream().collect(Collectors.toMap(WareInfoEntity::getId, WareInfoEntity::getName));
        List<WareSkuEntity> list = this.list(new QueryWrapper<WareSkuEntity>().in("sku_id", skuIds));
        List<SkuUpDto> collect = list.stream().map(item -> {
            SkuUpDto skuUpDto = new SkuUpDto();
            skuUpDto.setHasStock(item.getStock()>0);
            skuUpDto.setWareId(item.getWareId());
            skuUpDto.setSkuId(item.getSkuId());
            skuUpDto.setWareAddress(map.get(item.getWareId()));
            return skuUpDto;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public Map<Long, Boolean> getStock(List<Long> skuIds) {
        List<WareSkuEntity> list = this.list(new QueryWrapper<WareSkuEntity>().in("sku_id",skuIds));
        Map<Long, Boolean> collect = list.stream().collect(Collectors.toMap(WareSkuEntity::getSkuId, k -> {
            return k.getStock() - k.getStockLocked() > 0;
        }));
        return collect;
    }

    /**
     * 锁库存
     * @param stockVOS
     */
    @Override
    public void lockStock(List<LockStockVO> stockVOS) {
        List<Long> skuIds=new ArrayList<>();
        Map<Long, Integer> collect = stockVOS.stream().map(item->{
                skuIds.add(item.getSkuId());
                return item;
            }).collect(Collectors.toMap(LockStockVO::getSkuId, LockStockVO::getNum));
        QueryWrapper<WareSkuEntity> skuWrapper = new QueryWrapper<>();
        skuWrapper.in("sku_id",skuIds);
        List<WareSkuEntity> list = this.list(skuWrapper);
        List<WareSkuEntity> wareSkus = list.stream().map(item -> {
            Integer num = collect.get(item.getSkuId());
            if (item.getStock()-num<0){
                throw new UnderstockException("库存不足 请先添加库存");
            }
            item.setStock(item.getStock()-num);
            item.setStockLocked(item.getStockLocked()+num);
            return item;
        }).collect(Collectors.toList());
        this.updateBatchById(wareSkus);
    }

    /**
     * 解锁库存
     * @param stockVOS
     */
    @Override
    public void unlockStock(List<LockStockVO> stockVOS) {
        List<Long> skuIds=new ArrayList<>();
        Map<Long, Integer> collect = stockVOS.stream().map(item->{
            skuIds.add(item.getSkuId());
            return item;
        }).collect(Collectors.toMap(LockStockVO::getSkuId, LockStockVO::getNum));
        QueryWrapper<WareSkuEntity> skuWrapper = new QueryWrapper<>();
        skuWrapper.in("sku_id",skuIds);
        List<WareSkuEntity> list = this.list(skuWrapper);
        List<WareSkuEntity> wareSkus = list.stream().map(item -> {
            Integer num = collect.get(item.getSkuId());
            if (item.getStockLocked()-num<0){
                throw new UnderstockException("已经加过库存了 请联系商家核实");
            }
            item.setStock(item.getStock()+num);
            item.setStockLocked(item.getStockLocked()-num);
            return item;
        }).collect(Collectors.toList());
        this.updateBatchById(wareSkus);
    }
}