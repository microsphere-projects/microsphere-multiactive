中文版本：[简体中文](zh/microsphere-multiactive-spring-boot.md)

# microsphere-multiactive-spring-boot

## Overview

`microsphere-multiactive-spring-boot` activates the zone machinery automatically. It contains three classes:
one auto-configuration (`ZoneAutoConfiguration`) and two composed condition annotations
(`@ConditionalOnAvailabilityZoneAvailable`, `@ConditionalOnAvailabilityZoneEnabled`). It is registered through both
Spring Boot auto-configuration mechanisms and additionally contributes `DefaultZoneLocator` as a `ZoneLocator`
`spring.factories` entry, so zone discovery works without any application code.

On this line there is **no** `microsphere-multiactive-spring-boot-starter` module; you depend on this jar directly.

## Maven Coordinates

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-spring-boot</artifactId>
    <version>0.1.14-SNAPSHOT</version>
</dependency>
```

- `groupId`: `io.github.microsphere-projects`
- `artifactId`: `microsphere-multiactive-spring-boot`
- `version`: `${revision}` → `0.1.14-SNAPSHOT` in this snapshot; newest published release `0.1.13`
- `packaging`: `jar`
- `<name>`: `Microsphere :: Multi-Active :: Spring Boot`
- Managed by [the BOM](microsphere-multiactive-dependencies.md): **no** — the BOM of this line lists
  `microsphere-multiactive-spring-boot-starter` instead, so the version of this artifact must be declared explicitly.

## Version Information

| Item | Value | Verified in |
|------|-------|-------------|
| Documentation line / Git branch | `1.x` / `release-1.x` | this doc set |
| Artifact version | `0.1.14-SNAPSHOT` | root `pom.xml` `<revision>` |
| Compile-scope dependency | `io.github.microsphere-projects:microsphere-multiactive-spring:${revision}` (brings `commons`) | `microsphere-multiactive-spring-boot/pom.xml` |
| Optional dependencies | `microsphere-annotation-processor`, `microsphere-spring-boot-core`, `spring-boot-starter`, `spring-boot-starter-actuator`, `spring-boot-starter-web`, `spring-boot-configuration-processor`, `spring-boot-autoconfigure-processor` | `microsphere-multiactive-spring-boot/pom.xml` |
| Test dependencies | `junit-jupiter`, `spring-boot-starter-test`, `io.github.microsphere-projects:microsphere-spring-boot-test`, `logback-classic` | `microsphere-multiactive-spring-boot/pom.xml` |
| JDK | no compiler level declared in this repository; CI tests this module on JDK 8, 11, 17, 21, 25 | reactor POMs, `.github/workflows/maven-build.yml` |
| Spring Boot compatibility | Boot 2.x only, selected per Maven profile: `spring-cloud-hoxton` → `2.3.12.RELEASE`, `spring-cloud-2020` → `2.4.13`, `spring-cloud-2021` → `2.6.15` (default). Spring Boot 3.x is not part of this line's matrix | CI profiles in `.github/workflows/maven-build.yml`; versions defined by the external `microsphere-spring-cloud-parent:0.1.26` POM |
| Module requirements | a Spring Boot `ApplicationContext`; the two condition annotations require the `commons` and `spring` jars to be present | sources of this module |

> **Runtime prerequisites.** `ZoneAutoConfiguration` calls
> `io.microsphere.spring.core.io.support.SpringFactoriesLoaderUtils.loadFactories(...)` to collect `ZoneLocator`
> factories. That type comes from the Microsphere Spring libraries, which this POM declares optional
> (`microsphere-spring-boot-core`); optional dependencies are not transitive, so the provider must be on your runtime
> classpath. Likewise `microsphere-java-core` (used by `commons` and `spring`) is required at runtime. In practice: if
> you add only this jar to a Spring Boot application and see `NoClassDefFoundError` for an `io.microsphere.spring.*` or
> `io.microsphere.util.*` type, add the corresponding Microsphere artifact explicitly.

## Auto-Configuration Registration

Four resource entries ship in this module:

| Resource | Content | Effect |
|----------|---------|--------|
| `META-INF/spring.factories` → key `org.springframework.boot.autoconfigure.EnableAutoConfiguration` | `io.microsphere.multiple.active.zone.spring.boot.autoconfigure.ZoneAutoConfiguration` | registers the auto-configuration for Spring Boot 2.x (the mechanism used by every Boot version this line supports) |
| `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` | `io.microsphere.multiple.active.zone.spring.boot.autoconfigure.ZoneAutoConfiguration` | the newer registration file; ignored by Boot 2.3–2.6, used from Boot 2.7 onwards |
| `META-INF/spring.factories` → key `io.microsphere.multiple.active.zone.spring.ZoneLocator` | `io.microsphere.multiple.active.zone.spring.DefaultZoneLocator` | makes `DefaultZoneLocator` available to `CompositeZoneLocator` even when no bean of it exists |
| `META-INF/spring.factories` → key `io.microsphere.multiple.active.zone.spring.ZoneLocator` (aws module, same key) | the three AWS locators | see [microsphere-multiactive-aws.md](microsphere-multiactive-aws.md) |

This module contains **no** `spring-configuration-metadata.json`, so on this line IDEs offer no completion for the
`microsphere.availability.zone.*` keys. The authoritative key names and defaults are listed in
[microsphere-multiactive-commons.md](microsphere-multiactive-commons.md).

## Public API

### `ZoneAutoConfiguration` (`class`, package `io.microsphere.multiple.active.zone.spring.boot.autoconfigure`)

Declared annotations, exactly as in the source:

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnAvailabilityZoneAvailable
@Import(value = {ZoneContextChangedListener.class})
public class ZoneAutoConfiguration {
}
```

