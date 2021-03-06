package com.atguigu.product.controller;

import java.util.Arrays;
import java.util.List;

import com.atguigu.product.vo.CartVO;
import com.atguigu.product.vo.OrderItemVO;
import com.atguigu.product.vo.SkuQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.product.entity.SkuInfoEntity;
import com.atguigu.product.service.SkuInfoService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * sku信息
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:21:08
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(SkuQueryVO skuQueryVO){
        PageUtils page = skuInfoService.queryPage(skuQueryVO);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

    @RequestMapping("/order")
    public R orderProduct(@RequestBody List<Long> skuIds){
        List<OrderItemVO>  orderItems=skuInfoService.orderProduct(skuIds);
        return R.ok().put("orderItems",orderItems);
    }

    @RequestMapping("/order/attr/{skuId}")
    public R skuVal(@PathVariable("skuId") Long skuId){
        CartVO cart=skuInfoService.getCart(skuId);
        System.err.println(cart.toString());
        return R.ok().put("cartItem",cart);
    }

}
