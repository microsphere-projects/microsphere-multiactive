中文版本：[简体中文](zh/microsphere-multiactive-spring-boot.md)

# microsphere-multiactive-spring-boot

## Overview

`microsphere-multiactive-spring-boot` turns the zone model into a Spring Boot feature: add the jar, and a
`ZoneContext` bean plus a `CompositeZoneLocator` bean appear, are populated from `Environment` properties at
application start, and are refreshed whenever the environment changes — with zero wiring code.

It contains exactly three classes:

| Class | Role |
|-------|------|
| `ZoneAutoConfiguration` | registers `zoneContext` and `zoneLocator`, imports the `ZoneContextChangedListener` |
| `ConditionalOnAvailabilityZoneAvailable` | "zone classes present **and** zone feature enabled" |
| `ConditionalOnAvailabilityZoneEnabled` | "property `microsphere.availability.zone.enabled` is not `false`" |

Packages: `io.microsphere.multiple.active.zone.spring.boot.autoconfigure` and `...spring.boot.condition`.

## Maven Coordinates

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-spring-boot</artifactId>
    <version>0.2.14-SNAPSHOT</version>
</dependency>
```

- `groupId`: `io.github.microsphere-projects`
- `artifactId`: `microsphere-multiactive-spring-boot`
- `version`: `${revision}` → `0.2.14-SNAPSHOT`
- `packaging`: `jar`, `<name>`: `Microsphere :: Multi-Active :: Spring Boot`

> Note the artifact name: on this documentation line there is **no** `microsphere-multiactive-spring-boot-starter`
> module, even though the BOM reserves that coordinate. The jar you depend on is `microsphere-multiactive-spring-boot`,
> and because the BOM does not list it, its version must be written explicitly. See
> [microsphere-multiactive-dependencies.md](microsphere-multiactive-dependencies.md).

## Version Information

| Item | Value | Verified in |
|------|-------|-------------|
| Documentation line / branch | `current` / `release` | this doc set |
| Artifact version | `0.2.14-SNAPSHOT` | `<revision>` in the root `pom.xml` |
| JDK | 17 or later (CI matrix: 17, 21, 25) | `.github/workflows/maven-build.yml` |
| Multi-Active modules required | `microsphere-multiactive-spring` — **compile scope, not optional** (which itself requires `commons`) | `microsphere-multiactive-spring-boot/pom.xml` |
| Optional dependencies | `microsphere-spring-boot-core`, `spring-boot-starter`, `spring-boot-starter-actuator`, `spring-boot-starter-web`, `spring-boot-configuration-processor`, `spring-boot-autoconfigure-processor`, `microsphere-annotation-processor` | `microsphere-multiactive-spring-boot/pom.xml` |
| Test dependencies | `junit-jupiter`, `spring-boot-starter-test`, `microsphere-spring-boot-test`, `logback-classic` | `microsphere-multiactive-spring-boot/pom.xml` |
| Spring Boot version | not pinned in this repository; managed transitively by `microsphere-spring-cloud-dependencies:0.2.26` via `microsphere-multiactive-parent` | `microsphere-multiactive-parent/pom.xml` |
| Spring Cloud compatibility | `2022.0.x` – `2025.0.x` | root `README.md`, CI profiles |
| Netflix / AWS | not referenced by this module | sources |

All Spring Boot artifacts are `optional`, so this jar does not force a Boot version on you; it does require
`spring-boot-autoconfigure` and `spring-context` at runtime, which any Boot application already has.

## Registration Metadata

`src/main/resources/META-INF` holds two files and nothing else:

`org.springframework.boot.autoconfigure.AutoConfiguration.imports`

```
io.microsphere.multiple.active.zone.spring.boot.autoconfigure.ZoneAutoConfiguration
```

`spring.factories`

```
io.microsphere.multiple.active.zone.spring.ZoneLocator=\
io.microsphere.multiple.active.zone.spring.DefaultZoneLocator
```

The second one is the important detail: `DefaultZoneLocator` is contributed as a **`SpringFactoriesLoader` entry**, not
as a `@Bean`. That is why a Boot application gets a working (if placeholder-valued) locator chain simply by having
this jar on the classpath, and why downstream modules such as
[aws](microsphere-multiactive-aws.md) register their locators the same way.

No `spring-configuration-metadata.json` is checked in; IDE hints are produced at build time by
`spring-boot-configuration-processor` plus `microsphere-annotation-processor` from the `@ConfigurationProperty`
annotations on `ZoneConstants`.

## API Reference

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

- `zoneContext()` returns `ZoneContext.get()` — the JVM-wide singleton from commons, *not* a new object. Overriding it
  with your own bean is possible (`@ConditionalOnMissingBean`), but the singleton is still what commons code reads.
- `zoneLocator(...)` assembles the chain from **both** sources: every `ZoneLocator` bean in the context *plus* every
  `ZoneLocator` listed under the `io.microsphere.multiple.active.zone.spring.ZoneLocator` key in
  `META-INF/spring.factories` (`loadFactories`). The union is sorted with `AnnotationAwareOrderComparator` and wrapped
  in a `CompositeZoneLocator`, marked `@Primary` so injection by type is unambiguous even when several individual
  locators are present.
- `@Import(ZoneContextChangedListener.class)` installs the listener that copies environment properties into the
  context and publishes `ZoneContextChangedEvent`.
- Both beans are `@ConditionalOnMissingBean`, so a single `@Bean` of your own is enough to replace either.

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

Two requirements ANDed: the zone classes must be loadable (so the auto-configuration goes quiet if you exclude
`commons` or `spring`), and the feature must be enabled. Usable on types and methods — the AWS and Spring Cloud
auto-configurations in this project reuse it.

### `ConditionalOnAvailabilityZoneEnabled`

```java
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Documented
@ConditionalOnProperty(name = ZONE_ENABLED_PROPERTY_NAME, matchIfMissing = true)
public @interface ConditionalOnAvailabilityZoneEnabled
```

Expands to `@ConditionalOnProperty(name = "microsphere.availability.zone.enabled", matchIfMissing = true)`, i.e.
**the feature is on unless you set the property to `false`**. Only the literal `false` disables it; any other value
(including a missing one) keeps it on.

## Configuration Properties

This module defines no `@ConfigurationProperties` class. Everything is driven by the flat keys declared in
`ZoneConstants`, read by `ZoneContextChangedListener` (documented in
[microsphere-multiactive-spring.md](microsphere-multiactive-spring.md)):

| Property | Default | Effect in this module |
|----------|---------|-----------------------|
| `microsphere.availability.zone.enabled` | `true` | gates the auto-configuration itself, via `ConditionalOnAvailabilityZoneEnabled` |
| `microsphere.availability.zone` | `defaultZone` | becomes `ZoneContext.getZone()`; `originalZone` reverts to the located zone |
| `microsphere.availability.zone.preference.enabled` | `false` | turns same-zone filtering on |
| `microsphere.availability.zone.preference.filter.order` | `10` | `ZonePreferenceFilter.getOrder()` |
| `microsphere.availability.zone.preference.upstream.zone-ready-percentage` | `100` | filtering safety valve 1 |
| `microsphere.availability.zone.preference.upstream.same-zone-min-available` | `5` | filtering safety valve 2 |
| `microsphere.availability.zone.preference.upstream.disabled-zone` | *none* | zones excluded from routing |
| `microsphere.availability.zone.locator.fast-fail` | `false` | abort `CompositeZoneLocator` on the first failure |
| `microsphere.availability.zone.locator.timeout` | `3000` | read/connect timeout used by the AWS locators |

Typical `application.yaml`:

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

## Examples

### What the auto-configuration contributes (`ZoneAutoConfigurationTest`)

The module's test is a declarative check that the beans appear, and that the two negative conditions behave. It extends
`AutoConfigurationTest<ZoneAutoConfiguration>` (from `microsphere-spring-boot-test`):

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

Reading it as documentation:

| Scenario | Expected result |
|----------|---------------|
| Default Boot application with this jar | `ZoneContext` and `CompositeZoneLocator` beans exist |
| `microsphere.availability.zone.enabled=false` | auto-configuration is skipped, no beans |
| `ZoneContext` or `ZoneLocator` not on the classpath | auto-configuration is skipped, no beans |

### Consuming the beans in an application

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

Because `zoneLocator` is a `CompositeZoneLocator` that already includes `DefaultZoneLocator` from
`spring.factories`, adding a platform locator is a matter of publishing one more bean (or one more
`ZoneLocator=` line in your own `META-INF/spring.factories`) with a lower `order`:

```java
@Bean
public ZoneLocator ecsZoneLocator() {
    return new EcsTaskMetadataEndpointV4ZoneLocator();   // order 10, wins over DefaultZoneLocator (20)
}
```

## How to Integrate

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-spring-boot</artifactId>
    <version>0.2.14-SNAPSHOT</version>
</dependency>
```

