package com.atguigu.order.feign;

import com.atguigu.common.utils.R;
import com.atguigu.order.vo.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-member")
public interface MemberFeign {

    @RequestMapping("/member/memberreceiveaddress/addr/{memberId}")
    public List<Address> getMemberddr(@PathVariable("memberId") Long memberId);

    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    public R info(@PathVariable("id") Long id);

    @RequestMapping("/member/member/info/{id}")
    public R member(@PathVariable("id") Long id);
}
