package com.yang.wrench.starter.dynamic.config.center.domain.model.valobj;

/**
 * 属性值调整值对象（Value Object）
 *
 * 功能说明：
 * 1. 用于在配置中心中传递配置属性的键值对信息
 * 2. 作为配置变更消息的载体，通过Redis消息总线进行发布和订阅
 * 3. 实现配置属性的序列化和反序列化，支持跨进程传输
 *
 * 示例：
 * - 属性：database.timeout，值：5000
 * - 属性：redis.host，值：127.0.0.1
 * - 属性：feature.flag.enabled，值：true
 *
 * @author yang
 * @Description: 配置属性值对象，用于配置变更消息的传递
 */
public class AttributeVO {

    /**
     * 属性键（Key） - 配置项的唯一标识
     * 格式通常为：系统标识 + ":" + 属性名（如：user-service:database.timeout）
     * 用于在配置中心中唯一标识一个配置项
     */
    private String attribute;

    /**
     * 属性值（Value） - 配置项的具体值
     * 支持各种类型的数据，最终以字符串形式存储和传输
     * 接收方需要根据业务需求进行类型转换
     */
    private String value;

    /**
     * 默认构造函数
     * 用于序列化框架的反序列化操作
     */
    public AttributeVO() {
    }

    /**
     * 带参数的构造函数
     * 用于快速创建属性值对象
     *
     * @param attribute 属性键
     * @param value 属性值
     */
    public AttributeVO(String attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}