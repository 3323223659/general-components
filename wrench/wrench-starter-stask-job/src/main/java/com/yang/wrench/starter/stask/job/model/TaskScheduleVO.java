package com.yang.wrench.starter.stask.job.model;

import lombok.Data;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * 任务调度描述类（增强版）
 * 用于封装定时任务的基本信息、调度规则和执行逻辑，支持多种任务设置方式
 *
 * Created with IntelliJ IDEA.
 * @Author: yang
 * @Date: 2025/09/22/20:04
 * @Description: 任务调度描述类
 */
@Data
public class TaskScheduleVO {

    /** 任务唯一标识 */
    private Long id;

    /** 任务描述信息 */
    private String description;

    /** Cron表达式：定义任务执行的时间规则 */
    private String cornExpression;

    /** 任务参数：以字符串格式存储的任务执行参数（通常是JSON格式） */
    private String taskParam;

    /**
     * 任务执行器：使用Supplier封装Runnable任务
     * Supplier提供延迟执行能力，Runnable定义具体的执行逻辑
     */
    private Supplier<Runnable> taskExecutor;

    /**
     * 设置简单任务（方法重载1）
     * 适用于不需要参数的无状态任务
     * @param task 无参数的Runnable任务
     */
    public void setTask(Runnable task) {
        this.taskExecutor = () -> task;
    }

    /**
     * 设置带参数的任务（方法重载2）
     * 适用于需要任务ID和参数的有状态任务
     * @param task 接收任务ID和参数的BiConsumer任务
     */
    public void setTask(BiConsumer<Long, String> task) {
        this.taskExecutor = () -> () -> task.accept(id, taskParam);
    }

    /**
     * 重写toString方法，排除taskExecutor字段（避免日志输出过于复杂）
     * @return 格式化后的字符串表示
     */
    @Override
    public String toString() {
        return "TaskScheduleVO{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", cornExpression='" + cornExpression + '\'' +
                ", taskParam='" + taskParam + '\'' +
                '}';
    }
}