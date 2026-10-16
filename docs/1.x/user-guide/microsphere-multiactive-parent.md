中文版本：[简体中文](zh/microsphere-multiactive-parent.md)

# microsphere-multiactive-parent

## Overview

`microsphere-multiactive-parent` is a POM-only module: it contains no `src/` directory and produces no classes. It is
the Maven parent inherited by every other module of this branch — `commons`, `spring`, `spring-boot`,
`spring-cloud`, `aws`, `netflix` and the `dependencies` BOM — and its only responsibility is to import the external
`microsphere-spring-cloud-dependencies` BOM once, so that all Multi-Active artifacts resolve Spring, Spring Boot,
Spring Cloud, Eureka, Ribbon-adjacent and Jackson artifacts from the same dependency management source.

Two POM-only levels exist in this documentation line and both are described here because the reactor needs them
together:

| Level | `artifactId` | Packaging | Role in `release-1.x` |
|-------|--------------|-----------|-----------------------|
| Reactor root | `microsphere-multiactive` | `pom` | Aggregator: declares all eight `<modules>` entries — the six jar modules plus the two POM modules — and the `<revision>` property; inherits `microsphere-spring-cloud-parent:0.1.26` |
| Parent module | `microsphere-multiactive-parent` | `pom` | Parent of the other seven modules (six jar modules + the BOM); itself a child of the reactor root POM; imports `microsphere-spring-cloud-dependencies:0.1.26` |

Every other module of the reactor declares this parent explicitly, with the identical block (the six jar modules and
the `dependencies` BOM):

```xml
<parent>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-parent</artifactId>
    <version>${revision}</version>
    <relativePath>../microsphere-multiactive-parent/pom.xml</relativePath>
</parent>
```

The parent module itself is not the top of the inheritance chain: `microsphere-multiactive-parent/pom.xml` declares
the reactor root `microsphere-multiactive` as *its* Maven parent
(`<relativePath>../pom.xml</relativePath>`), and the root inherits the external
`microsphere-spring-cloud-parent:0.1.26`. The full chain of a jar module is therefore
`<module>` → `microsphere-multiactive-parent` → `microsphere-multiactive` → `microsphere-spring-cloud-parent`.

## Maven Coordinates

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-parent</artifactId>
    <version>0.1.14-SNAPSHOT</version>
    <type>pom</type>
