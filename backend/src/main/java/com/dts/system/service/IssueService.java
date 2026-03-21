package com.dts.system.service;

import com.dts.system.model.Issue;
import java.io.ByteArrayOutputStream;
import java.util.List;

public interface IssueService {
    Issue createIssue(Issue issue);
    List<Issue> getAllIssues();
    Issue getIssueById(Long id);
    Issue updateIssue(Long id, Issue issue);
    void deleteIssue(Long id);
    // 流程相关方法
    Issue submitIssue(Long id); // 提交工单
    Issue reviewIssue(Long id, String reviewStatus, String reviewComment); // 审核工单
    Issue assignToDeveloper(Long id, Long developerId); // 分配给开发人员
    Issue resolveIssue(Long id, String resolution); // 开发人员处理
    Issue reviewResolution(Long id, String reviewStatus, String reviewComment); // 开发经理审核处理结果
    Issue assignToTester(Long id, Long testerId); // 分配给测试人员回归
    Issue completeRegression(Long id, String regressionResult); // 完成回归
    // 筛选和导出方法
    List<Issue> filterIssues(String status, String priority, String processStatus, Long reporterId, Long assigneeId);
    ByteArrayOutputStream exportIssuesToExcel(List<Issue> issues) throws Exception;
}