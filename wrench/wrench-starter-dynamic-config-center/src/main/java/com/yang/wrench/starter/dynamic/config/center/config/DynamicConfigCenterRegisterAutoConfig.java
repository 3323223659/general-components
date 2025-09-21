package com.yang.wrench.starter.dynamic.config.center.config;

import com.yang.wrench.starter.dynamic.config.center.domain.model.valobj.AttributeVO;
import com.yang.wrench.starter.dynamic.config.center.domain.service.DynamicConfigCenterService;
import com.yang.wrench.starter.dynamic.config.center.domain.service.IDynamicConfigCenterService;
import com.yang.wrench.starter.dynamic.config.center.listener.DynamicConfigCenterAdjustListener;
import com.yang.wrench.starter.dynamic.config.center.types.common.Constant;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 动态配置中心注册自动配置类
 *
 * 功能说明：
 * 1. 动态配置中心的Spring Boot自动配置入口
 * 2. 初始化Redisson客户端连接Redis，作为配置中心的存储和消息总线
 * 3. 注册配置监听器，实现配置变更的实时通知和动态更新
 * 4. 创建配置服务Bean，提供配置的读写和管理能力
 *
 * 核心组件：
 * - RedissonClient: Redis客户端，用于配置存储和发布订阅
 * - IDynamicConfigCenterService: 配置中心核心服务接口
 * - DynamicConfigCenterAdjustListener: 配置变更监听器
 * - RTopic: Redis消息主题，用于配置变更通知
 *
 * @Author: yang
 * @Date: 2025/09/20/15:01
 * @Description: 动态配置中心自动配置类，负责核心组件的初始化和注册
 */
@Configuration  // 标识为Spring配置类
@EnableConfigurationProperties({DynamicConfigCenterAutoProperties.class, DynamicConfigCenterRegisterAutoConfigProperties.class})  // 启用配置属性绑定
public class DynamicConfigCenterRegisterAutoConfig {

    private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterRegisterAutoConfig.class);

    /**
     * 创建Redisson客户端Bean
     * 用于连接Redis服务器，作为配置中心的存储和消息总线
     *
     * @param properties Redis连接配置属性
     * @return RedissonClient实例
     */
    @Bean("WrenchRedissonClient")
    public RedissonClient redissonClient(DynamicConfigCenterRegisterAutoConfigProperties properties) {
        Config config = new Config();
        // 使用Jackson JSON编解码器，支持复杂对象的序列化
        config.setCodec(JsonJacksonCodec.INSTANCE);

        // 配置单节点Redis服务器连接
        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())  // Redis服务器地址
                .setPassword(properties.getPassword())  // Redis密码
                .setConnectionPoolSize(properties.getPoolSize())  // 连接池大小
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())  // 最小空闲连接数
                .setIdleConnectionTimeout(properties.getIdleTimeout())  // 空闲连接超时时间
                .setConnectTimeout(properties.getConnectTimeout())  // 连接超时时间
                .setRetryAttempts(properties.getRetryAttempts())  // 重试次数
                .setRetryInterval(properties.getRetryInterval())  // 重试间隔
                .setPingConnectionInterval(properties.getPingInterval())  // 心跳检测间隔
                .setKeepAlive(properties.isKeepAlive())  // 是否保持长连接
        ;

        RedissonClient redissonClient = Redisson.create(config);

        log.info("wrench，注册器（redis）链接初始化完成。host: {}, poolSize: {}, isShutdown: {}",
                properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());

        return redissonClient;
    }

    /**
     * 创建动态配置中心服务Bean
     * 提供配置的读取、写入、更新等核心功能
     *
     * @param dynamicConfigCenterAutoProperties 配置属性
     * @param WrenchRedissonClient Redisson客户端
     * @return 配置中心服务实例
     */
    @Bean
    public IDynamicConfigCenterService dynamicConfigCenterService(
            DynamicConfigCenterAutoProperties dynamicConfigCenterAutoProperties,
            RedissonClient WrenchRedissonClient) {
        return new DynamicConfigCenterService(dynamicConfigCenterAutoProperties, WrenchRedissonClient);
    }

    /**
     * 创建配置变更监听器Bean
     * 负责处理配置变更消息并执行相应的更新操作
     *
     * @param dynamicConfigCenterService 配置中心服务
     * @return 配置变更监听器实例
     */
    @Bean
    public DynamicConfigCenterAdjustListener dynamicConfigCenterAdjustListener(
            IDynamicConfigCenterService dynamicConfigCenterService) {
        return new DynamicConfigCenterAdjustListener(dynamicConfigCenterService);
    }

    /**
     * 创建Redis消息主题Bean并注册监听器
     * 用于发布和订阅配置变更消息
     *
     * @param dynamicConfigCenterAutoProperties 配置属性
     * @param redissonClient Redisson客户端
     * @param dynamicConfigCenterAdjustListener 配置变更监听器
     * @return Redis消息主题实例
     */
    @Bean(name = "dynamicConfigCenterRedisTopic")
    public RTopic threadPoolConfigAdjustListener(
            DynamicConfigCenterAutoProperties dynamicConfigCenterAutoProperties,
            RedissonClient redissonClient,
            DynamicConfigCenterAdjustListener dynamicConfigCenterAdjustListener) {
        // 获取系统对应的消息主题，格式为：system:config:topic
        RTopic topic = redissonClient.getTopic(Constant.getTopic(dynamicConfigCenterAutoProperties.getSystem()));
        // 注册消息监听器，监听AttributeVO类型的消息
        topic.addListener(AttributeVO.class, dynamicConfigCenterAdjustListener);
        return topic;
    }
}