中文版本：[简体中文](zh/microsphere-multiactive-parent.md)

# microsphere-multiactive-parent

## Overview

`microsphere-multiactive-parent` is a POM-only module: it produces no classes. It is the Maven parent of every other
module in this repository and its single job is to give the whole Multi-Active artifact set one consistent view of
external dependency management, so that the `commons`, `spring`, `spring-boot`, `spring-cloud`, `aws` and `netflix`
jars all resolve Spring, Spring Boot, Spring Cloud, Netflix and Jackson artifacts from the same BOM.

Two POM-only levels exist in this documentation line, and both are described here because the reactor needs them
together:

| Level | `artifactId` | Packaging | Role |
|-------|--------------|-----------|------|
| Reactor root | `microsphere-multiactive` | `pom` | Aggregator: declares `<modules>` and the `<revision>` property; inherits `microsphere-spring-cloud-parent:0.2.26` |
| Parent module | `microsphere-multiactive-parent` | `pom` | Parent of all eight modules; imports `microsphere-spring-cloud-dependencies:0.2.26` |

Every jar module declares:

```xml
<parent>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-parent</artifactId>
    <version>${revision}</version>
    <relativePath>../microsphere-multiactive-parent/pom.xml</relativePath>
</parent>
```

## Maven Coordinates

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-parent</artifactId>
    <version>0.2.14-SNAPSHOT</version>
    <type>pom</type>
</dependency>
```

- `groupId`: `io.github.microsphere-projects`
- `artifactId`: `microsphere-multiactive-parent`
- `version`: `${revision}`, i.e. `0.2.14-SNAPSHOT` on this branch
- `packaging`: `pom`
- `<name>`: `Microsphere :: Multi-Active :: Parent`

## Version Information

| Item | Value | Verified in |
|------|-------|-------------|
| Documentation line / branch | `current` / `release` | this doc set |
| Resolved version | `0.2.14-SNAPSHOT` | `<revision>` property in the root `pom.xml` |
| Upstream parent | `io.github.microsphere-projects:microsphere-spring-cloud-parent:0.2.26` | root `pom.xml` `<parent>` block |
| Imported BOM | `io.github.microsphere-projects:microsphere-spring-cloud-dependencies:0.2.26` (`<type>pom</type>`, `<scope>import</scope>`) | `microsphere-multiactive-parent/pom.xml`, property `microsphere-spring-cloud.version` |
| JDK | 17 or later; CI matrix builds JDK 17, 21, 25 | `.github/workflows/maven-build.yml`, `.github/workflows/maven-publish.yml` |
| Maven | Apache Maven 3.9.16 (Maven wrapper, `wrapperVersion=3.3.4`, `distributionType=only-script`) | `.mvn/wrapper/maven-wrapper.properties` |
| Spring Cloud line | `2022.0.x` – `2025.0.x` | root `README.md` branch table; CI profiles `spring-cloud-2022` … `spring-cloud-2025` |
| Required sibling modules | none — this module is the root of the inheritance chain | `microsphere-multiactive-parent/pom.xml` |

> The concrete Spring Boot, Spring Cloud, Eureka and Jackson versions are **not** written into this repository.
> They are managed by the imported `microsphere-spring-cloud-dependencies:0.2.26` BOM, which lives in another
> project. The only third-party version pinned inside this repository is `netflix.ribbon.version = 2.7.18`, declared
> in the `microsphere-multiactive-netflix` POM.

## Managed Plugins and Build Profiles

The parent POM of this line declares **no** `<build>` section of its own — no plugin management, no plugin
executions. Its full content is one `<properties>` block (`microsphere-spring-cloud.version`) and one
`<dependencyManagement>` import. Everything else (compiler level, surefire, sources/javadoc jar, deploy, GPG signing)
is inherited from the external `microsphere-spring-cloud-parent:0.2.26` chain and is therefore not verifiable from
this snapshot.

What *is* verifiable is which profiles the CI activates, because the workflows call them by name:

| Profile | Activated by | Purpose (as used in CI) |
|---------|--------------|-------------------------|
| `test` | `maven-build.yml` | unit tests on pull requests / pushes to `main` and `dev` |
| `coverage` | `maven-build.yml` | JaCoCo-style coverage report uploaded to Codecov |
| `spring-cloud-2022` / `-2023` / `-2024` / `-2025` | `maven-build.yml` matrix | selects the Spring Cloud release train under test |
| `publish` | `maven-publish.yml` | staging/deployment of the artifacts to `ossrh` |
| `ci` | `maven-publish.yml` | CI-only build settings during deploy |

## Revision Versioning

The reactor is a Maven *CI-friendly* multi-module build. Facts visible in this snapshot:

1. The root `pom.xml` sets `<revision>0.2.14-SNAPSHOT</revision>` and declares `<version>${revision}</version>`;
   every module repeats `<version>${revision}</version>` instead of a literal version, including the parent module
   and the BOM.
2. Builds override the property on the command line, so no file edit is needed to change the version:

```bash
# CI test run (maven-build.yml)
mvn --batch-mode --update-snapshots --file pom.xml -Drevision=0.0.1-SNAPSHOT \
    test --activate-profiles test,coverage,spring-cloud-2022

# CI release run (maven-publish.yml)
./mvnw --batch-mode --update-snapshots --file pom.xml -Drevision=${revision} \
    -Dgpg.skip=true deploy --activate-profiles publish,ci
```

3. `maven-publish.yml` validates the requested version with the pattern `^[0-9]+\.[0-9]+\.[0-9]+$` (no `-SNAPSHOT`),
   publishes from the `release` branch, tags it, appends generated notes to `release-notes.md`, and then rewrites the
   root `<revision>` to the next patch with a `-SNAPSHOT` suffix and commits it. That is why the tree you are reading
   carries `0.2.14-SNAPSHOT` while `0.2.13` is the newest published release of this line.
4. `${microsphere-multiactive.version}` is the property name the root `README.md` suggests for consumers who import
   the BOM.

## How to Integrate

You almost never depend on the parent directly. Two supported usages:

**Inherit it** when you build against this project and want the same managed versions:

```xml
<parent>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-parent</artifactId>
    <version>0.2.14-SNAPSHOT</version>
</parent>
```

**Import the BOM instead** (recommended for ordinary applications) — see
[microsphere-multiactive-dependencies.md](microsphere-multiactive-dependencies.md).

To rebuild this module locally:

```bash
./mvnw -pl microsphere-multiactive-parent -am install
```

## API Reference

None. This module is POM-only and contributes no Java types. The classes it makes resolvable are documented per
module: [commons](microsphere-multiactive-commons.md), [spring](microsphere-multiactive-spring.md),
[spring-boot](microsphere-multiactive-spring-boot.md), [spring-cloud](microsphere-multiactive-spring-cloud.md),
[aws](microsphere-multiactive-aws.md), [netflix](microsphere-multiactive-netflix.md).

## Related Documentation

- [User guide index](README.md)
- [microsphere-multiactive-dependencies.md](microsphere-multiactive-dependencies.md)
- Root [README](../../../README.md) and [release notes](../../../release-notes.md)
- Developer guide: [../developer-guide/microsphere-multiactive-parent.md](../developer-guide/microsphere-multiactive-parent.md)

## 中文

中文版本：[简体中文](zh/microsphere-multiactive-parent.md)
