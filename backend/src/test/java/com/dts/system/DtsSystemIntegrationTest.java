package com.dts.system;

import com.dts.system.model.Issue;
import com.dts.system.model.User;
import com.dts.system.service.IssueService;
import com.dts.system.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * DTS 系统集成测试类
 * 测试完整的流程机制
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DtsSystemIntegrationTest {

    @Autowired
    private IssueService issueService;

    @Autowired
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void testCompleteDtsFlow() {
        // 设置PasswordEncoder的mock行为
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        
        // 1. 创建测试人员
        User tester = new User();
        tester.setUsername("integration_tester");
        tester.setPassword("Test123!"); // 强密码
        tester.setEmail("integration_tester@example.com");
        tester.setRole("TESTER");
        User savedTester = userService.createUser(tester);
        assertNotNull(savedTester.getId());

        // 2. 创建测试经理
        User testManager = new User();
        testManager.setUsername("integration_test_manager");
        testManager.setPassword("Test123!"); // 强密码
        testManager.setEmail("integration_test_manager@example.com");
        testManager.setRole("TEST_MANAGER");
        User savedTestManager = userService.createUser(testManager);
        assertNotNull(savedTestManager.getId());

        // 3. 创建开发人员
        User developer = new User();
        developer.setUsername("integration_developer");
        developer.setPassword("Test123!"); // 强密码
        developer.setEmail("integration_developer@example.com");
        developer.setRole("DEVELOPER");
        User savedDeveloper = userService.createUser(developer);
        assertNotNull(savedDeveloper.getId());

        // 4. 创建开发经理
        User devManager = new User();
        devManager.setUsername("integration_dev_manager");
        devManager.setPassword("Test123!"); // 强密码
        devManager.setEmail("integration_dev_manager@example.com");
        devManager.setRole("DEV_MANAGER");
        User savedDevManager = userService.createUser(devManager);
        assertNotNull(savedDevManager.getId());

        // 5. 测试人员创建问题单
        Issue issue = new Issue();
        issue.setTitle("集成测试问题单");
        issue.setDescription("这是一个集成测试问题单");
        issue.setPriority("HIGH");
        issue.setReporterId(savedTester.getId());
        Issue savedIssue = issueService.createIssue(issue);
        assertNotNull(savedIssue.getId());
        assertEquals("OPEN", savedIssue.getStatus());

        // 6. 测试人员提交问题单
        Issue submittedIssue = issueService.submitIssue(savedIssue.getId());
        assertNotNull(submittedIssue);
        assertEquals("SUBMITTED", submittedIssue.getProcessStatus());

        // 7. 测试经理审核通过
        Issue reviewedIssue = issueService.reviewIssue(
                savedIssue.getId(), 
                "APPROVED", 
                "问题确认，可以处理"
        );
        assertNotNull(reviewedIssue);
        assertEquals("APPROVED", reviewedIssue.getReviewStatus());
        assertEquals("DEVELOPING", reviewedIssue.getProcessStatus());

        // 8. 分配给开发人员
        Issue assignedToDev = issueService.assignToDeveloper(
                savedIssue.getId(), 
                savedDeveloper.getId()
        );
        assertNotNull(assignedToDev);
        assertEquals(savedDeveloper.getId(), assignedToDev.getAssigneeId());

        // 9. 开发人员处理问题单
        Issue resolvedIssue = issueService.resolveIssue(
                savedIssue.getId(), 
                "已修复代码逻辑错误，请测试"
        );
        assertNotNull(resolvedIssue);
        assertEquals("已修复代码逻辑错误，请测试", resolvedIssue.getResolution());
        assertEquals("DEVELOPMENT_REVIEWING", resolvedIssue.getProcessStatus());

        // 10. 开发经理审核处理结果
        Issue reviewedResolution = issueService.reviewResolution(
                savedIssue.getId(), 
                "APPROVED", 
                "处理结果符合要求"
        );
        assertNotNull(reviewedResolution);
        assertEquals("APPROVED", reviewedResolution.getReviewStatus());
        assertEquals("REGRESSING", reviewedResolution.getProcessStatus());

        // 11. 分配给测试人员回归
        Issue assignedToTester = issueService.assignToTester(
                savedIssue.getId(), 
                savedTester.getId()
        );
        assertNotNull(assignedToTester);
        assertEquals(savedTester.getId(), assignedToTester.getAssigneeId());

        // 12. 测试人员完成回归
        Issue completedIssue = issueService.completeRegression(
                savedIssue.getId(), 
                "回归测试通过，功能正常"
        );
        assertNotNull(completedIssue);
        assertEquals("回归测试通过，功能正常", completedIssue.getRegressionResult());
        assertEquals("COMPLETED", completedIssue.getProcessStatus());

        System.out.println("完整DTS流程测试通过！");
    }

    @Test
    void testReviewRejectedFlow() {
        // 设置PasswordEncoder的mock行为
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        
        // 1. 创建测试人员
        User tester = new User();
        tester.setUsername("rejected_tester");
        tester.setPassword("Test123!"); // 强密码
        tester.setEmail("rejected_tester@example.com");
        tester.setRole("TESTER");
        User savedTester = userService.createUser(tester);

        // 2. 创建测试经理
        User testManager = new User();
        testManager.setUsername("rejected_test_manager");
        testManager.setPassword("Test123!"); // 强密码
        testManager.setEmail("rejected_test_manager@example.com");
        testManager.setRole("TEST_MANAGER");
        User savedTestManager = userService.createUser(testManager);

        // 3. 创建问题单并提交
        Issue issue = new Issue();
        issue.setTitle("审核拒绝测试");
        issue.setDescription("这是一个测试审核拒绝流程的问题单");
        issue.setPriority("LOW");
        issue.setReporterId(savedTester.getId());
        Issue savedIssue = issueService.createIssue(issue);
        issueService.submitIssue(savedIssue.getId());

        // 4. 测试经理审核拒绝
        Issue rejectedIssue = issueService.reviewIssue(
                savedIssue.getId(), 
                "REJECTED", 
                "问题描述不清晰，请补充"
        );
        
        assertNotNull(rejectedIssue);
        assertEquals("REJECTED", rejectedIssue.getReviewStatus());
        assertEquals("问题描述不清晰，请补充", rejectedIssue.getReviewComment());
        // 审核拒绝后，流程状态应该保持不变或回到初始状态
        
        System.out.println("审核拒绝流程测试通过！");
    }

    @Test
    void testUserAuthentication() {
        // 设置PasswordEncoder的mock行为
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(passwordEncoder.matches(anyString(), anyString())).thenAnswer(invocation -> {
            String rawPassword = invocation.getArgument(0);
            String encodedPassword = invocation.getArgument(1);
            return "Test123!".equals(rawPassword) && "encodedPassword".equals(encodedPassword);
        });
        
        // 1. 创建用户
        User user = new User();
        user.setUsername("auth_test_user");
        user.setPassword("Test123!"); // 强密码
        user.setEmail("auth_test@example.com");
        user.setRole("TESTER");
        User savedUser = userService.createUser(user);
        assertNotNull(savedUser.getId());

        // 2. 验证正确的用户名和密码
        User authenticatedUser = userService.validateUser("auth_test_user", "Test123!");
        assertNotNull(authenticatedUser);
        assertEquals("auth_test_user", authenticatedUser.getUsername());

        // 3. 验证错误的密码
        User wrongPasswordUser = userService.validateUser("auth_test_user", "wrong_password");
        assertNull(wrongPasswordUser);

        // 4. 验证不存在的用户
        User nonExistentUser = userService.validateUser("non_existent_user", "password");
        assertNull(nonExistentUser);

        System.out.println("用户认证测试通过！");
    }
}
