English version: [English](../microsphere-multiactive-dependencies.md)

# microsphere-multiactive-dependencies

## 概述

`microsphere-multiactive-dependencies` 是 `1.x` 文档线的 BOM（Bill of Materials，依赖清单）。它是一个不含 `src/`
目录的纯 POM 模块；其全部内容就是一个 `<dependencyManagement>` 段，把每个 Multi-Active 构件都钉到 `${revision}`，
也就是本分支的版本。

你在自己的 `<dependencyManagement>` 中导入它，从而让你使用的所有 Multi-Active 构件保持同一版本，并让整个构件集与
它所继承的外部 BOM（`microsphere-spring-cloud-dependencies:0.1.26`）保持对齐。

## Maven 坐标

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-dependencies</artifactId>
    <version>0.1.14-SNAPSHOT</version>
    <type>pom</type>
</dependency>
```

- `groupId`：`io.github.microsphere-projects`
- `artifactId`：`microsphere-multiactive-dependencies`
- `version`：`${revision}` → 本快照中为 `0.1.14-SNAPSHOT`；最新已发布版本 `0.1.13`
- `packaging`：`pom`
- `<name>`：`Microsphere :: Multi-Active :: Dependencies`
- 父工程：`io.github.microsphere-projects:microsphere-multiactive-parent:${revision}`

## 版本信息

| 项目 | 值 | 依据 |
|------|-------|-------------|
| 文档线 / Git 分支 | `1.x` / `release-1.x` | 本文档集 |
| 构件版本 | `0.1.14-SNAPSHOT`（`${revision}`） | 根 `pom.xml` 的 `<revision>`、`microsphere-multiactive-dependencies/pom.xml` |
| 托管版本表达式 | 每个被托管的依赖都使用 `<version>${revision}</version>` | `microsphere-multiactive-dependencies/pom.xml` |
| 继承来的依赖管理 | 通过父模块导入 `microsphere-spring-cloud-dependencies:0.1.26`（import 作用域，pom） | `microsphere-multiactive-parent/pom.xml` |
| JDK | 本仓库未声明编译级别；CI 矩阵为 JDK 8、11、17、21、25，部署使用 JDK 11 | reactor 各 POM、`.github/workflows/*` |
| Spring Boot / Spring Cloud 范围 | Spring Boot 2.x 搭配 Spring Cloud `Hoxton` – `2021.0.x`（CI profile `spring-cloud-hoxton`、`spring-cloud-2020`、`spring-cloud-2021`） | 根 `README.md`、`.github/workflows/maven-build.yml` |
| 模块依赖要求 | 无——BOM 在构建时不需要任何东西；它只发布 `dependencyManagement` | `microsphere-multiactive-dependencies/pom.xml` |

由于 BOM 继承自 `microsphere-multiactive-parent`，导入该 BOM 同时也会应用父模块自身导入的那部分依赖管理。
实践中，只需一次 `import` `microsphere-multiactive-dependencies:0.1.13`，即可同时对齐 Multi-Active 构件与上游托管的
Spring / Spring Boot / Spring Cloud / Jackson / Eureka 版本。

## 被托管的坐标

该 BOM 恰好声明了八个被托管的依赖，`groupId` 均为 `io.github.microsphere-projects`，版本均为 `${revision}`。
右列在本分支上很重要：

| 被托管的 `artifactId` | 由 `release-1.x` reactor 构建？ | 说明 |
|----------------------|-------------------------------------|-------|
| `microsphere-multiactive-commons` | 是 | [模块文档](microsphere-multiactive-commons.md) |
| `microsphere-multiactive-spring` | 是 | [模块文档](microsphere-multiactive-spring.md) |
| `microsphere-multiactive-aws` | 是 | [模块文档](microsphere-multiactive-aws.md) |
| `microsphere-multiactive-netflix` | 是 | [模块文档](microsphere-multiactive-netflix.md) |
| `microsphere-multiactive-spring-boot-starter` | **否** | 本快照中没有该模块 |
| `microsphere-multiactive-spring-cloud-starter` | **否** | 本快照中没有该模块 |
| `microsphere-multiactive-spring-cloud-loadbalancer` | **否** | 本快照中没有该模块 |
| `microsphere-multiactive-dubbo` | **否** | 本快照中没有该模块 |

本分支的 reactor 构建八个模块：`microsphere-multiactive-parent`、`microsphere-multiactive-dependencies`、
`microsphere-multiactive-commons`、`microsphere-multiactive-aws`、`microsphere-multiactive-netflix`、
`microsphere-multiactive-spring`、`microsphere-multiactive-spring-boot`、`microsphere-multiactive-spring-cloud`
（根 `pom.xml` 的 `<modules>`）。

> **本线的已知缺口。** 本分支实际构建的两个 artifactId——
> `microsphere-multiactive-spring-boot` 和 `microsphere-multiactive-spring-cloud`——**没有**被声明在 BOM 中，
> 而 BOM 声明的四个坐标**并不**由本分支构建。后果是：导入 1.x BOM 并**不会**为 `spring-boot` 或 `spring-cloud`
> jar 提供托管版本。请自行声明这两个版本（见下面的片段）。不要指望本分支提供
> `microsphere-multiactive-spring-boot-starter`、`microsphere-multiactive-spring-cloud-starter`、
> `microsphere-multiactive-spring-cloud-loadbalancer` 或 `microsphere-multiactive-dubbo` 而添加对它们的依赖。

## 导入 BOM

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

根 `README.md` 建议把版本保存在名为 `${microsphere-multiactive.version}` 的属性中：

```xml
<properties>
    <microsphere-multiactive.version>0.1.13</microsphere-multiactive.version>
</properties>
```

## 使用被托管与未被托管的构件

由 BOM 托管——无需写版本：

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

不被 BOM 托管——必须显式写版本：

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

当同一个构件被两处托管时顺序很重要：直接写在你的 POM 里的 `<dependencyManagement>` 条目优先于导入的 BOM。
如果你需要覆盖某个 Multi-Active 构件的版本，请在 `import` 之前声明它。

## API 参考

无。本模块是纯 POM 模块，不提供任何 Java 类型。

## 相关文档

- [用户指南索引](README.md)
- [microsphere-multiactive-parent.md](microsphere-multiactive-parent.md) — 该 BOM 继承的父工程
- 根 [README](../../../../README.md) 与[发布说明](../../../../release-notes.md)
- 开发者指南：[../../developer-guide/zh/microsphere-multiactive-dependencies.md](../../developer-guide/zh/microsphere-multiactive-dependencies.md)

## English

English version: [English](../microsphere-multiactive-dependencies.md)
