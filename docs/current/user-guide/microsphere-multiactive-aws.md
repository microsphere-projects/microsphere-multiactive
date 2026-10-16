中文版本：[简体中文](zh/microsphere-multiactive-aws.md)

# microsphere-multiactive-aws

## Overview

`microsphere-multiactive-aws` discovers the availability zone of the *current* process from the AWS platform itself, so
that `ZoneContext` is populated with a real zone instead of the `defaultZone` placeholder. It implements three
`ZoneLocator`s, one per AWS deployment shape:

| Deployment | Source of truth | Class |
|------------|-----------------|-------|
| ECS task (container metadata file, agent ≥ 1.15.0) | local JSON file named by `ECS_CONTAINER_METADATA_FILE` | `EcsContainerMetadataFileZoneLocator` |
| ECS task on EC2 with `awsvpc`/bridge networking (task metadata endpoint v4) | HTTP GET `<ECS_CONTAINER_METADATA_URI_V4>/task` | `EcsTaskMetadataEndpointV4ZoneLocator` |
| EC2 instance | HTTP GET the EC2 instance-metadata availability-zone endpoint | `Ec2AvailabilityZoneEndpointZoneLocator` |

There is **no AWS SDK involved**: the module reads instance/task metadata over plain HTTP (or the local filesystem)
with `HttpUtils` from [commons](microsphere-multiactive-commons.md) and parses JSON with `jackson-databind`. It therefore
adds no cloud-client version constraints to your application.

Package: `io.microsphere.multiple.active.zone.spring.aws`.

## Maven Coordinates

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-aws</artifactId>
    <version>0.2.14-SNAPSHOT</version>
