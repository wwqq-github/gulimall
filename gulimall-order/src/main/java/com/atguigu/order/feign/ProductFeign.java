package com.atguigu.order.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-product")
public interface ProductFeign {

    @RequestMapping("/product/skuinfo/order")
    public R orderProduct(List<Long> skuIds);

}
