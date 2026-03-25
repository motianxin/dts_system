package com.dts.system.controller;

import com.dts.system.model.User;
import com.dts.system.service.UserService;
import com.dts.system.util.FeishuAuthUtil;
import com.dts.system.util.LogUtil;
import com.lark.oapi.service.authen.v1.model.UserInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LogUtil.getLogger(AuthController.class);
    
    @Autowired
    private FeishuAuthUtil feishuAuthUtil;
    
    @Autowired
    private UserService userService;
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    /**
     * 获取飞书登录URL
     */
    @GetMapping("/feishu/login")
    public ResponseEntity<Map<String, String>> getFeishuLoginUrl() {
        try {
            logger.info("[API调用] 获取飞书登录URL");
            String authUrl = feishuAuthUtil.generateAuthUrl();
            Map<String, String> response = new HashMap<>();
            response.put("loginUrl", authUrl);
            LogUtil.logOperation(logger, "获取飞书登录URL", "成功生成飞书登录链接", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogUtil.logError(logger, "获取飞书登录URL", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 处理飞书回调
     */
    @GetMapping("/feishu/callback")
    public ResponseEntity<Map<String, Object>> handleFeishuCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state) {
        try {
            logger.info("[API调用] 处理飞书回调 - code: {}, state: {}", code, state);
            
            // 根据授权码获取访问令牌
            String accessToken = feishuAuthUtil.getAccessToken(code);
            
            // 根据访问令牌获取用户信息
            UserInfo userInfoData = feishuAuthUtil.getUserInfo(accessToken);
            
            // 创建或更新用户
            User user = new User();
            user.setFeishuUserId(userInfoData.getUserId());
            user.setFeishuOpenId(userInfoData.getOpenId());
            user.setFeishuUnionId(userInfoData.getUnionId());
            user.setFeishuAvatar(userInfoData.getAvatarUrl());
            user.setFeishuName(userInfoData.getName());
            user.setUsername(userInfoData.getEmail() != null ? userInfoData.getEmail() : userInfoData.getName());
            user.setEmail(userInfoData.getEmail());
            
            User savedUser = userService.createOrUpdateFeishuUser(user);
            
            // 生成JWT令牌
            String token = generateJwtToken(savedUser);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", savedUser);
            
            LogUtil.logOperation(logger, "飞书登录", "用户飞书登录成功: " + savedUser.getUsername(), savedUser.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogUtil.logError(logger, "处理飞书回调", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 生成JWT令牌
     */
    private String generateJwtToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}