package com.gulimall.auth.service.impl;

import com.gulimall.auth.exception.PhoneException;
import com.gulimall.auth.service.SmsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void sendSms(String phone){
        if (phone.length()!=11||!phone.startsWith("1")){
            throw new PhoneException("手机号码错误 请核实");
        }
        String s = stringRedisTemplate.opsForValue().get("phone-" + phone);
        if (StringUtils.isEmpty(s)){
            String code = "000000"+createCode();
            code=code.substring(code.length()-6);
            stringRedisTemplate.opsForValue().set("phone-" + phone,code,5, TimeUnit.MINUTES);
            System.err.println(code);
        }else {
            throw new PhoneException("手机号码已经发送验证码 请核实");
        }


    }



    public String createCode(){
        Integer random =(int) ((Math.random())*1000000);
        return random.toString();
    }
}
