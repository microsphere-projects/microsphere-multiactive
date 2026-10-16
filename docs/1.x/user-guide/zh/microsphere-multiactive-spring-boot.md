English version: [English](../microsphere-multiactive-spring-boot.md)

# microsphere-multiactive-spring-boot

## 概述

`microsphere-multiactive-spring-boot` 会自动激活可用区机制。它包含三个类：一个自动配置
（`ZoneAutoConfiguration`）和两个组合式条件注解（`@ConditionalOnAvailabilityZoneAvailable`、
`@ConditionalOnAvailabilityZoneEnabled`）。它通过两套 Spring Boot 自动配置注册机制都被登记，并额外把
`DefaultZoneLocator` 作为 `ZoneLocator` 的 `spring.factories` 条目贡献出去，因此无需任何应用代码即可让可用区发现工作。

在本线上**没有** `microsphere-multiactive-spring-boot-starter` 模块；你直接依赖这个 jar。

## Maven 坐标

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-spring-boot</artifactId>
    <version>0.1.14-SNAPSHOT</version>
</dependency>
```

- `groupId`：`io.github.microsphere-projects`
- `artifactId`：`microsphere-multiactive-spring-boot`
- `version`：`${revision}` → 本快照中为 `0.1.14-SNAPSHOT`；最新已发布版本 `0.1.13`
- `packaging`：`jar`
- `<name>`：`Microsphere :: Multi-Active :: Spring Boot`
- 是否由 [BOM](microsphere-multiactive-dependencies.md) 托管：**否**——本线的 BOM 列出的是
  `microsphere-multiactive-spring-boot-starter`，因此该构件的版本必须显式声明。

## 版本信息

| 项目 | 值 | 依据 |
|------|-------|-------------|
| 文档线 / Git 分支 | `1.x` / `release-1.x` | 本文档集 |
| 构件版本 | `0.1.14-SNAPSHOT` | 根 `pom.xml` 的 `<revision>` |
| compile 作用域依赖 | `io.github.microsphere-projects:microsphere-multiactive-spring:${revision}`（带入 `commons`） | `microsphere-multiactive-spring-boot/pom.xml` |
| optional 依赖 | `microsphere-annotation-processor`、`microsphere-spring-boot-core`、`spring-boot-starter`、`spring-boot-starter-actuator`、`spring-boot-starter-web`、`spring-boot-configuration-processor`、`spring-boot-autoconfigure-processor` | `microsphere-multiactive-spring-boot/pom.xml` |
| test 依赖 | `junit-jupiter`、`spring-boot-starter-test`、`io.github.microsphere-projects:microsphere-spring-boot-test`、`logback-classic` | `microsphere-multiactive-spring-boot/pom.xml` |
| JDK | 本仓库未声明编译级别；CI 在 JDK 8、11、17、21、25 上测试本模块 | reactor 各 POM、`.github/workflows/maven-build.yml` |
| Spring Boot 兼容性 | 仅 Boot 2.x，按 Maven profile 选择：`spring-cloud-hoxton` → `2.3.12.RELEASE`、`spring-cloud-2020` → `2.4.13`、`spring-cloud-2021` → `2.6.15`（默认）。Spring Boot 3.x 不在本线的矩阵中 | `.github/workflows/maven-build.yml` 的 CI profile；版本由外部 `microsphere-spring-cloud-parent:0.1.26` POM 定义 |
| 模块运行要求 | 一个 Spring Boot `ApplicationContext`；两个条件注解要求类路径上存在 `commons` 和 `spring` 两个 jar | 本模块源码 |

> **运行时前置条件。** `ZoneAutoConfiguration` 会调用
> `io.microsphere.spring.core.io.support.SpringFactoriesLoaderUtils.loadFactories(...)` 来收集 `ZoneLocator` 工厂。
> 该类型来自 Microsphere 的 Spring 系列库，而本 POM 将其声明为 optional（`microsphere-spring-boot-core`）；optional
> 依赖不传递，因此该提供者必须位于你的运行时类路径上。同理，`microsphere-java-core`（被 `commons` 和 `spring` 使用）
> 在运行时也是必需的。实践中：如果你只给 Spring Boot 应用添加这一个 jar，却看到某个 `io.microsphere.spring.*` 或
> `io.microsphere.util.*` 类型抛出 `NoClassDefFoundError`，请显式添加对应的 Microsphere 构件。

## 自动配置注册

本模块携带四个资源条目：

| 资源 | 内容 | 效果 |
|----------|---------|--------|
| `META-INF/spring.factories` → key `org.springframework.boot.autoconfigure.EnableAutoConfiguration` | `io.microsphere.multiple.active.zone.spring.boot.autoconfigure.ZoneAutoConfiguration` | 为 Spring Boot 2.x 注册该自动配置（本线支持的每个 Boot 版本都使用该机制） |
| `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` | `io.microsphere.multiple.active.zone.spring.boot.autoconfigure.ZoneAutoConfiguration` | 较新的注册文件；Boot 2.3–2.6 会忽略它，从 Boot 2.7 起生效 |
| `META-INF/spring.factories` → key `io.microsphere.multiple.active.zone.spring.ZoneLocator` | `io.microsphere.multiple.active.zone.spring.DefaultZoneLocator` | 即使不存在对应的 bean，也能让 `CompositeZoneLocator` 拿到 `DefaultZoneLocator` |
| `META-INF/spring.factories` → key `io.microsphere.multiple.active.zone.spring.ZoneLocator`（aws 模块，同一个 key） | 三个 AWS 定位器 | 见 [microsphere-multiactive-aws.md](microsphere-multiactive-aws.md) |

本模块**没有** `spring-configuration-metadata.json`，因此在本线上 IDE 不会对 `microsphere.availability.zone.*`
这些 key 提供补全。权威的 key 名与默认值列在
[microsphere-multiactive-commons.md](microsphere-multiactive-commons.md) 中。

## 公共 API

### `ZoneAutoConfiguration`（`class`，包 `io.microsphere.multiple.active.zone.spring.boot.autoconfigure`）

声明的注解，与源码完全一致：

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnAvailabilityZoneAvailable
@Import(value = {ZoneContextChangedListener.class})
public class ZoneAutoConfiguration {
}
```

