package com.atguigu.coupon.dao;

import com.atguigu.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-17 09:06:58
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
