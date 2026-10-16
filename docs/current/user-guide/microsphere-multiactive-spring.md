中文版本：[简体中文](zh/microsphere-multiactive-spring.md)

# microsphere-multiactive-spring

## Overview

`microsphere-multiactive-spring` adapts the framework-free zone model of
[commons](microsphere-multiactive-commons.md) to the Spring `Environment`. It introduces the one extension point that
matters — the `ZoneLocator` SPI — and the machinery that turns "a property changed somewhere" into "the `ZoneContext`
and every listener in the JVM know about it".

Three pieces:

1. **`ZoneLocator` / `AbstractZoneLocator` / `DefaultZoneLocator` / `CompositeZoneLocator`** — discover the zone from
   the Spring `Environment` (or from any deployment platform, in the AWS and Netflix modules).
2. **`ZoneContextChangedListener` + `ZoneContextChangedEvent`** — keep `ZoneContext` synchronised with external
   configuration (Spring Boot startup, Spring Cloud config refresh, plain context refresh) and announce the change as
   a Spring event.
3. **`ZoneUtils`** — the two well-known bean names the rest of the project relies on.

Package: `io.microsphere.multiple.active.zone.spring` (and `.spring.event`). Classes carry `@since 1.0.0`.

## Maven Coordinates

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-spring</artifactId>
    <version>0.2.14-SNAPSHOT</version>
</dependency>
```

- `groupId`: `io.github.microsphere-projects`
- `artifactId`: `microsphere-multiactive-spring`
- `version`: `${revision}` → `0.2.14-SNAPSHOT`; managed by
  [microsphere-multiactive-dependencies](microsphere-multiactive-dependencies.md)
- `packaging`: `jar`, `<name>`: `Microsphere :: Multi-Active :: Spring`

## Version Information

| Item | Value | Verified in |
|------|-------|-------------|
| Documentation line / branch | `current` / `release` | this doc set |
| Artifact version | `0.2.14-SNAPSHOT` | `<revision>` in the root `pom.xml` |
| JDK | 17 or later (CI matrix: 17, 21, 25) | `.github/workflows/maven-build.yml` |
| Multi-Active modules required | `microsphere-multiactive-commons` — **compile scope, not optional** | `microsphere-multiactive-spring/pom.xml` |
| Optional dependencies | `io.github.microsphere-projects:microsphere-spring-context`, `org.springframework:spring-context`, `io.github.microsphere-projects:microsphere-annotation-processor` | `microsphere-multiactive-spring/pom.xml` |
| Test dependencies | `junit-jupiter`, `org.springframework:spring-test`, `io.github.microsphere-projects:microsphere-spring-test`, `logback-classic` | `microsphere-multiactive-spring/pom.xml` |
| Spring Framework version | not pinned in this repository — resolved through `microsphere-multiactive-parent` → `microsphere-spring-cloud-dependencies:0.2.26` | `microsphere-multiactive-parent/pom.xml` |
| Spring Boot / Spring Cloud | not referenced: this module compiles against `spring-context` only (the listener resolves Boot/Cloud event classes reflectively at runtime) | sources |

Because `spring-context` is `<optional>true</optional>`, this module adds nothing Spring-related to your classpath; you
already have Spring. It does mean you must keep `microsphere-multiactive-commons` **and**
`microsphere-java-core` (commons' optional dependency) resolvable at runtime.

## API Reference

### `ZoneLocator` (interface)

```java
public interface ZoneLocator {
    boolean supports(Environment environment);   // false => locate() is never called for this environment
    String locate(Environment environment);      // the zone, or null when it cannot be located
}
```

`supports` is a cheap availability check ("am I running on ECS? is the metadata endpoint configured?"), `locate` does
the discovery. Returning `null` means "unknown" and lets the next locator try; throwing is also tolerated unless fast
fail is on.

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

Order is what makes a chain deterministic: `CompositeZoneLocator` sorts its delegates with
`AnnotationAwareOrderComparator`, so **lower order = tried earlier**. The published constants are:

| Locator | `DEFAULT_ORDER` | Module |
|---------|-----------------|--------|
| `EcsContainerMetadataFileZoneLocator` | `5` | aws |
| `EcsTaskMetadataEndpointV4ZoneLocator` | `10` | aws |
| `Ec2AvailabilityZoneEndpointZoneLocator` | `15` | aws |
| `DefaultZoneLocator` | `20` | spring |

`toString()` renders `ZoneLocator{beanName='...', order=20}` — the string the composite logs when a locator fails, so
giving locators bean names makes logs readable.

### `DefaultZoneLocator`

The always-available fallback:

```java
public class DefaultZoneLocator extends AbstractZoneLocator {
    public static final int DEFAULT_ORDER = 20;
    public boolean supports(Environment environment)   // always true
    public String locate(Environment environment)      // environment.getProperty("microsphere.availability.zone"), else "defaultZone"
}
```

It never returns `null` and never throws: when `microsphere.availability.zone` is absent or blank it logs at `trace`
and returns `ZoneConstants.DEFAULT_ZONE` (`defaultZone`), which in turn makes `ZonePreferenceFilter` a no-op. This is
the locator registered in `META-INF/spring.factories` by the
[Spring Boot module](microsphere-multiactive-spring-boot.md).

### `CompositeZoneLocator`

```java
public class CompositeZoneLocator implements ZoneLocator {
    public CompositeZoneLocator(List<ZoneLocator> zoneLocators)   // throws IllegalArgumentException on null
    public boolean supports(Environment environment)              // true if ANY delegate supports
    public String locate(Environment environment)
}
```

Semantics, all asserted by `CompositeZoneLocatorTest`:

- The delegate list is copied and sorted by order in the constructor.
- `locate()` short-circuits on the first delegate that (a) supports the environment and (b) returns a non-`null` zone.
- The result is **cached in the instance**: the second `locate()` call returns the same zone without re-running the
  chain (`testLocateCachesResult`).
- Delegates that do not support the environment are skipped silently (`testLocateSkipsUnsupportedLocators`); delegates
  that return `null` are logged at `warn` and the chain continues.
- A delegate that throws is logged at `error` and the chain continues — so a broken AWS probe never prevents the
  `defaultZone` fallback (`testLocateWithExceptionNoFastFail`).
- With `microsphere.availability.zone.locator.fast-fail=true`, the first `null` result or the first exception aborts
  the chain and throws `IllegalStateException("The zone can't be located by " + failedZoneLocator)`.
- If nobody locates a zone and fast fail is off, `locate()` returns `null` and logs a warning. An empty delegate list
  behaves exactly like that.
- On success, the located zone is published as the **system property** `microsphere.current.availability.zone`, which
  is what `ZoneContext.getCurrentZone()` reads. Note the cache: because of it, the system property is only written
  when `locate()` actually re-runs the chain.

### `ZoneUtils`

```java
public abstract class ZoneUtils {
    public static final String ZONE_CONTEXT_BEAN_NAME = "zoneContext";
    public static final String ZONE_LOCATOR_BEAN_NAME = "zoneLocator";

