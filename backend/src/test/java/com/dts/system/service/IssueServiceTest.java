package com.dts.system.service;

import com.dts.system.model.Issue;
import com.dts.system.repository.IssueRepository;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Date;
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
        testIssue.setCreatedAt(new Date());
        testIssue.setUpdatedAt(new Date());
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

    // 筛选功能测试
    @Test
    void testFilterIssues_ByStatus() {
        // 准备
        Issue issue2 = new Issue();
        issue2.setId(2L);
        issue2.setTitle("第二个问题单");
        issue2.setStatus("CLOSED");
        
        when(issueRepository.findAll()).thenReturn(Arrays.asList(testIssue, issue2));

        // 执行
        List<Issue> result = issueService.filterIssues("OPEN", null, null, null, null);

        // 验证
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("OPEN", result.get(0).getStatus());
        verify(issueRepository, times(1)).findAll();
    }

    @Test
    void testFilterIssues_ByPriority() {
        // 准备
        Issue issue2 = new Issue();
        issue2.setId(2L);
        issue2.setTitle("第二个问题单");
        issue2.setPriority("LOW");
        
        when(issueRepository.findAll()).thenReturn(Arrays.asList(testIssue, issue2));

        // 执行
        List<Issue> result = issueService.filterIssues(null, "HIGH", null, null, null);

        // 验证
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("HIGH", result.get(0).getPriority());
        verify(issueRepository, times(1)).findAll();
    }

    @Test
    void testFilterIssues_ByProcessStatus() {
        // 准备
        testIssue.setProcessStatus("SUBMITTED");
        Issue issue2 = new Issue();
        issue2.setId(2L);
        issue2.setTitle("第二个问题单");
        issue2.setProcessStatus("DEVELOPING");
        
        when(issueRepository.findAll()).thenReturn(Arrays.asList(testIssue, issue2));

        // 执行
        List<Issue> result = issueService.filterIssues(null, null, "SUBMITTED", null, null);

        // 验证
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SUBMITTED", result.get(0).getProcessStatus());
        verify(issueRepository, times(1)).findAll();
    }

    @Test
    void testFilterIssues_ByReporterId() {
        // 准备
        Issue issue2 = new Issue();
        issue2.setId(2L);
        issue2.setTitle("第二个问题单");
        issue2.setReporterId(2L);
        
        when(issueRepository.findAll()).thenReturn(Arrays.asList(testIssue, issue2));

        // 执行
        List<Issue> result = issueService.filterIssues(null, null, null, 1L, null);

        // 验证
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getReporterId());
        verify(issueRepository, times(1)).findAll();
    }

    @Test
    void testFilterIssues_ByAssigneeId() {
        // 准备
        testIssue.setAssigneeId(1L);
        Issue issue2 = new Issue();
        issue2.setId(2L);
        issue2.setTitle("第二个问题单");
        issue2.setAssigneeId(2L);
        
        when(issueRepository.findAll()).thenReturn(Arrays.asList(testIssue, issue2));

        // 执行
        List<Issue> result = issueService.filterIssues(null, null, null, null, 1L);

        // 验证
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getAssigneeId());
        verify(issueRepository, times(1)).findAll();
    }

    @Test
    void testFilterIssues_AllParameters() {
        // 准备
        testIssue.setProcessStatus("SUBMITTED");
        Issue issue2 = new Issue();
        issue2.setId(2L);
        issue2.setTitle("第二个问题单");
        issue2.setStatus("CLOSED");
        issue2.setPriority("LOW");
        issue2.setProcessStatus("DEVELOPING");
        issue2.setReporterId(2L);
        issue2.setAssigneeId(2L);
        
        when(issueRepository.findAll()).thenReturn(Arrays.asList(testIssue, issue2));

        // 执行
        List<Issue> result = issueService.filterIssues("OPEN", "HIGH", "SUBMITTED", 1L, null);

        // 验证
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("OPEN", result.get(0).getStatus());
        assertEquals("HIGH", result.get(0).getPriority());
        assertEquals("SUBMITTED", result.get(0).getProcessStatus());
        assertEquals(1L, result.get(0).getReporterId());
        verify(issueRepository, times(1)).findAll();
    }

    @Test
    void testFilterIssues_NoFilters() {
        // 准备
        Issue issue2 = new Issue();
        issue2.setId(2L);
        issue2.setTitle("第二个问题单");
        
        when(issueRepository.findAll()).thenReturn(Arrays.asList(testIssue, issue2));

        // 执行
        List<Issue> result = issueService.filterIssues(null, null, null, null, null);

        // 验证
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(issueRepository, times(1)).findAll();
    }

    // 导出Excel功能测试
    @Test
    void testExportIssuesToExcel() throws Exception {
        // 准备
        Issue issue2 = new Issue();
        issue2.setId(2L);
        issue2.setTitle("第二个问题单");
        issue2.setDescription("这是第二个测试问题单");
        issue2.setPriority("LOW");
        issue2.setStatus("CLOSED");
        issue2.setProcessStatus("COMPLETED");
        issue2.setReviewStatus("APPROVED");
        issue2.setReviewComment("审核通过");
        issue2.setResolution("已修复");
        issue2.setRegressionResult("回归通过");
        issue2.setReporterId(1L);
        issue2.setAssigneeId(2L);
        issue2.setCreatedAt(new Date());
        issue2.setUpdatedAt(new Date());
        
        List<Issue> issues = Arrays.asList(testIssue, issue2);

        // 执行
        ByteArrayOutputStream outputStream = issueService.exportIssuesToExcel(issues);

        // 验证
        assertNotNull(outputStream);
        assertTrue(outputStream.size() > 0);

        // 验证Excel文件内容
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(outputStream.toByteArray()))) {
            Sheet sheet = workbook.getSheet("问题单列表");
            assertNotNull(sheet);
            assertEquals(3, sheet.getPhysicalNumberOfRows()); // 1 header + 2 data rows
            
            // 验证表头
            assertEquals("ID", sheet.getRow(0).getCell(0).getStringCellValue());
            assertEquals("标题", sheet.getRow(0).getCell(1).getStringCellValue());
            assertEquals("描述", sheet.getRow(0).getCell(2).getStringCellValue());
            
            // 验证第一行数据
            assertEquals(1, (int) sheet.getRow(1).getCell(0).getNumericCellValue());
            assertEquals("测试问题单", sheet.getRow(1).getCell(1).getStringCellValue());
            
            // 验证第二行数据
            assertEquals(2, (int) sheet.getRow(2).getCell(0).getNumericCellValue());
            assertEquals("第二个问题单", sheet.getRow(2).getCell(1).getStringCellValue());
        }
    }

    @Test
    void testExportIssuesToExcel_EmptyList() throws Exception {
        // 准备
        List<Issue> issues = Arrays.asList();

        // 执行
        ByteArrayOutputStream outputStream = issueService.exportIssuesToExcel(issues);

        // 验证
        assertNotNull(outputStream);
        assertTrue(outputStream.size() > 0);

        // 验证Excel文件内容
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(outputStream.toByteArray()))) {
            Sheet sheet = workbook.getSheet("问题单列表");
            assertNotNull(sheet);
            assertEquals(1, sheet.getPhysicalNumberOfRows()); // 只有表头
            
            // 验证表头
            assertEquals("ID", sheet.getRow(0).getCell(0).getStringCellValue());
        }
    }

    @Test
    void testExportIssuesToExcel_WithNullFields() throws Exception {
        // 准备
        Issue issueWithNulls = new Issue();
        issueWithNulls.setId(1L);
        issueWithNulls.setTitle("问题单");
        // 其他字段保持null
        
        List<Issue> issues = Arrays.asList(issueWithNulls);

        // 执行
        ByteArrayOutputStream outputStream = issueService.exportIssuesToExcel(issues);

        // 验证
        assertNotNull(outputStream);
        assertTrue(outputStream.size() > 0);

        // 验证Excel文件内容
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(outputStream.toByteArray()))) {
            Sheet sheet = workbook.getSheet("问题单列表");
            assertNotNull(sheet);
            assertEquals(2, sheet.getPhysicalNumberOfRows()); // 1 header + 1 data row
            
            // 验证数据行
            assertEquals(1, (int) sheet.getRow(1).getCell(0).getNumericCellValue());
            assertEquals("问题单", sheet.getRow(1).getCell(1).getStringCellValue());
        }
    }
}