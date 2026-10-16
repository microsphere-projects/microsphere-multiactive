English version: [English](../microsphere-multiactive-aws.md)

# microsphere-multiactive-aws

## 概述

`microsphere-multiactive-aws` 直接从 AWS 平台本身发现*当前*进程的可用区，使 `ZoneContext` 得到真实可用区而不是
`defaultZone` 占位值。它实现了三个 `ZoneLocator`，分别对应一种 AWS 部署形态：

| 部署形态 | 事实来源 | 类 |
|------------|-----------------|-------|
| ECS 任务（容器元数据文件，agent ≥ 1.15.0） | 由 `ECS_CONTAINER_METADATA_FILE` 指定的本地 JSON 文件 | `EcsContainerMetadataFileZoneLocator` |
| EC2 上启用 `awsvpc`/bridge 网络的 ECS 任务（任务元数据端点 v4） | HTTP GET `<ECS_CONTAINER_METADATA_URI_V4>/task` | `EcsTaskMetadataEndpointV4ZoneLocator` |
| EC2 实例 | HTTP GET EC2 实例元数据的可用区端点 | `Ec2AvailabilityZoneEndpointZoneLocator` |

这里**不涉及任何 AWS SDK**：本模块用 commons 的 `HttpUtils` 通过普通 HTTP（或本地文件系统）读取实例/任务元数据，
并用 `jackson-databind` 解析 JSON。因此它不会给你的应用增加任何云客户端的版本约束。

包名：`io.microsphere.multiple.active.zone.spring.aws`。

## Maven 坐标

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-aws</artifactId>
    <version>0.2.14-SNAPSHOT</version>
