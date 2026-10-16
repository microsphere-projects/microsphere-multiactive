English version: [English](../microsphere-multiactive-netflix.md)

# microsphere-multiactive-netflix

## 概述

`microsphere-multiactive-netflix` 把可用区模型带到 Netflix OSS 技术栈：用 Eureka 做注册/发现，用 Ribbon
做客户端负载均衡。Spring Cloud 模块对接的是 Spring Cloud LoadBalancer，本模块对接的则是 Ribbon 的接口
（`ServerList`、`ServerListFilter`）与 Eureka 客户端（`InstanceInfo`、`PreRegistrationHandler`）。

它提供五个类型，分三组：

| 分组 | 类型 | 用途 |
|-------|-------|---------|
| Eureka | `EurekaInstanceInfoZoneResolver`、`ZoneAttachmentPreRegistrationHandler` | 读取已注册实例的可用区；在注册前写入你自己的可用区 |
| Ribbon | `RibbonServerZoneResolver`、`ZonePreferenceServerListFilter` | 从 Ribbon `Server` 读取可用区；把 Ribbon 服务列表收窄到同区 |
| 桥接 | `DiscoveryClientServer`、`DiscoveryClientServerList` | 让 Ribbon 使用 Spring Cloud `DiscoveryClient` 的结果，*并且*带上可用区 |

本模块**没有自动配置**：需要你自己装配 bean。它在 `0.2.0` 引入，取代了原先的
`microsphere-multiactive-spring-cloud-netflix` 模块。

包名：`io.microsphere.multiple.active.zone.netflix.eureka`、`.netflix.ribbon`、`.netflix.spring.cloud.ribbon`。

## Maven 坐标

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-netflix</artifactId>
    <version>0.2.14-SNAPSHOT</version>
</dependency>
```

- `groupId`：`io.github.microsphere-projects`
- `artifactId`：`microsphere-multiactive-netflix`
- `version`：`${revision}` → `0.2.14-SNAPSHOT`
- `packaging`：`jar`，`<name>`：`Microsphere :: Multi-Active :: Netflix`
- 该坐标**由** [microsphere-multiactive-dependencies](microsphere-multiactive-dependencies.md) 管理。

## 版本信息

| 项目 | 值 | 验证位置 |
|------|-------|-------------|
| 文档线 / 分支 | `current` / `release` | 本套文档 |
| 构件版本 | `0.2.14-SNAPSHOT` | 根 `pom.xml` 的 `<revision>` |
| JDK | 17 及以上（CI 矩阵：17、21、25） | `.github/workflows/maven-build.yml` |
| **Netflix Ribbon** | **`2.7.18`**，来自模块属性 `netflix.ribbon.version`，作用于 `com.netflix.ribbon:ribbon`、`ribbon-loadbalancer`、`ribbon-eureka`（均为可选） | `microsphere-multiactive-netflix/pom.xml` |
| Netflix Eureka | `com.netflix.eureka:eureka-client`（可选，**本仓库未写版本** —— 由 `microsphere-spring-cloud-dependencies:0.2.26` 管理）以及 `org.springframework.cloud:spring-cloud-netflix-eureka-client`（可选，同上） | `microsphere-multiactive-netflix/pom.xml`、`microsphere-multiactive-parent/pom.xml` |
| 依赖的其他 Multi-Active 模块 | `microsphere-multiactive-spring-cloud` —— 声明为**可选**，但它正是 `CloudServerZoneResolver`（被 `DiscoveryClientServer` 使用）以及经由 `spring-boot` → `spring` → `commons` 的整条 `ZoneLocator`/`ZoneContext` 链的来源。请自行声明它（或你实际需要的模块）。 | `microsphere-multiactive-netflix/pom.xml`、`DiscoveryClientServer.java` |
| 其他可选依赖 | `spring-boot-starter`、`spring-boot-starter-actuator`、`spring-boot-starter-web`、`spring-boot-configuration-processor`、`spring-boot-autoconfigure-processor`、`spring-cloud-commons`、`spring-cloud-context`、`microsphere-annotation-processor` | `microsphere-multiactive-netflix/pom.xml` |
| 测试依赖 | `junit-jupiter`、`spring-boot-starter-test`、`microsphere-spring-boot-test`、`logback-classic` | `microsphere-multiactive-netflix/pom.xml` |
| 测试源码 | **无** —— 本分支没有 `src/test/java` | 模块目录树 |
| AWS | 本模块未引用 | 源码 |

Ribbon 是已经停止演进的 Netflix OSS 组件；本模块是为仍在运行它的应用保留的。新的工作应优先选择
[microsphere-multiactive-spring-cloud.md](microsphere-multiactive-spring-cloud.md) 中的 Spring Cloud LoadBalancer 路径。

## 特性元数据

本模块没有 `AutoConfiguration.imports`，取而代之的是
`META-INF/config/default/features.yaml`，它把自己的类型声明为命名特性集合
（`microsphere.spring.cloud.features.named`，机制由 `microsphere-spring-cloud-commons` 定义）：

```yaml
microsphere:
  spring:
    cloud:
      features:
        named:
          microsphere-multiactive-netflix:
            - io.microsphere.multiple.active.zone.netflix.eureka.EurekaInstanceInfoZoneResolver
            - io.microsphere.multiple.active.zone.netflix.eureka.ZoneAttachmentPreRegistrationHandler
            - io.microsphere.multiple.active.zone.netflix.ribbon.RibbonServerZoneResolver
            - io.microsphere.multiple.active.zone.netflix.ribbon.ZonePreferenceServerListFilter
            - io.microsphere.multiple.active.zone.netflix.spring.cloud.ribbon.DiscoveryClientServerList
