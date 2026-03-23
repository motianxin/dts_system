package com.dts.system.service;

import com.dts.system.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(User user);
    Optional<User> login(String username, String password);
    Optional<User> findByUsername(String username);
    
    // 以下是测试代码中使用的方法
    User createUser(User user);
    List<User> getAllUsers();
    User getUserById(Long id);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    User validateUser(String username, String password);
    List<User> getUsersByRole(String role);
    
    // 飞书相关方法
    User getUserByFeishuUserId(String feishuUserId);
    User createOrUpdateFeishuUser(User user);
}