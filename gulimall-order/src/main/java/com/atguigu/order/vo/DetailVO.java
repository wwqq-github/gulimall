package com.atguigu.order.vo;

import com.atguigu.order.entity.OrderEntity;
import com.atguigu.order.entity.OrderItemEntity;
import lombok.Data;

import java.util.List;

@Data
public class DetailVO {

    private OrderEntity order;

    private List<OrderItemEntity> orderItems;
}
