package com.yang.wrench.rate.limiter.types.annotations;

import java.lang.annotation.*;

/**
 * 接口访问限流拦截器注解
 * 用于标记需要进行访问频率限制的方法，支持自定义限流规则和降级处理
 */
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时保留，可通过反射获取
@Target({ElementType.METHOD})       // 注解仅可用于方法级别
@Documented                          // 包含在JavaDoc中
public @interface RateLimiterAccessInterceptor {

    /**
     * 限流键：用于区分不同限流维度的字段名
     * 可用方法参数中的字段名，未配置则默认对所有请求统一限流("all")
     * 例如：key = "userId" 表示按用户ID进行限流
     */
    String key() default "all";

    /**
     * 许可速率：每秒允许的请求次数
     * 例如：permitsPerSecond = 10.0 表示每秒最多允许10次请求
     */
    double permitsPerSecond();

    /**
     * 黑名单阈值：触发多少次限流后将该key加入黑名单
     * 设置为0表示不启用黑名单功能
     * 例如：blacklistCount = 5.0 表示同一key被限流5次后加入黑名单
     */
    double blacklistCount() default 0;

    /**
     * 降级方法：当请求被限流时执行的回退方法名
     * 该方法必须与注解方法在同一类中，且具有相同的方法签名
     * 例如：fallbackMethod = "rateLimitFallback"
     */
    String fallbackMethod();

}