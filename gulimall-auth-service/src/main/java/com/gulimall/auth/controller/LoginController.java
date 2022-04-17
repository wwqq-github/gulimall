package com.gulimall.auth.controller;

import com.atguigu.common.vo.MemberVO;
import com.gulimall.auth.service.MemberService;
import com.gulimall.auth.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private MemberService memberService;
    @GetMapping("/login.html")
    public String login() {
        return "login";
    }


    @PostMapping("/login")
    public String login(LoginVO memberVo, HttpSession session) {
        MemberVO memberEntity=memberService.login(memberVo);
        session.setAttribute("userSession",memberEntity);
        session.setMaxInactiveInterval(30*24*60*60);
        return "redirect:http://gulimall.com/";
    }

    @GetMapping("/auth2.0/gitee/seccess")
    public String authLogin(@RequestParam("code") String code, HttpSession session){
        MemberVO memberEntity=memberService.authLogin(code);
        session.setAttribute("userSession",memberEntity);
        session.setMaxInactiveInterval(30*24*60*60);
        return "redirect:http://gulimall.com/";
    }
}
