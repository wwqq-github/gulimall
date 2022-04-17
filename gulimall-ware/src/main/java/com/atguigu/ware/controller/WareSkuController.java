package com.atguigu.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.ware.dto.SkuUpDto;
import com.atguigu.ware.vo.LockStockVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.ware.entity.WareSkuEntity;
import com.atguigu.ware.service.WareSkuService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 商品库存
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:22:49
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    @RequestMapping("/stock")
    public R stockAndAddr(@RequestBody List<Long> skuIds) {
        List<SkuUpDto> stockAndAddr = wareSkuService.getStockAndAddr(skuIds);
        return R.ok().put("data",stockAndAddr);
    }

    @RequestMapping("/sku/stock")
    public Map<Long,Boolean> getStock(@RequestBody List<Long> skuIds){
        Map<Long,Boolean> stock=wareSkuService.getStock(skuIds);
        return stock;
    }

    /**
     * 锁库存
     * @param stockVOS
     * @return
     */
    @RequestMapping("/lock/stock")
    public R lockStock(@RequestBody List<LockStockVO> stockVOS){
        wareSkuService.lockStock(stockVOS);
        return R.ok();
    }

    /**
     * 解锁库存
     * @param stockVOS
     * @return
     */
    @RequestMapping("/unlock/stock")
    public R unlockStock(@RequestBody List<LockStockVO> stockVOS){
        wareSkuService.unlockStock(stockVOS);
        return R.ok();
    }
}
