package com.yang.wrench.starter.dynamic.config.center.domain.service;

import com.yang.wrench.starter.dynamic.config.center.domain.model.valobj.AttributeVO;

/**
 * 动态配置中心服务接口
 *
 * 功能说明：
 * 1. 提供动态配置的核心服务能力，包括Bean代理和配置调整
 * 2. 作为配置中心与业务应用之间的桥梁，实现配置的动态管理
 * 3. 支持配置的实时更新和生效，无需重启应用
 *
 * 核心功能：
 * - Bean代理：对Spring Bean进行包装，使其具备动态配置能力
 * - 配置调整：接收配置变更通知并更新对应的Bean字段值
 *
 * @Author: yang
 * @Date: 2025/09/20/15:20
 * @Description: 动态配置中心服务接口，定义配置管理的核心操作
 */
public interface IDynamicConfigCenterService {

    /**
     * 代理对象方法
     * 对Spring Bean进行代理，使其具备动态配置能力
     *
     * @param bean 需要被代理的原始Bean对象
     * @return 代理后的Bean对象（具备动态配置能力）
     *
     * 工作原理：
     * 1. 扫描Bean中所有带有@DCCValue注解的字段
     * 2. 根据注解配置从配置中心获取初始值
     * 3. 创建代理对象，拦截字段访问和配置更新
     * 4. 注册配置变更监听器
     */
    Object proxyObject(Object bean);

    /**
     * 调整属性值方法
     * 处理配置变更消息，更新对应的Bean字段值
     *
     * @param attributeVO 属性值对象，包含配置键和新的配置值
     *
     * 示例：
     * attributeVO: {attribute: "user-service:timeout", value: "5000"}
     * → 更新所有@DCCValue("user-service:timeout")标注的字段值为5000
     */
    void adjustAttributeValue(AttributeVO attributeVO);

}