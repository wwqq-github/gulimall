package com.atguigu.cart.service;

import com.atguigu.cart.vo.AddProductVO;
import com.atguigu.cart.vo.CartPriceVO;
import com.atguigu.cart.vo.CartVO;
import com.atguigu.cart.vo.UpdateCartVO;

import java.util.List;

public interface CartService {
    void addToCart(AddProductVO addProductVO,Long memberId);

    CartPriceVO getUserCart(Long id);

    CartPriceVO addProductNum(Long skuId,Long memberId);

    CartPriceVO subProductNum(Long skuId, Long id);

    CartPriceVO checkItem(Long skuId, Long id);

    CartPriceVO delect(Long skuId, Long id);
}
