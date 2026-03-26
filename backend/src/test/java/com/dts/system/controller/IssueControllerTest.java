package com.dts.system.controller;

import com.dts.system.model.Issue;
import com.dts.system.service.IssueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * IssueController单元测试类
 */
@WebMvcTest(IssueController.class)
public class IssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IssueService issueService;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private ObjectMapper objectMapper;

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
    void testCreateIssue() throws Exception {
        // 准备
        when(issueService.createIssue(any())).thenReturn(testIssue);

        // 执行和验证
        mockMvc.perform(post("/api/issues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testIssue)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("测试问题单")))
                .andExpect(jsonPath("$.status", is("OPEN")));

        verify(issueService, times(1)).createIssue(any());

    }

    @Test
    void testGetAllIssues() throws Exception {
        // 准备
        Issue issue2 = new Issue();
        issue2.setId(2L);
        issue2.setTitle("第二个问题单");
        issue2.setStatus("OPEN");
        
        List<Issue> issues = Arrays.asList(testIssue, issue2);
        when(issueService.getAllIssues()).thenReturn(issues);

        // 执行和验证
        mockMvc.perform(get("/api/issues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("测试问题单")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("第二个问题单")));

        verify(issueService, times(1)).getAllIssues();
    }

    @Test
    void testGetIssueById() throws Exception {
        // 准备
        when(issueService.getIssueById(1L)).thenReturn(testIssue);

        // 执行和验证
        mockMvc.perform(get("/api/issues/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("测试问题单")))
                .andExpect(jsonPath("$.description", is("这是一个测试问题单")));

        verify(issueService, times(1)).getIssueById(1L);
    }

    @Test
    void testGetIssueById_NotFound() throws Exception {
        // 准备
        when(issueService.getIssueById(999L)).thenReturn(null);

        // 执行和验证
        mockMvc.perform(get("/api/issues/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Issue not found"));

        verify(issueService, times(1)).getIssueById(999L);
    }

    @Test
    void testUpdateIssue() throws Exception {
        // 准备
        Issue updatedIssue = new Issue();
        updatedIssue.setTitle("更新后的标题");
        updatedIssue.setDescription("更新后的描述");
        updatedIssue.setPriority("MEDIUM");
        updatedIssue.setStatus("CLOSED");

        when(issueService.updateIssue(eq(1L), any())).thenReturn(testIssue);

        // 执行和验证
        mockMvc.perform(put("/api/issues/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedIssue)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(issueService, times(1)).updateIssue(eq(1L), any());
    }

    @Test
    void testDeleteIssue() throws Exception {
        // 执行和验证
        mockMvc.perform(delete("/api/issues/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Issue deleted successfully"));

        verify(issueService, times(1)).deleteIssue(1L);
    }

    @Test
    void testSubmitIssue() throws Exception {
        // 准备
        when(issueService.submitIssue(1L)).thenReturn(testIssue);

        // 执行和验证
        mockMvc.perform(post("/api/issues/1/submit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(issueService, times(1)).submitIssue(1L);
    }

    @Test
    void testSubmitIssue_NotFound() throws Exception {
        // 准备
        when(issueService.submitIssue(999L)).thenReturn(null);

        // 执行和验证
        mockMvc.perform(post("/api/issues/999/submit"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Issue not found"));

        verify(issueService, times(1)).submitIssue(999L);
    }

    @Test
    void testReviewIssue() throws Exception {
        // 准备
        when(issueService.reviewIssue(eq(1L), eq("APPROVED"), eq("审核通过"))).thenReturn(testIssue);

        // 执行和验证
        mockMvc.perform(post("/api/issues/1/review")
                .param("reviewStatus", "APPROVED")
                .param("reviewComment", "审核通过"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(issueService, times(1)).reviewIssue(eq(1L), eq("APPROVED"), eq("审核通过"));
    }

    @Test
    void testAssignToDeveloper() throws Exception {
        // 准备
        when(issueService.assignToDeveloper(1L, 5L)).thenReturn(testIssue);

        // 执行和验证
        mockMvc.perform(post("/api/issues/1/assign-developer")
                .param("developerId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(issueService, times(1)).assignToDeveloper(1L, 5L);
    }

    @Test
    void testResolveIssue() throws Exception {
        // 准备
        when(issueService.resolveIssue(eq(1L), eq("已修复"))).thenReturn(testIssue);

        // 执行和验证
        mockMvc.perform(post("/api/issues/1/resolve")
                .param("resolution", "已修复"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(issueService, times(1)).resolveIssue(eq(1L), eq("已修复"));
    }

    @Test
    void testReviewResolution() throws Exception {
        // 准备
        when(issueService.reviewResolution(eq(1L), eq("APPROVED"), eq("处理结果符合要求"))).thenReturn(testIssue);

        // 执行和验证
        mockMvc.perform(post("/api/issues/1/review-resolution")
                .param("reviewStatus", "APPROVED")
                .param("reviewComment", "处理结果符合要求"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(issueService, times(1)).reviewResolution(eq(1L), eq("APPROVED"), eq("处理结果符合要求"));
    }

    @Test
    void testAssignToTester() throws Exception {
        // 准备
        when(issueService.assignToTester(1L, 1L)).thenReturn(testIssue);

        // 执行和验证
        mockMvc.perform(post("/api/issues/1/assign-tester")
                .param("testerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(issueService, times(1)).assignToTester(1L, 1L);
    }

    @Test
    void testCompleteRegression() throws Exception {
        // 准备
        when(issueService.completeRegression(eq(1L), eq("回归测试通过"))).thenReturn(testIssue);

        // 执行和验证
        mockMvc.perform(post("/api/issues/1/complete-regression")
                .param("regressionResult", "回归测试通过"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(issueService, times(1)).completeRegression(eq(1L), eq("回归测试通过"));
    }
}