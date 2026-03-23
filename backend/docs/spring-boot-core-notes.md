# Spring Boot 核心原理学习笔记

## 1. @SpringBootApplication 揭秘

### 1.1 本质是 3 个注解的组合

```java
@SpringBootApplication
  ├─ @SpringBootConfiguration    // 标注这是一个配置类（本质是@Configuration）
  ├─ @EnableAutoConfiguration    // 核心！启用自动配置
  └─ @ComponentScan              // 扫描当前包及子包的 Bean
```

### 1.2 核心：@EnableAutoConfiguration

**工作原理：**

1. Spring Boot 启动时读取 `META-INF/spring.factories` 文件
2. 文件中列出了所有自动配置类
3. 根据 classpath 中的依赖，决定启用哪些配置
4. 自动创建并注册 Bean 到 Spring 容器

**示例：**

```
pom.xml 中有 mysql-connector-j 依赖
    ↓
spring.factories 中有 DataSourceAutoConfiguration
    ↓
@ConditionalOnClass 检测到 DataSource 类存在
    ↓
自动配置生效，创建 DataSource Bean
    ↓
你可以直接 @Autowired 使用！
```

---

## 2. 自动配置的条件注解

### 2.1 @ConditionalOnClass

```java
@Configuration
@ConditionalOnClass({DataSource.class, Driver.class})  // 当 classpath 有这些类时生效
public class DataSourceAutoConfiguration {
    // ...
}
```

### 2.2 @ConditionalOnMissingBean

```java
@Bean
@ConditionalOnMissingBean  // 当容器中没有这个 Bean 时才创建
public DataSource dataSource() {
    // ...
}
```

### 2.3 @ConditionalOnProperty

```java
@Configuration
@ConditionalOnProperty(prefix = "spring.datasource", name = "url")  // 当配置了这个属性时生效
public class DataSourceAutoConfiguration {
    // ...
}
```

---

## 3. 你的项目自动配置了什么？

| 依赖 | 自动配置类 | 生效条件 |
|------|-----------|---------|
| spring-boot-starter-web | WebMvcAutoConfiguration | 有 DispatcherServlet |
| mybatis-plus-boot-starter | MybatisPlusAutoConfiguration | 有 SqlSessionFactory |
| mysql-connector-j | DataSourceAutoConfiguration | 有 DataSource 类 |
| spring-boot-starter-security | SecurityAutoConfiguration | 有 AuthenticationManager |
| spring-boot-starter-data-redis | RedisAutoConfiguration | 有 RedisConnectionFactory |

---

## 4. Spring Boot 启动流程

```java
SpringApplication.run(TakeoutApplication.class, args);
```

**4 个步骤：**

1. **创建 SpringApplication 对象**
   - 推断应用类型（Servlet/Reactive）
   - 加载 ApplicationContextInitializer
   - 加载 ApplicationListener

2. **准备 Environment**
   - 加载 application.yml/properties
   - 加载系统环境变量
   - 加载命令行参数

3. **创建 ApplicationContext**
   - 创建 AnnotationConfigServletWebServerApplicationContext
   - 设置 Parent 容器

4. **刷新容器（最关键）**
   - 扫描 @Component、@Bean
   - 执行自动配置
   - 调用 CommandLineRunner
   - 启动内嵌 Tomcat

---

## 5. 实战技巧

### 5.1 查看自动配置报告

**application.yml:**
```yaml
debug: true  # 开启 debug 模式
```

重启后查看控制台的 `CONDITIONS EVALUATION REPORT`

### 5.2 排除自动配置

```java
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,  // 排除数据源自动配置
    SecurityAutoConfiguration.class     // 排除安全自动配置
})
```

### 5.3 自定义 Starter

**结构：**
```
my-custom-starter/
├── pom.xml
└── src/main/java/
    └── com/example/
        ├── CustomProperties.java      // 配置属性
        ├── CustomService.java         // 业务类
        └── CustomAutoConfiguration.java  // 自动配置类
```

**CustomAutoConfiguration.java:**
```java
@Configuration
@ConditionalOnClass(CustomService.class)
@EnableConfigurationProperties(CustomProperties.class)
public class CustomAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public CustomService customService(CustomProperties properties) {
        return new CustomService(properties);
    }
}
```

**META-INF/spring.factories:**
```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.example.CustomAutoConfiguration
```

---

## 6. 常见问题

### Q1: 为什么我的配置没生效？
- 检查 application.yml 缩进是否正确
- 检查是否有拼写错误
- 查看自动配置报告（debug=true）

### Q2: 如何覆盖自动配置？
- 自己定义一个同类型的 Bean
- 使用 @ConditionalOnMissingBean 的配置会被覆盖

### Q3: 启动太慢怎么办？
- 排除不需要的自动配置
- 使用 @EnableAutoConfiguration(exclude=...)
- 检查是否有慢的 CommandLineRunner

---

## 7. 关键结论

1. **Spring Boot = Spring + 约定优于配置**
2. **自动配置的核心是 spring.factories**
3. **条件注解决定配置是否生效**
4. **你可以随时覆盖自动配置**
5. **debug=true 是调试神器**

---

## 8. 下一步

- [ ] 理解 @SpringBootApplication 的 3 个组成部分
- [ ] 学会查看自动配置报告
- [ ] 理解条件注解的作用
- [ ] 了解自定义 Starter 的流程

---

**记住：Spring Boot 不是魔法，是大量的条件配置 + 约定！**
