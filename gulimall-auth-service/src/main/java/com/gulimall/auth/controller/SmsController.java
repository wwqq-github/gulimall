package com.gulimall.auth.controller;

import com.atguigu.common.utils.R;
import com.gulimall.auth.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SmsController {

    @Autowired
    private SmsService smsService;

    @RequestMapping("/sms/sendcode")
    @ResponseBody
    public R sendSms(@RequestParam("phone") String phone){

        smsService.sendSms(phone);
        return  R.ok();
    }
}
