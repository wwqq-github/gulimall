package com.gulimall.auth.service;

import com.atguigu.common.vo.MemberVO;
import com.gulimall.auth.vo.LoginVO;
import com.gulimall.auth.vo.RegisteVO;

public interface MemberService {

    void registerMember(RegisteVO user);

    MemberVO login(LoginVO memberVo);

    MemberVO authLogin(String code);
}