</dependency>
```

- `groupId`：`io.github.microsphere-projects`
- `artifactId`：`microsphere-multiactive-aws`
- `version`：`${revision}` → `0.2.14-SNAPSHOT`
- `packaging`：`jar`，`<name>`：`Microsphere :: Multi-Active :: AWS`
- `<description>`：`Microsphere Multi-Active Amazon Web Services`
- 该坐标**在** [microsphere-multiactive-dependencies](microsphere-multiactive-dependencies.md) 中被管理，
  因此导入 BOM 后可以省略版本。

## 版本信息

| 项目 | 值 | 验证位置 |
|------|-------|-------------|
| 文档线 / 分支 | `current` / `release` | 本套文档 |
| 构件版本 | `0.2.14-SNAPSHOT` | 根 `pom.xml` 的 `<revision>` |
| JDK | 17 及以上（CI 矩阵：17、21、25） | `.github/workflows/maven-build.yml` |
| 依赖的其他 Multi-Active 模块 | `microsphere-multiactive-spring-cloud` —— 声明为**可选**，但它是获取 `AbstractZoneLocator` 的唯一途径（spring-cloud → spring-boot → spring → commons）。如果你在不使用 Spring Cloud 集成的情况下使用本模块，请显式添加它（或直接依赖 `microsphere-multiactive-spring`）。 | `microsphere-multiactive-aws/pom.xml` |
| 可选依赖 | `spring-boot-starter`、`spring-boot-starter-actuator`、`spring-boot-configuration-processor`、`spring-boot-autoconfigure-processor`、`spring-cloud-commons`、`spring-cloud-context`、`com.fasterxml.jackson.core:jackson-databind`、`microsphere-annotation-processor` | `microsphere-multiactive-aws/pom.xml` |
| 测试依赖 | `junit-jupiter`、`spring-boot-starter-test`、`microsphere-spring-boot-test`、`logback-classic` | `microsphere-multiactive-aws/pom.xml` |
| 测试源码 | **无** —— 本分支上该模块不含 `src/test/java` | 模块目录树 |
| AWS SDK | 完全不是依赖（仅使用元数据端点） | `microsphere-multiactive-aws/pom.xml` |
| Jackson | `jackson-databind`，可选，版本由 `microsphere-spring-cloud-dependencies:0.2.26` 管理 —— 未在本仓库锁定 | `microsphere-multiactive-aws/pom.xml`、`microsphere-multiactive-parent/pom.xml` |
| Spring Boot / Spring Cloud | 版本如上由外部管理；兼容范围 `2022.0.x` – `2025.0.x` | 根 `README.md`、CI profile |

`jackson-databind` 是可选依赖，因此**两个 ECS 定位器的 JSON 解析需要你的 classpath 上有 Jackson**。
Spring Boot Web 应用通常自带；纯命令行服务可能没有。

## 注册元数据

整个模块通过一个文件接入 Boot —— `META-INF/spring.factories`，以 `ZoneLocator` SPI 接口为 key：

```
io.microsphere.multiple.active.zone.spring.ZoneLocator=\
io.microsphere.multiple.active.zone.spring.aws.EcsContainerMetadataFileZoneLocator,\
io.microsphere.multiple.active.zone.spring.aws.EcsTaskMetadataEndpointV4ZoneLocator,\
io.microsphere.multiple.active.zone.spring.aws.Ec2AvailabilityZoneEndpointZoneLocator
```

[Spring Boot 模块](microsphere-multiactive-spring-boot.md)中的 `ZoneAutoConfiguration` 会加载这些 factory 条目，
并与所有 `ZoneLocator` bean 一起折叠进 `CompositeZoneLocator`，随后按 `order` 排序。因此只要把这个 jar 放进
classpath 就够了 —— 本模块没有自动配置类，也不需要声明任何 `@Bean`。

## API 参考

定位链按 `order` 从小到大执行，并在第一个非 `null` 可用区处停止：

| 类 | `DEFAULT_ORDER` | 生效条件（`supports`） | 可用区来源 |
|-------|-----------------|-----------------------------|-------------|
| `EcsContainerMetadataFileZoneLocator` | `5` | `environment.containsProperty("ECS_CONTAINER_METADATA_FILE")` | JSON 元数据文件的 `AvailabilityZone` 字段 |
| `EcsTaskMetadataEndpointV4ZoneLocator` | `10` | `environment.containsProperty("ECS_CONTAINER_METADATA_URI_V4")` | `GET <uri>/task` 响应中的 `AvailabilityZone` 字段 |
| `Ec2AvailabilityZoneEndpointZoneLocator` | `15` | 恒为 `true` | `GET` 可用区端点 URI 的响应正文 |
| `DefaultZoneLocator` | `20` | 恒为 `true` | `microsphere.availability.zone`，否则为 `defaultZone` |

### `EcsContainerMetadataFileZoneLocator`

```java
public class EcsContainerMetadataFileZoneLocator extends AbstractZoneLocator {
    public static final String METADATA_FILE_ENV_NAME = "ECS_CONTAINER_METADATA_FILE";
    public static final int DEFAULT_ORDER = 5;
}
```

读取该环境变量指定的文件，按 JSON 解析成 `Map`，返回 `String.valueOf(map.get("AvailabilityZone"))`。
所有失败路径都不致命且被记录：文件不可读 ⇒ `warn`；解析/IO 异常 ⇒ `error`；变量为空白 ⇒ `trace`；
这些情况一律返回 `null`，从而让出给下一个定位器。它没有实现 `EnvironmentAware`，因此不受
`microsphere.availability.zone.locator.timeout` 影响。

### `EcsTaskMetadataEndpointV4ZoneLocator`

```java
public class EcsTaskMetadataEndpointV4ZoneLocator extends AbstractZoneLocator implements EnvironmentAware {
    public static final String METADATA_URI_V4_ENV_NAME = "ECS_CONTAINER_METADATA_URI_V4";
    public static final int DEFAULT_ORDER = 10;
}
```

通过 `HttpUtils.doGet(url, timeout)` 执行 `GET ${ECS_CONTAINER_METADATA_URI_V4}/task`，解析 JSON 并返回
`AvailabilityZone` 字段。`setEnvironment` 读取 `microsphere.availability.zone.locator.timeout`（默认 `3000`
毫秒），同时作为连接与读取超时。按该类的 Javadoc 所述，对于运行 agent ≥ 1.39.0 的 EC2 Linux 实例上的 ECS 任务、
以及 agent ≥ 1.54.0 的 Windows `awsvpc` 任务，该变量默认会被注入容器。

### `Ec2AvailabilityZoneEndpointZoneLocator`

```java
public class Ec2AvailabilityZoneEndpointZoneLocator extends AbstractZoneLocator implements EnvironmentAware {
    public static final String AVAILABILITY_ZONE_ENDPOINT_URI_PROPERTY_NAME = "EC2_AVAILABILITY_ZONE_ENDPOINT_URI";
    public static final String DEFAULT_AVAILABILITY_ZONE_ENDPOINT_URI =
            "http://169.254.169.254/latest/meta-data/placement/availability-zone";
    public static final int DEFAULT_ORDER = 15;
}
```

URI 本身是可配置的，解析方式为
`environment.getProperty(AVAILABILITY_ZONE_ENDPOINT_URI_PROPERTY_NAME, DEFAULT_AVAILABILITY_ZONE_ENDPOINT_URI)` ——
因此它可以来自环境变量，*也可以*来自任意 Spring 属性（抽象是 `Environment`，而不是只有 OS 环境）。
响应正文原样作为可用区返回；`microsphere.availability.zone.locator.timeout` 同样生效。

**需要预先考虑的行为：**`supports` 无条件返回 `true`，所以在非 EC2 主机上这个定位器*仍然会*尝试访问
`http://169.254.169.254/...`（order 15，先于默认定位器），并等待到超时。异常被捕获并记为 `error`，链会继续，
因此后果是启动变慢而不是崩溃。如果你从不跑在 EC2 上，请把 `EC2_AVAILABILITY_ZONE_ENDPOINT_URI` 指向某个本地地址、
调小 timeout，或把该定位器的 `order` 提到 `DefaultZoneLocator` 之上。

