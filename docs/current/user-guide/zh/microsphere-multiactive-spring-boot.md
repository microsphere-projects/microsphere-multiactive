English version: [English](../microsphere-multiactive-spring-boot.md)

# microsphere-multiactive-spring-boot

## 概述

`microsphere-multiactive-spring-boot` 把可用区模型变成一个 Spring Boot 特性：加上这个 jar，就会出现一个
`ZoneContext` bean 与一个 `CompositeZoneLocator` bean，它们在应用启动时从 `Environment` 属性填充，并在环境变化时刷新 ——
完全不需要写装配代码。

本模块只有三个类：

| 类 | 角色 |
|-------|------|
| `ZoneAutoConfiguration` | 注册 `zoneContext` 与 `zoneLocator`，并导入 `ZoneContextChangedListener` |
| `ConditionalOnAvailabilityZoneAvailable` | “可用区类在 classpath 上**并且**功能已启用” |
| `ConditionalOnAvailabilityZoneEnabled` | “属性 `microsphere.availability.zone.enabled` 不为 `false`” |

包名：`io.microsphere.multiple.active.zone.spring.boot.autoconfigure` 与 `...spring.boot.condition`。

## Maven 坐标

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-spring-boot</artifactId>
    <version>0.2.14-SNAPSHOT</version>
</dependency>
```

- `groupId`：`io.github.microsphere-projects`
- `artifactId`：`microsphere-multiactive-spring-boot`
- `version`：`${revision}` → `0.2.14-SNAPSHOT`
- `packaging`：`jar`，`<name>`：`Microsphere :: Multi-Active :: Spring Boot`

> 注意构件名：在本文档线上**没有** `microsphere-multiactive-spring-boot-starter` 模块，尽管 BOM 预留了该坐标。
> 你要依赖的 jar 是 `microsphere-multiactive-spring-boot`，而 BOM 并未覆盖它，因此必须显式写出版本。见
> [microsphere-multiactive-dependencies.md](microsphere-multiactive-dependencies.md)。

## 版本信息

| 项目 | 值 | 验证位置 |
|------|-------|-------------|
| 文档线 / 分支 | `current` / `release` | 本套文档 |
| 构件版本 | `0.2.14-SNAPSHOT` | 根 `pom.xml` 的 `<revision>` |
| JDK | 17 及以上（CI 矩阵：17、21、25） | `.github/workflows/maven-build.yml` |
| 依赖的其他 Multi-Active 模块 | `microsphere-multiactive-spring` —— **compile 作用域，非可选**（其本身又依赖 `commons`） | `microsphere-multiactive-spring-boot/pom.xml` |
| 可选依赖 | `microsphere-spring-boot-core`、`spring-boot-starter`、`spring-boot-starter-actuator`、`spring-boot-starter-web`、`spring-boot-configuration-processor`、`spring-boot-autoconfigure-processor`、`microsphere-annotation-processor` | `microsphere-multiactive-spring-boot/pom.xml` |
| 测试依赖 | `junit-jupiter`、`spring-boot-starter-test`、`microsphere-spring-boot-test`、`logback-classic` | `microsphere-multiactive-spring-boot/pom.xml` |
| Spring Boot 版本 | 未在本仓库锁定；经 `microsphere-multiactive-parent` 由 `microsphere-spring-cloud-dependencies:0.2.26` 传递管理 | `microsphere-multiactive-parent/pom.xml` |
| Spring Cloud 兼容性 | `2022.0.x` – `2025.0.x` | 根 `README.md`、CI profile |
| Netflix / AWS | 本模块未引用 | 源码 |

所有 Spring Boot 依赖都是 `optional`，所以这个 jar 不会把某个 Boot 版本强加给你；但它确实要求运行期存在
`spring-boot-autoconfigure` 与 `spring-context` —— 任何 Boot 应用本来就有。

## 注册元数据

`src/main/resources/META-INF` 只有两个文件，别无其他：

`org.springframework.boot.autoconfigure.AutoConfiguration.imports`

```
io.microsphere.multiple.active.zone.spring.boot.autoconfigure.ZoneAutoConfiguration
```

`spring.factories`

```
io.microsphere.multiple.active.zone.spring.ZoneLocator=\
io.microsphere.multiple.active.zone.spring.DefaultZoneLocator
```

第二个是关键细节：`DefaultZoneLocator` 是以 **`SpringFactoriesLoader` 条目**贡献的，而不是 `@Bean`。
这就是为什么一个 Boot 应用只要把这个 jar 放进 classpath，就拥有一条可用的（即便取值是占位符的）定位器链；
也是为什么下游模块（如 [aws](microsphere-multiactive-aws.md)）用同样方式注册它们的定位器。

仓库里没有签入 `spring-configuration-metadata.json`；IDE 提示在构建期由
`spring-boot-configuration-processor` 加上 `microsphere-annotation-processor` 依据 `ZoneConstants` 上的
`@ConfigurationProperty` 注解生成。

## API 参考

### `ZoneAutoConfiguration`

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnAvailabilityZoneAvailable
@Import(value = {ZoneContextChangedListener.class})
public class ZoneAutoConfiguration {

    @Bean(name = ZONE_CONTEXT_BEAN_NAME)      // "zoneContext"
    @ConditionalOnMissingBean
    public ZoneContext zoneContext()

    @Primary
    @Bean(name = ZONE_LOCATOR_BEAN_NAME)      // "zoneLocator"
    @ConditionalOnMissingBean
    public CompositeZoneLocator zoneLocator(Collection<ZoneLocator> zoneLocatorBeans,
                                            ConfigurableApplicationContext context)
}
```