| Bean | Method / name | Conditions | Value |
|------|---------------|------------|-------|
| `ZoneContext` | `zoneContext()`, bean name `zoneContext` (`ZoneUtils.ZONE_CONTEXT_BEAN_NAME`), `@ConditionalOnMissingBean` | class + enabled conditions of the configuration | returns the shared `ZoneContext.get()` singleton |
| `CompositeZoneLocator` | `zoneLocator(Collection<ZoneLocator>, ConfigurableApplicationContext)`, bean name `zoneLocator` (`ZoneUtils.ZONE_LOCATOR_BEAN_NAME`), `@Primary`, `@ConditionalOnMissingBean` | same | builds the delegate list from (1) all `ZoneLocator` beans in the context and (2) all `ZoneLocator` entries under the `spring.factories` key, then sorts them with `AnnotationAwareOrderComparator` and wraps them in a `CompositeZoneLocator` |

`ZoneContextChangedListener` is imported (not `@Bean`-declared), so it participates as a `SmartApplicationListener` and
refreshes the `ZoneContext` on `ApplicationStartedEvent` — plus `EnvironmentChangeEvent` when Spring Cloud Context is
present. See [microsphere-multiactive-spring.md](microsphere-multiactive-spring.md) for the trigger matrix.

`@Primary` on the locator means a by-type `ZoneLocator` injection resolves to the composite rather than to one of the
individual locator beans you may have registered.

Because both beans are `@ConditionalOnMissingBean`, you can replace them: declare your own bean named `zoneContext` or
`zoneLocator` (or of the same type) and the auto-configured one is skipped.

### `@ConditionalOnAvailabilityZoneAvailable` (`annotation`, package `...spring.boot.condition`)

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

Applies on types and methods. It combines "both core classes present" with "the feature is enabled", so it is the
condition to reuse on your own zone-aware configuration.

### `@ConditionalOnAvailabilityZoneEnabled` (`annotation`, package `...spring.boot.condition`)

```java
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Documented
@ConditionalOnProperty(name = ZONE_ENABLED_PROPERTY_NAME, matchIfMissing = true)
public @interface ConditionalOnAvailabilityZoneEnabled {
}
```

