package com.yang.wrench.rate.limiter.config;

import com.yang.wrench.rate.limiter.aop.RateLimiterAOP;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 限流配置
 *
 * @author yang
 */
@Configuration
public class RateLimiterAutoConfig {

    @Bean
    public RateLimiterAOP rateLimiterAOP() {
        return new RateLimiterAOP();
    }

}
