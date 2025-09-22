package com.yang.wrench.rate.limiter.aop;

import com.yang.wrench.rate.limiter.types.annotations.RateLimiterAccessInterceptor;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import com.yang.wrench.starter.dynamic.config.center.types.annotations.DCCValue;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 限流切面
 * 基于AOP实现的方法级别访问频率控制，支持黑名单机制和降级处理
 * @author yang
 */
@Aspect // 声明这是一个切面类
public class RateLimiterAOP {

    // 日志记录器
    private final Logger log = LoggerFactory.getLogger(RateLimiterAOP.class);

    // 动态配置中心开关：从配置中心获取限流开关状态
    @DCCValue("rateLimiterSwitch:open")
    private String rateLimiterSwitch;

    // 个人限频记录缓存：使用Guava Cache，1秒过期
    // key: 限流标识(如用户ID), value: RateLimiter实例
    private final Cache<String, RateLimiter> loginRecord = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS) // 1秒后过期
            .build();

    // 黑名单缓存：24小时过期
    // key: 限流标识, value: 违规次数
    // 分布式场景下可替换为Redis实现
    private final Cache<String, Long> blacklist = CacheBuilder.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS) // 24小时后过期
            .build();

    /**
     * 切入点定义：拦截带有@RateLimiterAccessInterceptor注解的方法
     */
    @Pointcut("@annotation(com.yang.wrench.rate.limiter.types.annotations.RateLimiterAccessInterceptor)")
    public void aopPoint() {
    }

    /**
     * 环绕通知：执行限流逻辑
     * @param jp 连接点
     * @param rateLimiterAccessInterceptor 限流注解实例
     * @return 方法执行结果或降级结果
     * @throws Throwable 可能抛出的异常
     */
    @Around("aopPoint() && @annotation(rateLimiterAccessInterceptor)")
    public Object doRouter(ProceedingJoinPoint jp, RateLimiterAccessInterceptor rateLimiterAccessInterceptor) throws Throwable {
        // 0. 检查限流开关，如果关闭则直接放行
        if (StringUtils.isBlank(rateLimiterSwitch) || "close".equals(rateLimiterSwitch)) {
            return jp.proceed();
        }

        // 1. 获取限流key配置
        String key = rateLimiterAccessInterceptor.key();
        if (StringUtils.isBlank(key)) {
            throw new RuntimeException("annotation RateLimiter uId is null！");
        }

        // 2. 从方法参数中提取限流标识字段值
        String keyAttr = getAttrValue(key, jp.getArgs());
        log.info("aop attr {}", keyAttr);

        // 3. 黑名单检查：如果该标识在黑名单中且超过阈值，直接执行降级
        if (!"all".equals(keyAttr) &&
                rateLimiterAccessInterceptor.blacklistCount() != 0 &&
                null != blacklist.getIfPresent(keyAttr) &&
                blacklist.getIfPresent(keyAttr) > rateLimiterAccessInterceptor.blacklistCount()) {
            log.info("限流-黑名单拦截(24h)：{}", keyAttr);
            return fallbackMethodResult(jp, rateLimiterAccessInterceptor.fallbackMethod());
        }

        // 4. 获取或创建RateLimiter实例（Guava缓存1秒）
        RateLimiter rateLimiter = loginRecord.getIfPresent(keyAttr);
        if (null == rateLimiter) {
            rateLimiter = RateLimiter.create(rateLimiterAccessInterceptor.permitsPerSecond());
            loginRecord.put(keyAttr, rateLimiter);
        }

        // 5. 尝试获取许可，如果失败则进行限流处理
        if (!rateLimiter.tryAcquire()) {
            // 更新黑名单计数器
            if (rateLimiterAccessInterceptor.blacklistCount() != 0) {
                Long currentCount = blacklist.getIfPresent(keyAttr);
                if (null == currentCount) {
                    blacklist.put(keyAttr, 1L);
                } else {
                    blacklist.put(keyAttr, currentCount + 1L);
                }
            }
            log.info("限流-超频次拦截：{}", keyAttr);
            return fallbackMethodResult(jp, rateLimiterAccessInterceptor.fallbackMethod());
        }

        // 6. 获取许可成功，执行原方法
        return jp.proceed();
    }

    /**
     * 执行降级方法
     * @param jp 连接点
     * @param fallbackMethod 降级方法名
     * @return 降级方法的执行结果
     */
    private Object fallbackMethodResult(JoinPoint jp, String fallbackMethod) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        // 获取降级方法实例
        Method method = jp.getTarget().getClass().getMethod(fallbackMethod, methodSignature.getParameterTypes());
        // 调用降级方法
        return method.invoke(jp.getThis(), jp.getArgs());
    }

    /**
     * 从方法参数中提取指定属性的值
     * @param attr 属性名
     * @param args 方法参数数组
     * @return 属性值
     */
    public String getAttrValue(String attr, Object[] args) {
        // 如果第一个参数是String类型，直接返回
        if (args[0] instanceof String) {
            return args[0].toString();
        }

        String filedValue = null;
        // 遍历所有参数，尝试提取指定属性值
        for (Object arg : args) {
            try {
                if (StringUtils.isNotBlank(filedValue)) {
                    break;
                }
                // 使用反射获取属性值（解决Lombok生成的get方法命名问题）
                filedValue = String.valueOf(this.getValueByName(arg, attr));
            } catch (Exception e) {
                log.error("获取路由属性值失败 attr：{}", attr, e);
            }
        }
        return filedValue;
    }

    /**
     * 通过反射获取对象的属性值
     * @param item 目标对象
     * @param name 属性名
     * @return 属性值
     */
    private Object getValueByName(Object item, String name) {
        try {
            Field field = getFieldByName(item, name);
            if (field == null) {
                return null;
            }
            field.setAccessible(true); // 设置可访问私有字段
            Object o = field.get(item); // 获取字段值
            field.setAccessible(false); // 恢复访问权限
            return o;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 获取对象的字段（包括父类的字段）
     * @param item 目标对象
     * @param name 字段名
     * @return Field对象，如果找不到返回null
     */
    private Field getFieldByName(Object item, String name) {
        try {
            Field field;
            try {
                // 先尝试从当前类获取
                field = item.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                // 如果当前类没有，尝试从父类获取
                field = item.getClass().getSuperclass().getDeclaredField(name);
            }
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}