    public static ZoneContext getZoneContext(ConfigurableListableBeanFactory beanFactory)
    public static ZoneLocator getZoneLocator(ConfigurableListableBeanFactory beanFactory)
}
```

Lookup helpers for code that only has a bean factory (custom `BeanFactoryPostProcessor`s, initialisers). They
`getBean(name, type)` — so the beans must be registered under exactly those names. `ZoneAutoConfiguration` in the
Spring Boot module does that, which is why `zoneContext` and `zoneLocator` are the names to use if you register the
beans by hand.

### `ZoneContextChangedEvent`

```java
public class ZoneContextChangedEvent extends ApplicationContextEvent {
    public ZoneContextChangedEvent(ApplicationContext context, ZoneContext zoneContext,
                                   List<PropertyChangeEvent> propertyChangeEvents)
    public ZoneContext getZoneContext()
    public List<PropertyChangeEvent> getPropertyChangeEvents()   // unmodifiable, source-preserving
}
```

Published once per batch of actual changes. `getSource()` and `getApplicationContext()` both return the context that
published it (`ZoneContextChangedEventTest`). Listen to this event if you need to re-read the zone after a config
refresh — it is the only place where "what exactly changed, from what to what" is exposed as a Spring event.

### `ZoneContextChangedListener`

```java
public class ZoneContextChangedListener implements SmartApplicationListener, ApplicationContextAware, EnvironmentAware
```

The bridge from configuration to `ZoneContext`. It:

- reacts to one of three event types, chosen from what is on the classpath:
  `org.springframework.boot.context.event.ApplicationStartedEvent` (Spring Boot), and additionally
  `org.springframework.cloud.context.environment.EnvironmentChangeEvent` (Spring Cloud, i.e. `/actuator/refresh`);
  otherwise plain `ContextRefreshedEvent`. Both Boot/Cloud classes are resolved reflectively, so the module compiles
  without them.
- writes these seven properties into the context, in this mapping:

| Property | Applied to |
|----------|-----------|
| `microsphere.availability.zone` | `zoneContext.setZone(...)`, with the `originalZone` special case below |
| `microsphere.availability.zone.enabled` | `setEnabled(boolean)` |
| `microsphere.availability.zone.preference.enabled` | `setPreferenceEnabled(boolean)` |
| `microsphere.availability.zone.preference.filter.order` | `setPreferenceFilterOrder(int)` |
| `microsphere.availability.zone.preference.upstream.zone-ready-percentage` | `setPreferenceUpstreamZoneReadyPercentage(int)` |
| `microsphere.availability.zone.preference.upstream.same-zone-min-available` | `setPreferenceUpstreamSameZoneMinAvailable(int)` |
| `microsphere.availability.zone.preference.upstream.disabled-zone` | `setPreferenceUpstreamDisabledZone(String)` |

- **never overwrites with a missing property**, except for the boolean/int values: an absent property falls back to the
  documented default (hence "deleted property ⇒ default restored"). For the zone itself an absent or blank value leaves
  the current zone untouched (`testChangeZoneWithEmptyStringPropertyDoesNotChangeZone`).
- supports `microsphere.availability.zone=originalZone`: the value `originalZone` means "throw away the override and
  ask the `ZoneLocator` chain again". If the locator does not support the environment, the zone reverts to
  `defaultZone` (`testRevertOriginalZoneWhenLocatorDoesNotSupport`).
- installs a temporary `PropertyChangeListener` around the batch so it can collect exactly the changes it caused, and
  publishes one `ZoneContextChangedEvent` if — and only if — the list is non-empty.
- requires a `ZoneContext` bean and a `ZoneLocator` bean in the context (`setApplicationContext` looks both up).

The list `ZONE_CONTEXT_PROPERTY_NAMES` (seven entries) and the handler map are `protected`, so subclasses can extend
the set of synchronised properties.

## Examples

Adapted from `src/test/java` of this module.

### Locating a zone from a property (`DefaultZoneLocatorTest`)

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

### Building a chain (`CompositeZoneLocatorTest`)

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

Failure tolerance and fast fail:

```java
// a throwing locator is skipped, the next one still provides the zone
CompositeZoneLocator resilient = new CompositeZoneLocator(
        Arrays.asList(throwingLocator(), fixedLocator("zone-fallback")));