</dependency>
```

- `groupId`: `io.github.microsphere-projects`
- `artifactId`: `microsphere-multiactive-parent`
- `version`: `${revision}` → `0.1.14-SNAPSHOT` in this snapshot
- `packaging`: `pom`
- `<name>`: `Microsphere :: Multi-Active :: Parent`
- `<description>`: `Microsphere Multi-Active Parent`

## Version Information

| Item | Value | Verified in |
|------|-------|-------------|
| Documentation line / Git branch | `1.x` / `release-1.x` | this doc set; `.github/workflows/maven-build.yml`, `maven-publish.yml` |
| Resolved artifact version | `0.1.14-SNAPSHOT` | `<revision>` property of the root `pom.xml` |
| Last published release of this line | `0.1.13` | `release-notes.md`, root `README.md` branch table |
| Upstream parent of the reactor root | `io.github.microsphere-projects:microsphere-spring-cloud-parent:0.1.26` | root `pom.xml` `<parent>` block |
| Imported BOM | `io.github.microsphere-projects:microsphere-spring-cloud-dependencies:0.1.26` (`<type>pom</type>`, `<scope>import</scope>`) | `microsphere-multiactive-parent/pom.xml`, property `microsphere-spring-cloud.version` |
| JDK | No compiler level is declared in any POM of this repository. CI compiles and tests on JDK 8, 11, 17, 21 and 25 (Temurin) and deploys on JDK 11 | `grep` over all reactor POMs (no `java.version` / `maven.compiler.*`), `.github/workflows/maven-build.yml`, `maven-publish.yml` |
| Maven | Apache Maven 3.9.15 via the Maven wrapper (`wrapperVersion=3.3.4`, `distributionType=only-script`) | `.mvn/wrapper/maven-wrapper.properties` |
| Spring Cloud compatibility | `Hoxton` – `2021.0.x` (root `README.md`), exercised by the CI profiles `spring-cloud-hoxton`, `spring-cloud-2020`, `spring-cloud-2021` | root `README.md` branch table, `.github/workflows/maven-build.yml` |
| Spring Boot versions selected by those profiles | `spring-cloud-hoxton` → Spring Boot 2.3.12.RELEASE; `spring-cloud-2020` → Spring Boot 2.4.13; `spring-cloud-2021` → Spring Boot 2.6.15 (default profile) | declared by the external `microsphere-spring-cloud-parent:0.1.26` POM — **not** inside this repository |
| Third-party version pinned inside this repository | Netflix Ribbon `2.7.18` (property `netflix.ribbon.version`, `microsphere-multiactive-netflix` only) | `microsphere-multiactive-netflix/pom.xml` |
| Required sibling modules | the reactor root POM — `microsphere-multiactive-parent/pom.xml` declares `microsphere-multiactive` as its parent via `<relativePath>../pom.xml</relativePath>` | `microsphere-multiactive-parent/pom.xml` `<parent>` block |

> The exact Spring Boot, Spring Cloud, Eureka and Jackson versions are not written into this repository. They come
> from the imported `microsphere-spring-cloud-dependencies:0.1.26` BOM and the external parent chain. Pinning
> `microsphere-multiactive-parent` / the BOM at `0.1.13` is therefore what fixes your Spring Boot version on this
> line: this branch is a Spring Boot 2.x line, not a Spring Boot 3.x line.

## Managed Plugins and Build Profiles

`microsphere-multiactive-parent/pom.xml` declares **no** `<build>` section of its own: no plugin management, no
plugin executions, no `<modules>`. Its entire content is one `<properties>` block
(`microsphere-spring-cloud.version=0.1.26`) and one `<dependencyManagement>` import. Compiler level, surefire,
sources/javadoc jars, Checkstyle, deploy repository and signing all come from the external
`microsphere-spring-cloud-parent:0.1.26` chain and are therefore not verifiable from this snapshot.

What *is* verifiable here is which profiles the CI calls by name:

| Profile | Activated by | Purpose as used in this branch |
|---------|--------------|--------------------------------|
| `test` | `maven-build.yml` | run the unit/integration tests |
| `coverage` | `maven-build.yml` | produce the coverage report uploaded to Codecov (`slug: microsphere-projects/microsphere-multiactive`) |
| `spring-cloud-hoxton` / `spring-cloud-2020` / `spring-cloud-2021` | `maven-build.yml` matrix | select the Spring Cloud / Spring Boot release train under test |
| `publish` | `maven-publish.yml` | staging/deployment of the artifacts |
| `ci` | `maven-publish.yml` | CI-only build settings during deploy |

## Revision Versioning

The reactor is a Maven *CI-friendly* multi-module build. Facts visible in this snapshot:

1. The root `pom.xml` declares `<revision>0.1.14-SNAPSHOT</revision>`, `<version>${revision}</version>` and every
   module repeats `<version>${revision}</version>` — including the parent module itself and the BOM — instead of a
   literal version.
2. Builds override the property on the command line, so no file needs editing to change the version:

```bash
# CI test run (maven-build.yml)
mvn --batch-mode --update-snapshots --file pom.xml -Drevision=0.0.1-SNAPSHOT \
    test --activate-profiles test,coverage,spring-cloud-2021

# CI release run (maven-publish.yml)
./mvnw --batch-mode --update-snapshots --file pom.xml -Drevision=${revision} \
    -Dgpg.skip=true deploy --activate-profiles publish,ci
```

3. `maven-publish.yml` runs only against the `release-1.x` branch, validates the requested version against the
   pattern `^[0-9]+\.[0-9]+\.[0-9]+$` (so `-SNAPSHOT` is rejected), tags it, appends generated notes to
   `release-notes.md`, and finally rewrites the root `<revision>` to the next patch with a `-SNAPSHOT` suffix and
   merges `release-1.x` into `dev-1.x`. That is why this tree carries `0.1.14-SNAPSHOT` while `0.1.13` is the newest
   published release of the line, and why the last commit is
   `chore: bump version to next patch after publishing 0.1.13`.
4. `${microsphere-multiactive.version}` is the property name the root `README.md` suggests to consumers importing the
   BOM.

## How to Integrate

You almost never depend on the parent directly. Two supported usages:

**Inherit it** when you build your own module set against this project and want the same managed versions:

```xml
<parent>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-parent</artifactId>
    <version>0.1.13</version>
</parent>
```

**Import the BOM instead** (recommended for ordinary applications) — see
[microsphere-multiactive-dependencies.md](microsphere-multiactive-dependencies.md).

To rebuild only this module locally:

```bash
./mvnw -pl microsphere-multiactive-parent -am install
```

## API Reference

None. This module is POM-only and contributes no Java types. The types it makes resolvable are documented per module:
[commons](microsphere-multiactive-commons.md), [spring](microsphere-multiactive-spring.md),
[spring-boot](microsphere-multiactive-spring-boot.md), [spring-cloud](microsphere-multiactive-spring-cloud.md),
[aws](microsphere-multiactive-aws.md), [netflix](microsphere-multiactive-netflix.md).

## Related Documentation

- [User guide index](README.md)
- [microsphere-multiactive-dependencies.md](microsphere-multiactive-dependencies.md) — the BOM of this line
- Root [README](../../../README.md) and [release notes](../../../release-notes.md)
- Developer guide: [../developer-guide/microsphere-multiactive-parent.md](../developer-guide/microsphere-multiactive-parent.md)

## 中文

中文版本：[简体中文](zh/microsphere-multiactive-parent.md)