Minimal setup:

1. Add the dependency above (it drags in `spring` and `commons`).
2. Add `io.github.microsphere-projects:microsphere-java-core` explicitly. `commons` declares it as an optional
   dependency (and so does this module for `microsphere-spring-boot-core`), so it arrives nowhere transitively —
   `ZoneContext`, `ZonePreferenceFilter` and `HttpUtils` all import classes from it.
3. Set `microsphere.availability.zone` (or rely on a platform locator, see
   [aws](microsphere-multiactive-aws.md)).
4. Set `microsphere.availability.zone.preference.enabled=true` if you want same-zone routing; it is off by default.
5. Nothing else. Disable the whole feature with `microsphere.availability.zone.enabled=false`.

If you also want the zone written into your service-registration metadata and applied to load balancing, use
[microsphere-multiactive-spring-cloud.md](microsphere-multiactive-spring-cloud.md) instead — it depends on this module
transitively.

## Related Documentation

- [User guide index](README.md)
- [microsphere-multiactive-spring.md](microsphere-multiactive-spring.md) — `ZoneLocator`, the listener, the event
- [microsphere-multiactive-spring-cloud.md](microsphere-multiactive-spring-cloud.md) — the next auto-configuration up
- Root [README](../../../README.md) and [release notes](../../../release-notes.md)
- Developer guide: [../developer-guide/microsphere-multiactive-spring-boot.md](../developer-guide/microsphere-multiactive-spring-boot.md)

## 中文

中文版本：[简体中文](zh/microsphere-multiactive-spring-boot.md)