```

分组名就是本模块的 artifact id，因此这组特性可以整体切换；这里没有任何东西会自动注册为 Spring bean。

## API 参考

### `EurekaInstanceInfoZoneResolver`

```java
public class EurekaInstanceInfoZoneResolver implements ZoneResolver<InstanceInfo> {
    public String resolve(InstanceInfo instanceInfo)
}
```

即 `instanceInfo.getMetadata().get("microsphere.availability.zone")`，当元数据 map 为 `null` 时返回 `null`。
关于入参的约定：不接受 `null` 的 `InstanceInfo`（方法会立即解引用它），而在 Ribbon/Eureka 的回调场景里实例总是存在的，
所以没有问题。

### `ZoneAttachmentPreRegistrationHandler`

```java
public class ZoneAttachmentPreRegistrationHandler implements PreRegistrationHandler {
    public ZoneAttachmentPreRegistrationHandler(ApplicationInfoManager applicationInfoManager,
                                                ZoneAttachmentHandler zoneAttachmentHandler)
    public void beforeRegistration()
}
```

实现 `com.netflix.discovery.PreRegistrationHandler`。在 Eureka 客户端注册实例之前，它取
`applicationInfoManager.getInfo().getMetadata()` 并调用 `ZoneAttachmentHandler.attachZone(...)`，
由后者把 `ZoneContext` 中的可用区以 `microsphere.availability.zone` 写入。请通过
`AbstractDiscoveryClientOptionalArgs`（Javadoc 指出这就是注入点）把它挂到 discovery client 上，这样每次注册都会携带
可用区 —— 与 Spring Cloud 模块中 `ZoneAttachmentListener` 达成的效果完全一致。

### `RibbonServerZoneResolver`

```java
public class RibbonServerZoneResolver<T extends Server> implements ZoneResolver<T> {
    public String resolve(T server)   // server.getZone()
}
```

本项目里最简单的解析器：它直接信任 Ribbon 在 `com.netflix.loadbalancer.Server` 上携带的可用区
（例如由 `DiscoveryClientServer` 设置）。类型参数 `<T extends Server>` 让一个实例可服务任意 Server 子类。

### `ZonePreferenceServerListFilter`

```java
public class ZonePreferenceServerListFilter<T extends Server> implements ServerListFilter<T> {
    public ZonePreferenceServerListFilter(ZonePreferenceFilter<T> filter)
    public List<T> getFilteredListOfServers(List<T> servers)   // filter.filter(servers)
}
```

一个薄薄的适配器：入口是 Ribbon 的 `ServerListFilter`，真正干活的是 commons 的 `ZonePreferenceFilter`。
关于该过滤器文档化的一切都适用 —— enabled/preference 开关、`defaultZone` 忽略规则、禁用区剔除、
就绪百分比与同区最小数量全部由被委托对象评估，而这些阈值取自活的 `ZoneContext`。

### `DiscoveryClientServer`

```java
public class DiscoveryClientServer extends Server {
    public DiscoveryClientServer(ServiceInstance serviceInstance)
    public String getId()             // serviceInstance.getInstanceId()
    public MetaInfo getMetaInfo()     // ServiceInstanceMetaInfo
}
```

把 Spring Cloud 的 `ServiceInstance` 包装成 Ribbon 的 `Server`：

- 父类 `Server(scheme, host, port)` 由该实例构造；
- 使用 `CloudServerZoneResolver.INSTANCE.resolve(serviceInstance)` 调用 `setZone(...)`，也就是把可用区从实例元数据
  （`microsphere.availability.zone`）里取出来 —— 而这正是 Spring Cloud 模块在注册阶段写入的内容；
- 嵌套的 `final static class ServiceInstanceMetaInfo implements MetaInfo` 把 `getAppName()`/
  `getServiceIdForDiscovery()` 映射为 `serviceInstance.getServiceId()`，`getInstanceId()` 映射为
  `serviceInstance.getInstanceId()`，`getServerGroup()` 返回 `null`。

### `DiscoveryClientServerList`

```java
public class DiscoveryClientServerList implements ServerList<DiscoveryClientServer> {
    public DiscoveryClientServerList(DiscoveryClient discoveryClient, String serviceName)
    public List<DiscoveryClientServer> getInitialListOfServers()   // delegates
    public List<DiscoveryClientServer> getUpdatedListOfServers()    // discoveryClient.getInstances(serviceName).map(DiscoveryClientServer::new)
}
```

让 Ribbon 从 Spring Cloud 的 `DiscoveryClient` 取数，而不是原生的 Eureka `ApplicationInfoManager`
列表，因此 Ribbon 客户端可以继续对接 Spring Cloud 配置的任意注册中心 —— 同时把可用区带上。
`ServerList` 的两个方法返回的是同一次实时查询的结果。

## 示例

本分支上该模块没有测试，因此下面的代码片段依据类自身的签名，并结合其委托对象（commons 过滤器）的文档编写。

### 可用区感知的 Ribbon 过滤

```java
ZoneContext zoneContext = ZoneContext.get();
zoneContext.setZone("zone-a");
zoneContext.setPreferenceEnabled(true);
zoneContext.setPreferenceUpstreamSameZoneMinAvailable(2);

