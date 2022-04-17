package com.gulimall.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberVO;
import com.gulimall.auth.exception.MemberRegisterException;
import com.gulimall.auth.exception.PhoneException;
import com.gulimall.auth.feign.MemberFeign;
import com.gulimall.auth.service.MemberService;
import com.gulimall.auth.vo.GiteeTokenVO;
import com.gulimall.auth.vo.GiteeUserVO;
import com.gulimall.auth.vo.LoginVO;
import com.gulimall.auth.vo.RegisteVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MemberFeign memberFeign;
    @Autowired
    private RestTemplate restTemplate;
    @Override
    public void registerMember(RegisteVO user) {

        String s = stringRedisTemplate.opsForValue().get("phone-" + user.getPhone());
        if (StringUtils.isEmpty(s)){
            throw  new PhoneException("验证码已经过期了");
        }
        System.err.println(user.toString());
        R r=memberFeign.registerMember(user);
        if (r.getCode()!=0){
            throw new MemberRegisterException("用户注册失败");
        }
    }

    @Override
    public MemberVO login(LoginVO memberVo) {
        R r=memberFeign.login(memberVo);
        MemberVO member=(MemberVO)r.getData("member",new TypeReference<MemberVO>(){});
        return member;
    }

    /**
     * 社交登陆
     * @param code
     * @return
     */
    @Override
    public MemberVO authLogin(String code) {
        Map<String,String> patam=new HashMap<>();
        patam.put("grant_type","authorization_code");
        patam.put("client_id","8fb208bea7f10517d1dfe0a61d7d19e2d4b20d433d06515414cc494e6cadf4f3");
        patam.put("redirect_uri","http://auth.gulimall.com/auth2.0/gitee/seccess");
        patam.put("client_secret","ac60a141dae44cc99e3077c2ad8fb17888a965996a1c928068e3a4d5de87f681");
        patam.put("code",code);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> request = new HttpEntity<String>(JSON.toJSONString(patam),headers);
        String s = restTemplate.postForObject("https://gitee.com/oauth/token", request, String.class, patam);
        GiteeTokenVO giteeTokenVO = JSONObject.parseObject(s, GiteeTokenVO.class);
        Map<String, String> userPatem=new HashMap<>();
        userPatem.put("access_token",giteeTokenVO.getAccess_token());
        String user=restTemplate.getForObject("https://gitee.com/api/v5/user?access_token="+giteeTokenVO.getAccess_token(),String.class);
        GiteeUserVO o = JSONObject.parseObject(user, GiteeUserVO.class);
        MemberVO memberVO=new MemberVO();
        memberVO.setAuthType("gitee");
        memberVO.setAuthId(o.getId());
        memberVO.setAuthToken(giteeTokenVO.getAccess_token());
        memberVO.setUsername(o.getName());
        memberVO.setNickname(o.getName());
        memberVO.setEmail(o.getEmail());
        memberVO.setCreateTime(new Date());
        System.err.println(memberVO.toString());
        R r = memberFeign.authLogin(memberVO);
        MemberVO member=(MemberVO)r.getData("member",new TypeReference<MemberVO>(){});
        return member;
    }
}
