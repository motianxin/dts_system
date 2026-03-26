package com.dts.system.util;

import com.dts.system.model.User;
import com.dts.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class PasswordResetUtil {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 使用密码编码器加密密码
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    /**
     * 生成强密码
     * 要求：至少8位，包含字母、数字和特殊符号
     */
    public String generateStrongPassword() {
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "@#$%^&+=!";
        
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        
        // 确保包含每种类型的字符
        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));
        
        // 填充剩余字符
        String allChars = uppercase + lowercase + digits + special;
        for (int i = 4; i < 12; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // 打乱密码
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }
    
    /**
     * 重置所有用户密码
     */
    public void resetAllUserPasswords() {
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            String newPassword = generateStrongPassword();
            user.setPassword(newPassword);
            try {
                userService.createUser(user);
                System.out.println("用户 " + user.getUsername() + " 的密码已重置为: " + newPassword);
            } catch (Exception e) {
                System.err.println("重置用户 " + user.getUsername() + " 的密码失败: " + e.getMessage());
            }
        }
    }
}
