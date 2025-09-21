package com.yang.wrench.starter.dynamic.config.center.domain.service;

import com.yang.wrench.starter.dynamic.config.center.config.DynamicConfigCenterAutoProperties;
import com.yang.wrench.starter.dynamic.config.center.domain.model.valobj.AttributeVO;
import com.yang.wrench.starter.dynamic.config.center.types.annotations.DCCValue;
import com.yang.wrench.starter.dynamic.config.center.types.common.Constant;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: yang
 * @Date: 2025/09/20/15:23
 * @Description: 动态配置中心服务实现类
 */

public class DynamicConfigCenterService implements IDynamicConfigCenterService{

    private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterService.class);

    private final DynamicConfigCenterAutoProperties properties;
    private final RedissonClient redissonClient;
    private final Map<String, Object> dccBeamGroup = new ConcurrentHashMap<>();

    public DynamicConfigCenterService(DynamicConfigCenterAutoProperties dynamicConfigCenterAutoProperties, RedissonClient redissonClient) {
        this.properties = dynamicConfigCenterAutoProperties;
        this.redissonClient = redissonClient;
    }

    /**
     * 代理对象方法 - 处理带有@DCCValue注解的字段
     * 1. 支持AOP代理对象的处理
     * 2. 从配置中心读取配置值并注入字段
     * 3. 缓存Bean对象用于后续配置更新
     */
    @Override
    public Object proxyObject(Object bean) {
        Class<?> targetBeanClass = bean.getClass();
        Object targetBeanObject = bean;
        // 处理AOP代理对象，获取目标类
        if (AopUtils.isAopProxy(bean)){
            targetBeanClass = AopUtils.getTargetClass(bean);
            targetBeanObject = AopProxyUtils.getSingletonTarget(bean);
        }

        // 扫描所有字段，查找@DCCValue注解
        Field[] fields = targetBeanClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(DCCValue.class)) {
                continue;
            }
            DCCValue dccValue = field.getAnnotation(DCCValue.class);
            String value = dccValue.value();
            if (StringUtils.isBlank(value)) {
                throw new RuntimeException("DCCValue注解的value属性不能为空");
            }
            // 解析注解值格式：attribute:defaultValue
            // @DCCValue("user:10") → 分割为["user", "10"]
            String[] split = value.split(Constant.SYMBOL_COLON);
            // 生成完整的配置键：system:attribute
            String key = properties.getKey(split[0].trim());

            // 获取默认值
            String defaultValue = split.length == 2 ? split[1] : null;
            String setValue = defaultValue;

            try{
                if (StringUtils.isBlank(defaultValue)){
                    throw new RuntimeException("DCCValue注解的value属性不能为空");
                }

                // 从Redis配置中心读取配置
                RBucket<String> bucket = redissonClient.getBucket(key);
                if (!bucket.isExists()) {
                    // 配置不存在，使用默认值并保存到配置中心
                    bucket.set(defaultValue);
                } else {
                    // 配置存在，使用配置中心的值
                    setValue = bucket.get();
                }
                // 反射设置字段值
                field.setAccessible(true);
                field.set(targetBeanObject, setValue);
                field.setAccessible(false);
            } catch (Exception e) {
                throw new RuntimeException("获取属性值失败" + e);
            }

            // 缓存Bean对象，key为配置键，value为Bean实例
            dccBeamGroup.put(key, targetBeanObject);
        }

        return bean;
    }

    /**
     * 调整属性值方法 - 响应配置变更
     * 1. 更新配置中心的值
     * 2. 更新所有相关Bean的字段值
     */
    @Override
    public void adjustAttributeValue(AttributeVO attributeVO) {
        String key = properties.getKey(attributeVO.getAttribute());
        String value = attributeVO.getValue();
        // 更新配置中心的值
        RBucket<String> bucket = redissonClient.getBucket(key);
        if (!bucket.isExists()){
            return;
        }
        bucket.set(value);

        // 获取使用该配置的Bean对象
        Object objectBean = dccBeamGroup.get(key);
        if (objectBean == null){
            return;
        }
        Class<?> objectBeanClass = objectBean.getClass();

        // 处理AOP代理对象
        if (AopUtils.isAopProxy(objectBean)){
            objectBeanClass = AopUtils.getTargetClass(objectBean);
        }

        try {
            // 通过反射更新字段值
            Field field = objectBeanClass.getDeclaredField(attributeVO.getAttribute());
            field.setAccessible(true);
            field.set(objectBean, value);
            field.setAccessible(false);
        } catch(Exception e){
            throw new RuntimeException("刷新属性值失败" + e);
        }
    }
}