assertEquals("zone-fallback", resilient.locate(newEnvironment()));

// same chain, but fast-fail enabled -> IllegalStateException
CompositeZoneLocator strict = new CompositeZoneLocator(Collections.singletonList(nullLocator()));
assertThrows(IllegalStateException.class, () -> strict.locate(newFastFailEnvironment()));
```

where `newFastFailEnvironment()` is a `StandardEnvironment` with
`microsphere.availability.zone.locator.fast-fail=true` added as the first property source.

### Bootstrapping a plain Spring context (`ZoneContextChangedListenerTest`)

With no Spring Boot involved, register the two well-known beans, add the listener, and refresh:

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

Each documented property is applied the same way — for example these assertions come straight from the test class:

```java
props.put(ZONE_ENABLED_PROPERTY_NAME, "false");                              // -> isEnabled() == false
props.put(PREFERENCE_ENABLED_PROPERTY_NAME, "true");                         // -> isPreferenceEnabled() == true
props.put(PREFERENCE_FILTER_ORDER_PROPERTY_NAME, "42");                      // -> getPreferenceFilterOrder() == 42
props.put(PREFERENCE_UPSTREAM_ZONE_READY_PERCENTAGE_PROPERTY_NAME, "80");    // -> 80
props.put(PREFERENCE_UPSTREAM_SAME_ZONE_MIN_AVAILABLE_PROPERTY_NAME, "3");   // -> 3
props.put(PREFERENCE_UPSTREAM_DISABLED_ZONE_PROPERTY_NAME, "zone-d");        // -> "zone-d"
```

Reacting to a change:

```java
@EventListener
public void onZoneChanged(ZoneContextChangedEvent event) {
    for (PropertyChangeEvent pce : event.getPropertyChangeEvents()) {
        log.warn("zone property '{}' : {} -> {}", pce.getPropertyName(), pce.getOldValue(), pce.getNewValue());
    }
}
```

### Looking beans up (`ZoneUtilsTest`)

```java
StaticApplicationContext context = new StaticApplicationContext();
ZoneContext zoneContext = ZoneContext.get();
context.getBeanFactory().registerSingleton(ZoneUtils.ZONE_CONTEXT_BEAN_NAME, zoneContext);
assertSame(zoneContext, ZoneUtils.getZoneContext(context.getBeanFactory()));
```

## How to Integrate

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-spring</artifactId>
    <version>0.2.14-SNAPSHOT</version>
</dependency>
```

Then, in a non-Boot Spring application, declare three beans — the context, the locator chain, and the listener — and
set `microsphere.availability.zone` in your `Environment`:

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

In a Spring Boot application, all of this is done for you by `ZoneAutoConfiguration`; prefer
[microsphere-multiactive-spring-boot.md](microsphere-multiactive-spring-boot.md).

## Related Documentation

- [User guide index](README.md)
- [microsphere-multiactive-commons.md](microsphere-multiactive-commons.md) — what the listener writes into
- [microsphere-multiactive-aws.md](microsphere-multiactive-aws.md) — the platform `ZoneLocator` implementations
- [microsphere-multiactive-spring-boot.md](microsphere-multiactive-spring-boot.md) — auto-configuration of these beans
- Root [README](../../../README.md) and [release notes](../../../release-notes.md)
- Developer guide: [../developer-guide/microsphere-multiactive-spring.md](../developer-guide/microsphere-multiactive-spring.md)

## 中文

中文版本：[简体中文](zh/microsphere-multiactive-spring.md)