- `zoneContext()` 返回 `ZoneContext.get()` —— commons 里那个 JVM 级单例，*不是*新对象。你当然可以用自己的 bean
  覆盖它（`@ConditionalOnMissingBean`），但 commons 的代码读取的仍然是这个单例。
- `zoneLocator(...)` 从**两个**来源装配链条：容器中所有 `ZoneLocator` bean，*加上*在
  `META-INF/spring.factories` 的 `io.microsphere.multiple.active.zone.spring.ZoneLocator` key 下声明的
  `ZoneLocator`（`loadFactories`）。两者合并后用 `AnnotationAwareOrderComparator` 排序，再包装成
  `CompositeZoneLocator`，并标注 `@Primary`，使得按类型注入在存在多个单独定位器时依然唯一。
- `@Import(ZoneContextChangedListener.class)` 安装那个把环境属性复制进 `ZoneContext` 并发布
  `ZoneContextChangedEvent` 的监听器。
- 两个 bean 都带 `@ConditionalOnMissingBean`，所以你只需声明一个自己的 `@Bean` 就能替换其中任意一个。

### `ConditionalOnAvailabilityZoneAvailable`

```java
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Documented
@ConditionalOnClass(value = {
        ZoneContext.class,                  // Microsphere Multi-Active Commons
        ZoneLocator.class                   // Microsphere Multi-Active Spring
})
@ConditionalOnAvailabilityZoneEnabled
public @interface ConditionalOnAvailabilityZoneAvailable
```

两个条件相与：可用区类必须可加载（于是若你排除了 `commons` 或 `spring`，自动配置会静默退出），并且功能必须开启。
可用于类型与方法 —— 本项目的 AWS 与 Spring Cloud 自动配置都复用了它。

### `ConditionalOnAvailabilityZoneEnabled`

```java
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Documented
@ConditionalOnProperty(name = ZONE_ENABLED_PROPERTY_NAME, matchIfMissing = true)
public @interface ConditionalOnAvailabilityZoneEnabled
```

展开即 `@ConditionalOnProperty(name = "microsphere.availability.zone.enabled", matchIfMissing = true)`，
也就是说**除非你把该属性设为 `false`，功能默认是开的**。只有字面值 `false` 会关闭它；其他任何取值（包括不设置）都保持开启。

## 配置属性

本模块没有定义任何 `@ConfigurationProperties` 类。一切都由 `ZoneConstants` 中声明的扁平 key 驱动，
由 `ZoneContextChangedListener` 读取（详见
[microsphere-multiactive-spring.md](microsphere-multiactive-spring.md)）：

| 属性 | 默认值 | 在本模块中的作用 |
|----------|---------|-----------------------|
| `microsphere.availability.zone.enabled` | `true` | 通过 `ConditionalOnAvailabilityZoneEnabled` 决定自动配置是否生效 |
| `microsphere.availability.zone` | `defaultZone` | 成为 `ZoneContext.getZone()`；`originalZone` 表示回退到定位到的可用区 |
| `microsphere.availability.zone.preference.enabled` | `false` | 打开同区过滤 |
| `microsphere.availability.zone.preference.filter.order` | `10` | `ZonePreferenceFilter.getOrder()` |
| `microsphere.availability.zone.preference.upstream.zone-ready-percentage` | `100` | 过滤安全阀 1 |
| `microsphere.availability.zone.preference.upstream.same-zone-min-available` | `5` | 过滤安全阀 2 |
| `microsphere.availability.zone.preference.upstream.disabled-zone` | *无* | 被排除在路由之外的可用区 |
| `microsphere.availability.zone.locator.fast-fail` | `false` | `CompositeZoneLocator` 在首次失败时中断 |
| `microsphere.availability.zone.locator.timeout` | `3000` | AWS 定位器使用的连接/读取超时 |

