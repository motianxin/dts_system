package com.dts.system.util;

import com.dts.system.config.FeishuConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class FeishuAuthUtil {
    
    @Autowired
    private FeishuConfig feishuConfig;
    
    private final WebClient webClient;
    
    public FeishuAuthUtil(FeishuConfig feishuConfig) {
        this.feishuConfig = feishuConfig;
        this.webClient = WebClient.create();
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
    public Map<String, Object> getAccessToken(String code) throws Exception {
        Map<String, Object> response = webClient.post()
                .uri("https://open.feishu.cn/open-apis/authen/v1/access_token")
                .header("Content-Type", "application/json")
                .bodyValue(Map.of(
                        "app_id", feishuConfig.getAppId(),
                        "app_secret", feishuConfig.getAppSecret(),
                        "code", code
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        
        if (response == null || !"0".equals(response.get("code").toString())) {
            throw new Exception("获取访问令牌失败: " + response);
        }
        return (Map<String, Object>) response.get("data");
    }
    
    /**
     * 根据访问令牌获取用户信息
     */
    public Map<String, Object> getUserInfo(String accessToken) throws Exception {
        Map<String, Object> response = webClient.get()
                .uri("https://open.feishu.cn/open-apis/authen/v1/user_info")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        
        if (response == null || !"0".equals(response.get("code").toString())) {
            throw new Exception("获取用户信息失败: " + response);
        }
        return (Map<String, Object>) response.get("data");
    }
}