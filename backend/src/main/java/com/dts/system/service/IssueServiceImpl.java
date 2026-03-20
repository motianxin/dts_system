package com.dts.system.service;

import com.dts.system.model.Issue;
import com.dts.system.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Date;

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
}