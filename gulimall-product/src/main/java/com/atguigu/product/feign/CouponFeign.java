package com.atguigu.product.feign;

import com.atguigu.common.utils.R;
import com.atguigu.product.dto.SkuFullReductionDO;
import com.atguigu.product.dto.SkuLadderReductionDO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-coupon")
public interface CouponFeign {
    @RequestMapping("/coupon/skufullreduction/save")
    public R saveFull(@RequestBody SkuFullReductionDO skuFullReduction);

    @RequestMapping("/coupon/skuladder/save")
    public R saveLadder(@RequestBody SkuLadderReductionDO skuFullReduction);
}