Key: `microsphere.availability.zone.enabled`. `matchIfMissing = true` — the feature is **on** unless you set the
property to `false` (Spring's `@ConditionalOnProperty` with no `havingValue` treats any value other than `false` as a
match).

## Configuration Properties Used by This Module

| Property | Default | Effect |
|----------|---------|--------|
| `microsphere.availability.zone.enabled` | `true` | switches the whole auto-configuration off when `false` |
| `microsphere.availability.zone` | `defaultZone` | read by `DefaultZoneLocator` (registered automatically) and by the imported `ZoneContextChangedListener` |
| `microsphere.availability.zone.preference.enabled` | `false` | pushed into `ZoneContext` by the imported listener |
| `microsphere.availability.zone.preference.filter.order` | `10` | pushed into `ZoneContext` by the imported listener |
| `microsphere.availability.zone.preference.upstream.zone-ready-percentage` | `100` | pushed into `ZoneContext` by the imported listener |
| `microsphere.availability.zone.preference.upstream.same-zone-min-available` | `5` | pushed into `ZoneContext` by the imported listener |
| `microsphere.availability.zone.preference.upstream.disabled-zone` | unset | pushed into `ZoneContext` by the imported listener |
| `microsphere.availability.zone.locator.fast-fail` | `false` | read lazily by `CompositeZoneLocator.locate(...)` |
| `microsphere.availability.zone.locator.timeout` | `3000` | read by the AWS locators through `EnvironmentAware` |

## Examples

### Verifying the auto-configuration (`ZoneAutoConfigurationTest`)

This snapshot tests the configuration with `AutoConfigurationTest<ZoneAutoConfiguration>` from
`microsphere-spring-boot-test`. The contract it encodes is worth reading as documentation: the configuration applies
when the classes are present and the property is on, and it backs off when either the property is `false` or one of the
two core classes is missing.

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

The two `autoConfiguredClasses` entries are exactly the beans `ZoneAutoConfiguration` produces, which confirms the
expected outcome: with the library on the classpath and the property unset, a `ZoneContext` bean and a
`CompositeZoneLocator` bean exist.

### Replacing the auto-configured context

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

Declaring this bean suppresses `ZoneAutoConfiguration#zoneContext` (`@ConditionalOnMissingBean`); `zoneLocator` is
still created.

### Adding your own locator

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

Order `1` places it before the AWS locators (5, 10, 15) and `DefaultZoneLocator` (20), and because
`ZoneAutoConfiguration#zoneLocator` injects `Collection<ZoneLocator>`, it is added to the composite automatically — no
`spring.factories` entry needed. Registering it under the `io.microsphere.multiple.active.zone.spring.ZoneLocator`
`spring.factories` key is the alternative for classes that are not Spring beans.

## Integration

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-spring-boot</artifactId>
    <version>0.1.13</version>
</dependency>
```

The version is mandatory: [the 1.x BOM](microsphere-multiactive-dependencies.md) does not manage this artifactId.

Minimal setup — no `@Enable...` annotation exists on this line; adding the jar and one property is enough:

```properties
microsphere.availability.zone=zone-a
microsphere.availability.zone.preference.enabled=true
```

Then inject the locator or the context anywhere:

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

Set `microsphere.availability.zone.enabled=false` to remove both beans and revert to stock behaviour.

## Related Documentation

- [User guide index](README.md)
- Upstream: [commons](microsphere-multiactive-commons.md), [spring](microsphere-multiactive-spring.md)
- Downstream: [spring-cloud](microsphere-multiactive-spring-cloud.md), [aws](microsphere-multiactive-aws.md),
  [netflix](microsphere-multiactive-netflix.md)
- Root [README](../../../README.md) and [release notes](../../../release-notes.md)
- Developer guide: [../developer-guide/microsphere-multiactive-spring-boot.md](../developer-guide/microsphere-multiactive-spring-boot.md)

## 中文

中文版本：[简体中文](zh/microsphere-multiactive-spring-boot.md)
