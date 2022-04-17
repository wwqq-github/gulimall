package com.atguigu.product.service;

import com.atguigu.product.vo.AttrRelationVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.AttrAttrgroupRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:21:09
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void findByttrAndGroupdelete(List<AttrAttrgroupRelationEntity>  relations);
}

