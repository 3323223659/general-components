package com.yang.wrench.starter.stask.job.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 任务调度器配置属性类
 * 通过@ConfigurationProperties注解将配置文件中的属性映射到该类的字段
 *
 * Created with IntelliJ IDEA.
 * @Author: yang
 * @Date: 2025/09/22/20:00
 * @Description: 任务调度器配置属性
 */
@ConfigurationProperties(prefix = "wrench.task.job", ignoreInvalidFields = true)
public class TaskJobAutoProperties {

    /** 是否启用任务调度器，默认值为true */
    private boolean enabled = true;

    /** 线程池大小，默认值为10 */
    private int poolSize = 10;

    /** 线程名称前缀，默认值为"task-scheduler-" */
    private String threadNamePrefix = "task-scheduler-";

    /** 关闭时是否等待任务完成，默认值为true */
    private boolean waitForTasksToCompleteOnShutdown = true;

    /** 等待终止时间（秒），默认值为60秒 */
    private int awaitTerminationSeconds = 60;

    /** 任务刷新间隔（毫秒），默认值为60000毫秒（1分钟） */
    private long refreshInterval = 60000;

    /** 清理无效任务的cron表达式，默认值为"0 0/10 * * * ?"（每10分钟执行一次） */
    private String cleanInvalidTasksCron = "0 0/10 * * * ?";

    /**
     * 获取是否启用任务调度器
     * @return 启用状态
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用任务调度器
     * @param enabled 启用状态
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取线程池大小
     * @return 线程池大小
     */
    public int getPoolSize() {
        return poolSize;
    }

    /**
     * 设置线程池大小
     * @param poolSize 线程池大小
     */
    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    /**
     * 获取线程名称前缀
     * @return 线程名称前缀
     */
    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    /**
     * 设置线程名称前缀
     * @param threadNamePrefix 线程名称前缀
     */
    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    /**
     * 获取关闭时是否等待任务完成
     * @return 等待任务完成状态
     */
    public boolean isWaitForTasksToCompleteOnShutdown() {
        return waitForTasksToCompleteOnShutdown;
    }

    /**
     * 设置关闭时是否等待任务完成
     * @param waitForTasksToCompleteOnShutdown 等待任务完成状态
     */
    public void setWaitForTasksToCompleteOnShutdown(boolean waitForTasksToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
    }

    /**
     * 获取等待终止时间（秒）
     * @return 等待终止时间
     */
    public int getAwaitTerminationSeconds() {
        return awaitTerminationSeconds;
    }

    /**
     * 设置等待终止时间（秒）
     * @param awaitTerminationSeconds 等待终止时间
     */
    public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
    }

    /**
     * 获取任务刷新间隔（毫秒）
     * @return 刷新间隔
     */
    public long getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * 设置任务刷新间隔（毫秒）
     * @param refreshInterval 刷新间隔
     */
    public void setRefreshInterval(long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    /**
     * 获取清理无效任务的cron表达式
     * @return cron表达式
     */
    public String getCleanInvalidTasksCron() {
        return cleanInvalidTasksCron;
    }

    /**
     * 设置清理无效任务的cron表达式
     * @param cleanInvalidTasksCron cron表达式
     */
    public void setCleanInvalidTasksCron(String cleanInvalidTasksCron) {
        this.cleanInvalidTasksCron = cleanInvalidTasksCron;
    }
}