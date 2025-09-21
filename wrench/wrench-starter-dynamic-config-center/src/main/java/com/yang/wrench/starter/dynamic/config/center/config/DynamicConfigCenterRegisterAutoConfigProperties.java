package com.yang.wrench.starter.dynamic.config.center.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 动态配置中心Redis连接配置属性类
 *
 * 功能说明：
 * 1. 用于读取和应用配置文件中以"wrench.config.register"为前缀的Redis连接配置属性
 * 2. 提供Redis连接池、超时、重试等详细参数的配置支持
 * 3. 作为Redisson客户端配置的数据载体，支持灵活的Redis连接配置
 *
 * 配置示例：
 * wrench:
 *   config:
 *     register:
 *       host: 127.0.0.1           # Redis服务器地址
 *       port: 6379                # Redis服务器端口
 *       password: 1234            # Redis访问密码
 *       poolSize: 64              # 连接池大小
 *       minIdleSize: 10           # 最小空闲连接数
 *       idleTimeout: 10000        # 空闲连接超时时间(毫秒)
 *       connectTimeout: 10000     # 连接超时时间(毫秒)
 *       retryAttempts: 3          # 重试次数
 *       retryInterval: 1000       # 重试间隔时间(毫秒)
 *       pingInterval: 0           # 心跳检测间隔(毫秒)
 *       keepAlive: true           # 是否保持长连接
 */
@ConfigurationProperties(prefix = "wrench.config.register", ignoreInvalidFields = true)  // 读取wrench.config.register前缀的配置，忽略无效字段
public class DynamicConfigCenterRegisterAutoConfigProperties {

    /**
     * Redis服务器主机地址
     * 示例：127.0.0.1 或 redis.example.com
     */
    private String host;

    /**
     * Redis服务器端口号
     * 默认端口：6379
     */
    private int port;

    /**
     * Redis访问密码
     * 如果Redis未设置密码，可以留空或注释掉该配置
     */
    private String password;

    /**
     * 连接池大小 - 最大连接数
     * 默认值：64，根据应用并发量和服务器资源调整
     */
    private int poolSize = 64;

    /**
     * 连接池最小空闲连接数
     * 默认值：10，保持一定数量的空闲连接以提高响应速度
     */
    private int minIdleSize = 10;

    /**
     * 空闲连接超时时间（单位：毫秒）
     * 默认值：10000（10秒），超过该时间的空闲连接将被关闭
     */
    private int idleTimeout = 10000;

    /**
     * 连接超时时间（单位：毫秒）
     * 默认值：10000（10秒），建立连接时的最大等待时间
     */
    private int connectTimeout = 10000;

    /**
     * 连接重试次数
     * 默认值：3，连接失败时的重试次数
     */
    private int retryAttempts = 3;

    /**
     * 连接重试间隔时间（单位：毫秒）
     * 默认值：1000（1秒），每次重试之间的等待时间
     */
    private int retryInterval = 1000;

    /**
     * 心跳检测间隔（单位：毫秒）
     * 默认值：0，表示不进行定期检查；建议生产环境设置为30000（30秒）
     */
    private int pingInterval = 0;

    /**
     * 是否保持长连接
     * 默认值：true，启用TCP keepalive机制保持连接活跃
     */
    private boolean keepAlive = true;

    // Getter和Setter方法
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getMinIdleSize() {
        return minIdleSize;
    }

    public void setMinIdleSize(int minIdleSize) {
        this.minIdleSize = minIdleSize;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public void setRetryAttempts(int retryAttempts) {
        this.retryAttempts = retryAttempts;
    }

    public int getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
    }

    public int getPingInterval() {
        return pingInterval;
    }

    public void setPingInterval(int pingInterval) {
        this.pingInterval = pingInterval;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }
}