# Wrench 项目总览

Wrench 是一个基于 Spring Boot 2.7 与 Java 17 的多模块组件化工程，聚焦“通用中间件与工程实践”的落地示例。项目内包含可独立引入的 Starter（动态配置中心、设计模式框架、限流器、任务调度）以及一个示例应用用于演示集成与使用方式。

## 模块结构

- `wrench-bom`：BOM（Bill of Materials）版本对齐模块
  - 统一管理各 Starter 的版本，业务侧可通过引入 BOM 实现无版本号依赖声明与一致的版本控制。
  - 位置：`wrench/wrench-bom/pom.xml`
- `wrench-starter-dynamic-config-center`：动态配置中心（DCC）Starter
  - 提供注解 `@DCCValue` 等能力，支持配置的动态注入与热更新。
  - 典型场景：开关类配置、超时、并发数、阈值等需要在线调整的业务参数。
- `wrench-starter-design-framework`：设计模式框架 Starter
  - 提供策略路由、链路编排等常见业务架构模式的抽象实现，便于沉淀复用。
  - 如 `AbstractStrategyRouter` 实现按入参动态选择策略并执行的通用能力。
- `wrench-starter-rate-limiter`：接口限流 Starter
  - 通过注解 `@RateLimiterAccessInterceptor` 定义限流维度、速率、黑名单阈值与降级方法。
  - 基于 Google Guava 等实现令牌桶速率限制，支持方法级 AOP 拦截。
- `wrench-starter-stask-job`：任务调度 Starter
  - 基于 Spring `@Scheduled` 提供任务刷新与清理等定时作业能力，可通过配置开关启停。
  - 如 `TaskJob` 提供 `refreshTasks` 与 `cleanInvalidTasks` 两个定时任务。
- `wrench-test`：示例应用与测试
  - 演示 Web 接口、限流注解使用、线程池与任务调度配置等。

## 关键能力与核心类

- **动态配置中心（DCC）**

  - 注解：`com.yang.wrench.starter.dynamic.config.center.types.annotations.DCCValue`
    - 声明在字段上，支持从配置中心动态获取与热更新。
    - 例：`@DCCValue("feature.flag.enabled") private boolean enabled;`

- **设计模式框架**

  - 策略路由：`com.yang.wrench.starter.design.framework.tree.AbstractStrategyRouter`
    - 方法 `router(T request, D ctx)` 将入参映射为 `StrategyHandler` 并执行，若未命中使用默认处理器。

- **限流**

  - 注解：`com.yang.wrench.rate.limiter.types.annotations.RateLimiterAccessInterceptor`
    - 关键参数：
      - `key`：限流维度键，支持基于方法参数（如 userId）；默认 `all`。
      - `permitsPerSecond`：每秒许可数（必填）。
      - `blacklistCount`：被限流达到阈值后拉黑（0 表示关闭）。
      - `fallbackMethod`：同类内降级方法名，需与原方法签名一致。

- **任务调度**
  - 作业类：`com.yang.wrench.starter.stask.job.TaskJob`
    - `@Scheduled(fixedRateString = "${wrench.task.job.refresh-interval:60000}")` 周期刷新任务配置。
    - `@Scheduled(cron = "${wrench.task.job.clean-invalid-tasks-cron:0 0/10 * * * ?}")` 定期清理无效任务。

## 快速开始

### 环境准备

- JDK 17+
- Maven 3.8+
- 可选：本地 Redis（若在 DCC 或其他组件中启用注册/缓存等扩展功能）

### 构建

```bash
mvn -f wrench/pom.xml clean install -DskipTests
```

### 运行示例应用

`wrench-test` 为演示应用，默认使用 `application.yml` 激活 `dev` 配置，可参考模板 `application-template.yml` 设置端口与组件参数。

1. 复制模板并按需调整：

```bash
copy wrench\wrench-test\src\main\resources\application-template.yml wrench\wrench-test\src\main\resources\application.yml
```

将 `server.port`、`wrench.task.job.*`、`thread.pool.executor.config.*` 等项按需修改（模板默认端口 9191）。

2. 运行：

```bash
mvn -f wrench/pom.xml -pl wrench-test -am spring-boot:run
```

3. 访问示例接口（限流演示）：

```bash
curl --request GET "http://127.0.0.1:9191/api/v1/index/draw?userId=yang"
```

当触发限流时，返回由 `fallbackMethod` 定义的降级结果（示例：`rateLimiter`）。

