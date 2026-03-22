package com.dts.system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "issues")
@Data
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private Long assigneeId;
    private Long reporterId;
    private Date createdAt;
    private Date updatedAt;
    private String reviewStatus; // 审核状态：PENDING, APPROVED, REJECTED
    private String reviewComment; // 审核意见
    private String resolution; // 处理结果
    private String regressionResult; // 回归结果
    private String processStatus; // 流程状态：SUBMITTED, REVIEWING, DEVELOPING, DEVELOPMENT_REVIEWING, REGRESSING, COMPLETED
}