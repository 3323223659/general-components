package com.yang.wrench.starter.dynamic.config.center.types.common;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: yang
 * @Date: 2025/09/20/15:07
 * @Description: 常量类
 */

public class Constant {

    public static final String DYNAMIC_CONFIG_CENTER_REDIS_TOPIC = "DYNAMIC_CONFIG_CENTER_REDIS_TOPIC";
    public static final String SYMBOL_COLON = ":";
    public static final String LINE = "_";
    public static String getTopic(String application){
        return DYNAMIC_CONFIG_CENTER_REDIS_TOPIC + SYMBOL_COLON + application;
    }

}