ZonePreferenceFilter<Server> zoneFilter =
        new ZonePreferenceFilter<>(zoneContext, new RibbonServerZoneResolver<>());

ServerListFilter<Server> ribbonFilter = new ZonePreferenceServerListFilter<>(zoneFilter);
List<Server> targets = ribbonFilter.getFilteredListOfServers(serversFromEureka);
```

如果你需要在 Ribbon 的组合过滤器里对 commons 过滤器排序：commons 过滤器的 `getOrder()` 返回
`zoneContext.getPreferenceFilterOrder()`（默认 `10`）。

### 让 Ribbon 走 Spring Cloud `DiscoveryClient`

```java
@Bean
public ServerList<DiscoveryClientServer> serverList(DiscoveryClient discoveryClient) {
    return new DiscoveryClientServerList(discoveryClient, "order-service");
}

@Bean
public ZonePreferenceServerListFilter<DiscoveryClientServer> zonePreferenceFilter(ZoneContext zoneContext) {
    return new ZonePreferenceServerListFilter<>(
            new ZonePreferenceFilter<>(zoneContext, new RibbonServerZoneResolver<>()));
}
```

两者是配合关系：`DiscoveryClientServer` 把可用区写到每个 `Server` 上，`RibbonServerZoneResolver` 再把它读出来。

### 向 Eureka 广播你的可用区

```java
@Bean
public ZoneAttachmentPreRegistrationHandler zoneAttachmentPreRegistrationHandler(
        ApplicationInfoManager applicationInfoManager, ZoneAttachmentHandler zoneAttachmentHandler) {
    return new ZoneAttachmentPreRegistrationHandler(applicationInfoManager, zoneAttachmentHandler);
}
```

`ZoneAttachmentHandler` 是 [commons](microsphere-multiactive-commons.md) 里的普通类；你可以自己注册它（构造参数是
`ZoneContext` bean），也可以通过 Spring Cloud 模块的 `ZoneCloudAutoConfiguration` 获得 —— 它 `@Import` 了这个类。

### 读取对端的可用区

```java
ZoneResolver<InstanceInfo> resolver = new EurekaInstanceInfoZoneResolver();
String peerZone = resolver.resolve(instanceInfo);   // null when the peer never published a zone
```

## 如何集成

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-netflix</artifactId>
</dependency>

<!-- the netflix module's spring-cloud dependency is optional: declare what you use -->
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-spring-cloud</artifactId>
    <version>0.2.14-SNAPSHOT</version>
</dependency>

<!-- Ribbon itself is optional too -->
<dependency>
    <groupId>com.netflix.ribbon</groupId>
    <artifactId>ribbon-loadbalancer</artifactId>
    <version>2.7.18</version>
</dependency>
```

1. 把 `microsphere-multiactive-netflix` 加到你的 Eureka client 与 Ribbon 依赖旁边（这两者在 Netflix 模块的 POM
   中都是 `optional`，因此不会传递引入）。
2. 保留 `microsphere-multiactive-spring-boot`/`spring-cloud` 在 classpath 上，这样 `zoneContext` 会被自动配置并被填充。
3. 声明上面片段中你需要的 bean —— 没有任何东西是替你注册的。
4. 设置 `microsphere.availability.zone.preference.enabled=true`；在此之前 `ZonePreferenceServerListFilter`
   会原样返回列表。

## 相关文档

- [用户指南索引](README.md)
- [microsphere-multiactive-commons.md](microsphere-multiactive-commons.md) —— `ZonePreferenceFilter`、`ZoneAttachmentHandler`
- [microsphere-multiactive-spring-cloud.md](microsphere-multiactive-spring-cloud.md) —— `CloudServerZoneResolver`、
  更现代的 LoadBalancer 路径
- 根 [README](../../../../README.md) 与[发布说明](../../../../release-notes.md)
- 开发者指南：[../../developer-guide/zh/microsphere-multiactive-netflix.md](../../developer-guide/zh/microsphere-multiactive-netflix.md)

## English

English version: [English](../microsphere-multiactive-netflix.md)
