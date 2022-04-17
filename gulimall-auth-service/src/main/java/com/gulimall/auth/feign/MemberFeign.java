package com.gulimall.auth.feign;

import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberVO;
import com.gulimall.auth.vo.LoginVO;
import com.gulimall.auth.vo.RegisteVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("gulimall-member")
public interface MemberFeign {

    @PostMapping("/member/member/register")
    public R registerMember(RegisteVO user);

    @PostMapping("/member/member/login")
    public R login(LoginVO memberVo);

    @PostMapping("/member/member/auth.2/login")
    public R authLogin(MemberVO memberVo);
}
