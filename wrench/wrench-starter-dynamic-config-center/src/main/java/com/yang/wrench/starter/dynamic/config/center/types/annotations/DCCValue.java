package com.yang.wrench.starter.dynamic.config.center.types.annotations;

import java.lang.annotation.*;

/**
 * DCCValue 注解（Dynamic Config Center Value）
 *
 * 功能说明：
 * 1. 用于标记需要动态配置的字段，实现配置的自动注入和热更新
 * 2. 支持从配置中心动态获取配置值，并在配置变更时自动更新字段值
 * 3. 提供零侵入的配置管理方式，业务代码无需关心配置来源和更新逻辑
 *
 * 使用场景：
 * - 需要动态调整的配置参数（如超时时间、开关标志、连接数等）
 * - 需要支持热更新的业务配置
 * - 多环境统一管理的配置项
 *
 * 示例：
 * @DCCValue("user-service:database.timeout")
 * private int databaseTimeout;
 *
 * @DCCValue("feature.flag.enabled")
 * private boolean featureEnabled;
 *
 * @Author: yang
 * @Date: 2025/09/20/15:10
 * @Description: 动态配置中心值注解，用于标记需要动态配置的字段
 */
@Retention(RetentionPolicy.RUNTIME)  // 注解在运行时保留，可以通过反射获取
@Target({ElementType.FIELD})         // 注解只能应用于字段
@Documented                          // 注解包含在Javadoc中
public @interface DCCValue {

    /**
     * 配置键（Key）
     * 格式：通常在配置中心中的完整键名
     * 示例：system:attributeName（如：user-service:database.timeout）
     *
     * 如果值为空字符串，默认使用 字段名 作为配置键（需要配合命名策略）
     *
     * @return 配置中心中的键名
     */
    String value() default "";
}