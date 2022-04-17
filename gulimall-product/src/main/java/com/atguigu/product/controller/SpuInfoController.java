package com.atguigu.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.product.vo.AddSpuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.product.entity.SpuInfoEntity;
import com.atguigu.product.service.SpuInfoService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * spu信息
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:21:08
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 上架商品
     * @param spuId
     * @return
     */
    @RequestMapping("/{spuId}/up")
    public R up(@PathVariable("spuId") Long spuId){
        spuInfoService.asynUpSku(spuId);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     *
     *
     */
    @RequestMapping("/save")
    public R save(@RequestBody AddSpuVO addSpuVO){
        spuInfoService.asynAddSpu(addSpuVO);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 异步执行
     * @param addSpuVO
     * @return
     */
    @RequestMapping("/asyn/save")
    public R asynAddSpu(@RequestBody AddSpuVO addSpuVO){
        spuInfoService.asynAddSpu(addSpuVO);
        return R.ok();
    }

    /**
     * 上架商品 异步执行
     * @param spuId
     * @return
     */
    @RequestMapping("/{spuId}/asyn/up")
    public R asynUp(@PathVariable("spuId") Long spuId){
        spuInfoService.asynUpSku(spuId);
        return R.ok();
    }

}
