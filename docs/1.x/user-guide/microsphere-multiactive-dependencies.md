中文版本：[简体中文](zh/microsphere-multiactive-dependencies.md)

# microsphere-multiactive-dependencies

## Overview

`microsphere-multiactive-dependencies` is the Bill of Materials (BOM) of the `1.x` line. It is a POM-only module with
no `src/` directory; its whole content is one `<dependencyManagement>` block that pins every Multi-Active artifact to
`${revision}`, i.e. to the version of this branch.

You import it in your own `<dependencyManagement>` so that the Multi-Active artifacts you use all carry the same
version, and so that the artifact set stays aligned with the external BOM it inherits from
(`microsphere-spring-cloud-dependencies:0.1.26`).

## Maven Coordinates

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-dependencies</artifactId>
    <version>0.1.14-SNAPSHOT</version>
    <type>pom</type>
</dependency>
```

- `groupId`: `io.github.microsphere-projects`
- `artifactId`: `microsphere-multiactive-dependencies`
- `version`: `${revision}` → `0.1.14-SNAPSHOT` in this snapshot; newest published release `0.1.13`
- `packaging`: `pom`
- `<name>`: `Microsphere :: Multi-Active :: Dependencies`
- Parent: `io.github.microsphere-projects:microsphere-multiactive-parent:${revision}`

## Version Information

| Item | Value | Verified in |
|------|-------|-------------|
| Documentation line / Git branch | `1.x` / `release-1.x` | this doc set |
| Artifact version | `0.1.14-SNAPSHOT` (`${revision}`) | root `pom.xml` `<revision>`, `microsphere-multiactive-dependencies/pom.xml` |
| Managed version expression | every managed dependency uses `<version>${revision}</version>` | `microsphere-multiactive-dependencies/pom.xml` |
| Inherited management | `microsphere-spring-cloud-dependencies:0.1.26` (import scope, pom) via the parent module | `microsphere-multiactive-parent/pom.xml` |
| JDK | no compiler level declared in this repository; CI matrix JDK 8, 11, 17, 21, 25, deploy on JDK 11 | reactor POMs, `.github/workflows/*` |
| Spring Boot / Spring Cloud range | Spring Boot 2.x with Spring Cloud `Hoxton` – `2021.0.x` (CI profiles `spring-cloud-hoxton`, `spring-cloud-2020`, `spring-cloud-2021`) | root `README.md`, `.github/workflows/maven-build.yml` |
| Module requirements | none — a BOM requires nothing at build time; it only publishes `dependencyManagement` | `microsphere-multiactive-dependencies/pom.xml` |

Because the BOM inherits `microsphere-multiactive-parent`, importing the BOM also applies the dependency management
that the parent itself imports. In practice one `import` of
`microsphere-multiactive-dependencies:0.1.13` aligns both the Multi-Active artifacts and the
Spring / Spring Boot / Spring Cloud / Jackson / Eureka versions managed upstream.

## Managed Coordinates

The BOM declares exactly eight managed dependencies, all with `groupId`
`io.github.microsphere-projects` and version `${revision}`. The right-hand column is important on this branch:

| Managed `artifactId` | Built by the `release-1.x` reactor? | Notes |
|----------------------|-------------------------------------|-------|
| `microsphere-multiactive-commons` | yes | [module doc](microsphere-multiactive-commons.md) |
| `microsphere-multiactive-spring` | yes | [module doc](microsphere-multiactive-spring.md) |
| `microsphere-multiactive-aws` | yes | [module doc](microsphere-multiactive-aws.md) |
| `microsphere-multiactive-netflix` | yes | [module doc](microsphere-multiactive-netflix.md) |
| `microsphere-multiactive-spring-boot-starter` | **no** | no such module in this snapshot |
| `microsphere-multiactive-spring-cloud-starter` | **no** | no such module in this snapshot |
| `microsphere-multiactive-spring-cloud-loadbalancer` | **no** | no such module in this snapshot |
| `microsphere-multiactive-dubbo` | **no** | no such module in this snapshot |

The reactor of this branch builds eight modules: `microsphere-multiactive-parent`,
`microsphere-multiactive-dependencies`, `microsphere-multiactive-commons`, `microsphere-multiactive-aws`,
`microsphere-multiactive-netflix`, `microsphere-multiactive-spring`, `microsphere-multiactive-spring-boot`,
`microsphere-multiactive-spring-cloud` (root `pom.xml` `<modules>`).

> **Known gap on this line.** The two artifactIds that this branch actually builds —
> `microsphere-multiactive-spring-boot` and `microsphere-multiactive-spring-cloud` — are **not** declared in the BOM,
> and four coordinates that the BOM declares are **not** built by this branch. Consequence: importing the 1.x BOM does
> **not** give you a managed version for the `spring-boot` or `spring-cloud` jar. Declare those two versions yourself
> (see the snippet below). Do not add a dependency on `microsphere-multiactive-spring-boot-starter`,
> `microsphere-multiactive-spring-cloud-starter`, `microsphere-multiactive-spring-cloud-loadbalancer` or
> `microsphere-multiactive-dubbo` expecting this branch to provide them.

## Importing the BOM

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-multiactive-dependencies</artifactId>
            <version>0.1.13</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

The root `README.md` suggests keeping the version in a property named `${microsphere-multiactive.version}`:

```xml
<properties>
    <microsphere-multiactive.version>0.1.13</microsphere-multiactive.version>
</properties>
```

## Using the Managed and Unmanaged Artifacts

Managed by the BOM — no version needed:

```xml
<dependencies>
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-multiactive-commons</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-multiactive-spring</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-multiactive-aws</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-multiactive-netflix</artifactId>
    </dependency>
</dependencies>
```

Not managed by the BOM — the version must be explicit:

```xml
<dependencies>
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-multiactive-spring-boot</artifactId>
        <version>${microsphere-multiactive.version}</version>
    </dependency>
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-multiactive-spring-cloud</artifactId>
        <version>${microsphere-multiactive.version}</version>
    </dependency>
</dependencies>
```

Order matters when the same artifact is managed twice: a `<dependencyManagement>` entry declared directly in your POM
wins over an imported BOM. If you need to override one Multi-Active artifact, declare it before the `import`.

## API Reference

None. This module is POM-only and contributes no Java types.

## Related Documentation

- [User guide index](README.md)
- [microsphere-multiactive-parent.md](microsphere-multiactive-parent.md) — the parent this BOM inherits
- Root [README](../../../README.md) and [release notes](../../../release-notes.md)
- Developer guide: [../developer-guide/microsphere-multiactive-dependencies.md](../developer-guide/microsphere-multiactive-dependencies.md)

## 中文

中文版本：[简体中文](zh/microsphere-multiactive-dependencies.md)
