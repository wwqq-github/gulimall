package com.atguigu.ware.dao;

import com.atguigu.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:22:49
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