| Bean | 方法 / 名称 | 条件 | 值 |
|------|---------------|------------|-------|
| `ZoneContext` | `zoneContext()`，bean 名 `zoneContext`（`ZoneUtils.ZONE_CONTEXT_BEAN_NAME`），`@ConditionalOnMissingBean` | 该配置类的 class 条件与 enabled 条件 | 返回共享的 `ZoneContext.get()` 单例 |
| `CompositeZoneLocator` | `zoneLocator(Collection<ZoneLocator>, ConfigurableApplicationContext)`，bean 名 `zoneLocator`（`ZoneUtils.ZONE_LOCATOR_BEAN_NAME`），`@Primary`，`@ConditionalOnMissingBean` | 同上 | 从（1）上下文中所有 `ZoneLocator` bean 和（2）`spring.factories` 该 key 下所有 `ZoneLocator` 条目构建委托列表，随后用 `AnnotationAwareOrderComparator` 排序并包装为 `CompositeZoneLocator` |

`ZoneContextChangedListener` 是被 `@Import` 进来的（而不是用 `@Bean` 声明），因此它作为一个 `SmartApplicationListener`
参与工作，并在 `ApplicationStartedEvent` 上刷新 `ZoneContext`——当 Spring Cloud Context 存在时还会响应
`EnvironmentChangeEvent`。触发矩阵见
[microsphere-multiactive-spring.md](microsphere-multiactive-spring.md)。

