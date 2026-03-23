package com.dts.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeishuConfig {
    
    @Value("${feishu.app.id}")
    private String appId;
    
    @Value("${feishu.app.secret}")
    private String appSecret;
    
    @Value("${feishu.redirect.uri}")
    private String redirectUri;
    
    @Value("${feishu.app.token}")
    private String appToken;
    
    public String getAppId() {
        return appId;
    }
    
    public String getAppSecret() {
        return appSecret;
    }
    
    public String getRedirectUri() {
        return redirectUri;
    }
    
    public String getAppToken() {
        return appToken;
    }
}