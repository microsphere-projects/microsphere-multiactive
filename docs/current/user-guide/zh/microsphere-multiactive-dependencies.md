English version: [English](../microsphere-multiactive-dependencies.md)

# microsphere-multiactive-dependencies

## 概述

`microsphere-multiactive-dependencies` 是本项目的 BOM（Bill of Materials）。它是一个纯 POM 模块，唯一内容是一个
`<dependencyManagement>` 段，把每个 Multi-Active 构件都锁定到 `${revision}`，这样应用就可以混用 `commons`、
`spring`、`aws`、`netflix` 等 jar 而无需重复写版本，也不会意外混用本项目的两个不兼容构建。

根 `README.md` 推荐给消费者的入口就是这个构件。

## Maven 坐标

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-dependencies</artifactId>
    <version>0.2.14-SNAPSHOT</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

- `groupId`：`io.github.microsphere-projects`
- `artifactId`：`microsphere-multiactive-dependencies`
- `version`：`${revision}`，在本分支上即 `0.2.14-SNAPSHOT`
- `packaging`：`pom`
- `<name>`：`Microsphere :: Multi-Active :: Dependencies`
- 父 POM：`microsphere-multiactive-parent:${revision}`（因此该 BOM 本身已经叠加在
  `microsphere-spring-cloud-dependencies:0.2.26` 之上）

## 版本信息

| 项目 | 值 | 验证位置 |
|------|-------|-------------|
| 文档线 / 分支 | `current` / `release` | 本套文档 |
| 解析后的版本 | `0.2.14-SNAPSHOT` | 根 `pom.xml` 的 `<revision>` 属性 |
| 受管的项目版本 | 列表内每一项都使用 `${revision}` | `microsphere-multiactive-dependencies/pom.xml` |
| 继承的外部管理 | `microsphere-spring-cloud-dependencies:0.2.26`，由父 POM 导入 | `microsphere-multiactive-parent/pom.xml` |
| JDK | 17 及以上（CI 矩阵：17、21、25） | `.github/workflows/maven-build.yml` |
| Spring Cloud 版本线 | `2022.0.x` – `2025.0.x` | 根 `README.md`；CI profile `spring-cloud-2022` … `spring-cloud-2025` |
| Spring Boot 版本 | 本仓库未写出；通过继承的 `microsphere-spring-cloud-dependencies` BOM 解析 | `microsphere-multiactive-parent/pom.xml` |
| Netflix Ribbon | `2.7.18` —— 写在 `microsphere-multiactive-netflix` 的 POM 中（属性 `netflix.ribbon.version`），**不在**本 BOM 中 | `microsphere-multiactive-netflix/pom.xml` |
| Netflix Eureka 客户端 | `com.netflix.eureka:eureka-client` 在 Netflix 模块 POM 中声明为可选且未写版本；由继承的外部 BOM 管理 | `microsphere-multiactive-netflix/pom.xml` |
| AWS | 不管理也不使用任何 AWS SDK；AWS 模块通过普通 HTTP 访问 ECS/EC2 元数据端点，并用 `com.fasterxml.jackson.core:jackson-databind`（可选，版本由外部管理）解析 JSON | `microsphere-multiactive-aws/pom.xml` |
| 依赖的其他 Multi-Active 模块 | 无 —— 它管理坐标但本身不含代码 | `microsphere-multiactive-dependencies/pom.xml` |

## 受管坐标

完整列表，与声明保持一致：

| `artifactId` | 受管版本 | 是否由 `release` reactor 构建 |
|--------------|-----------------|---------------------------------|
| `microsphere-multiactive-commons` | `${revision}` | 是 |
| `microsphere-multiactive-spring` | `${revision}` | 是 |
| `microsphere-multiactive-spring-boot-starter` | `${revision}` | 否 —— reactor 构建的是 `microsphere-multiactive-spring-boot` |
| `microsphere-multiactive-spring-cloud-starter` | `${revision}` | 否 —— reactor 构建的是 `microsphere-multiactive-spring-cloud` |
| `microsphere-multiactive-spring-cloud-loadbalancer` | `${revision}` | 否 —— 负载均衡相关类位于 `microsphere-multiactive-spring-cloud` |
| `microsphere-multiactive-aws` | `${revision}` | 是 |
| `microsphere-multiactive-dubbo` | `${revision}` | 否 —— 不是本分支的模块 |
| `microsphere-multiactive-netflix` | `${revision}` | 是 |

**实际影响。** 在本文档线上，BOM 锁定了 `commons`、`spring`、`aws`、`netflix` 的版本。而本分支真正发布出来的
Spring Boot 与 Spring Cloud 构件 —— `microsphere-multiactive-spring-boot` 与 `microsphere-multiactive-spring-cloud`
—— **并不**在 BOM 覆盖范围内，因此依赖它们时必须显式写出版本。

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-spring-cloud</artifactId>
    <version>${microsphere-multiactive.version}</version><!-- not managed by the BOM on this line -->
</dependency>
```

如果你只使用 `commons`、`spring`、`aws` 或 `netflix`，那么只导入 BOM 就够了，无需逐个声明版本。

## 如何集成

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

Maven `dependencyManagement` 的声明顺序很重要：若希望 Spring Boot 自身的依赖/插件管理生效，请把本 BOM 放在
Spring Boot BOM **之后**；若希望 Multi-Active 的传递管理（经由 `microsphere-spring-cloud-dependencies`）生效，
请放在**之前**。对同一个坐标，Maven 保留第一次出现的声明。

## API 参考

无。本模块是纯 POM 模块，不提供任何 Java 类型。

## 相关文档

- [用户指南索引](README.md)
- [microsphere-multiactive-parent.md](microsphere-multiactive-parent.md) —— 外部 BOM 导入发生的地方
- 根 [README](../../../../README.md)（“Getting Started” 一节）与[发布说明](../../../../release-notes.md)
- 开发者指南：[../../developer-guide/zh/microsphere-multiactive-dependencies.md](../../developer-guide/zh/microsphere-multiactive-dependencies.md)

## English

English version: [English](../microsphere-multiactive-dependencies.md)
