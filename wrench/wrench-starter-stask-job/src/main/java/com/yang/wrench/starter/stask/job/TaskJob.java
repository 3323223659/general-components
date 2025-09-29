package com.yang.wrench.starter.stask.job;

import com.yang.wrench.starter.stask.job.config.TaskJobAutoProperties;
import com.yang.wrench.starter.stask.job.service.ITaskJobService;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 任务调度作业类
 * 负责定时执行任务刷新和清理操作，动态管理任务调度配置
 * 通过Spring的@Scheduled注解实现定时任务调度
 *
 * @author yang
 */
public class TaskJob {

    // 任务调度配置属性
    private final TaskJobAutoProperties properties;

    // 任务作业服务接口，提供任务管理功能
    private final ITaskJobService taskJobService;

    /**
     * 构造函数
     * @param properties 任务调度配置属性
     * @param taskJobService 任务作业服务实例
     */
    public TaskJob(TaskJobAutoProperties properties, ITaskJobService taskJobService) {
        this.properties = properties;
        this.taskJobService = taskJobService;
    }

    /**
     * 定时刷新任务调度配置
     * 使用fixedRate定时策略，默认每60秒执行一次
     * 可通过配置文件中的wrench.task.job.refresh-interval属性自定义间隔时间
     */
    @Scheduled(fixedRateString = "${wrench.task.job.refresh-interval:60000}")
    public void refreshTasks() {
        // 检查任务调度器是否启用
        if (!properties.isEnabled()) {
            return;
        }
        // 调用任务服务刷新任务配置
        taskJobService.refreshTask();
    }

    /**
     * 定时清理无效任务
     * 使用cron表达式定时策略，默认每10分钟执行一次
     * 可通过配置文件中的wrench.task.job.clean-invalid-tasks-cron属性自定义cron表达式
     */
    @Scheduled(cron = "${wrench.task.job.clean-invalid-tasks-cron:0 0/10 * * * ?}")
    public void cleanInvalidTasks() {
        // 检查任务调度器是否启用
        if (!properties.isEnabled()) {
            return;
        }
        // 调用任务服务清理无效任务
        taskJobService.cleanInvalidTask();
    }
}