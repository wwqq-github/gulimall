package com.atguigu.order.dao;

import com.atguigu.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:18:39
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
