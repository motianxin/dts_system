-- DTS问题单系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS dts_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE dts_system;

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS issues;
DROP TABLE IF EXISTS users;

-- 创建用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    role VARCHAR(20) NOT NULL COMMENT '角色：TESTER, TEST_MANAGER, DEVELOPER, DEV_MANAGER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建问题单表
CREATE TABLE issues (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '问题单ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    description TEXT COMMENT '描述',
    status VARCHAR(20) DEFAULT 'OPEN' COMMENT '状态：OPEN, CLOSED',
    priority VARCHAR(20) DEFAULT 'MEDIUM' COMMENT '优先级：LOW, MEDIUM, HIGH',
    assignee_id BIGINT COMMENT '指派人ID',
    reporter_id BIGINT COMMENT '报告人ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    review_status VARCHAR(20) COMMENT '审核状态：PENDING, APPROVED, REJECTED',
    review_comment TEXT COMMENT '审核意见',
    resolution TEXT COMMENT '处理结果',
    regression_result TEXT COMMENT '回归结果',
    process_status VARCHAR(50) COMMENT '流程状态：SUBMITTED, DEVELOPING, DEVELOPMENT_REVIEWING, REGRESSING, COMPLETED',
    FOREIGN KEY (assignee_id) REFERENCES users(id),
    FOREIGN KEY (reporter_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问题单表';

-- 插入测试用户数据
-- 测试人员
INSERT INTO users (username, password, email, role) VALUES
('tester1', '123456', 'tester1@example.com', 'TESTER'),
('tester2', '123456', 'tester2@example.com', 'TESTER');

-- 测试经理
INSERT INTO users (username, password, email, role) VALUES
('test_manager1', '123456', 'testmanager1@example.com', 'TEST_MANAGER'),
('test_manager2', '123456', 'testmanager2@example.com', 'TEST_MANAGER');

-- 开发人员
INSERT INTO users (username, password, email, role) VALUES
('developer1', '123456', 'developer1@example.com', 'DEVELOPER'),
('developer2', '123456', 'developer2@example.com', 'DEVELOPER');

-- 开发经理
INSERT INTO users (username, password, email, role) VALUES
('dev_manager1', '123456', 'devmanager1@example.com', 'DEV_MANAGER'),
('dev_manager2', '123456', 'devmanager2@example.com', 'DEV_MANAGER');

-- 插入测试问题单数据
-- 问题单1：待提交状态
INSERT INTO issues (title, description, status, priority, reporter_id, assignee_id, process_status) VALUES
('登录页面无法显示', '用户反馈登录页面加载缓慢，有时无法显示', 'OPEN', 'HIGH', 1, NULL, NULL);

-- 问题单2：已提交，待审核
INSERT INTO issues (title, description, status, priority, reporter_id, assignee_id, process_status, review_status) VALUES
('数据导出功能异常', '导出Excel文件时出现乱码', 'OPEN', 'MEDIUM', 1, NULL, 'SUBMITTED', 'PENDING');

-- 问题单3：审核通过，开发中
INSERT INTO issues (title, description, status, priority, reporter_id, assignee_id, process_status, review_status, review_comment) VALUES
('搜索功能结果不准确', '搜索关键词"测试"时返回结果不匹配', 'OPEN', 'HIGH', 2, 5, 'DEVELOPING', 'APPROVED', '问题确认，分配给开发人员处理');

-- 问题单4：开发完成，待审核
INSERT INTO issues (title, description, status, priority, reporter_id, assignee_id, process_status, review_status, review_comment, resolution) VALUES
('用户头像上传失败', '上传头像时提示文件格式不支持', 'OPEN', 'LOW', 1, 5, 'DEVELOPMENT_REVIEWING', 'APPROVED', '问题确认，分配给开发人员处理', '已修复头像上传功能，支持jpg、png、gif格式');

-- 问题单5：审核通过，回归中
INSERT INTO issues (title, description, status, priority, reporter_id, assignee_id, process_status, review_status, review_comment, resolution) VALUES
('页面加载速度慢', '首页加载时间超过5秒', 'OPEN', 'HIGH', 2, 6, 'REGRESSING', 'APPROVED', '性能问题，需要优化', '已优化数据库查询，添加索引');

-- 问题单6：已完成
INSERT INTO issues (title, description, status, priority, reporter_id, assignee_id, process_status, review_status, review_comment, resolution, regression_result) VALUES
('密码重置邮件未收到', '用户点击密码重置后未收到邮件', 'CLOSED', 'MEDIUM', 1, 6, 'COMPLETED', 'APPROVED', '邮件服务配置问题', '已修复邮件服务配置', '回归测试通过，功能正常');

-- 问题单7：审核拒绝
INSERT INTO issues (title, description, status, priority, reporter_id, assignee_id, process_status, review_status, review_comment) VALUES
('界面颜色不喜欢', '建议将主题颜色改为蓝色', 'OPEN', 'LOW', 2, NULL, 'SUBMITTED', 'REJECTED', '此为需求建议，非缺陷问题，请提交需求工单');

-- 查询验证数据
SELECT '用户数据' as 数据类型, COUNT(*) as 数量 FROM users
UNION ALL
SELECT '问题单数据', COUNT(*) FROM issues;

-- 查询所有用户
SELECT id, username, email, role, created_at FROM users;

-- 查询所有问题单
SELECT i.id, i.title, i.priority, i.status, i.process_status, i.review_status, u1.username as reporter, u2.username as assignee
FROM issues i
LEFT JOIN users u1 ON i.reporter_id = u1.id
LEFT JOIN users u2 ON i.assignee_id = u2.id;
