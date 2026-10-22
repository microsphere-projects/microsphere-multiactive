# Source Diff (src/main): 0.2.13 vs 0.1.13

[简体中文](main-diff-0.2.13-vs-0.1.13.zh.md)

Comparison of the `src/main` content of every Maven module between git tags `0.2.13`
(release line `0.2.x`, Spring Cloud 2022.0.x – 2025.0.x) and `0.1.13`
(release line `0.1.x`, Spring Cloud Hoxton – 2021.0.x).

- Method: `git diff 0.2.13 0.1.13 -- '*/src/main/*'`, module by module, with matched module names only.
- Modules present in only one tag (none in this pair) are ignored.
- Java 17 + vs Java 8 syntax/API differences and Java EE (`javax.*`) vs Jakarta EE (`jakarta.*`)
  differences were checked and excluded from the conclusions below. **No source file in either tag
  imports `javax.*` or `jakarta.*`, and no language-level variance was found**, so no such difference
  needed filtering in this release pair.

## Module-level summary

| Maven module | Java files under `src/main/java` | Difference in `src/main` | Reason |
|---|---|---|---|
| microsphere-multiactive-commons | 6 | Identical | — |
| microsphere-multiactive-aws | 3 | Identical | — |
| microsphere-multiactive-netflix | 6 | Identical | — |
| microsphere-multiactive-spring | 7 | Identical | — |
| microsphere-multiactive-spring-boot | 3 | `META-INF/spring.factories` gains an `EnableAutoConfiguration` entry in 0.1.13 | API compatibility (legacy auto-configuration registration) |
| microsphere-multiactive-spring-cloud | 5 | `META-INF/spring.factories` (with `EnableAutoConfiguration` entry) only exists in 0.1.13 | API compatibility (legacy auto-configuration registration) |
| microsphere-multiactive-parent | n/a (POM only) | Identical except parent version property `microsphere-spring-cloud` `0.2.26` → `0.1.26` | Release-line dependency pinning |
| microsphere-multiactive-dependencies | n/a (BOM only) | Identical | — |

The root `pom.xml` also differs only in `<revision>` (`0.2.13-SNAPSHOT` vs `0.1.13-SNAPSHOT`)
and the parent `microsphere-spring-cloud-parent` version (`0.2.26` vs `0.1.26`).

## Detailed differences

### 1. microsphere-multiactive-spring-boot — `META-INF/spring.factories`

`src/main/resources/META-INF/spring.factories`

```diff
+org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
+io.microsphere.multiple.active.zone.spring.boot.autoconfigure.ZoneAutoConfiguration
+
 io.microsphere.multiple.active.zone.spring.ZoneLocator=\
 io.microsphere.multiple.active.zone.spring.DefaultZoneLocator
```

**Explanation — API compatibility.** Both tags ship
`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` with the same
`ZoneAutoConfiguration` entry (the Spring Boot 2.7+/3.x registration mechanism). The 0.1.x line
additionally keeps the legacy `EnableAutoConfiguration=` key in `spring.factories` because it must
support Spring Boot / Spring Cloud versions before 2.7 (Hoxton – 2021.0.x), where the
`.imports` file is not read. The `ZoneLocator=` key is a custom SPI lookup used by both lines and
is unchanged.

### 2. microsphere-multiactive-spring-cloud — `META-INF/spring.factories`

The whole file is added in 0.1.13:

```diff
+org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
+io.microsphere.multiple.active.zone.spring.cloud.autoconfigure.ZoneCloudAutoConfiguration,\
+io.microsphere.multiple.active.zone.spring.cloud.loadbalancer.CustomizedLoadBalancerAutoConfiguration
```

**Explanation — API compatibility.** The two auto-configuration classes are registered via the
`.imports` file in both tags; 0.1.x repeats the registration in the legacy `spring.factories`
format so the module auto-configures on Spring Boot < 2.7. `CustomizedLoadBalancerAutoConfiguration`
covers the Spring Cloud LoadBalancer path and does not conflict on Boot 2.7+ because Spring deduplicates
registered auto-configuration classes.

## Conclusions

- All Java source under `src/main` is byte-identical between the two tags: the 0.1.x and 0.2.x lines
  deliberately share one implementation, relying on API-stable code (no `javax`/`jakarta` imports,
  no Java-version-specific syntax).
- The only functional difference is dual registration of auto-configurations for the pre-2.7 Spring Boot
  discovery mechanism — pure compatibility, not feature divergence.
- Behavioral divergence between the release lines therefore comes from the upstream dependency pins
  (`microsphere-spring-cloud-parent` / `microsphere-spring-cloud` 0.2.26 vs 0.1.26), not from this
  repository's source.
