package com.dts.system.controller;

import com.dts.system.model.User;
import com.dts.system.service.UserService;
import com.dts.system.util.LogUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LogUtil.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        logger.info("[API调用] 用户注册 - 用户名: {}", user.getUsername());
        try {
            User registeredUser = userService.register(user);
            LogUtil.logOperation(logger, "用户注册", "成功注册用户: " + registeredUser.getUsername(), registeredUser.getId());
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (Exception e) {
            LogUtil.logError(logger, "用户注册 - 用户名: " + user.getUsername(), e);
            return new ResponseEntity<>("Registration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        logger.info("[API调用] 用户登录 - 用户名: {}", user.getUsername());
        try {
            var optionalUser = userService.login(user.getUsername(), user.getPassword());
            if (optionalUser.isPresent()) {
                User loggedInUser = optionalUser.get();
                LogUtil.logOperation(logger, "用户登录", "用户登录成功: " + loggedInUser.getUsername(), loggedInUser.getId());
                return new ResponseEntity<>(loggedInUser, HttpStatus.OK);
            } else {
                LogUtil.logWarn(logger, "用户登录失败 - 用户名: " + user.getUsername() + ", 原因: 用户名或密码错误");
                return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "用户登录 - 用户名: " + user.getUsername(), e);
            return new ResponseEntity<>("Login failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}