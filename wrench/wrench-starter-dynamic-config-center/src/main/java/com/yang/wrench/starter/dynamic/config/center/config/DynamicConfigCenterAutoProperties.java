package com.yang.wrench.starter.dynamic.config.center.config;

import com.yang.wrench.starter.dynamic.config.center.types.common.Constant;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 动态配置中心属性配置类
 *
 * 功能说明：
 * 1. 用于读取和应用配置文件中以"wrench.config"为前缀的配置属性
 * 2. 提供配置键的生成方法，用于在配置中心中存储和检索配置
 * 3. 作为配置属性的承载对象，与Spring Boot的配置绑定机制集成
 *
 * 配置示例：
 * wrench:
 *   config:
 *     system: user-service  # 系统标识，用于配置键的前缀
 *
 * @Author: yang
 * @Date: 2025/09/20/15:01
 * @Description: 动态配置中心属性配置类，负责配置属性的加载和键的生成
 */
@ConfigurationProperties(prefix = "wrench.config", ignoreInvalidFields = true)  // 读取wrench.config前缀的配置，忽略无效字段
public class DynamicConfigCenterAutoProperties {

    /**
     * 系统标识
     * 用途：作为配置键的前缀，用于区分不同系统或服务的配置
     * 示例：如果system="user-service"，则生成的配置键为"user-service:attributeName"
     * 配置方式：在application.yml中配置 wrench.config.system=your-system-name
     */
    private String system;

    /**
     * 生成完整的配置键
     * 格式：system:attributeName
     *
     * @param attributeName 属性名称
     * @return 完整的配置键，用于在配置中心存储和检索
     *
     * 示例：
     * - getKey("timeout") → "user-service:timeout"
     * - getKey("database.url") → "user-service:database.url"
     */
    public String getKey(String attributeName) {
        return this.system + Constant.SYMBOL_COLON + attributeName;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
}