示例控制器：`wrench-test/src/main/java/com/yang/wrench/trigger/IndexController.java`

## 使用 BOM 进行版本对齐（推荐）

在业务项目中引入 `wrench-bom` 后，可省略各 Starter 的版本号，由 BOM 统一约束：

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.yang.wrench</groupId>
      <artifactId>wrench-bom</artifactId>
      <version>1.0.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

随后以“无版本号”的方式引入需要的 Starter：

```xml
<dependencies>
  <dependency>
    <groupId>com.yang.wrench</groupId>
    <artifactId>wrench-starter-rate-limiter</artifactId>
  </dependency>
  <dependency>
    <groupId>com.yang.wrench</groupId>
    <artifactId>wrench-starter-dynamic-config-center</artifactId>
  </dependency>
  <dependency>
    <groupId>com.yang.wrench</groupId>
    <artifactId>wrench-starter-design-framework</artifactId>
  </dependency>
  <dependency>
    <groupId>com.yang.wrench</groupId>
    <artifactId>wrench-starter-stask-job</artifactId>
  </dependency>
</dependencies>
```

> 若不使用 BOM，也可为每个 Starter 显式声明版本号（不推荐，维护成本更高）。

## 配置说明（示例）

在模板 `wrench-test/src/main/resources/application-template.yml` 中可配置：

- **服务端口**：

```yaml
server:
  port: 9191
```

- **线程池**：

```yaml
thread:
  pool:
    executor:
      config:
        core-pool-size: 20
        max-pool-size: 50
        keep-alive-time: 5000
        block-queue-size: 5000
        policy: CallerRunsPolicy
```

- **通用组件与任务调度**：

```yaml
wrench:
  config:
    system: test-system
    register:
      host: 127.0.0.1
      port: 6379
  task:
    job:
      enabled: true
      pool-size: 5
      thread-name-prefix: "test-task-scheduler-"
      wait-for-tasks-to-complete-on-shutdown: true
      await-termination-seconds: 30
      refresh-interval: 30000
      clean-invalid-tasks-cron: "0 0/5 * * * ?"
```

- **日志**：

```yaml
logging:
  level:
    root: info
  config: classpath:logback-spring.xml
```

## 在业务项目中引入（不使用 BOM 的方式）

如下为不使用 BOM、显式写版本号的示例（不推荐）：

```xml
<dependencies>
  <dependency>
    <groupId>com.yang.wrench</groupId>
    <artifactId>wrench-starter-rate-limiter</artifactId>
    <version>1.0</version>
  </dependency>
  <dependency>
    <groupId>com.yang.wrench</groupId>
    <artifactId>wrench-starter-dynamic-config-center</artifactId>
    <version>1.0</version>
  </dependency>
</dependencies>
```

限流使用示例：

```java
@RateLimiterAccessInterceptor(key = "userId", permitsPerSecond = 1.0d, blacklistCount = 1, fallbackMethod = "rateLimitFallback")
public String draw(String userId) { return "ok"; }
public String rateLimitFallback(String userId) { return "rateLimiter"; }
```

## 开发与测试

- Java 代码风格：遵循清晰命名、早返回与异常处理的最佳实践。
- 测试：`wrench-test` 下提供了设计模式链路、树形路由与任务调度的示例测试用例，可参考 `src/test/java` 目录。

## 版本与兼容性

- Java：17
- Spring Boot：2.7.12
- 主要依赖：Guava 32.1.3-jre、Lombok 1.18.38、FastJSON 2.0.x 等。

## 目录导航

- 根 POM：`wrench/pom.xml`
- 模块 POM：`wrench/*/pom.xml`
- 示例应用：`wrench-test` 模块
- 关键类位置：
  - `@DCCValue`：`wrench-starter-dynamic-config-center/.../types/annotations/DCCValue.java`
  - `AbstractStrategyRouter`：`wrench-starter-design-framework/.../tree/AbstractStrategyRouter.java`
  - `@RateLimiterAccessInterceptor`：`wrench-starter-rate-limiter/.../types/annotations/RateLimiterAccessInterceptor.java`
  - `TaskJob`：`wrench-starter-stask-job/.../TaskJob.java`

---

如需进一步扩展（如注册中心、配置持久化、黑名单存储等），可在对应 Starter 内扩展接口与实现，或在业务侧以 `@Configuration` 进行装配。
