English version: [English](../microsphere-multiactive-spring.md)

# microsphere-multiactive-spring

## 概述

`microsphere-multiactive-spring` 把 [commons](microsphere-multiactive-commons.md) 的框架无关可用区模型适配到
Spring `Environment`。它引入了真正重要的那个扩展点 —— `ZoneLocator` SPI —— 以及“某处配置发生了变化”如何变成
“`ZoneContext` 与 JVM 内所有监听器都感知到”的全部机制。

三块内容：

1. **`ZoneLocator` / `AbstractZoneLocator` / `DefaultZoneLocator` / `CompositeZoneLocator`** —— 从 Spring
   `Environment`（或任意部署平台，见 AWS 与 Netflix 模块）发现可用区。
2. **`ZoneContextChangedListener` + `ZoneContextChangedEvent`** —— 让 `ZoneContext` 与外部配置保持同步
   （Spring Boot 启动、Spring Cloud 配置刷新、普通容器刷新），并把变更以 Spring 事件形式广播。
3. **`ZoneUtils`** —— 项目其余部分依赖的两个约定好的 bean 名称。

包名：`io.microsphere.multiple.active.zone.spring`（以及 `.spring.event`）。类的 Javadoc 标注 `@since 1.0.0`。

## Maven 坐标

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-spring</artifactId>
    <version>0.2.14-SNAPSHOT</version>
</dependency>
```

- `groupId`：`io.github.microsphere-projects`
- `artifactId`：`microsphere-multiactive-spring`
- `version`：`${revision}` → `0.2.14-SNAPSHOT`；由
  [microsphere-multiactive-dependencies](microsphere-multiactive-dependencies.md) 管理
- `packaging`：`jar`，`<name>`：`Microsphere :: Multi-Active :: Spring`

## 版本信息

| 项目 | 值 | 验证位置 |
|------|-------|-------------|
| 文档线 / 分支 | `current` / `release` | 本套文档 |
| 构件版本 | `0.2.14-SNAPSHOT` | 根 `pom.xml` 的 `<revision>` |
| JDK | 17 及以上（CI 矩阵：17、21、25） | `.github/workflows/maven-build.yml` |
| 依赖的其他 Multi-Active 模块 | `microsphere-multiactive-commons` —— **compile 作用域，非可选** | `microsphere-multiactive-spring/pom.xml` |
| 可选依赖 | `io.github.microsphere-projects:microsphere-spring-context`、`org.springframework:spring-context`、`io.github.microsphere-projects:microsphere-annotation-processor` | `microsphere-multiactive-spring/pom.xml` |
| 测试依赖 | `junit-jupiter`、`org.springframework:spring-test`、`io.github.microsphere-projects:microsphere-spring-test`、`logback-classic` | `microsphere-multiactive-spring/pom.xml` |
| Spring Framework 版本 | 未在本仓库锁定 —— 经 `microsphere-multiactive-parent` → `microsphere-spring-cloud-dependencies:0.2.26` 解析 | `microsphere-multiactive-parent/pom.xml` |
| Spring Boot / Spring Cloud | 未引用：本模块只针对 `spring-context` 编译（监听器在运行时通过反射解析 Boot/Cloud 的事件类） | 源码 |

由于 `spring-context` 是 `<optional>true</optional>`，本模块不会往你的 classpath 里塞任何 Spring 相关构件；你本来就有
Spring。但你需要保证 `microsphere-multiactive-commons` **以及** `commons` 的可选依赖 `microsphere-java-core`
在运行期可解析。

## API 参考

### `ZoneLocator`（接口）

```java
public interface ZoneLocator {
    boolean supports(Environment environment);   // false => locate() is never called for this environment
    String locate(Environment environment);      // the zone, or null when it cannot be located
}
```

`supports` 是廉价的可用性判断（“我跑在 ECS 上吗？元数据端点配置了吗？”），`locate` 负责真正的发现。返回 `null`
表示“未知”，让下一个定位器尝试；抛出异常同样可以容忍，除非开启了快速失败。

### `AbstractZoneLocator`

```java
public abstract class AbstractZoneLocator implements ZoneLocator, BeanNameAware, Ordered {
    protected final Logger logger;      // per-subclass microsphere logger
    protected String beanName;          // set by Spring
    protected int order;                // constructor argument, mutable via setOrder(int)

