package com.atguigu.member.service;

import com.atguigu.common.vo.MemberVO;
import com.atguigu.member.vo.LoginVo;
import com.atguigu.member.vo.RegisteVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-17 09:58:52
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void registerMember(RegisteVO user);

    MemberEntity loginMember(LoginVo user);

    MemberEntity authLogin(MemberEntity memberVo);
}

