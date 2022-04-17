package com.atguigu.product.dao;

import com.atguigu.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品属性
 * 
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:21:09
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
	
}
