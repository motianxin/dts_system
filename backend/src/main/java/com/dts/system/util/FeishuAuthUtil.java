package com.dts.system.util;

import com.dts.system.config.FeishuConfig;
import com.lark.oapi.Client;
import com.lark.oapi.service.authen.v1.model.CreateAccessTokenReq;
import com.lark.oapi.service.authen.v1.model.CreateAccessTokenReqBody;
import com.lark.oapi.service.authen.v1.model.CreateAccessTokenResp;
import com.lark.oapi.service.authen.v1.model.GetUserInfoReq;
import com.lark.oapi.service.authen.v1.model.GetUserInfoResp;
import com.lark.oapi.service.authen.v1.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class FeishuAuthUtil {
    
    private final FeishuConfig feishuConfig;
    private final Client client;
    
    @Autowired
    public FeishuAuthUtil(FeishuConfig feishuConfig) {
        this.feishuConfig = feishuConfig;
        this.client = Client.newBuilder(feishuConfig.getAppId(), feishuConfig.getAppSecret())
                .build();
    }
    
    /**
     * 生成飞书授权URL
     */
    public String generateAuthUrl() {
        String state = String.valueOf(System.currentTimeMillis());
        String redirectUri = URLEncoder.encode(feishuConfig.getRedirectUri(), StandardCharsets.UTF_8);
        
        return "https://open.feishu.cn/open-apis/authen/v1/index?app_id=" + feishuConfig.getAppId() +
                "&redirect_uri=" + redirectUri +
                "&scope=user_info&state=" + state;
    }
    
    /**
     * 根据授权码获取访问令牌
     */
    public String getAccessToken(String code) throws Exception {
        CreateAccessTokenReq req = CreateAccessTokenReq.newBuilder()
                .body(CreateAccessTokenReqBody.newBuilder()
                        .grantType("authorization_code")
                        .appSecret(feishuConfig.getAppSecret())
                        .code(code)
                        .build())
                .build();
        
        CreateAccessTokenResp resp = client.authen().v1().accessToken().create(req);
        if (!resp.success()) {
            throw new Exception("获取访问令牌失败: " + resp.getMsg());
        }
        return resp.getData().getAccessToken();
    }
    
    /**
     * 根据访问令牌获取用户信息
     */
    public UserInfo getUserInfo(String accessToken) throws Exception {
        GetUserInfoReq req = GetUserInfoReq.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();
        
        GetUserInfoResp resp = client.authen().v1().userInfo().get(req);
        if (!resp.success()) {
            throw new Exception("获取用户信息失败: " + resp.getMsg());
        }
        return resp.getData();
    }
}