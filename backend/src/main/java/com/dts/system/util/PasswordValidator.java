package com.dts.system.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {
    
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    
    /**
     * 验证密码是否符合要求
     * 要求：至少8位，包含字母、数字和特殊符号
     */
    public static boolean validate(String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
    
    /**
     * 获取密码验证规则
     */
    public static String getPasswordRules() {
        return "密码必须至少8位，包含字母、数字和特殊符号（@#$%^&+=!）";
    }
}