定位器上的 `@Primary` 意味着按类型的 `ZoneLocator` 注入会解析到组合定位器，而不是你或许注册过的某个单独定位器 bean。

由于两个 bean 都带 `@ConditionalOnMissingBean`，你可以替换它们：声明一个你自己的、名为 `zoneContext` 或
`zoneLocator`（或同类型）的 bean，自动配置的那一个就会被跳过。

### `@ConditionalOnAvailabilityZoneAvailable`（`annotation`，包 `...spring.boot.condition`）

```java
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Documented
@ConditionalOnClass(value = {
        ZoneContext.class,                  // Microsphere Multi-Active Commons
        ZoneLocator.class                   // Microsphere Multi-Active Spring
})
@ConditionalOnAvailabilityZoneEnabled
public @interface ConditionalOnAvailabilityZoneAvailable {
}
```

作用于类型和方法。它把"两个核心类都存在"与"该特性已启用"组合在一起，所以在你自己的可用区感知配置上应复用这个条件。

### `@ConditionalOnAvailabilityZoneEnabled`（`annotation`，包 `...spring.boot.condition`）

```java
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Documented
@ConditionalOnProperty(name = ZONE_ENABLED_PROPERTY_NAME, matchIfMissing = true)
public @interface ConditionalOnAvailabilityZoneEnabled {
}
```

Key：`microsphere.availability.zone.enabled`。`matchIfMissing = true`——除非你把该属性设为 `false`，否则特性是
**开启**的（Spring 的 `@ConditionalOnProperty` 在未指定 `havingValue` 时，把除 `false` 之外的任何值都视为匹配）。

## 本模块使用的配置属性

| 属性 | 默认值 | 效果 |
|----------|---------|--------|
| `microsphere.availability.zone.enabled` | `true` | 为 `false` 时整个自动配置关闭 |
| `microsphere.availability.zone` | `defaultZone` | 由 `DefaultZoneLocator`（自动注册）和被导入的 `ZoneContextChangedListener` 读取 |
| `microsphere.availability.zone.preference.enabled` | `false` | 由被导入的监听器写入 `ZoneContext` |
| `microsphere.availability.zone.preference.filter.order` | `10` | 由被导入的监听器写入 `ZoneContext` |
| `microsphere.availability.zone.preference.upstream.zone-ready-percentage` | `100` | 由被导入的监听器写入 `ZoneContext` |
| `microsphere.availability.zone.preference.upstream.same-zone-min-available` | `5` | 由被导入的监听器写入 `ZoneContext` |
| `microsphere.availability.zone.preference.upstream.disabled-zone` | 未设置 | 由被导入的监听器写入 `ZoneContext` |
| `microsphere.availability.zone.locator.fast-fail` | `false` | 由 `CompositeZoneLocator.locate(...)` 延迟读取 |
| `microsphere.availability.zone.locator.timeout` | `3000` | 由 AWS 定位器通过 `EnvironmentAware` 读取 |

## 示例

### 验证自动配置（`ZoneAutoConfigurationTest`）

本快照使用来自 `microsphere-spring-boot-test` 的 `AutoConfigurationTest<ZoneAutoConfiguration>` 来测试该配置。
它编码的契约值得当作文档来读：当类存在且属性开启时该配置生效；当属性为 `false` 或两个核心类中任一缺失时它退避。

```java
@SpringBootTest(
        classes = {
                ZoneAutoConfigurationTest.class
        },
        webEnvironment = NONE
)
class ZoneAutoConfigurationTest extends AutoConfigurationTest<ZoneAutoConfiguration> {

    @Override
    protected void configureAutoConfiguredClasses(Set<Class<?>> autoConfiguredClasses) {
        autoConfiguredClasses.add(ZoneContext.class);
        autoConfiguredClasses.add(CompositeZoneLocator.class);
    }

    @Override
    protected void configureGlobalDisabledPropertyValues(Set<String> globalDisabledPropertyValues) {
        globalDisabledPropertyValues.add("microsphere.availability.zone.enabled=false");
    }

    @Override
    protected void configureGlobalMissingClasses(Set<Class<?>> globalMissingClasses) {
        globalMissingClasses.add(ZoneContext.class);
        globalMissingClasses.add(ZoneLocator.class);
    }
}
```

