package com.yang.wrench.starter.stask.job.service;

import com.yang.wrench.starter.stask.job.model.TaskScheduleVO;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: yang
 * @Date: 2025/09/22/20:12
 * @Description: 任务调度服务接口
 */

public interface ITaskJobService {

    // 添加任务
    boolean addTask(TaskScheduleVO taskScheduleVO);

    // 删除任务
    boolean removeTask(Long taskId);

    // 刷新任务
    void refreshTask();

    // 清理无效任务
    void cleanInvalidTask();

    // 停止所有任务
    void stopAllTask();

    // 获取当前正在执行的任务数量
    int getActiveTaskCount();

    // 初始化任务
    void initializeTasks();
}
