package com.yang.wrench.test.task;

import com.yang.wrench.starter.stask.job.model.TaskScheduleVO;
import com.yang.wrench.starter.stask.job.provider.ITaskDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * 测试任务数据提供者
 * @author yang
 */
@Service
public class TestTaskDataProvider01 implements ITaskDataProvider {

    private static final Logger log = LoggerFactory.getLogger(TestTaskDataProvider01.class);

    @Override
    public List<TaskScheduleVO> queryAllValidTaskSchedule() {
        List<TaskScheduleVO> tasks = new ArrayList<>();
        
        // 任务1：使用函数式编程方式
        TaskScheduleVO task1 = new TaskScheduleVO();
        task1.setId(1L);
        task1.setDescription("测试任务1 - 数据处理");
        task1.setCornExpression("0/10 * * * * ?"); // 每10秒执行一次
        task1.setTaskParam("{\"type\":\"data_process\",\"batch_size\":100}");
        
        // 使用BiConsumer方式设置任务逻辑
        BiConsumer<Long, String> task1Logic = (taskId, taskParam) -> {
            log.info("执行测试任务1 - 任务ID: {}, 任务参数: {}", taskId, taskParam);
            try {
                Thread.sleep(500); // 模拟任务执行时间
                log.info("测试任务1执行完成");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("测试任务1执行被中断", e);
            }
        };
        task1.setTask(task1Logic);

        tasks.add(task1);
        
        return tasks;
    }

    @Override
    public List<Long> queryAllInvalidTaskScheduleIds() {
        // 返回一些无效的任务ID用于测试
        return Arrays.asList(999L, 1000L);
    }

}