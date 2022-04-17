package com.atguigu.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.atguigu.common.vo.MemberVO;
import com.atguigu.member.vo.LoginVo;
import com.atguigu.member.vo.RegisteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.member.entity.MemberEntity;
import com.atguigu.member.service.MemberService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 会员
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-17 09:58:52
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    /**
     * 注册
     * @param user
     * @return
     */
    @PostMapping("/register")
    public R registerMember(@RequestBody  RegisteVO user){
        memberService.registerMember(user);
        return R.ok();
    }


    /**
     * 登陆
     * @param memberVo
     * @return
     */
    @PostMapping("/login")
    public R loginMember(@RequestBody LoginVo memberVo){
        MemberEntity member=memberService.loginMember(memberVo);
        return R.ok().put("member",member);
    }

    @PostMapping("/auth.2/login")
    public R authLogin(@RequestBody MemberEntity memberVo){
        System.err.println(memberVo.toString());
        MemberEntity member=memberService.authLogin(memberVo);
        return R.ok().put("member",member);
    }
}
