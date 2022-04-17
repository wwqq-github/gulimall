package com.atguigu.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartPriceVO {

    private BigDecimal totalPrice;

    private List<CartVO> carts;
}
