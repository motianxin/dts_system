package com.dts.system.service;

import com.dts.system.model.User;
import com.dts.system.repository.UserRepository;
import com.dts.system.util.LogUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogUtil.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole("ROLE_USER");
            User savedUser = userRepository.save(user);
            LogUtil.logBusiness(logger, "用户注册", "成功注册用户: " + savedUser.getUsername() + ", ID: " + savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            LogUtil.logError(logger, "用户注册 - 用户名: " + user.getUsername(), e);
            throw e;
        }
    }

    @Override
    public Optional<User> login(String username, String password) {
        try {
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
                LogUtil.logBusiness(logger, "用户登录", "用户登录成功: " + username);
                return user;
            } else {
                LogUtil.logWarn(logger, "用户登录失败: " + username + ", 原因: 用户名或密码错误");
                return Optional.empty();
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "用户登录 - 用户名: " + username, e);
            throw e;
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                LogUtil.logBusiness(logger, "用户查询", "查询用户: " + username);
            } else {
                LogUtil.logWarn(logger, "查询用户不存在: " + username);
            }
            return user;
        } catch (Exception e) {
            LogUtil.logError(logger, "查询用户 - 用户名: " + username, e);
            throw e;
        }
    }

    // 以下是测试代码中使用的方法
    @Override
    public User createUser(User user) {
        try {
            if (user.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            User savedUser = userRepository.save(user);
            LogUtil.logBusiness(logger, "用户创建", "成功创建用户: " + savedUser.getUsername() + ", ID: " + savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            LogUtil.logError(logger, "创建用户 - 用户名: " + user.getUsername(), e);
            throw e;
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            LogUtil.logBusiness(logger, "用户查询", "查询所有用户，共 " + users.size() + " 条记录");
            return users;
        } catch (Exception e) {
            LogUtil.logError(logger, "查询所有用户", e);
            throw e;
        }
    }

    @Override
    public User getUserById(Long id) {
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                LogUtil.logBusiness(logger, "用户查询", "查询用户详情，ID: " + id);
                return user.get();
            } else {
                LogUtil.logWarn(logger, "查询用户不存在，ID: " + id);
                return null;
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "查询用户详情 - ID: " + id, e);
            throw e;
        }
    }

    @Override
    public User updateUser(Long id, User user) {
        try {
            Optional<User> existingUser = userRepository.findById(id);
            if (existingUser.isPresent()) {
                User updatedUser = existingUser.get();
                if (user.getUsername() != null) {
                    updatedUser.setUsername(user.getUsername());
                }
                if (user.getPassword() != null) {
                    updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));
                }
                if (user.getEmail() != null) {
                    updatedUser.setEmail(user.getEmail());
                }
                if (user.getRole() != null) {
                    updatedUser.setRole(user.getRole());
                }
                User savedUser = userRepository.save(updatedUser);
                LogUtil.logBusiness(logger, "用户更新", "成功更新用户，ID: " + id);
                return savedUser;
            } else {
                LogUtil.logWarn(logger, "更新用户不存在，ID: " + id);
                return null;
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "更新用户 - ID: " + id, e);
            throw e;
        }
    }

    @Override
    public void deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
            LogUtil.logBusiness(logger, "用户删除", "成功删除用户，ID: " + id);
        } catch (Exception e) {
            LogUtil.logError(logger, "删除用户 - ID: " + id, e);
            throw e;
        }
    }

    @Override
    public User validateUser(String username, String password) {
        try {
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
                LogUtil.logBusiness(logger, "用户验证", "验证用户成功: " + username);
                return user.get();
            } else {
                LogUtil.logWarn(logger, "用户验证失败: " + username);
                return null;
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "验证用户 - 用户名: " + username, e);
            throw e;
        }
    }

    @Override
    public List<User> getUsersByRole(String role) {
        try {
            List<User> users = userRepository.findByRole(role);
            LogUtil.logBusiness(logger, "用户查询", "按角色查询用户，角色: " + role + ", 结果数: " + users.size());
            return users;
        } catch (Exception e) {
            LogUtil.logError(logger, "按角色查询用户 - 角色: " + role, e);
            throw e;
        }
    }
    
    @Override
    public User getUserByFeishuUserId(String feishuUserId) {
        try {
            Optional<User> user = userRepository.findByFeishuUserId(feishuUserId);
            if (user.isPresent()) {
                LogUtil.logBusiness(logger, "用户查询", "通过飞书用户ID查询用户: " + feishuUserId);
                return user.get();
            } else {
                LogUtil.logWarn(logger, "通过飞书用户ID查询用户不存在: " + feishuUserId);
                return null;
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "通过飞书用户ID查询用户 - ID: " + feishuUserId, e);
            throw e;
        }
    }
    
    @Override
    public User createOrUpdateFeishuUser(User user) {
        try {
            // 先尝试通过飞书用户ID查找用户
            Optional<User> existingUser = userRepository.findByFeishuUserId(user.getFeishuUserId());
            
            if (existingUser.isPresent()) {
                // 更新现有用户
                User updatedUser = existingUser.get();
                if (user.getFeishuOpenId() != null) {
                    updatedUser.setFeishuOpenId(user.getFeishuOpenId());
                }
                if (user.getFeishuUnionId() != null) {
                    updatedUser.setFeishuUnionId(user.getFeishuUnionId());
                }
                if (user.getFeishuAvatar() != null) {
                    updatedUser.setFeishuAvatar(user.getFeishuAvatar());
                }
                if (user.getFeishuName() != null) {
                    updatedUser.setFeishuName(user.getFeishuName());
                }
                if (user.getUsername() != null) {
                    updatedUser.setUsername(user.getUsername());
                }
                if (user.getEmail() != null) {
                    updatedUser.setEmail(user.getEmail());
                }
                if (user.getRole() != null) {
                    updatedUser.setRole(user.getRole());
                }
                User savedUser = userRepository.save(updatedUser);
                LogUtil.logBusiness(logger, "用户更新", "成功更新飞书用户: " + savedUser.getFeishuUserId());
                return savedUser;
            } else {
                // 创建新用户
                // 为飞书用户生成随机密码
                String randomPassword = generateRandomPassword();
                user.setPassword(passwordEncoder.encode(randomPassword));
                // 默认为普通用户角色
                if (user.getRole() == null) {
                    user.setRole("ROLE_USER");
                }
                User savedUser = userRepository.save(user);
                LogUtil.logBusiness(logger, "用户创建", "成功创建飞书用户: " + savedUser.getFeishuUserId());
                return savedUser;
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "创建或更新飞书用户 - ID: " + user.getFeishuUserId(), e);
            throw e;
        }
    }
    
    /**
     * 生成随机密码
     */
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }
}