典型的 `application.yaml`：

```yaml
microsphere:
  availability:
    zone: zone-sh-a
    preference:
      enabled: true
      upstream:
        same-zone-min-available: 2
        zone-ready-percentage: 80
        disabled-zone: zone-sh-b
```

## 示例

### 自动配置贡献了什么（`ZoneAutoConfigurationTest`）

本模块的测试是一种声明式校验：验证 bean 是否出现，以及两个否定条件是否生效。它继承自
`AutoConfigurationTest<ZoneAutoConfiguration>`（来自 `microsphere-spring-boot-test`）：

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

把它当作文档来读：

| 场景 | 期望结果 |
|----------|---------------|
| 带本 jar 的默认 Boot 应用 | 存在 `ZoneContext` 与 `CompositeZoneLocator` bean |
| `microsphere.availability.zone.enabled=false` | 自动配置被跳过，没有 bean |
| classpath 上缺少 `ZoneContext` 或 `ZoneLocator` | 自动配置被跳过，没有 bean |

### 在应用中消费这些 bean

```java
@Service
public class ZoneAwarePrinter {

    private final ZoneContext zoneContext;      // bean "zoneContext"
    private final ZoneLocator zoneLocator;      // bean "zoneLocator", @Primary CompositeZoneLocator

    public ZoneAwarePrinter(ZoneContext zoneContext, ZoneLocator zoneLocator) {
        this.zoneContext = zoneContext;
        this.zoneLocator = zoneLocator;
    }

    @EventListener
    public void onZoneContextChanged(ZoneContextChangedEvent event) {
        // fires after the listener applied environment properties to the context
        System.out.println(event.getZoneContext());
    }
}
```

由于 `zoneLocator` 是 `CompositeZoneLocator`，且已经从 `spring.factories` 吸收了 `DefaultZoneLocator`，
增加一个平台定位器只需要再发布一个 bean（或在你自己的 `META-INF/spring.factories` 里再加一行
`ZoneLocator=`），并给出更小的 `order`：

```java
@Bean
public ZoneLocator ecsZoneLocator() {
    return new EcsTaskMetadataEndpointV4ZoneLocator();   // order 10, wins over DefaultZoneLocator (20)
}
```

## 如何集成

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-spring-boot</artifactId>
    <version>0.2.14-SNAPSHOT</version>
</dependency>
```

最小步骤：

1. 添加上面的依赖（它会带入 `spring` 与 `commons`）。
2. 显式添加 `io.github.microsphere-projects:microsphere-java-core`。`commons` 把它声明为可选依赖
   （本模块对 `microsphere-spring-boot-core` 亦然），所以它不会通过任何传递路径出现 ——
   `ZoneContext`、`ZonePreferenceFilter`、`HttpUtils` 都 import 了它的类。
3. 设置 `microsphere.availability.zone`（或依赖平台定位器，见 [aws](microsphere-multiactive-aws.md)）。
4. 如果需要同区优先路由，设置 `microsphere.availability.zone.preference.enabled=true`；它默认关闭。
5. 其余无需处理。用 `microsphere.availability.zone.enabled=false` 可整体关闭。

如果你还想把可用区写入服务注册元数据并应用到负载均衡，请使用
[microsphere-multiactive-spring-cloud.md](microsphere-multiactive-spring-cloud.md) —— 它已经传递依赖本模块。

## 相关文档

- [用户指南索引](README.md)
- [microsphere-multiactive-spring.md](microsphere-multiactive-spring.md) —— `ZoneLocator`、监听器、事件
- [microsphere-multiactive-spring-cloud.md](microsphere-multiactive-spring-cloud.md) —— 更上一层的自动配置
- 根 [README](../../../../README.md) 与[发布说明](../../../../release-notes.md)
- 开发者指南：[../../developer-guide/zh/microsphere-multiactive-spring-boot.md](../../developer-guide/zh/microsphere-multiactive-spring-boot.md)

## English

English version: [English](../microsphere-multiactive-spring-boot.md)
