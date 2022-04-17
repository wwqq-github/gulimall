package com.atguigu.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Data
@Component
public class AliPayProperties {

    //网关地址
    public String gatewayUrl;
    //应用Id
    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public String appId;
    //应用私钥
    // 商户私钥，您的PKCS8格式RSA2私钥
    public String merchantPrivateKey;
    //支付宝公钥
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public String alipayPublicKey;
    //请求格式，固定值json
    public String format;
    //字符集
    public String charset;

    //签名类型
    public String signType;


    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://工程公网访问地址/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://工程公网访问地址/alipay.trade.page.pay-JAVA-UTF-8/return_url.jsp";



}
