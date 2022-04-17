package com.atguigu.order.service;

import com.atguigu.order.vo.AliPayVO;
import com.atguigu.order.vo.ConfirmVO;
import com.atguigu.order.vo.DetailVO;
import com.atguigu.order.vo.SubmitOrderVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.order.entity.OrderEntity;

import java.util.List;
import java.util.Map;

/**
 * 订单
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:18:39
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    ConfirmVO confirm(Long memberId);

    OrderEntity saveOrder(SubmitOrderVO submitOrderVO);

    List<DetailVO> orderList(Long id);

    DetailVO getOrder(Long orderId);

    String updatePayState(AliPayVO aliPayVO);
}