## 配置属性

| key | 类型 / 默认值 | 使用者 | 效果 |
|-----|----------------|---------|--------|
| `ECS_CONTAINER_METADATA_FILE` | 环境变量，未设置 | `EcsContainerMetadataFileZoneLocator` | 存在即启用该定位器；取值是元数据文件路径 |
| `ECS_CONTAINER_METADATA_URI_V4` | 环境变量，未设置 | `EcsTaskMetadataEndpointV4ZoneLocator` | 存在即启用该定位器；请求时会追加 `/task` |
| `EC2_AVAILABILITY_ZONE_ENDPOINT_URI` | 属性，默认上表的 IMDS 路径 | `Ec2AvailabilityZoneEndpointZoneLocator` | 被 GET 的 URI |
| `microsphere.availability.zone.locator.timeout` | int，`3000` | 两个 HTTP 定位器 | 连接 + 读取超时（毫秒） |
| `microsphere.availability.zone.locator.fast-fail` | boolean，`false` | `CompositeZoneLocator` | 定位器返回 `null` 或失败时抛异常而不是继续兜底 |
| `microsphere.availability.zone` | String，`defaultZone` | `DefaultZoneLocator` | 手工覆盖，链条的最后一环 |

## 示例

本分支上该模块没有自己的测试，因此下面的示例（a）来自定位器自身的 API，（b）改编自覆盖其 HTTP 基础的测试，
即 [microsphere-multiactive-commons.md](microsphere-multiactive-commons.md) 中的 `HttpUtilsTest`。

### 让 Boot 选择可用区

```java
// application on ECS with the v4 metadata endpoint:
// ECS_CONTAINER_METADATA_URI_V4=http://169.254.170.2 -> order 10 wins
@Autowired
private ZoneLocator zoneLocator;              // CompositeZoneLocator, bean name "zoneLocator"

String zone = zoneLocator.locate(environment); // the AWS availability zone, never the placeholder
// CompositeZoneLocator also publishes it as a system property:
assertEquals(zone, ZoneContext.getCurrentZone());
assertEquals(zone, System.getProperty(CURRENT_ZONE_PROPERTY_NAME));
```

