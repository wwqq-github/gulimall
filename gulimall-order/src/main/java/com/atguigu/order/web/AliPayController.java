package com.atguigu.order.web;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.order.config.AliPayProperties;
import com.atguigu.order.config.AlipayConfig;
import com.atguigu.order.service.OrderService;
import com.atguigu.order.vo.AliPayVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class AliPayController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private AliPayProperties aliPayProperties;
    @PostMapping("/alipay/notify")
    public String notifyUrl(AliPayVO aliPayVO) throws AlipayApiException {
        //验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(null, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名
        if (signVerified){
            String result = orderService.updatePayState(aliPayVO);
            return result;
        }else {
            return "fail";
        }

    }
}
