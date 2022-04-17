package com.gulimall.auth.controller;

import com.gulimall.auth.service.MemberService;
import com.gulimall.auth.vo.RegisteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/register.html")
    public String register(){
        return "register";
    }

    @PostMapping("/register")
    public String register(RegisteVO user){
        memberService.registerMember(user);
        return "redirect:http://auth.gulimall.com/login.html";
    }
}