`ZoneContext.getZone()` 才是路由真正比对的值，监听器用 `microsphere.availability.zone` 属性填充它；
只有当该属性被设置为 `originalZone` 回退标记时才会用到定位到的可用区 —— 见
[microsphere-multiactive-spring.md](microsphere-multiactive-spring.md)。

### 在本地验证元数据端点（`HttpUtilsTest` 的写法）

```java
String expectedContent = "hello zone";

// Start a local HTTP server
HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
int port = server.getAddress().getPort();
server.createContext("/zone", exchange -> {
    byte[] response = expectedContent.getBytes();
    exchange.sendResponseHeaders(200, response.length);
    try (OutputStream os = exchange.getResponseBody()) {
        os.write(response);
    }
});
server.start();
try {
    String url = "http://localhost:" + port + "/zone";
    String result = HttpUtils.doGet(url, 3000);
    assertEquals(expectedContent, result);
} finally {
    server.stop(0);
}
```

把 `EC2_AVAILABILITY_ZONE_ENDPOINT_URI` 指向这样的服务器，即可在非 EC2 环境验证
`Ec2AvailabilityZoneEndpointZoneLocator`：

```java
StandardEnvironment environment = new StandardEnvironment();
Map<String, Object> props = new HashMap<>();
props.put("EC2_AVAILABILITY_ZONE_ENDPOINT_URI", "http://localhost:" + port + "/zone");
environment.getPropertySources().addFirst(new MapPropertySource("aws-test", props));

Ec2AvailabilityZoneEndpointZoneLocator locator = new Ec2AvailabilityZoneEndpointZoneLocator();
locator.setEnvironment(environment);          // applies microsphere.availability.zone.locator.timeout
assertTrue(locator.supports(environment));    // always true
assertEquals("hello zone", locator.locate(environment));
```

### 不使用 Boot 时注册定位器

```java
List<ZoneLocator> locators = Arrays.asList(
        new EcsContainerMetadataFileZoneLocator(),   // order 5
        new EcsTaskMetadataEndpointV4ZoneLocator(),  // order 10
        new Ec2AvailabilityZoneEndpointZoneLocator(),// order 15
        new DefaultZoneLocator());                   // order 20
ZoneLocator composite = new CompositeZoneLocator(locators);  // sorts them for you
```

## 如何集成

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-aws</artifactId>
</dependency>

<!-- the module's own dependency on spring-cloud is optional; declare what you need -->
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-spring-boot</artifactId>
    <version>0.2.14-SNAPSHOT</version>
</dependency>
```

1. 添加 `microsphere-multiactive-aws`，以及你使用的 Multi-Active Spring/Boot/Cloud 模块（AWS jar 对
   `spring-cloud` 的依赖是 `optional`，因此不传递）。
2. 确保 `jackson-databind` 存在（两个 ECS 定位器需要）。
3. 其余什么都不用做：三个定位器通过 `META-INF/spring.factories` 到位，`ZoneAutoConfiguration` 会把它们折叠进
   `CompositeZoneLocator`。
4. 如果你要求“可用区必须是真实的、不能是 `defaultZone`”，请设置
   `microsphere.availability.zone.locator.fast-fail=true`。

## 相关文档

- [用户指南索引](README.md)
- [microsphere-multiactive-spring.md](microsphere-multiactive-spring.md) —— `ZoneLocator` 契约与 `order` 语义
- [microsphere-multiactive-spring-boot.md](microsphere-multiactive-spring-boot.md) —— 谁读取 `spring.factories`
- [microsphere-multiactive-spring-cloud.md](microsphere-multiactive-spring-cloud.md) —— 定位到的可用区随后被用于什么
- 根 [README](../../../../README.md) 与[发布说明](../../../../release-notes.md)
- 开发者指南：[../../developer-guide/zh/microsphere-multiactive-aws.md](../../developer-guide/zh/microsphere-multiactive-aws.md)

## English

English version: [English](../microsphere-multiactive-aws.md)
