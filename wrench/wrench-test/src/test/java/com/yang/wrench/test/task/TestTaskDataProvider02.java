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
public class TestTaskDataProvider02 implements ITaskDataProvider {

    private static final Logger log = LoggerFactory.getLogger(TestTaskDataProvider02.class);

    @Override
    public List<TaskScheduleVO> queryAllValidTaskSchedule() {
        List<TaskScheduleVO> tasks = new ArrayList<>();

        // 另一个函数式编程示例
        TaskScheduleVO task2 = new TaskScheduleVO();
        task2.setId(2L);
        task2.setDescription("测试任务2 - 报表生成");
        task2.setCornExpression("0/5 * * * * ?"); // 每5秒钟执行一次
        task2.setTaskParam("{\"report_type\":\"daily\",\"format\":\"pdf\"}");
        
        // 使用BiConsumer方式设置任务逻辑
        BiConsumer<Long, String> task2Logic = (taskId, taskParam) -> {
            log.info("执行测试任务2 - 任务ID: {}, 任务参数: {}", taskId, taskParam);
            try {
                Thread.sleep(1000); // 模拟耗时操作
                log.info("测试任务2执行完成");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("测试任务2执行被中断", e);
            }
        };
        task2.setTask(task2Logic);
        tasks.add(task2);

        return tasks;
    }

    @Override
    public List<Long> queryAllInvalidTaskScheduleIds() {
        // 返回一些无效的任务ID用于测试
        return Arrays.asList(999L, 1000L);
    }

}