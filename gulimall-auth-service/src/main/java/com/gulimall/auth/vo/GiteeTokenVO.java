package com.gulimall.auth.vo;

import lombok.Data;

@Data
public class GiteeTokenVO {
    private String access_token;
    private String token_type;
    private String expires_in;
    private String refresh_token;
    private String scope;
    private String created_at;
}
