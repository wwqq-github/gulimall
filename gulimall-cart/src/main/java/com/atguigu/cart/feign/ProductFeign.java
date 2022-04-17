package com.atguigu.cart.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-product")
public interface ProductFeign {

    @RequestMapping("/product/skuinfo/order/attr/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
