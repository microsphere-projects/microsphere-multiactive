English version: [English](../microsphere-multiactive-parent.md)

# microsphere-multiactive-parent

## 概述

`microsphere-multiactive-parent` 是一个纯 POM 模块：它不产出任何 class。它是本仓库其余所有模块的 Maven 父 POM，
唯一职责是让整个 Multi-Active 构件集对外部依赖管理保持一致视图，从而使 `commons`、`spring`、`spring-boot`、
`spring-cloud`、`aws`、`netflix` 这些 jar 中的 Spring、Spring Boot、Spring Cloud、Netflix 与 Jackson 构件
都由同一个 BOM 解析。

在本文档线中存在两层纯 POM 结构，二者需要一起说明，因为 reactor 离不开它们：

| 层级 | `artifactId` | Packaging | 角色 |
|-------|--------------|-----------|------|
| reactor 根 | `microsphere-multiactive` | `pom` | 聚合器：声明 `<modules>` 与 `<revision>` 属性；继承 `microsphere-spring-cloud-parent:0.2.26` |
| 父模块 | `microsphere-multiactive-parent` | `pom` | 全部八个模块的父 POM；导入 `microsphere-spring-cloud-dependencies:0.2.26` |

每个 jar 模块都声明：

```xml
<parent>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-parent</artifactId>
    <version>${revision}</version>
    <relativePath>../microsphere-multiactive-parent/pom.xml</relativePath>
</parent>
```

## Maven 坐标

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-parent</artifactId>
    <version>0.2.14-SNAPSHOT</version>
    <type>pom</type>
</dependency>
```

- `groupId`：`io.github.microsphere-projects`
- `artifactId`：`microsphere-multiactive-parent`
- `version`：`${revision}`，在本分支上即 `0.2.14-SNAPSHOT`
- `packaging`：`pom`
- `<name>`：`Microsphere :: Multi-Active :: Parent`

## 版本信息

| 项目 | 值 | 验证位置 |
|------|-------|-------------|
| 文档线 / 分支 | `current` / `release` | 本套文档 |
| 解析后的版本 | `0.2.14-SNAPSHOT` | 根 `pom.xml` 的 `<revision>` 属性 |
| 上游父 POM | `io.github.microsphere-projects:microsphere-spring-cloud-parent:0.2.26` | 根 `pom.xml` 的 `<parent>` 段 |
| 导入的 BOM | `io.github.microsphere-projects:microsphere-spring-cloud-dependencies:0.2.26`（`<type>pom</type>`、`<scope>import</scope>`） | `microsphere-multiactive-parent/pom.xml`，属性 `microsphere-spring-cloud.version` |
| JDK | 17 及以上；CI 矩阵构建 JDK 17、21、25 | `.github/workflows/maven-build.yml`、`.github/workflows/maven-publish.yml` |
| Maven | Apache Maven 3.9.16（Maven wrapper，`wrapperVersion=3.3.4`、`distributionType=only-script`） | `.mvn/wrapper/maven-wrapper.properties` |
| Spring Cloud 版本线 | `2022.0.x` – `2025.0.x` | 根 `README.md` 的分支表；CI profile `spring-cloud-2022` … `spring-cloud-2025` |
| 依赖的其他 Multi-Active 模块 | 无 —— 本模块是继承链的根 | `microsphere-multiactive-parent/pom.xml` |

> 具体的 Spring Boot、Spring Cloud、Eureka 与 Jackson 版本**并未**写在本仓库中，由被导入的
> `microsphere-spring-cloud-dependencies:0.2.26` BOM 管理，而该 BOM 属于另一个项目。本仓库内部唯一直接锁定的
> 第三方版本是 `netflix.ribbon.version = 2.7.18`，声明在 `microsphere-multiactive-netflix` 的 POM 中。

## 受管插件与构建 Profile

本条线的父 POM **没有**自己的 `<build>` 段 —— 没有插件管理，也没有插件执行。它的全部内容就是一个
`<properties>` 段（`microsphere-spring-cloud.version`）与一个 `<dependencyManagement>` 导入。其余一切
（编译级别、surefire、sources/javadoc jar、deploy、GPG 签名）都继承自外部的
`microsphere-spring-cloud-parent:0.2.26` 继承链，因此无法从本快照核实。

可以核实的是 CI 激活了哪些 profile，因为 workflow 中按名字调用它们：

| Profile | 激活者 | 用途（按 CI 中的调用方式） |
|---------|--------------|-------------------------|
| `test` | `maven-build.yml` | 针对 `main`/`dev` 的推送与 pull request 运行单元测试 |
| `coverage` | `maven-build.yml` | 生成覆盖率报告并上传 Codecov |
| `spring-cloud-2022` / `-2023` / `-2024` / `-2025` | `maven-build.yml` 矩阵 | 选择受测的 Spring Cloud 发布列车 |
| `publish` | `maven-publish.yml` | 将构件发布/部署到 `ossrh` |
| `ci` | `maven-publish.yml` | 部署期间的 CI 专用构建配置 |

## revision 版本管理

该 reactor 采用 Maven *CI 友好*（CI-friendly）多模块版本方式。快照中可见的事实：

1. 根 `pom.xml` 设置 `<revision>0.2.14-SNAPSHOT</revision>` 并声明 `<version>${revision}</version>`；
   每个模块都重复 `<version>${revision}</version>` 而不是字面版本，父模块与 BOM 也不例外。
2. 构建时通过命令行覆盖该属性，改版本无需修改任何文件：

```bash
# CI test run (maven-build.yml)
mvn --batch-mode --update-snapshots --file pom.xml -Drevision=0.0.1-SNAPSHOT \
    test --activate-profiles test,coverage,spring-cloud-2022

