package com.yang.wrench.starter.stask.job.provider;

import com.yang.wrench.starter.stask.job.model.TaskScheduleVO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: yang
 * @Date: 2025/09/22/20:36
 * @Description: 任务数据提供接口
 */

public interface ITaskDataProvider {

    // 查询所有有效的任务
    List<TaskScheduleVO> queryAllValidTaskSchedule();

    // 查询所有无效的任务
    List<Long> queryAllInvalidTaskScheduleIds();
}