</dependency>
```

- `groupId`: `io.github.microsphere-projects`
- `artifactId`: `microsphere-multiactive-aws`
- `version`: `${revision}` → `0.2.14-SNAPSHOT`
- `packaging`: `jar`, `<name>`: `Microsphere :: Multi-Active :: AWS`
- `<description>`: `Microsphere Multi-Active Amazon Web Services`
- The coordinate **is** listed in [microsphere-multiactive-dependencies](microsphere-multiactive-dependencies.md), so
  the version can be omitted when the BOM is imported.

## Version Information

| Item | Value | Verified in |
|------|-------|-------------|
| Documentation line / branch | `current` / `release` | this doc set |
| Artifact version | `0.2.14-SNAPSHOT` | `<revision>` in the root `pom.xml` |
| JDK | 17 or later (CI matrix: 17, 21, 25) | `.github/workflows/maven-build.yml` |
| Multi-Active modules required | `microsphere-multiactive-spring-cloud` — declared **optional**, yet it is the only route to `AbstractZoneLocator` (spring-cloud → spring-boot → spring → commons). Add it explicitly (or depend on `microsphere-multiactive-spring`) if you use this module without the Spring Cloud integration. | `microsphere-multiactive-aws/pom.xml` |
| Optional dependencies | `spring-boot-starter`, `spring-boot-starter-actuator`, `spring-boot-configuration-processor`, `spring-boot-autoconfigure-processor`, `spring-cloud-commons`, `spring-cloud-context`, `com.fasterxml.jackson.core:jackson-databind`, `microsphere-annotation-processor` | `microsphere-multiactive-aws/pom.xml` |
| Test dependencies | `junit-jupiter`, `spring-boot-starter-test`, `microsphere-spring-boot-test`, `logback-classic` | `microsphere-multiactive-aws/pom.xml` |
| Test sources | **none** — this module ships no `src/test/java` on this branch | module tree |
| AWS SDK | not a dependency at all (metadata endpoints only) | `microsphere-multiactive-aws/pom.xml` |
| Jackson | `jackson-databind`, optional, version managed by `microsphere-spring-cloud-dependencies:0.2.26` — not pinned in this repository | `microsphere-multiactive-aws/pom.xml`, `microsphere-multiactive-parent/pom.xml` |
| Spring Boot / Spring Cloud | versions managed externally as above; compatibility `2022.0.x` – `2025.0.x` | root `README.md`, CI profiles |

`jackson-databind` is optional, so **JSON parsing in the two ECS locators needs Jackson on your classpath**. A Spring
Boot web application normally has it; a plain CLI service may not.

## Registration Metadata

The whole module plugs into Boot through one file,
`META-INF/spring.factories`, keyed by the `ZoneLocator` SPI interface:

```
io.microsphere.multiple.active.zone.spring.ZoneLocator=\
io.microsphere.multiple.active.zone.spring.aws.EcsContainerMetadataFileZoneLocator,\
io.microsphere.multiple.active.zone.spring.aws.EcsTaskMetadataEndpointV4ZoneLocator,\
io.microsphere.multiple.active.zone.spring.aws.Ec2AvailabilityZoneEndpointZoneLocator
```

`ZoneAutoConfiguration` (in [spring-boot](microsphere-multiactive-spring-boot.md)) loads those factory entries and folds
them into the `CompositeZoneLocator` together with any `ZoneLocator` beans, then sorts by `order`. Simply having this
jar on the classpath is therefore enough — there is no auto-configuration class and no `@Bean` to declare here.

## API Reference

The chain runs lowest `order` first and stops at the first non-`null` zone:

| Class | `DEFAULT_ORDER` | Activated when (`supports`) | Zone source |
|-------|-----------------|-----------------------------|-------------|
| `EcsContainerMetadataFileZoneLocator` | `5` | `environment.containsProperty("ECS_CONTAINER_METADATA_FILE")` | `AvailabilityZone` field of the JSON metadata file |
| `EcsTaskMetadataEndpointV4ZoneLocator` | `10` | `environment.containsProperty("ECS_CONTAINER_METADATA_URI_V4")` | `AvailabilityZone` field of `GET <uri>/task` |
| `Ec2AvailabilityZoneEndpointZoneLocator` | `15` | always `true` | body text of `GET` the availability-zone endpoint URI |
| `DefaultZoneLocator` | `20` | always `true` | `microsphere.availability.zone`, else `defaultZone` |

### `EcsContainerMetadataFileZoneLocator`

```java
public class EcsContainerMetadataFileZoneLocator extends AbstractZoneLocator {
    public static final String METADATA_FILE_ENV_NAME = "ECS_CONTAINER_METADATA_FILE";
    public static final int DEFAULT_ORDER = 5;
}
```

Reads the file named by the environment variable, parses it as JSON into a `Map`, and returns
`String.valueOf(map.get("AvailabilityZone"))`. Every failure mode is non-fatal and logged: unreadable file ⇒ `warn`;
parse/IO exception ⇒ `error`; blank variable ⇒ `trace`; all of them return `null` so the next locator gets a turn.
Not `EnvironmentAware`, so it is unaffected by `microsphere.availability.zone.locator.timeout`.

### `EcsTaskMetadataEndpointV4ZoneLocator`

```java
public class EcsTaskMetadataEndpointV4ZoneLocator extends AbstractZoneLocator implements EnvironmentAware {
    public static final String METADATA_URI_V4_ENV_NAME = "ECS_CONTAINER_METADATA_URI_V4";
    public static final int DEFAULT_ORDER = 10;
}
```

`GET ${ECS_CONTAINER_METADATA_URI_V4}/task` via `HttpUtils.doGet(url, timeout)`, parses the JSON, returns the
`AvailabilityZone` field. `setEnvironment` reads `microsphere.availability.zone.locator.timeout` (default `3000` ms),
used as both connect and read timeout. According to the Javadoc of the class, the variable is injected by default for
ECS tasks on EC2 Linux instances with agent ≥ 1.39.0, and for Windows `awsvpc` tasks with agent ≥ 1.54.0.

### `Ec2AvailabilityZoneEndpointZoneLocator`

```java
public class Ec2AvailabilityZoneEndpointZoneLocator extends AbstractZoneLocator implements EnvironmentAware {
    public static final String AVAILABILITY_ZONE_ENDPOINT_URI_PROPERTY_NAME = "EC2_AVAILABILITY_ZONE_ENDPOINT_URI";
    public static final String DEFAULT_AVAILABILITY_ZONE_ENDPOINT_URI =
            "http://169.254.169.254/latest/meta-data/placement/availability-zone";
    public static final int DEFAULT_ORDER = 15;
}
```

The URI itself is configurable and resolved as `environment.getProperty(AVAILABILITY_ZONE_ENDPOINT_URI_PROPERTY_NAME,
DEFAULT_AVAILABILITY_ZONE_ENDPOINT_URI)` — so it can be supplied as an environment variable *or* as any Spring property
(the `Environment` is the abstraction, not only the OS environment). The response body is returned verbatim as the
zone; `microsphere.availability.zone.locator.timeout` applies here too.

**Behaviour worth planning for:** `supports` returns `true` unconditionally, so on a non-EC2 host this locator *will*
attempt `http://169.254.169.254/...` (order 15, before the default locator) and wait up to the timeout. The failure is
caught and logged at `error`, and the chain continues, so the outcome is a slow start-up rather than a crash. Set
`EC2_AVAILABILITY_ZONE_ENDPOINT_URI` to something local, lower the timeout, or raise the locator's `order` above
`DefaultZoneLocator` if you never run on EC2.

