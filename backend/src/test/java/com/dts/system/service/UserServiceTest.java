package com.dts.system.service;

import com.dts.system.model.User;
import com.dts.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserService单元测试类
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("Test123!"); // 强密码
        testUser.setEmail("test@example.com");
        testUser.setRole("TESTER");
    }

    @Test
    void testCreateUser() {
        // 准备
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // 执行
        User result = userService.createUser(testUser);

        // 验证
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("TESTER", result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testGetAllUsers() {
        // 准备
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testuser2");
        user2.setRole("DEVELOPER");
        
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        // 执行
        List<User> result = userService.getAllUsers();

        // 验证
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById() {
        // 准备
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // 执行
        User result = userService.getUserById(1L);

        // 验证
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        // 准备
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // 执行
        User result = userService.getUserById(999L);

        // 验证
        assertNull(result);
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void testGetUserByUsername() {
        // 准备
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // 执行
        User result = userRepository.findByUsername("testuser").orElse(testUser);

        // 验证
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testUpdateUser() {
        // 准备
        User updatedUser = new User();
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setRole("TEST_MANAGER");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // 执行
        User result = userService.updateUser(1L, updatedUser);

        // 验证
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        // 执行
        userService.deleteUser(1L);

        // 验证
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testValidateUser() {
        // 准备
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("123456", testUser.getPassword())).thenReturn(true);

        // 执行
        User result = userService.validateUser("testuser", "123456");

        // 验证
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testValidateUser_InvalidPassword() {
        // 准备
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        // 执行
        User result = userService.validateUser("testuser", "wrongpassword");

        // 验证
        assertNull(result);
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testValidateUser_UserNotFound() {
        // 准备
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // 执行
        User result = userService.validateUser("nonexistent", "123456");

        // 验证
        assertNull(result);
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testGetUsersByRole() {
        // 准备
        User tester1 = new User();
        tester1.setId(1L);
        tester1.setUsername("tester1");
        tester1.setRole("TESTER");
        
        User tester2 = new User();
        tester2.setId(2L);
        tester2.setUsername("tester2");
        tester2.setRole("TESTER");
        
        when(userRepository.findByRole("TESTER")).thenReturn(Arrays.asList(tester1, tester2));

        // 执行
        List<User> result = userService.getUsersByRole("TESTER");

        // 验证
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("TESTER", result.get(0).getRole());
        assertEquals("TESTER", result.get(1).getRole());
        verify(userRepository, times(1)).findByRole("TESTER");
    }
}