    public AbstractZoneLocator(int order)
    public final void setBeanName(String name)
    public final int getOrder()
    public final void setOrder(int order)
}
```

order 决定链的行为是否确定：`CompositeZoneLocator` 用 `AnnotationAwareOrderComparator` 排序它的委托，因此
**order 越小越早被尝试**。公开的常量如下：

| 定位器 | `DEFAULT_ORDER` | 模块 |
|---------|-----------------|--------|
| `EcsContainerMetadataFileZoneLocator` | `5` | aws |
| `EcsTaskMetadataEndpointV4ZoneLocator` | `10` | aws |
| `Ec2AvailabilityZoneEndpointZoneLocator` | `15` | aws |
| `DefaultZoneLocator` | `20` | spring |

`toString()` 渲染为 `ZoneLocator{beanName='...', order=20}` —— 组合定位器在某个定位器失败时打印的就是这个字符串，
因此给定位器起 bean 名称能让日志更易读。

### `DefaultZoneLocator`

永远可用的兜底实现：

```java
public class DefaultZoneLocator extends AbstractZoneLocator {
    public static final int DEFAULT_ORDER = 20;
    public boolean supports(Environment environment)   // always true
    public String locate(Environment environment)      // environment.getProperty("microsphere.availability.zone"), else "defaultZone"
}
```

它绝不返回 `null` 也绝不抛异常：当 `microsphere.availability.zone` 缺失或为空白时，它记录一条 `trace`
并返回 `ZoneConstants.DEFAULT_ZONE`（`defaultZone`），而后者会让 `ZonePreferenceFilter` 变成空操作。
这正是 [Spring Boot 模块](microsphere-multiactive-spring-boot.md)在 `META-INF/spring.factories` 中注册的那个定位器。

### `CompositeZoneLocator`

```java
public class CompositeZoneLocator implements ZoneLocator {
    public CompositeZoneLocator(List<ZoneLocator> zoneLocators)   // throws IllegalArgumentException on null
    public boolean supports(Environment environment)              // true if ANY delegate supports
    public String locate(Environment environment)
}
```

语义，全部由 `CompositeZoneLocatorTest` 断言：

- 委托列表在构造时拷贝并按 order 排序。
- `locate()` 在第一个（a）支持当前环境且（b）返回非 `null` 可用区的委托处短路。
- 结果会被**缓存在实例中**：第二次调用 `locate()` 直接返回同一个可用区，不再重跑链（`testLocateCachesResult`）。
- 不支持当前环境的委托被静默跳过（`testLocateSkipsUnsupportedLocators`）；返回 `null` 的委托被记为 `warn`，链继续。
- 抛出异常的委托被记为 `error`，链继续 —— 因此一个失败的 AWS 探测不会阻止 `defaultZone` 兜底
  （`testLocateWithExceptionNoFastFail`）。
- 当 `microsphere.availability.zone.locator.fast-fail=true` 时，第一个 `null` 结果或第一个异常会中断链并抛出
  `IllegalStateException("The zone can't be located by " + failedZoneLocator)`。
- 若没有任何定位器发现可用区且未开启快速失败，`locate()` 返回 `null` 并记录 warning。空委托列表的行为完全一致。
- 成功时，发现的可用区会以**系统属性** `microsphere.current.availability.zone` 发布，也就是
  `ZoneContext.getCurrentZone()` 读取的位置。注意缓存效应：由于缓存在，`locate()` 真正重跑链时才会写系统属性。

### `ZoneUtils`

```java
public abstract class ZoneUtils {
    public static final String ZONE_CONTEXT_BEAN_NAME = "zoneContext";
    public static final String ZONE_LOCATOR_BEAN_NAME = "zoneLocator";

