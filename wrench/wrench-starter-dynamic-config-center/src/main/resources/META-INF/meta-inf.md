SPI（Service Provider Interface）是一种服务发现机制，允许框架在运行时动态加载实现类。

在Java中，SPI通常通过在META-INF/services或META-INF/spring.factories等配置文件中声明接口实现类来实现。

Spring Boot 利用 SPI 机制实现自动装配（AutoConfiguration），只需在组件的 META-INF/spring.factories 文件中配置自动注册类，Spring Boot 启动时会自动加载并初始化这些类。

在本项目中， 通过 SPI 机制，将 DynamicConfigCenterRegisterAutoConfig 和 DynamicConfigCenterAutoConfig 注册到 Spring Boot 应用中，

实现动态配置中心组件的自动装配和初始化。这样，用户只需引入依赖，无需手动配置，即可使用动态配置中心的全部功能。