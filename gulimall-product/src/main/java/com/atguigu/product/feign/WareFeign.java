package com.atguigu.product.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeign {

    @RequestMapping("/ware/waresku/stock")
    public R stockAndAddr(List<Long> skuIds);
}