    public static ZoneContext getZoneContext(ConfigurableListableBeanFactory beanFactory)
    public static ZoneLocator getZoneLocator(ConfigurableListableBeanFactory beanFactory)
}
```

给只拿得到 bean factory 的代码（自定义 `BeanFactoryPostProcessor`、初始化器）使用的查找助手。它们调用
`getBean(name, type)` —— 因此 bean 必须以这两个名字注册。Spring Boot 模块的 `ZoneAutoConfiguration` 就是这么做的，
这也是手工注册时应沿用的名字。

### `ZoneContextChangedEvent`

```java
public class ZoneContextChangedEvent extends ApplicationContextEvent {
    public ZoneContextChangedEvent(ApplicationContext context, ZoneContext zoneContext,
                                   List<PropertyChangeEvent> propertyChangeEvents)
    public ZoneContext getZoneContext()
    public List<PropertyChangeEvent> getPropertyChangeEvents()   // unmodifiable, source-preserving
}
```

每一批实际发生的变更发布一次。`getSource()` 与 `getApplicationContext()` 都返回发布它的容器
（`ZoneContextChangedEventTest`）。如果你需要在配置刷新后重新读取可用区，就监听这个事件 —— 它是唯一把
“到底改了什么、从什么改成了什么”以 Spring 事件暴露出来的地方。

### `ZoneContextChangedListener`

```java
public class ZoneContextChangedListener implements SmartApplicationListener, ApplicationContextAware, EnvironmentAware
```

配置到 `ZoneContext` 之间的桥。它会：

- 根据 classpath 上有什么，响应三种事件之一：
  `org.springframework.boot.context.event.ApplicationStartedEvent`（Spring Boot），在 Spring Cloud 场景下额外响应
  `org.springframework.cloud.context.environment.EnvironmentChangeEvent`（即 `/actuator/refresh`）；
  否则退化为普通 `ContextRefreshedEvent`。Boot/Cloud 的两个类都通过反射解析，因此本模块不需要它们也能编译。
- 把下面七个属性按此映射写入上下文：

| 属性 | 应用到 |
|----------|-----------|
| `microsphere.availability.zone` | `zoneContext.setZone(...)`，并有 `originalZone` 特殊分支（见下） |
| `microsphere.availability.zone.enabled` | `setEnabled(boolean)` |
| `microsphere.availability.zone.preference.enabled` | `setPreferenceEnabled(boolean)` |
| `microsphere.availability.zone.preference.filter.order` | `setPreferenceFilterOrder(int)` |
| `microsphere.availability.zone.preference.upstream.zone-ready-percentage` | `setPreferenceUpstreamZoneReadyPercentage(int)` |
| `microsphere.availability.zone.preference.upstream.same-zone-min-available` | `setPreferenceUpstreamSameZoneMinAvailable(int)` |
| `microsphere.availability.zone.preference.upstream.disabled-zone` | `setPreferenceUpstreamDisabledZone(String)` |

- **不会用缺失的属性覆盖已有值**，除了 boolean/int 值：属性缺失时回落到文档化的默认值（也就是“删除属性 ⇒ 恢复默认”）。
  对可用区本身，缺失或空白的值不会改变当前可用区（`testChangeZoneWithEmptyStringPropertyDoesNotChangeZone`）。
- 支持 `microsphere.availability.zone=originalZone`：取值 `originalZone` 表示“丢弃覆盖值，重新询问 `ZoneLocator`
  链”。如果定位器不支持当前环境，可用区回落到 `defaultZone`
  （`testRevertOriginalZoneWhenLocatorDoesNotSupport`）。
- 在这一批处理前后临时安装/卸载一个 `PropertyChangeListener`，从而精确收集自己造成的变更，并且仅在列表非空时发布一个
  `ZoneContextChangedEvent`。
- 要求容器中存在 `ZoneContext` bean 与 `ZoneLocator` bean（`setApplicationContext` 会去查找二者）。

`ZONE_CONTEXT_PROPERTY_NAMES`（七个元素）与 handler map 都是 `protected` 的，子类可以扩展被同步的属性集合。

## 示例

改编自本模块的 `src/test/java`。

### 从属性发现可用区（`DefaultZoneLocatorTest`）

```java
DefaultZoneLocator locator = new DefaultZoneLocator();
assertEquals(20, locator.getOrder());
assertTrue(locator.supports(new StandardEnvironment()));      // always supported

StandardEnvironment environment = new StandardEnvironment();
Map<String, Object> props = new HashMap<>();
props.put(ZONE_PROPERTY_NAME, "zone-a");                      // microsphere.availability.zone
environment.getPropertySources().addFirst(new MapPropertySource("test", props));

assertEquals("zone-a", locator.locate(environment));
assertEquals(DEFAULT_ZONE, new DefaultZoneLocator().locate(new StandardEnvironment())); // fallback
```

### 组装一条链（`CompositeZoneLocatorTest`）

```java
ZoneLocator metadataLocator = new ZoneLocator() {
    @Override
    public boolean supports(Environment env) {
        return env.containsProperty("ECS_CONTAINER_METADATA_URI_V4");
    }

    @Override
    public String locate(Environment env) {
        return "zone-from-metadata";
    }
};

CompositeZoneLocator composite = new CompositeZoneLocator(Arrays.asList(metadataLocator, new DefaultZoneLocator()));

assertTrue(composite.supports(newEnvironment()));
assertEquals("zone-from-metadata", composite.locate(newEnvironment()));  // first non-null wins
```

容错与快速失败：

```java
// a throwing locator is skipped, the next one still provides the zone
CompositeZoneLocator resilient = new CompositeZoneLocator(
        Arrays.asList(throwingLocator(), fixedLocator("zone-fallback")));
assertEquals("zone-fallback", resilient.locate(newEnvironment()));

