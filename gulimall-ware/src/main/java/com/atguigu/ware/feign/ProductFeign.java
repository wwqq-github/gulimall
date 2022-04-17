package com.atguigu.ware.feign;

import com.atguigu.common.utils.R;
import com.atguigu.ware.vo.SkuQueryVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-product")
public interface ProductFeign {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);


    @RequestMapping("/product/skuinfo/list")
    public R list(SkuQueryVO skuQueryVO);

}
