package com.dts.system.controller;

import com.dts.system.model.Issue;
import com.dts.system.service.IssueService;
import com.dts.system.util.LogUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private static final Logger logger = LogUtil.getLogger(IssueController.class);

    @Autowired
    private IssueService issueService;

    @PostMapping
    public ResponseEntity<?> createIssue(@RequestBody Issue issue) {
        logger.info("[API调用] 创建问题单 - 标题: {}", issue.getTitle());
        try {
            Issue createdIssue = issueService.createIssue(issue);
            LogUtil.logOperation(logger, "创建问题单", "问题单ID: " + createdIssue.getId() + ", 标题: " + createdIssue.getTitle(), issue.getReporterId());
            return new ResponseEntity<>(createdIssue, HttpStatus.CREATED);
        } catch (Exception e) {
            LogUtil.logError(logger, "创建问题单", e, issue.getReporterId());
            return new ResponseEntity<>("Failed to create issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Issue>> getAllIssues() {
        logger.info("[API调用] 查询所有问题单");
        try {
            List<Issue> issues = issueService.getAllIssues();
            LogUtil.logDataAccess(logger, "查询", "问题单列表", "全部");
            return new ResponseEntity<>(issues, HttpStatus.OK);
        } catch (Exception e) {
            LogUtil.logError(logger, "查询所有问题单", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIssueById(@PathVariable Long id) {
        logger.info("[API调用] 查询问题单详情 - ID: {}", id);
        try {
            Issue issue = issueService.getIssueById(id);
            if (issue != null) {
                LogUtil.logDataAccess(logger, "查询", "问题单", id);
                return new ResponseEntity<>(issue, HttpStatus.OK);
            } else {
                LogUtil.logWarn(logger, "查询问题单不存在 - ID: " + id);
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "查询问题单详情 - ID: " + id, e);
            return new ResponseEntity<>("Failed to get issue: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateIssue(@PathVariable Long id, @RequestBody Issue issue) {
        logger.info("[API调用] 更新问题单 - ID: {}", id);
        try {
            Issue updatedIssue = issueService.updateIssue(id, issue);
            if (updatedIssue != null) {
                LogUtil.logOperation(logger, "更新问题单", "问题单ID: " + id, issue.getReporterId());
                return new ResponseEntity<>(updatedIssue, HttpStatus.OK);
            } else {
                LogUtil.logWarn(logger, "更新问题单不存在 - ID: " + id);
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "更新问题单 - ID: " + id, e, issue.getReporterId());
            return new ResponseEntity<>("Failed to update issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIssue(@PathVariable Long id) {
        logger.info("[API调用] 删除问题单 - ID: {}", id);
        try {
            issueService.deleteIssue(id);
            LogUtil.logOperation(logger, "删除问题单", "问题单ID: " + id, null);
            return new ResponseEntity<>("Issue deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            LogUtil.logError(logger, "删除问题单 - ID: " + id, e);
            return new ResponseEntity<>("Failed to delete issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 流程相关API
    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitIssue(@PathVariable Long id) {
        logger.info("[API调用] 提交问题单 - ID: {}", id);
        try {
            Issue submittedIssue = issueService.submitIssue(id);
            if (submittedIssue != null) {
                LogUtil.logProcess(logger, "问题单流程", "提交", "成功 - ID: " + id);
                return new ResponseEntity<>(submittedIssue, HttpStatus.OK);
            } else {
                LogUtil.logWarn(logger, "提交问题单不存在 - ID: " + id);
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "提交问题单 - ID: " + id, e);
            return new ResponseEntity<>("Failed to submit issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<?> reviewIssue(@PathVariable Long id, @RequestParam String reviewStatus, @RequestParam String reviewComment) {
        logger.info("[API调用] 审核问题单 - ID: {}, 审核状态: {}", id, reviewStatus);
        try {
            Issue reviewedIssue = issueService.reviewIssue(id, reviewStatus, reviewComment);
            if (reviewedIssue != null) {
                LogUtil.logProcess(logger, "问题单流程", "审核", reviewStatus + " - ID: " + id);
                return new ResponseEntity<>(reviewedIssue, HttpStatus.OK);
            } else {
                LogUtil.logWarn(logger, "审核问题单不存在 - ID: " + id);
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to review issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/assign-developer")
    public ResponseEntity<?> assignToDeveloper(@PathVariable Long id, @RequestParam Long developerId) {
        logger.info("[API调用] 分配开发人员 - 问题单ID: {}, 开发人员ID: {}", id, developerId);
        try {
            Issue assignedIssue = issueService.assignToDeveloper(id, developerId);
            if (assignedIssue != null) {
                LogUtil.logProcess(logger, "问题单流程", "分配开发人员", "问题单ID: " + id + ", 开发人员ID: " + developerId);
                return new ResponseEntity<>(assignedIssue, HttpStatus.OK);
            } else {
                LogUtil.logWarn(logger, "分配开发人员时问题单不存在 - ID: " + id);
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "分配开发人员 - 问题单ID: " + id, e);
            return new ResponseEntity<>("Failed to assign issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<?> resolveIssue(@PathVariable Long id, @RequestParam String resolution) {
        logger.info("[API调用] 解决问题单 - ID: {}", id);
        try {
            Issue resolvedIssue = issueService.resolveIssue(id, resolution);
            if (resolvedIssue != null) {
                LogUtil.logProcess(logger, "问题单流程", "解决问题单", "成功 - ID: " + id);
                return new ResponseEntity<>(resolvedIssue, HttpStatus.OK);
            } else {
                LogUtil.logWarn(logger, "解决问题单时问题单不存在 - ID: " + id);
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "解决问题单 - ID: " + id, e);
            return new ResponseEntity<>("Failed to resolve issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/review-resolution")
    public ResponseEntity<?> reviewResolution(@PathVariable Long id, @RequestParam String reviewStatus, @RequestParam String reviewComment) {
        logger.info("[API调用] 审核处理结果 - 问题单ID: {}, 审核状态: {}", id, reviewStatus);
        try {
            Issue reviewedIssue = issueService.reviewResolution(id, reviewStatus, reviewComment);
            if (reviewedIssue != null) {
                LogUtil.logProcess(logger, "问题单流程", "审核处理结果", reviewStatus + " - ID: " + id);
                return new ResponseEntity<>(reviewedIssue, HttpStatus.OK);
            } else {
                LogUtil.logWarn(logger, "审核处理结果时问题单不存在 - ID: " + id);
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "审核处理结果 - ID: " + id, e);
            return new ResponseEntity<>("Failed to review resolution: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/assign-tester")
    public ResponseEntity<?> assignToTester(@PathVariable Long id, @RequestParam Long testerId) {
        logger.info("[API调用] 分配测试人员 - 问题单ID: {}, 测试人员ID: {}", id, testerId);
        try {
            Issue assignedIssue = issueService.assignToTester(id, testerId);
            if (assignedIssue != null) {
                LogUtil.logProcess(logger, "问题单流程", "分配测试人员", "问题单ID: " + id + ", 测试人员ID: " + testerId);
                return new ResponseEntity<>(assignedIssue, HttpStatus.OK);
            } else {
                LogUtil.logWarn(logger, "分配测试人员时问题单不存在 - ID: " + id);
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "分配测试人员 - 问题单ID: " + id, e);
            return new ResponseEntity<>("Failed to assign issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/complete-regression")
    public ResponseEntity<?> completeRegression(@PathVariable Long id, @RequestParam String regressionResult) {
        logger.info("[API调用] 完成回归测试 - ID: {}", id);
        try {
            Issue completedIssue = issueService.completeRegression(id, regressionResult);
            if (completedIssue != null) {
                LogUtil.logProcess(logger, "问题单流程", "完成回归测试", "成功 - ID: " + id);
                return new ResponseEntity<>(completedIssue, HttpStatus.OK);
            } else {
                LogUtil.logWarn(logger, "完成回归测试时问题单不存在 - ID: " + id);
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "完成回归测试 - ID: " + id, e);
            return new ResponseEntity<>("Failed to complete regression: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 筛选问题单API
    @GetMapping("/filter")
    public ResponseEntity<List<Issue>> filterIssues(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String processStatus,
            @RequestParam(required = false) Long reporterId,
            @RequestParam(required = false) Long assigneeId) {
        logger.info("[API调用] 筛选问题单 - 状态: {}, 优先级: {}, 流程状态: {}", status, priority, processStatus);
        try {
            List<Issue> filteredIssues = issueService.filterIssues(status, priority, processStatus, reporterId, assigneeId);
            LogUtil.logDataAccess(logger, "筛选", "问题单列表", "条件: 状态=" + status + ", 优先级=" + priority + ", 流程状态=" + processStatus);
            return new ResponseEntity<>(filteredIssues, HttpStatus.OK);
        } catch (Exception e) {
            LogUtil.logError(logger, "筛选问题单", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 导出问题单到Excel API
    @GetMapping("/export")
    public ResponseEntity<Resource> exportIssuesToExcel(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String processStatus,
            @RequestParam(required = false) Long reporterId,
            @RequestParam(required = false) Long assigneeId) {
        logger.info("[API调用] 导出问题单到Excel - 状态: {}, 优先级: {}, 流程状态: {}", status, priority, processStatus);
        try {
            List<Issue> issues = issueService.filterIssues(status, priority, processStatus, reporterId, assigneeId);
            ByteArrayOutputStream outputStream = issueService.exportIssuesToExcel(issues);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String fileName = "issues_" + dateFormat.format(new Date()) + ".xlsx";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
            
            LogUtil.logOperation(logger, "导出问题单", "导出 " + issues.size() + " 条记录到Excel", null);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (Exception e) {
            LogUtil.logError(logger, "导出问题单到Excel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}