// same chain, but fast-fail enabled -> IllegalStateException
CompositeZoneLocator strict = new CompositeZoneLocator(Collections.singletonList(nullLocator()));
assertThrows(IllegalStateException.class, () -> strict.locate(newFastFailEnvironment()));
```

其中 `newFastFailEnvironment()` 是一个把
`microsphere.availability.zone.locator.fast-fail=true` 作为首个属性源加入的 `StandardEnvironment`。

### 在非 Boot 的 Spring 环境中装配（`ZoneContextChangedListenerTest`）

没有 Spring Boot 时，注册两个约定 bean、加入监听器并 refresh：

```java
GenericApplicationContext ctx = new GenericApplicationContext();
ctx.getBeanFactory().registerSingleton(ZoneUtils.ZONE_CONTEXT_BEAN_NAME, ZoneContext.get());
ctx.getBeanFactory().registerSingleton(ZoneUtils.ZONE_LOCATOR_BEAN_NAME,
        new CompositeZoneLocator(Collections.singletonList(new DefaultZoneLocator())));

Map<String, Object> props = new HashMap<>();
props.put(ZONE_PROPERTY_NAME, "zone-from-prop");
ctx.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test-props", props));

ctx.refresh();

ZoneContextChangedListener listener = new ZoneContextChangedListener();
listener.setEnvironment(ctx.getEnvironment());
listener.setApplicationContext(ctx);
ctx.addApplicationListener(listener);
listener.onApplicationEvent(new ContextRefreshedEvent(ctx));

assertEquals("zone-from-prop", ZoneContext.get().getZone());
```

每个文档化的属性都以同样方式生效 —— 下面这些断言直接来自测试类：

```java
props.put(ZONE_ENABLED_PROPERTY_NAME, "false");                              // -> isEnabled() == false
props.put(PREFERENCE_ENABLED_PROPERTY_NAME, "true");                         // -> isPreferenceEnabled() == true
props.put(PREFERENCE_FILTER_ORDER_PROPERTY_NAME, "42");                      // -> getPreferenceFilterOrder() == 42
props.put(PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE_PROPERTY_NAME, "80");    // -> 80
props.put(PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE_PROPERTY_NAME, "3");   // -> 3
props.put(PREFERENCE_UPSTREAM_DISABLED_ZONE_PROPERTY_NAME, "zone-d");        // -> "zone-d"
```

响应变更：

```java
@EventListener
public void onZoneChanged(ZoneContextChangedEvent event) {
    for (PropertyChangeEvent pce : event.getPropertyChangeEvents()) {
        log.warn("zone property '{}' : {} -> {}", pce.getPropertyName(), pce.getOldValue(), pce.getNewValue());
    }
}
```

### 按名字查找 bean（`ZoneUtilsTest`）

```java
StaticApplicationContext context = new StaticApplicationContext();
ZoneContext zoneContext = ZoneContext.get();
context.getBeanFactory().registerSingleton(ZoneUtils.ZONE_CONTEXT_BEAN_NAME, zoneContext);
assertSame(zoneContext, ZoneUtils.getZoneContext(context.getBeanFactory()));
```

## 如何集成

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-spring</artifactId>
    <version>0.2.14-SNAPSHOT</version>
</dependency>
```

之后，在非 Boot 的 Spring 应用中声明三个 bean —— 上下文、定位器链、监听器 —— 并在 `Environment` 中设置
`microsphere.availability.zone`：

```java
@Bean(name = ZoneUtils.ZONE_CONTEXT_BEAN_NAME)
public ZoneContext zoneContext() {
    return ZoneContext.get();
}

@Bean(name = ZoneUtils.ZONE_LOCATOR_BEAN_NAME)
@Primary
public ZoneLocator zoneLocator(List<ZoneLocator> locators) {
    return new CompositeZoneLocator(locators);
}

@Bean
public ZoneContextChangedListener zoneContextChangedListener() {
    return new ZoneContextChangedListener();
}
```

在 Spring Boot 应用中，以上全部由 `ZoneAutoConfiguration` 代劳；请优先阅读
[microsphere-multiactive-spring-boot.md](microsphere-multiactive-spring-boot.md)。

## 相关文档

- [用户指南索引](README.md)
- [microsphere-multiactive-commons.md](microsphere-multiactive-commons.md) —— 监听器写入的目标
- [microsphere-multiactive-aws.md](microsphere-multiactive-aws.md) —— 面向平台的 `ZoneLocator` 实现
- [microsphere-multiactive-spring-boot.md](microsphere-multiactive-spring-boot.md) —— 这些 bean 的自动配置
- 根 [README](../../../../README.md) 与[发布说明](../../../../release-notes.md)
- 开发者指南：[../../developer-guide/zh/microsphere-multiactive-spring.md](../../developer-guide/zh/microsphere-multiactive-spring.md)

## English

English version: [English](../microsphere-multiactive-spring.md)