## Configuration Properties

| Key | Type / default | Used by | Effect |
|-----|----------------|---------|--------|
| `ECS_CONTAINER_METADATA_FILE` | env var, unset | `EcsContainerMetadataFileZoneLocator` | presence enables the locator; value is the metadata file path |
| `ECS_CONTAINER_METADATA_URI_V4` | env var, unset | `EcsTaskMetadataEndpointV4ZoneLocator` | presence enables the locator; `/task` is appended |
| `EC2_AVAILABILITY_ZONE_ENDPOINT_URI` | property, default IMDS path above | `Ec2AvailabilityZoneEndpointZoneLocator` | the URI that is GET-ed |
| `microsphere.availability.zone.locator.timeout` | int, `3000` | both HTTP locators | connect + read timeout in ms |
| `microsphere.availability.zone.locator.fast-fail` | boolean, `false` | `CompositeZoneLocator` | throw instead of falling through when a locator returns `null` or fails |
| `microsphere.availability.zone` | String, `defaultZone` | `DefaultZoneLocator` | manual override, the last resort of the chain |

## Examples

This module has no tests of its own on this branch, so the examples below are (a) verbatim from the locators' API and
(b) adapted from the test that covers their HTTP primitive,
`HttpUtilsTest` in [microsphere-multiactive-commons.md](microsphere-multiactive-commons.md).

### Letting Boot pick the zone

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

`ZoneContext.getZone()` is the value routing actually compares against, and the listener fills it from the
`microsphere.availability.zone` property; the located zone is used when that property is set to the `originalZone`
revert marker — see [microsphere-multiactive-spring.md](microsphere-multiactive-spring.md).

### Verifying a metadata endpoint locally (`HttpUtilsTest` pattern)

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

Point `EC2_AVAILABILITY_ZONE_ENDPOINT_URI` at such a server to exercise
`Ec2AvailabilityZoneEndpointZoneLocator` off EC2:

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

### Registering the locators without Boot

```java
List<ZoneLocator> locators = Arrays.asList(
        new EcsContainerMetadataFileZoneLocator(),   // order 5
        new EcsTaskMetadataEndpointV4ZoneLocator(),  // order 10
        new Ec2AvailabilityZoneEndpointZoneLocator(),// order 15
        new DefaultZoneLocator());                   // order 20
ZoneLocator composite = new CompositeZoneLocator(locators);  // sorts them for you
```

## How to Integrate

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

1. Add `microsphere-multiactive-aws` plus the Multi-Active Spring/Boot/Cloud module you use (the AWS jar's dependency on
   `spring-cloud` is `optional`, so it is not transitive).
2. Ensure `jackson-databind` is present (needed by the two ECS locators).
3. Nothing else: the three locators arrive through `META-INF/spring.factories`, and `ZoneAutoConfiguration` folds them
   into the `CompositeZoneLocator`.
4. If you want a hard guarantee that the zone is real rather than `defaultZone`, set
   `microsphere.availability.zone.locator.fast-fail=true`.

## Related Documentation

- [User guide index](README.md)
- [microsphere-multiactive-spring.md](microsphere-multiactive-spring.md) — the `ZoneLocator` contract and `order` semantics
- [microsphere-multiactive-spring-boot.md](microsphere-multiactive-spring-boot.md) — who reads `spring.factories`
- [microsphere-multiactive-spring-cloud.md](microsphere-multiactive-spring-cloud.md) — what the located zone is then used for
- Root [README](../../../README.md) and [release notes](../../../release-notes.md)
- Developer guide: [../developer-guide/microsphere-multiactive-aws.md](../developer-guide/microsphere-multiactive-aws.md)

## 中文

中文版本：[简体中文](zh/microsphere-multiactive-aws.md)
