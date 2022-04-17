package com.atguigu.product.feign;

import com.atguigu.common.utils.R;
import com.atguigu.product.dto.SkuUpDto;
import com.atguigu.product.vo.SkuEsModule;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-search")
public interface SearchFeign {

    @RequestMapping("/search/product/save")
    public R saveProduct(@RequestBody List<SkuEsModule> skuUpDtos);

}
