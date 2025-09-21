package com.yang.wrench.starter.dynamic.config.center.config;

import com.yang.wrench.starter.dynamic.config.center.domain.service.IDynamicConfigCenterService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

/**
 * 动态配置中心自动配置类
 *
 * 功能说明：
 * 1. 作为Spring Boot Starter的自动配置类，实现动态配置中心的自动装配
 * 2. 通过BeanPostProcessor接口对所有Bean进行后处理，实现配置的动态代理
 * 3. 提供运行时配置热更新能力，支持配置变更时的动态生效
 *
 * 工作原理：
 * - 在Spring容器初始化每个Bean后，通过IDynamicConfigCenterService对Bean进行代理
 * - 代理后的Bean能够监听配置中心的变更并实时更新自身配置
 *
 * @Author: yang
 * @Date: 2025/09/20/15:01
 * @Description: 动态配置中心自动配置类，负责配置的动态代理和热更新
 */
@Configuration
public class DynamicConfigCenterAutoConfig implements BeanPostProcessor {

    // 动态配置中心服务接口，提供配置代理能力
    private final IDynamicConfigCenterService dynamicConfigCenterService;

    /**
     * 构造函数，通过Spring依赖注入配置中心服务
     * @param dynamicConfigCenterService 动态配置中心服务实现
     */
    public DynamicConfigCenterAutoConfig(IDynamicConfigCenterService dynamicConfigCenterService) {
        this.dynamicConfigCenterService = dynamicConfigCenterService;
    }

    /**
     * Bean初始化后处理方法
     * 对容器中的所有Bean进行代理，使其具备动态配置能力
     *
     * @param bean 已经初始化的Bean实例
     * @param beanName Bean的名称
     * @return 代理后的Bean实例（如果该Bean需要动态配置）或原始Bean实例
     * @throws BeansException 如果代理过程中发生异常
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 使用动态配置中心服务对Bean进行代理
        // 代理后的Bean能够响应配置变更并自动更新
        return dynamicConfigCenterService.proxyObject(bean);
    }
}