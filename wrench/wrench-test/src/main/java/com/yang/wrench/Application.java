package com.yang.wrench;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: yang
 * @Date: 2025/09/20/15:59
 * @Description: 测试模块启动类
 */

@Configurable
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
