package com.dts.system.controller;

import com.dts.system.model.Issue;
import com.dts.system.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @PostMapping
    public ResponseEntity<?> createIssue(@RequestBody Issue issue) {
        try {
            Issue createdIssue = issueService.createIssue(issue);
            return new ResponseEntity<>(createdIssue, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Issue>> getAllIssues() {
        List<Issue> issues = issueService.getAllIssues();
        return new ResponseEntity<>(issues, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIssueById(@PathVariable Long id) {
        Issue issue = issueService.getIssueById(id);
        if (issue != null) {
            return new ResponseEntity<>(issue, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateIssue(@PathVariable Long id, @RequestBody Issue issue) {
        try {
            Issue updatedIssue = issueService.updateIssue(id, issue);
            if (updatedIssue != null) {
                return new ResponseEntity<>(updatedIssue, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIssue(@PathVariable Long id) {
        try {
            issueService.deleteIssue(id);
            return new ResponseEntity<>("Issue deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 流程相关API
    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitIssue(@PathVariable Long id) {
        try {
            Issue submittedIssue = issueService.submitIssue(id);
            if (submittedIssue != null) {
                return new ResponseEntity<>(submittedIssue, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to submit issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<?> reviewIssue(@PathVariable Long id, @RequestParam String reviewStatus, @RequestParam String reviewComment) {
        try {
            Issue reviewedIssue = issueService.reviewIssue(id, reviewStatus, reviewComment);
            if (reviewedIssue != null) {
                return new ResponseEntity<>(reviewedIssue, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to review issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/assign-developer")
    public ResponseEntity<?> assignToDeveloper(@PathVariable Long id, @RequestParam Long developerId) {
        try {
            Issue assignedIssue = issueService.assignToDeveloper(id, developerId);
            if (assignedIssue != null) {
                return new ResponseEntity<>(assignedIssue, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to assign issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<?> resolveIssue(@PathVariable Long id, @RequestParam String resolution) {
        try {
            Issue resolvedIssue = issueService.resolveIssue(id, resolution);
            if (resolvedIssue != null) {
                return new ResponseEntity<>(resolvedIssue, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to resolve issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/review-resolution")
    public ResponseEntity<?> reviewResolution(@PathVariable Long id, @RequestParam String reviewStatus, @RequestParam String reviewComment) {
        try {
            Issue reviewedIssue = issueService.reviewResolution(id, reviewStatus, reviewComment);
            if (reviewedIssue != null) {
                return new ResponseEntity<>(reviewedIssue, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to review resolution: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/assign-tester")
    public ResponseEntity<?> assignToTester(@PathVariable Long id, @RequestParam Long testerId) {
        try {
            Issue assignedIssue = issueService.assignToTester(id, testerId);
            if (assignedIssue != null) {
                return new ResponseEntity<>(assignedIssue, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to assign issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/complete-regression")
    public ResponseEntity<?> completeRegression(@PathVariable Long id, @RequestParam String regressionResult) {
        try {
            Issue completedIssue = issueService.completeRegression(id, regressionResult);
            if (completedIssue != null) {
                return new ResponseEntity<>(completedIssue, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to complete regression: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}