package com.dts.system.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 * 提供统一的日志记录方法
 */
public class LogUtil {

    /**
     * 获取Logger实例
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * 记录操作日志
     */
    public static void logOperation(Logger logger, String operation, String details) {
        logger.info("[操作日志] {} - {}", operation, details);
    }

    /**
     * 记录操作日志（带用户ID）
     */
    public static void logOperation(Logger logger, String operation, String details, Long userId) {
        logger.info("[操作日志] 用户[{}] {} - {}", userId, operation, details);
    }

    /**
     * 记录业务日志
     */
    public static void logBusiness(Logger logger, String businessType, String message) {
        logger.info("[业务日志] {} - {}", businessType, message);
    }

    /**
     * 记录错误日志
     */
    public static void logError(Logger logger, String operation, Exception e) {
        logger.error("[错误日志] {} - 异常信息: {}", operation, e.getMessage(), e);
    }

    /**
     * 记录错误日志（带用户ID）
     */
    public static void logError(Logger logger, String operation, Exception e, Long userId) {
        logger.error("[错误日志] 用户[{}] {} - 异常信息: {}", userId, operation, e.getMessage(), e);
    }

    /**
     * 记录警告日志
     */
    public static void logWarn(Logger logger, String message) {
        logger.warn("[警告日志] {}", message);
    }

    /**
     * 记录调试日志
     */
    public static void logDebug(Logger logger, String message) {
        logger.debug("[调试日志] {}", message);
    }

    /**
     * 记录数据访问日志
     */
    public static void logDataAccess(Logger logger, String operation, String dataType, Object dataId) {
        logger.info("[数据访问] {} {} - ID: {}", operation, dataType, dataId);
    }

    /**
     * 记录流程日志
     */
    public static void logProcess(Logger logger, String processName, String step, String status) {
        logger.info("[流程日志] {} - 步骤: {} - 状态: {}", processName, step, status);
    }
}