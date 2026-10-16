中文版本：[简体中文](zh/microsphere-multiactive-netflix.md)

# microsphere-multiactive-netflix

## Overview

`microsphere-multiactive-netflix` brings the zone model to the Netflix OSS stack: Eureka for registration/discovery and
Ribbon for client-side load balancing. Where the Spring Cloud module hooks into Spring Cloud LoadBalancer, this module
hooks into the Ribbon interfaces (`ServerList`, `ServerListFilter`) and the Eureka client (`InstanceInfo`,
`PreRegistrationHandler`).

It provides five types, in three groups:

| Group | Types | Purpose |
|-------|-------|---------|
| Eureka | `EurekaInstanceInfoZoneResolver`, `ZoneAttachmentPreRegistrationHandler` | read the zone of a registered instance; write your own zone before registration |
| Ribbon | `RibbonServerZoneResolver`, `ZonePreferenceServerListFilter` | read the zone from a Ribbon `Server`; narrow a Ribbon server list to the same zone |
| Bridge | `DiscoveryClientServer`, `DiscoveryClientServerList` | expose Spring Cloud `DiscoveryClient` results to Ribbon *with* zones attached |

This module has **no auto-configuration**: you wire the beans yourself. It was introduced in `0.2.0`, replacing the
former `microsphere-multiactive-spring-cloud-netflix` module.

Packages: `io.microsphere.multiple.active.zone.netflix.eureka`, `.netflix.ribbon`, `.netflix.spring.cloud.ribbon`.

## Maven Coordinates

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-netflix</artifactId>
    <version>0.2.14-SNAPSHOT</version>