两个 `autoConfiguredClasses` 条目正好就是 `ZoneAutoConfiguration` 产出的 bean，这印证了预期结果：库在类路径上且属性
未设置时，存在一个 `ZoneContext` bean 和一个 `CompositeZoneLocator` bean。

### 替换自动配置的上下文

```java
@SpringBootApplication
public class MyApplication {

    @Bean(name = ZoneUtils.ZONE_CONTEXT_BEAN_NAME)
    public ZoneContext zoneContext() {
        ZoneContext zoneContext = ZoneContext.get();
        zoneContext.setZone("zone-a");
        zoneContext.setPreferenceEnabled(true);
        zoneContext.setPreferenceUpstreamSameZoneMinAvailable(1);
        return zoneContext;
    }
}
```

声明这个 bean 会使 `ZoneAutoConfiguration#zoneContext` 不生效（`@ConditionalOnMissingBean`）；`zoneLocator` 仍会被创建。

### 添加你自己的定位器

```java
@Component
public class LocalPropertiesZoneLocator extends AbstractZoneLocator {

    public static final int DEFAULT_ORDER = 1;

    public LocalPropertiesZoneLocator() {
        super(DEFAULT_ORDER);
    }

    @Override
    public boolean supports(Environment environment) {
        return environment.containsProperty("my.zone");
    }

    @Override
    public String locate(Environment environment) {
        return environment.getProperty("my.zone");
    }
}
```

顺序 `1` 使它排在 AWS 定位器（5、10、15）和 `DefaultZoneLocator`（20）之前；又因为
`ZoneAutoConfiguration#zoneLocator` 注入的是 `Collection<ZoneLocator>`，它会被自动加入组合定位器——无需
`spring.factories` 条目。对于那些不是 Spring bean 的类，替代做法是在
`io.microsphere.multiple.active.zone.spring.ZoneLocator` 这个 `spring.factories` key 下注册它。

## 集成

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-spring-boot</artifactId>
    <version>0.1.13</version>
</dependency>
```

版本是必写的：[1.x BOM](microsphere-multiactive-dependencies.md) 并不托管这个 artifactId。

最小配置——本线不存在任何 `@Enable...` 注解；添加该 jar 并配一个属性即可：

```properties
microsphere.availability.zone=zone-a
microsphere.availability.zone.preference.enabled=true
```

然后在任意位置注入定位器或上下文：

```java
@Autowired
private ZoneLocator zoneLocator;      // the @Primary CompositeZoneLocator

@Autowired
private ZoneContext zoneContext;

@PostConstruct
public void logZone() {
    String zone = zoneLocator.locate(environment);      // discovery runs once and is cached
    System.out.println(zone + " / " + zoneContext.getZone());
}
```

设置 `microsphere.availability.zone.enabled=false` 即可移除这两个 bean，回到原生行为。

## 相关文档

- [用户指南索引](README.md)
- 上游：[commons](microsphere-multiactive-commons.md)、[spring](microsphere-multiactive-spring.md)
- 下游：[spring-cloud](microsphere-multiactive-spring-cloud.md)、[aws](microsphere-multiactive-aws.md)、
  [netflix](microsphere-multiactive-netflix.md)
- 根 [README](../../../../README.md) 与[发布说明](../../../../release-notes.md)
- 开发者指南：[../../developer-guide/zh/microsphere-multiactive-spring-boot.md](../../developer-guide/zh/microsphere-multiactive-spring-boot.md)

## English

English version: [English](../microsphere-multiactive-spring-boot.md)
