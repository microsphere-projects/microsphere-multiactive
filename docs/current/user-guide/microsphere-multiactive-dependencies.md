中文版本：[简体中文](zh/microsphere-multiactive-dependencies.md)

# microsphere-multiactive-dependencies

## Overview

`microsphere-multiactive-dependencies` is the Bill of Materials (BOM) of the project. It is a POM-only module whose
only content is a `<dependencyManagement>` block that pins every Multi-Active artifact to `${revision}`, so an
application can mix `commons`, `spring`, `aws` and `netflix` jars without repeating the version, and never accidentally
mix two incompatible builds of this project.

This is the artifact the root `README.md` recommends as the entry point for consumers.

## Maven Coordinates

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-dependencies</artifactId>
    <version>0.2.14-SNAPSHOT</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

- `groupId`: `io.github.microsphere-projects`
- `artifactId`: `microsphere-multiactive-dependencies`
- `version`: `${revision}`, i.e. `0.2.14-SNAPSHOT` on this branch
- `packaging`: `pom`
- `<name>`: `Microsphere :: Multi-Active :: Dependencies`
- Parent: `microsphere-multiactive-parent:${revision}` (so the BOM itself already sits on top of
  `microsphere-spring-cloud-dependencies:0.2.26`)

## Version Information

| Item | Value | Verified in |
|------|-------|-------------|
| Documentation line / branch | `current` / `release` | this doc set |
| Resolved version | `0.2.14-SNAPSHOT` | `<revision>` property in the root `pom.xml` |
| Managed project version | every listed entry uses `${revision}` | `microsphere-multiactive-dependencies/pom.xml` |
| Inherited external management | `microsphere-spring-cloud-dependencies:0.2.26`, imported by the parent POM | `microsphere-multiactive-parent/pom.xml` |
| JDK | 17 or later (CI matrix: 17, 21, 25) | `.github/workflows/maven-build.yml` |
| Spring Cloud line | `2022.0.x` – `2025.0.x` | root `README.md`, CI profiles `spring-cloud-2022` … `spring-cloud-2025` |
| Spring Boot version | not written in this repository; resolved through the inherited `microsphere-spring-cloud-dependencies` BOM | `microsphere-multiactive-parent/pom.xml` |
| Netflix Ribbon | `2.7.18` — pinned in the `microsphere-multiactive-netflix` POM (`netflix.ribbon.version`), **not** in this BOM | `microsphere-multiactive-netflix/pom.xml` |
| Netflix Eureka client | `com.netflix.eureka:eureka-client` is declared optional in the Netflix POM with no version here; it is managed by the inherited external BOM | `microsphere-multiactive-netflix/pom.xml` |
| AWS | no AWS SDK is managed or used; the AWS module talks to ECS/EC2 metadata endpoints over plain HTTP and parses JSON with `com.fasterxml.jackson.core:jackson-databind` (optional, version managed externally) | `microsphere-multiactive-aws/pom.xml` |
| Required sibling modules | none — it manages coordinates but contains no code | `microsphere-multiactive-dependencies/pom.xml` |

## Managed Coordinates

The complete list, exactly as declared:

| `artifactId` | Managed version | Built by the `release` reactor? |
|--------------|-----------------|---------------------------------|
| `microsphere-multiactive-commons` | `${revision}` | yes |
| `microsphere-multiactive-spring` | `${revision}` | yes |
| `microsphere-multiactive-spring-boot-starter` | `${revision}` | no — the reactor builds `microsphere-multiactive-spring-boot` instead |
| `microsphere-multiactive-spring-cloud-starter` | `${revision}` | no — the reactor builds `microsphere-multiactive-spring-cloud` instead |
| `microsphere-multiactive-spring-cloud-loadbalancer` | `${revision}` | no — the load-balancer classes live in `microsphere-multiactive-spring-cloud` |
| `microsphere-multiactive-aws` | `${revision}` | yes |
| `microsphere-multiactive-dubbo` | `${revision}` | no — not a module of this branch |
| `microsphere-multiactive-netflix` | `${revision}` | yes |

**Practical consequence.** On this documentation line, the BOM pins versions for `commons`, `spring`, `aws` and
`netflix`. The Spring Boot and Spring Cloud jars that this branch actually publishes — `microsphere-multiactive-spring-boot`
and `microsphere-multiactive-spring-cloud` — are *not* covered by it, so their version has to be stated explicitly
when you depend on them.

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-spring-cloud</artifactId>
    <version>${microsphere-multiactive.version}</version><!-- not managed by the BOM on this line -->
</dependency>
```

If you only use `commons`, `spring`, `aws` or `netflix`, importing the BOM is enough and no per-dependency version is
needed.

## How to Integrate

```xml
<properties>
    <microsphere-multiactive.version>0.2.14-SNAPSHOT</microsphere-multiactive.version>
</properties>

<dependencyManagement>
    <dependencies>
        <!-- Microsphere Multiactive Dependencies -->
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-multiactive-dependencies</artifactId>
            <version>${microsphere-multiactive.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-multiactive-commons</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-multiactive-spring</artifactId>
    </dependency>
</dependencies>
```

Order matters in Maven `dependencyManagement`: import this BOM **after** a Spring Boot BOM if you want Spring Boot's
own plugin/dependency management to win, or **before** it if you want Multi-Active's transitive management (through
`microsphere-spring-cloud-dependencies`) to win. The first declaration of a coordinate is the one Maven keeps.

## API Reference

None. This module is POM-only and contributes no Java types.

## Related Documentation

- [User guide index](README.md)
- [microsphere-multiactive-parent.md](microsphere-multiactive-parent.md) — where the external BOM import happens
- Root [README](../../../README.md) (the "Getting Started" section) and [release notes](../../../release-notes.md)
- Developer guide: [../developer-guide/microsphere-multiactive-dependencies.md](../developer-guide/microsphere-multiactive-dependencies.md)

## 中文

中文版本：[简体中文](zh/microsphere-multiactive-dependencies.md)
