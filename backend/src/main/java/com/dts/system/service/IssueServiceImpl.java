package com.dts.system.service;

import com.dts.system.model.Issue;
import com.dts.system.repository.IssueRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    @Autowired
    private IssueRepository issueRepository;

    @Override
    public Issue createIssue(Issue issue) {
        issue.setCreatedAt(new Date());
        issue.setUpdatedAt(new Date());
        issue.setStatus("OPEN");
        return issueRepository.save(issue);
    }

    @Override
    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    @Override
    public Issue getIssueById(Long id) {
        Optional<Issue> issue = issueRepository.findById(id);
        return issue.orElse(null);
    }

    @Override
    public Issue updateIssue(Long id, Issue issue) {
        Optional<Issue> existingIssue = issueRepository.findById(id);
        if (existingIssue.isPresent()) {
            Issue updatedIssue = existingIssue.get();
            updatedIssue.setTitle(issue.getTitle());
            updatedIssue.setDescription(issue.getDescription());
            updatedIssue.setStatus(issue.getStatus());
            updatedIssue.setPriority(issue.getPriority());
            updatedIssue.setAssigneeId(issue.getAssigneeId());
            updatedIssue.setUpdatedAt(new Date());
            return issueRepository.save(updatedIssue);
        }
        return null;
    }

    @Override
    public void deleteIssue(Long id) {
        issueRepository.deleteById(id);
    }

    @Override
    public Issue submitIssue(Long id) {
        Optional<Issue> existingIssue = issueRepository.findById(id);
        if (existingIssue.isPresent()) {
            Issue issue = existingIssue.get();
            issue.setProcessStatus("SUBMITTED");
            issue.setUpdatedAt(new Date());
            return issueRepository.save(issue);
        }
        return null;
    }

    @Override
    public Issue reviewIssue(Long id, String reviewStatus, String reviewComment) {
        Optional<Issue> existingIssue = issueRepository.findById(id);
        if (existingIssue.isPresent()) {
            Issue issue = existingIssue.get();
            issue.setReviewStatus(reviewStatus);
            issue.setReviewComment(reviewComment);
            if ("APPROVED".equals(reviewStatus)) {
                issue.setProcessStatus("DEVELOPING");
            }
            issue.setUpdatedAt(new Date());
            return issueRepository.save(issue);
        }
        return null;
    }

    @Override
    public Issue assignToDeveloper(Long id, Long developerId) {
        Optional<Issue> existingIssue = issueRepository.findById(id);
        if (existingIssue.isPresent()) {
            Issue issue = existingIssue.get();
            issue.setAssigneeId(developerId);
            issue.setProcessStatus("DEVELOPING");
            issue.setUpdatedAt(new Date());
            return issueRepository.save(issue);
        }
        return null;
    }

    @Override
    public Issue resolveIssue(Long id, String resolution) {
        Optional<Issue> existingIssue = issueRepository.findById(id);
        if (existingIssue.isPresent()) {
            Issue issue = existingIssue.get();
            issue.setResolution(resolution);
            issue.setProcessStatus("DEVELOPMENT_REVIEWING");
            issue.setUpdatedAt(new Date());
            return issueRepository.save(issue);
        }
        return null;
    }

    @Override
    public Issue reviewResolution(Long id, String reviewStatus, String reviewComment) {
        Optional<Issue> existingIssue = issueRepository.findById(id);
        if (existingIssue.isPresent()) {
            Issue issue = existingIssue.get();
            issue.setReviewStatus(reviewStatus);
            issue.setReviewComment(reviewComment);
            if ("APPROVED".equals(reviewStatus)) {
                issue.setProcessStatus("REGRESSING");
            }
            issue.setUpdatedAt(new Date());
            return issueRepository.save(issue);
        }
        return null;
    }

    @Override
    public Issue assignToTester(Long id, Long testerId) {
        Optional<Issue> existingIssue = issueRepository.findById(id);
        if (existingIssue.isPresent()) {
            Issue issue = existingIssue.get();
            issue.setAssigneeId(testerId);
            issue.setProcessStatus("REGRESSING");
            issue.setUpdatedAt(new Date());
            return issueRepository.save(issue);
        }
        return null;
    }

    @Override
    public Issue completeRegression(Long id, String regressionResult) {
        Optional<Issue> existingIssue = issueRepository.findById(id);
        if (existingIssue.isPresent()) {
            Issue issue = existingIssue.get();
            issue.setRegressionResult(regressionResult);
            issue.setProcessStatus("COMPLETED");
            issue.setUpdatedAt(new Date());
            return issueRepository.save(issue);
        }
        return null;
    }

    @Override
    public List<Issue> filterIssues(String status, String priority, String processStatus, Long reporterId, Long assigneeId) {
        List<Issue> allIssues = issueRepository.findAll();
        
        return allIssues.stream()
                .filter(issue -> status == null || status.isEmpty() || status.equals(issue.getStatus()))
                .filter(issue -> priority == null || priority.isEmpty() || priority.equals(issue.getPriority()))
                .filter(issue -> processStatus == null || processStatus.isEmpty() || processStatus.equals(issue.getProcessStatus()))
                .filter(issue -> reporterId == null || reporterId.equals(issue.getReporterId()))
                .filter(issue -> assigneeId == null || assigneeId.equals(issue.getAssigneeId()))
                .collect(Collectors.toList());
    }

    @Override
    public ByteArrayOutputStream exportIssuesToExcel(List<Issue> issues) throws Exception {
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
        
        return outputStream;
    }
}