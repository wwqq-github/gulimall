package com.atguigu.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class TestConfig {

//    @Bean
    public AlipayClient alipay(AliPayProperties aliPayProperties) throws AlipayApiException {
        AlipayConfig alipayConfig = new AlipayConfig();
        //设置网关地址
        alipayConfig.setServerUrl(aliPayProperties.gatewayUrl);
        //设置应用Id
        alipayConfig.setAppId(aliPayProperties.appId);
        //设置应用私钥
        alipayConfig.setPrivateKey(aliPayProperties.merchantPrivateKey);
        //设置请求格式，固定值json
        alipayConfig.setFormat("json");
        //设置字符集
        alipayConfig.setCharset(aliPayProperties.charset);
        //设置支付宝公钥
        alipayConfig.setAlipayPublicKey(aliPayProperties.alipayPublicKey);
        //设置签名类型
        alipayConfig.setSignType(aliPayProperties.signType);
        //构造client
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);

        return alipayClient;
    }
}
