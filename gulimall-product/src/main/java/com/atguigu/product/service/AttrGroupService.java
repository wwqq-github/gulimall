package com.atguigu.product.service;

import com.atguigu.product.entity.AttrEntity;
import com.atguigu.product.vo.BaseAttrVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.AttrGroupEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:21:09
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<AttrEntity> getAttrRelation(Long attrGroupId, Map<String, Object> params);

    PageUtils getNoAttrRelation(Long attrGroupId, Map<String, Object> params);

    List<BaseAttrVO> withattr(Long catelogId, Map<String, Object> params);
}

