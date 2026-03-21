package com.dts.system.service;

import com.dts.system.model.Issue;
import com.dts.system.repository.IssueRepository;
import com.dts.system.util.LogUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class IssueServiceImpl implements IssueService {

    private static final Logger logger = LogUtil.getLogger(IssueServiceImpl.class);

    @Autowired
    private IssueRepository issueRepository;

    @Override
    public Issue createIssue(Issue issue) {
        try {
            issue.setCreatedAt(new Date());
            issue.setUpdatedAt(new Date());
            issue.setStatus("OPEN");
            Issue savedIssue = issueRepository.save(issue);
            LogUtil.logBusiness(logger, "问题单创建", "成功创建问题单，ID: " + savedIssue.getId() + ", 标题: " + savedIssue.getTitle());
            return savedIssue;
        } catch (Exception e) {
            LogUtil.logError(logger, "创建问题单", e, issue.getReporterId());
            throw e;
        }
    }

    @Override
    public List<Issue> getAllIssues() {
        try {
            List<Issue> issues = issueRepository.findAll();
            LogUtil.logBusiness(logger, "问题单查询", "查询所有问题单，共 " + issues.size() + " 条记录");
            return issues;
        } catch (Exception e) {
            LogUtil.logError(logger, "查询所有问题单", e);
            throw e;
        }
    }

    @Override
    public Issue getIssueById(Long id) {
        try {
            Optional<Issue> issue = issueRepository.findById(id);
            if (issue.isPresent()) {
                LogUtil.logBusiness(logger, "问题单查询", "查询问题单详情，ID: " + id);
                return issue.get();
            } else {
                LogUtil.logWarn(logger, "查询问题单不存在，ID: " + id);
                return null;
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "查询问题单详情 - ID: " + id, e);
            throw e;
        }
    }

    @Override
    public Issue updateIssue(Long id, Issue issue) {
        try {
            Optional<Issue> existingIssue = issueRepository.findById(id);
            if (existingIssue.isPresent()) {
                Issue updatedIssue = existingIssue.get();
                updatedIssue.setTitle(issue.getTitle());
                updatedIssue.setDescription(issue.getDescription());
                updatedIssue.setStatus(issue.getStatus());
                updatedIssue.setPriority(issue.getPriority());
                updatedIssue.setAssigneeId(issue.getAssigneeId());
                updatedIssue.setUpdatedAt(new Date());
                Issue savedIssue = issueRepository.save(updatedIssue);
                LogUtil.logBusiness(logger, "问题单更新", "成功更新问题单，ID: " + id);
                return savedIssue;
            } else {
                LogUtil.logWarn(logger, "更新问题单不存在，ID: " + id);
                return null;
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "更新问题单 - ID: " + id, e, issue.getReporterId());
            throw e;
        }
    }

    @Override
    public void deleteIssue(Long id) {
        try {
            issueRepository.deleteById(id);
            LogUtil.logBusiness(logger, "问题单删除", "成功删除问题单，ID: " + id);
        } catch (Exception e) {
            LogUtil.logError(logger, "删除问题单 - ID: " + id, e);
            throw e;
        }
    }

    @Override
    public Issue submitIssue(Long id) {
        try {
            Optional<Issue> existingIssue = issueRepository.findById(id);
            if (existingIssue.isPresent()) {
                Issue issue = existingIssue.get();
                issue.setProcessStatus("SUBMITTED");
                issue.setUpdatedAt(new Date());
                Issue savedIssue = issueRepository.save(issue);
                LogUtil.logProcess(logger, "问题单流程", "提交", "问题单ID: " + id + ", 流程状态更新为: SUBMITTED");
                return savedIssue;
            } else {
                LogUtil.logWarn(logger, "提交问题单不存在，ID: " + id);
                return null;
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "提交问题单 - ID: " + id, e);
            throw e;
        }
    }

    @Override
    public Issue reviewIssue(Long id, String reviewStatus, String reviewComment) {
        try {
            Optional<Issue> existingIssue = issueRepository.findById(id);
            if (existingIssue.isPresent()) {
                Issue issue = existingIssue.get();
                issue.setReviewStatus(reviewStatus);
                issue.setReviewComment(reviewComment);
                if ("APPROVED".equals(reviewStatus)) {
                    issue.setProcessStatus("DEVELOPING");
                }
                issue.setUpdatedAt(new Date());
                Issue savedIssue = issueRepository.save(issue);
                LogUtil.logProcess(logger, "问题单流程", "审核", "问题单ID: " + id + ", 审核状态: " + reviewStatus);
                return savedIssue;
            } else {
                LogUtil.logWarn(logger, "审核问题单不存在，ID: " + id);
                return null;
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "审核问题单 - ID: " + id, e);
            throw e;
        }
    }

    @Override
    public Issue assignToDeveloper(Long id, Long developerId) {
        try {
            Optional<Issue> existingIssue = issueRepository.findById(id);
            if (existingIssue.isPresent()) {
                Issue issue = existingIssue.get();
                issue.setAssigneeId(developerId);
                issue.setProcessStatus("DEVELOPING");
                issue.setUpdatedAt(new Date());
                Issue savedIssue = issueRepository.save(issue);
                LogUtil.logProcess(logger, "问题单流程", "分配开发人员", "问题单ID: " + id + ", 开发人员ID: " + developerId);
                return savedIssue;
            } else {
                LogUtil.logWarn(logger, "分配开发人员时问题单不存在，ID: " + id);
                return null;
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "分配开发人员 - 问题单ID: " + id, e);
            throw e;
        }
    }

    @Override
    public Issue resolveIssue(Long id, String resolution) {
        try {
            Optional<Issue> existingIssue = issueRepository.findById(id);
            if (existingIssue.isPresent()) {
                Issue issue = existingIssue.get();
                issue.setResolution(resolution);
                issue.setProcessStatus("DEVELOPMENT_REVIEWING");
                issue.setUpdatedAt(new Date());
                Issue savedIssue = issueRepository.save(issue);
                LogUtil.logProcess(logger, "问题单流程", "解决问题单", "问题单ID: " + id + ", 流程状态更新为: DEVELOPMENT_REVIEWING");
                return savedIssue;
            } else {
                LogUtil.logWarn(logger, "解决问题单时问题单不存在，ID: " + id);
                return null;
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "解决问题单 - ID: " + id, e);
            throw e;
        }
    }

    @Override
    public Issue reviewResolution(Long id, String reviewStatus, String reviewComment) {
        try {
            Optional<Issue> existingIssue = issueRepository.findById(id);
            if (existingIssue.isPresent()) {
                Issue issue = existingIssue.get();
                issue.setReviewStatus(reviewStatus);
                issue.setReviewComment(reviewComment);
                if ("APPROVED".equals(reviewStatus)) {
                    issue.setProcessStatus("REGRESSING");
                }
                issue.setUpdatedAt(new Date());
                Issue savedIssue = issueRepository.save(issue);
                LogUtil.logProcess(logger, "问题单流程", "审核处理结果", "问题单ID: " + id + ", 审核状态: " + reviewStatus);
                return savedIssue;
            } else {
                LogUtil.logWarn(logger, "审核处理结果时问题单不存在，ID: " + id);
                return null;
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "审核处理结果 - ID: " + id, e);
            throw e;
        }
    }

    @Override
    public Issue assignToTester(Long id, Long testerId) {
        try {
            Optional<Issue> existingIssue = issueRepository.findById(id);
            if (existingIssue.isPresent()) {
                Issue issue = existingIssue.get();
                issue.setAssigneeId(testerId);
                issue.setProcessStatus("REGRESSING");
                issue.setUpdatedAt(new Date());
                Issue savedIssue = issueRepository.save(issue);
                LogUtil.logProcess(logger, "问题单流程", "分配测试人员", "问题单ID: " + id + ", 测试人员ID: " + testerId);
                return savedIssue;
            } else {
                LogUtil.logWarn(logger, "分配测试人员时问题单不存在，ID: " + id);
                return null;
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "分配测试人员 - 问题单ID: " + id, e);
            throw e;
        }
    }

    @Override
    public Issue completeRegression(Long id, String regressionResult) {
        try {
            Optional<Issue> existingIssue = issueRepository.findById(id);
            if (existingIssue.isPresent()) {
                Issue issue = existingIssue.get();
                issue.setRegressionResult(regressionResult);
                issue.setProcessStatus("COMPLETED");
                issue.setUpdatedAt(new Date());
                Issue savedIssue = issueRepository.save(issue);
                LogUtil.logProcess(logger, "问题单流程", "完成回归测试", "问题单ID: " + id + ", 流程状态更新为: COMPLETED");
                return savedIssue;
            } else {
                LogUtil.logWarn(logger, "完成回归测试时问题单不存在，ID: " + id);
                return null;
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "完成回归测试 - ID: " + id, e);
            throw e;
        }
    }

    @Override
    public List<Issue> filterIssues(String status, String priority, String processStatus, Long reporterId, Long assigneeId) {
        try {
            List<Issue> allIssues = issueRepository.findAll();
            List<Issue> filteredIssues = allIssues.stream()
                    .filter(issue -> status == null || status.isEmpty() || status.equals(issue.getStatus()))
                    .filter(issue -> priority == null || priority.isEmpty() || priority.equals(issue.getPriority()))
                    .filter(issue -> processStatus == null || processStatus.isEmpty() || processStatus.equals(issue.getProcessStatus()))
                    .filter(issue -> reporterId == null || reporterId.equals(issue.getReporterId()))
                    .filter(issue -> assigneeId == null || assigneeId.equals(issue.getAssigneeId()))
                    .collect(Collectors.toList());
            LogUtil.logBusiness(logger, "问题单筛选", "筛选条件: 状态=" + status + ", 优先级=" + priority + ", 流程状态=" + processStatus + ", 结果数=" + filteredIssues.size());
            return filteredIssues;
        } catch (Exception e) {
            LogUtil.logError(logger, "筛选问题单", e);
            throw e;
        }
    }

    @Override
    public ByteArrayOutputStream exportIssuesToExcel(List<Issue> issues) throws Exception {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("问题单列表");
        
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        String[] headers = {"ID", "标题", "描述", "状态", "优先级", "流程状态", "审核状态", "审核意见", 
                          "处理结果", "回归结果", "报告人ID", "指派人ID", "创建时间", "更新时间"};
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (int i = 0; i < issues.size(); i++) {
            Issue issue = issues.get(i);
            Row row = sheet.createRow(i + 1);
            
            row.createCell(0).setCellValue(issue.getId() != null ? issue.getId() : 0);
            row.createCell(1).setCellValue(issue.getTitle() != null ? issue.getTitle() : "");
            row.createCell(2).setCellValue(issue.getDescription() != null ? issue.getDescription() : "");
            row.createCell(3).setCellValue(issue.getStatus() != null ? issue.getStatus() : "");
            row.createCell(4).setCellValue(issue.getPriority() != null ? issue.getPriority() : "");
            row.createCell(5).setCellValue(issue.getProcessStatus() != null ? issue.getProcessStatus() : "");
            row.createCell(6).setCellValue(issue.getReviewStatus() != null ? issue.getReviewStatus() : "");
            row.createCell(7).setCellValue(issue.getReviewComment() != null ? issue.getReviewComment() : "");
            row.createCell(8).setCellValue(issue.getResolution() != null ? issue.getResolution() : "");
            row.createCell(9).setCellValue(issue.getRegressionResult() != null ? issue.getRegressionResult() : "");
            row.createCell(10).setCellValue(issue.getReporterId() != null ? issue.getReporterId() : 0);
            row.createCell(11).setCellValue(issue.getAssigneeId() != null ? issue.getAssigneeId() : 0);
            row.createCell(12).setCellValue(issue.getCreatedAt() != null ? dateFormat.format(issue.getCreatedAt()) : "");
            row.createCell(13).setCellValue(issue.getUpdatedAt() != null ? dateFormat.format(issue.getUpdatedAt()) : "");
            
            for (int j = 0; j < headers.length; j++) {
                sheet.autoSizeColumn(j);
            }
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        LogUtil.logBusiness(logger, "Excel导出", "成功导出 " + issues.size() + " 条问题单记录到Excel");
        return outputStream;
        } catch (Exception e) {
            LogUtil.logError(logger, "导出问题单到Excel", e);
            throw e;
        }
    }
}