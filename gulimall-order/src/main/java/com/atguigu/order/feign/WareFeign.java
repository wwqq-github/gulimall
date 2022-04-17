package com.atguigu.order.feign;

import com.atguigu.common.utils.R;
import com.atguigu.order.vo.Address;
import com.atguigu.order.vo.LockStockVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@FeignClient("gulimall-ware")
public interface WareFeign {

    @RequestMapping("/ware/waresku/sku/stock")
    public Map<Long,Boolean> getStock(List<Long> skuIds);

    @RequestMapping("/ware/waresku/lock/stock")
    public R lockStock(List<LockStockVO> stockVOS);

    @RequestMapping("/ware/waresku/unlock/stock")
    public R unlockStock(List<LockStockVO> stockVOS);
}