</dependency>
```

- `groupId`: `io.github.microsphere-projects`
- `artifactId`: `microsphere-multiactive-netflix`
- `version`: `${revision}` → `0.2.14-SNAPSHOT`
- `packaging`: `jar`, `<name>`: `Microsphere :: Multi-Active :: Netflix`
- The coordinate **is** managed by [microsphere-multiactive-dependencies](microsphere-multiactive-dependencies.md).

## Version Information

| Item | Value | Verified in |
|------|-------|-------------|
| Documentation line / branch | `current` / `release` | this doc set |
| Artifact version | `0.2.14-SNAPSHOT` | `<revision>` in the root `pom.xml` |
| JDK | 17 or later (CI matrix: 17, 21, 25) | `.github/workflows/maven-build.yml` |
| **Netflix Ribbon** | **`2.7.18`**, from the module property `netflix.ribbon.version`, applied to `com.netflix.ribbon:ribbon`, `ribbon-loadbalancer`, `ribbon-eureka` (all optional) | `microsphere-multiactive-netflix/pom.xml` |
| Netflix Eureka | `com.netflix.eureka:eureka-client` (optional, **no version in this repository** — managed by `microsphere-spring-cloud-dependencies:0.2.26`) and `org.springframework.cloud:spring-cloud-netflix-eureka-client` (optional, same) | `microsphere-multiactive-netflix/pom.xml`, `microsphere-multiactive-parent/pom.xml` |
| Multi-Active modules required | `microsphere-multiactive-spring-cloud` — declared **optional**, but it is what provides `CloudServerZoneResolver` (used by `DiscoveryClientServer`) and the whole `ZoneLocator`/`ZoneContext` chain down through `spring-boot` → `spring` → `commons`. Declare it (or the modules you need) yourself. | `microsphere-multiactive-netflix/pom.xml`, `DiscoveryClientServer.java` |
| Other optional dependencies | `spring-boot-starter`, `spring-boot-starter-actuator`, `spring-boot-starter-web`, `spring-boot-configuration-processor`, `spring-boot-autoconfigure-processor`, `spring-cloud-commons`, `spring-cloud-context`, `microsphere-annotation-processor` | `microsphere-multiactive-netflix/pom.xml` |
| Test dependencies | `junit-jupiter`, `spring-boot-starter-test`, `microsphere-spring-boot-test`, `logback-classic` | `microsphere-multiactive-netflix/pom.xml` |
| Test sources | **none** — no `src/test/java` on this branch | module tree |
| AWS | not referenced by this module | sources |

Ribbon is end-of-life Netflix OSS; this module exists for applications that still run it. New work should prefer the
Spring Cloud LoadBalancer path in [microsphere-multiactive-spring-cloud.md](microsphere-multiactive-spring-cloud.md).

## Feature Metadata

Instead of `AutoConfiguration.imports`, the module ships
`META-INF/config/default/features.yaml`, declaring its types as a named feature set
(`microsphere.spring.cloud.features.named`, the mechanism defined by `microsphere-spring-cloud-commons`):

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

The group name is the module's artifact id, so the feature set can be switched as a unit; nothing here registers a
Spring bean automatically.

## API Reference

### `EurekaInstanceInfoZoneResolver`

```java
public class EurekaInstanceInfoZoneResolver implements ZoneResolver<InstanceInfo> {
    public String resolve(InstanceInfo instanceInfo)
}
```

`instanceInfo.getMetadata().get("microsphere.availability.zone")`, returning `null` when the metadata map is `null`.
Note the contract of the argument: a `null` `InstanceInfo` is not tolerated (the method dereferences it immediately),
which is fine inside Ribbon/Eureka callbacks where the instance is always present.

### `ZoneAttachmentPreRegistrationHandler`

```java
public class ZoneAttachmentPreRegistrationHandler implements PreRegistrationHandler {
    public ZoneAttachmentPreRegistrationHandler(ApplicationInfoManager applicationInfoManager,
                                                ZoneAttachmentHandler zoneAttachmentHandler)
    public void beforeRegistration()
}
```

Implements `com.netflix.discovery.PreRegistrationHandler`. Before the Eureka client registers the instance, it takes
`applicationInfoManager.getInfo().getMetadata()` and calls `ZoneAttachmentHandler.attachZone(...)`, which writes your
`ZoneContext` zone under `microsphere.availability.zone`. Attach it to the discovery client through
`AbstractDiscoveryClientOptionalArgs` (the Javadoc references that type as the injection point) so the zone travels with
every registration — exactly the same effect `ZoneAttachmentListener` achieves for Spring Cloud registries.

### `RibbonServerZoneResolver`

```java
public class RibbonServerZoneResolver<T extends Server> implements ZoneResolver<T> {
    public String resolve(T server)   // server.getZone()
}
```

The simplest resolver in the project: it trusts the zone Ribbon already carries on `com.netflix.loadbalancer.Server`
(set, for example, by `DiscoveryClientServer`). Type parameter `<T extends Server>` lets one instance serve any Server
subclass.

### `ZonePreferenceServerListFilter`

```java
public class ZonePreferenceServerListFilter<T extends Server> implements ServerListFilter<T> {
    public ZonePreferenceServerListFilter(ZonePreferenceFilter<T> filter)
    public List<T> getFilteredListOfServers(List<T> servers)   // filter.filter(servers)
}
```

A thin adapter: Ribbon's `ServerListFilter` in, commons' `ZonePreferenceFilter` doing the work. Everything documented
for the filter applies — the enabled/preference flags, the `defaultZone` ignore rule, the disabled-zone exclusion, the
zone-ready percentage and the same-zone minimum are all evaluated by the delegate, and the delegate's thresholds come
from the live `ZoneContext`.

### `DiscoveryClientServer`

```java
public class DiscoveryClientServer extends Server {
    public DiscoveryClientServer(ServiceInstance serviceInstance)
    public String getId()             // serviceInstance.getInstanceId()
    public MetaInfo getMetaInfo()     // ServiceInstanceMetaInfo
}
```

Wraps a Spring Cloud `ServiceInstance` as a Ribbon `Server`:

- the super `Server(scheme, host, port)` is built from the instance;
- `setZone(...)` is called with `CloudServerZoneResolver.INSTANCE.resolve(serviceInstance)`, i.e. the zone is lifted out
  of the instance metadata (`microsphere.availability.zone`) — which is precisely what the Spring Cloud module writes
  during registration;
- the nested `final static class ServiceInstanceMetaInfo implements MetaInfo` maps `getAppName()`/
  `getServiceIdForDiscovery()` to `serviceInstance.getServiceId()`, `getInstanceId()` to
  `serviceInstance.getInstanceId()`, and `getServerGroup()` to `null`.

### `DiscoveryClientServerList`

```java
public class DiscoveryClientServerList implements ServerList<DiscoveryClientServer> {
    public DiscoveryClientServerList(DiscoveryClient discoveryClient, String serviceName)
    public List<DiscoveryClientServer> getInitialListOfServers()   // delegates
    public List<DiscoveryClientServer> getUpdatedListOfServers()    // discoveryClient.getInstances(serviceName).map(DiscoveryClientServer::new)
}
```

Feeds Ribbon from a Spring Cloud `DiscoveryClient` instead of a native Eureka `ApplicationInfoManager` list, so Ribbon
clients keep working against whatever registry Spring Cloud is configured with — while still carrying zones. Both
`ServerList` methods return the same live query result.

## Examples

There are no tests in this module on this branch, so the snippets below are assembled from the classes' own
signatures and from the commons documentation of the filter they delegate to.

### Zone-aware Ribbon filtering

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

`getOrder()` on the commons filter returns `zoneContext.getPreferenceFilterOrder()` (default `10`) if you need to order
this filter against other Ribbon filters in a compound filter.

### Ribbon over a Spring Cloud `DiscoveryClient`

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

The two cooperate: `DiscoveryClientServer` puts the zone on each `Server`, `RibbonServerZoneResolver` reads it back.

### Advertising your zone to Eureka

```java
@Bean
public ZoneAttachmentPreRegistrationHandler zoneAttachmentPreRegistrationHandler(
        ApplicationInfoManager applicationInfoManager, ZoneAttachmentHandler zoneAttachmentHandler) {
    return new ZoneAttachmentPreRegistrationHandler(applicationInfoManager, zoneAttachmentHandler);
}
```

`ZoneAttachmentHandler` is a plain class from [commons](microsphere-multiactive-commons.md); register it yourself (it
takes the `ZoneContext` bean) or get it from `ZoneCloudAutoConfiguration` in the Spring Cloud module, which
`@Import`s it.

### Reading a peer's zone

```java
ZoneResolver<InstanceInfo> resolver = new EurekaInstanceInfoZoneResolver();
String peerZone = resolver.resolve(instanceInfo);   // null when the peer never published a zone
```

## How to Integrate

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

1. Add `microsphere-multiactive-netflix` next to your Eureka client and Ribbon dependencies (both are `optional` in this
   POM, so they are not pulled transitively).
2. Keep `microsphere-multiactive-spring-boot`/`spring-cloud` on the classpath so `zoneContext` is auto-configured and
   populated.
3. Declare the beans you need from the snippets above — nothing is registered for you.
4. Set `microsphere.availability.zone.preference.enabled=true`; until then `ZonePreferenceServerListFilter` returns the
   list unchanged.

## Related Documentation

- [User guide index](README.md)
- [microsphere-multiactive-commons.md](microsphere-multiactive-commons.md) — `ZonePreferenceFilter`, `ZoneAttachmentHandler`
- [microsphere-multiactive-spring-cloud.md](microsphere-multiactive-spring-cloud.md) — `CloudServerZoneResolver`, the
  modern LoadBalancer path
- Root [README](../../../README.md) and [release notes](../../../release-notes.md)
- Developer guide: [../developer-guide/microsphere-multiactive-netflix.md](../developer-guide/microsphere-multiactive-netflix.md)

## 中文

中文版本：[简体中文](zh/microsphere-multiactive-netflix.md)
