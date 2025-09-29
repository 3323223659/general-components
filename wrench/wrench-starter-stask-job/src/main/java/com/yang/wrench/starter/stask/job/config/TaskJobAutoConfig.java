package com.yang.wrench.starter.stask.job.config;

import com.yang.wrench.starter.stask.job.TaskJob;
import com.yang.wrench.starter.stask.job.provider.ITaskDataProvider;
import com.yang.wrench.starter.stask.job.service.ITaskJobService;
import com.yang.wrench.starter.stask.job.service.TaskJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.List;

/**
 * 任务调度器自动配置类
 * 负责配置和初始化任务调度相关的组件
 *
 * Created with IntelliJ IDEA.
 * @Author: yang
 * @Date: 2025/09/22/20:00
 * @Description: 任务调度器自动配置类
 */
@Configuration // 标识这是一个Spring配置类
@EnableScheduling // 启用Spring的定时任务功能
@EnableConfigurationProperties(TaskJobAutoProperties.class) // 启用配置属性绑定
@ConditionalOnProperty(prefix = "wrench.task.job", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TaskJobAutoConfig {

    private final Logger log = LoggerFactory.getLogger(TaskJobAutoConfig.class);

    /**
     * 创建线程池任务调度器实例，用于执行定时任务和异步任务调度
     * @param properties 任务调度配置属性
     * @return 配置好的TaskScheduler实例
     */
    @Bean("WrenchTaskScheduler")
    public TaskScheduler taskScheduler(TaskJobAutoProperties properties) {
        // 创建线程池任务调度器
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 设置线程池大小
        scheduler.setPoolSize(properties.getPoolSize());
        // 设置线程名称前缀，便于日志追踪
        scheduler.setThreadNamePrefix(properties.getThreadNamePrefix());
        // 设置应用关闭时是否等待任务完成
        scheduler.setWaitForTasksToCompleteOnShutdown(properties.isWaitForTasksToCompleteOnShutdown());
        // 设置等待任务终止的最大时间（秒）
        scheduler.setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds());
        // 初始化调度器
        scheduler.initialize();

        // 记录初始化日志
        log.info("wrench，任务调度器初始化完成。线程池大小: {}, 线程名前缀: {}",
                properties.getPoolSize(), properties.getThreadNamePrefix());

        return scheduler;
    }

    /**
     * 创建任务作业服务实例
     * @param WrenchTaskScheduler 任务调度器实例
     * @param taskDataProviders 任务数据提供者列表（Spring会自动注入所有ITaskDataProvider实现）
     * @return 配置好的任务作业服务实例
     */
    @Bean
    public ITaskJobService taskJobService(TaskScheduler WrenchTaskScheduler, List<ITaskDataProvider> taskDataProviders) {
        // 实例化任务服务并初始化调度
        TaskJobService taskJobService = new TaskJobService(WrenchTaskScheduler, taskDataProviders);
        // 初始化所有任务
        taskJobService.initializeTasks();

        return taskJobService;
    }

    /**
     * 创建任务作业实例，负责自动检测和执行任务
     * @param properties 任务调度配置属性
     * @param taskJobService 任务作业服务
     * @return 配置好的TaskJob实例
     */
    @Bean
    public TaskJob taskJob(TaskJobAutoProperties properties, ITaskJobService taskJobService) {
        // 记录初始化日志
        log.info("wrench，任务调度作业初始化完成。刷新间隔: {}ms, 清理cron: {}",
                properties.getRefreshInterval(), properties.getCleanInvalidTasksCron());
        // 创建并返回TaskJob实例
        return new TaskJob(properties, taskJobService);
    }
}