# CI release run (maven-publish.yml)
./mvnw --batch-mode --update-snapshots --file pom.xml -Drevision=${revision} \
    -Dgpg.skip=true deploy --activate-profiles publish,ci
```

3. `maven-publish.yml` 用正则 `^[0-9]+\.[0-9]+\.[0-9]+$` 校验目标版本（不允许 `-SNAPSHOT`），从 `release`
   分支发布、打 tag、把自动生成的说明追加到 `release-notes.md`，随后把根 `<revision>` 改写为下一个 patch 并加上
   `-SNAPSHOT` 后缀提交。这就是为什么你正在阅读的这棵代码树是 `0.2.14-SNAPSHOT`，而本条线最新的已发布版本是 `0.2.13`。
4. 根 `README.md` 建议消费者导入 BOM 时使用属性名 `${microsphere-multiactive.version}`。

## 如何集成

通常你不会直接依赖父 POM。两种受支持的用法：

**继承它**：当你需要基于本工程构建并希望沿用同一套受管版本时：

```xml
<parent>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-parent</artifactId>
    <version>0.2.14-SNAPSHOT</version>
</parent>
```

**改为导入 BOM**（普通应用推荐做法）—— 见
[microsphere-multiactive-dependencies.md](microsphere-multiactive-dependencies.md)。

本地重建本模块：

```bash
./mvnw -pl microsphere-multiactive-parent -am install
```

## API 参考

无。本模块是纯 POM 模块，不提供任何 Java 类型。由它使这些外部构件可解析，各类说明分散在各模块文档中：
[commons](microsphere-multiactive-commons.md)、[spring](microsphere-multiactive-spring.md)、
[spring-boot](microsphere-multiactive-spring-boot.md)、[spring-cloud](microsphere-multiactive-spring-cloud.md)、
[aws](microsphere-multiactive-aws.md)、[netflix](microsphere-multiactive-netflix.md)。

## 相关文档

- [用户指南索引](README.md)
- [microsphere-multiactive-dependencies.md](microsphere-multiactive-dependencies.md)
- 根 [README](../../../../README.md) 与[发布说明](../../../../release-notes.md)
- 开发者指南：[../../developer-guide/zh/microsphere-multiactive-parent.md](../../developer-guide/zh/microsphere-multiactive-parent.md)

## English

English version: [English](../microsphere-multiactive-parent.md)
