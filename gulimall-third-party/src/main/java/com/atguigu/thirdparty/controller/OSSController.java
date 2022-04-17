package com.atguigu.thirdparty.controller;


import com.atguigu.common.utils.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Title: OSSController</p>
 * Description：生成后端签名
 * date：2020/6/1 17:18
 */
@RestController
public class OSSController {


    //	@Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;

    //	@Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucket;

    //	@Value("${spring.cloud.alicloud.access-key}")
    private String accessId;

    @RequestMapping("/thirdparty/oss/policy")
    public R policy() {
        String host = "https://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint
        // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
        String format = "hs";//LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String dir = format + "/"; // 用户上传文件时指定的前缀。

        Map<String, String> respMap = null;
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
//			PolicyConditions policyConds = new PolicyConditions();
//			policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
//			policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = "ossClient.generatePostPolicy(expiration, policyConds)";
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = "BinaryUtil.toBase64String(binaryData)";
            String postSignature = "ossClient.calculatePostSignature(postPolicy)";

            respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", accessId);
            respMap.put("policy", "encodedPolicy");
//            respMap.put("policy", encodedPolicy);
            respMap.put("signature","postSignature" );
            respMap.put("dir", dir);
            host = "https://img0.baidu.com/it/u=4070157440,3476549993&fm=26&fmt=auto&gp=0.jpg";
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return R.ok().put("data", respMap);
    }

    @RequestMapping("/thirdparty/upload")
    public String uplode() {
        return "upload";
    }
}
