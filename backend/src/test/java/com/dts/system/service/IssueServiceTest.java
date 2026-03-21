package com.dts.system.service;

import com.dts.system.model.Issue;
import com.dts.system.repository.IssueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * IssueService单元测试类
 */
@ExtendWith(MockitoExtension.class)
public class IssueServiceTest {

    @Mock
    private IssueRepository issueRepository;

    @InjectMocks
    private IssueServiceImpl issueService;

    private Issue testIssue;

    @BeforeEach
    void setUp() {
        testIssue = new Issue();
        testIssue.setId(1L);
        testIssue.setTitle("测试问题单");
        testIssue.setDescription("这是一个测试问题单");
        testIssue.setPriority("HIGH");
        testIssue.setStatus("OPEN");
        testIssue.setReporterId(1L);
    }

    @Test
    void testCreateIssue() {
        // 准备
        when(issueRepository.save(any(Issue.class))).thenReturn(testIssue);

        // 执行
        Issue result = issueService.createIssue(testIssue);

        // 验证
        assertNotNull(result);
        assertEquals("测试问题单", result.getTitle());
        assertEquals("OPEN", result.getStatus());
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    void testGetAllIssues() {
        // 准备
        Issue issue2 = new Issue();
        issue2.setId(2L);
        issue2.setTitle("第二个问题单");
        when(issueRepository.findAll()).thenReturn(Arrays.asList(testIssue, issue2));

        // 执行
        List<Issue> result = issueService.getAllIssues();

        // 验证
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(issueRepository, times(1)).findAll();
    }

    @Test
    void testGetIssueById() {
        // 准备
        when(issueRepository.findById(1L)).thenReturn(Optional.of(testIssue));

        // 执行
        Issue result = issueService.getIssueById(1L);

        // 验证
        assertNotNull(result);
        assertEquals("测试问题单", result.getTitle());
        verify(issueRepository, times(1)).findById(1L);
    }

    @Test
    void testGetIssueById_NotFound() {
        // 准备
        when(issueRepository.findById(999L)).thenReturn(Optional.empty());

        // 执行
        Issue result = issueService.getIssueById(999L);

        // 验证
        assertNull(result);
        verify(issueRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdateIssue() {
        // 准备
        Issue updatedIssue = new Issue();
        updatedIssue.setTitle("更新后的标题");
        updatedIssue.setDescription("更新后的描述");
        updatedIssue.setPriority("MEDIUM");
        updatedIssue.setStatus("CLOSED");

        when(issueRepository.findById(1L)).thenReturn(Optional.of(testIssue));
        when(issueRepository.save(any(Issue.class))).thenReturn(testIssue);

        // 执行
        Issue result = issueService.updateIssue(1L, updatedIssue);

        // 验证
        assertNotNull(result);
        verify(issueRepository, times(1)).findById(1L);
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    void testDeleteIssue() {
        // 执行
        issueService.deleteIssue(1L);

        // 验证
        verify(issueRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSubmitIssue() {
        // 准备
        when(issueRepository.findById(1L)).thenReturn(Optional.of(testIssue));
        when(issueRepository.save(any(Issue.class))).thenReturn(testIssue);

        // 执行
        Issue result = issueService.submitIssue(1L);

        // 验证
        assertNotNull(result);
        assertEquals("SUBMITTED", result.getProcessStatus());
        verify(issueRepository, times(1)).findById(1L);
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    void testReviewIssue_Approved() {
        // 准备
        when(issueRepository.findById(1L)).thenReturn(Optional.of(testIssue));
        when(issueRepository.save(any(Issue.class))).thenReturn(testIssue);

        // 执行
        Issue result = issueService.reviewIssue(1L, "APPROVED", "审核通过");

        // 验证
        assertNotNull(result);
        assertEquals("APPROVED", result.getReviewStatus());
        assertEquals("审核通过", result.getReviewComment());
        assertEquals("DEVELOPING", result.getProcessStatus());
        verify(issueRepository, times(1)).findById(1L);
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    void testReviewIssue_Rejected() {
        // 准备
        when(issueRepository.findById(1L)).thenReturn(Optional.of(testIssue));
        when(issueRepository.save(any(Issue.class))).thenReturn(testIssue);

        // 执行
        Issue result = issueService.reviewIssue(1L, "REJECTED", "问题描述不清晰");

        // 验证
        assertNotNull(result);
        assertEquals("REJECTED", result.getReviewStatus());
        assertEquals("问题描述不清晰", result.getReviewComment());
        verify(issueRepository, times(1)).findById(1L);
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    void testAssignToDeveloper() {
        // 准备
        when(issueRepository.findById(1L)).thenReturn(Optional.of(testIssue));
        when(issueRepository.save(any(Issue.class))).thenReturn(testIssue);

        // 执行
        Issue result = issueService.assignToDeveloper(1L, 5L);

        // 验证
        assertNotNull(result);
        assertEquals(5L, result.getAssigneeId());
        assertEquals("DEVELOPING", result.getProcessStatus());
        verify(issueRepository, times(1)).findById(1L);
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    void testResolveIssue() {
        // 准备
        when(issueRepository.findById(1L)).thenReturn(Optional.of(testIssue));
        when(issueRepository.save(any(Issue.class))).thenReturn(testIssue);

        // 执行
        Issue result = issueService.resolveIssue(1L, "已修复代码逻辑错误");

        // 验证
        assertNotNull(result);
        assertEquals("已修复代码逻辑错误", result.getResolution());
        assertEquals("DEVELOPMENT_REVIEWING", result.getProcessStatus());
        verify(issueRepository, times(1)).findById(1L);
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    void testReviewResolution() {
        // 准备
        when(issueRepository.findById(1L)).thenReturn(Optional.of(testIssue));
        when(issueRepository.save(any(Issue.class))).thenReturn(testIssue);

        // 执行
        Issue result = issueService.reviewResolution(1L, "APPROVED", "处理结果符合要求");

        // 验证
        assertNotNull(result);
        assertEquals("APPROVED", result.getReviewStatus());
        assertEquals("处理结果符合要求", result.getReviewComment());
        assertEquals("REGRESSING", result.getProcessStatus());
        verify(issueRepository, times(1)).findById(1L);
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    void testAssignToTester() {
        // 准备
        when(issueRepository.findById(1L)).thenReturn(Optional.of(testIssue));
        when(issueRepository.save(any(Issue.class))).thenReturn(testIssue);

        // 执行
        Issue result = issueService.assignToTester(1L, 1L);

        // 验证
        assertNotNull(result);
        assertEquals(1L, result.getAssigneeId());
        assertEquals("REGRESSING", result.getProcessStatus());
        verify(issueRepository, times(1)).findById(1L);
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    void testCompleteRegression() {
        // 准备
        when(issueRepository.findById(1L)).thenReturn(Optional.of(testIssue));
        when(issueRepository.save(any(Issue.class))).thenReturn(testIssue);

        // 执行
        Issue result = issueService.completeRegression(1L, "回归测试通过，功能正常");

        // 验证
        assertNotNull(result);
        assertEquals("回归测试通过，功能正常", result.getRegressionResult());
        assertEquals("COMPLETED", result.getProcessStatus());
        verify(issueRepository, times(1)).findById(1L);
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    void testSubmitIssue_NotFound() {
        // 准备
        when(issueRepository.findById(999L)).thenReturn(Optional.empty());

        // 执行
        Issue result = issueService.submitIssue(999L);

        // 验证
        assertNull(result);
        verify(issueRepository, times(1)).findById(999L);
        verify(issueRepository, never()).save(any(Issue.class));
    }
}