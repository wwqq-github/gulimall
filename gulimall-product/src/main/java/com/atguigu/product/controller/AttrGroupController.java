package com.atguigu.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.product.entity.AttrEntity;
import com.atguigu.product.vo.BaseAttrVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.product.entity.AttrGroupEntity;
import com.atguigu.product.service.AttrGroupService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 属性分组
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:21:09
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;


    /**
     * 查找没有被关联的关联关系
     */
    @RequestMapping("/{attrGroupId}/noattr/relation")
    public R noAttrRelation(@PathVariable("attrGroupId") Long attrGroupId,@RequestParam Map<String, Object> params){
        PageUtils page = attrGroupService.getNoAttrRelation(attrGroupId,params);
        return R.ok().put("page", page);
    }

    /**
     * 查找已经关联的属性关系
     */
    @RequestMapping("/{attrGroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrGroupId") Long attrGroupId,@RequestParam Map<String, Object> params){
        List<AttrEntity> data = attrGroupService.getAttrRelation(attrGroupId, params);
        return R.ok().put("data", data);
    }

    /**
     * 查找属性关系
     */
    @RequestMapping("/{catelogId}/withattr")
    public R withattr(@PathVariable("catelogId") Long catelogId,@RequestParam Map<String, Object> params){
        List<BaseAttrVO> data = attrGroupService.withattr(catelogId, params);
        return R.ok().put("data", data);
    }


    /**
     * 列表
     */
    @RequestMapping("/list/{catId}")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrGroupService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
