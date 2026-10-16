English version: [English](../microsphere-multiactive-parent.md)

# microsphere-multiactive-parent

## 概述

`microsphere-multiactive-parent` 是一个纯 POM 模块：它不含 `src/` 目录，也不产出任何类。它是本分支中所有其他模块
——`commons`、`spring`、`spring-boot`、`spring-cloud`、`aws`、`netflix` 以及 `dependencies` BOM——所继承的 Maven
父工程，其唯一职责是只导入一次外部的 `microsphere-spring-cloud-dependencies` BOM，从而使所有 Multi-Active 构件都从
同一份依赖管理来源解析 Spring、Spring Boot、Spring Cloud、Eureka、Ribbon 相关以及 Jackson 构件。

本文档线中存在两个纯 POM 层级，因为 reactor 需要它们协同工作，所以在此一并说明：

| 层级 | `artifactId` | 打包方式 | 在 `release-1.x` 中的角色 |
|-------|--------------|-----------|-----------------------|
| Reactor 根 | `microsphere-multiactive` | `pom` | 聚合器：声明全部八个 `<modules>` 条目——六个 jar 模块加两个 POM 模块——以及 `<revision>` 属性；继承 `microsphere-spring-cloud-parent:0.1.26` |
| 父模块 | `microsphere-multiactive-parent` | `pom` | 其余七个模块（六个 jar 模块 + BOM）的父工程；自身是 reactor 根 POM 的子级；导入 `microsphere-spring-cloud-dependencies:0.1.26` |

reactor 中其余每个模块都显式声明了这个父工程，并且用的是完全相同的片段（六个 jar 模块与 `dependencies` BOM）：

```xml
<parent>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-parent</artifactId>
    <version>${revision}</version>
    <relativePath>../microsphere-multiactive-parent/pom.xml</relativePath>
</parent>
```

父模块本身并不是继承链的顶端：`microsphere-multiactive-parent/pom.xml` 把 reactor 根
`microsphere-multiactive` 声明为*它自己*的 Maven 父工程（`<relativePath>../pom.xml</relativePath>`），
而根 POM 继承外部的 `microsphere-spring-cloud-parent:0.1.26`。因此一个 jar 模块的完整继承链是
`<module>` → `microsphere-multiactive-parent` → `microsphere-multiactive` → `microsphere-spring-cloud-parent`。

## Maven 坐标

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-parent</artifactId>
    <version>0.1.14-SNAPSHOT</version>
    <type>pom</type>
</dependency>
```

- `groupId`：`io.github.microsphere-projects`
- `artifactId`：`microsphere-multiactive-parent`
- `version`：`${revision}` → 在本快照中为 `0.1.14-SNAPSHOT`
- `packaging`：`pom`
- `<name>`：`Microsphere :: Multi-Active :: Parent`
- `<description>`：`Microsphere Multi-Active Parent`

## 版本信息

| 项目 | 值 | 依据 |
|------|-------|-------------|
| 文档线 / Git 分支 | `1.x` / `release-1.x` | 本文档集；`.github/workflows/maven-build.yml`、`maven-publish.yml` |
| 解析后的构件版本 | `0.1.14-SNAPSHOT` | 根 `pom.xml` 的 `<revision>` 属性 |
| 本线最后发布的版本 | `0.1.13` | `release-notes.md`、根 `README.md` 分支表 |
| reactor 根的上游父工程 | `io.github.microsphere-projects:microsphere-spring-cloud-parent:0.1.26` | 根 `pom.xml` 的 `<parent>` 块 |
| 导入的 BOM | `io.github.microsphere-projects:microsphere-spring-cloud-dependencies:0.1.26`（`<type>pom</type>`、`<scope>import</scope>`） | `microsphere-multiactive-parent/pom.xml`，属性 `microsphere-spring-cloud.version` |
| JDK | 本仓库任何 POM 中都没有声明编译级别。CI 在 JDK 8、11、17、21 和 25（Temurin）上编译与测试，并在 JDK 11 上部署 | 对所有 reactor POM 的 `grep`（无 `java.version` / `maven.compiler.*`）、`.github/workflows/maven-build.yml`、`maven-publish.yml` |
| Maven | 通过 Maven wrapper 使用 Apache Maven 3.9.15（`wrapperVersion=3.3.4`、`distributionType=only-script`） | `.mvn/wrapper/maven-wrapper.properties` |
| Spring Cloud 兼容范围 | `Hoxton` – `2021.0.x`（根 `README.md`），由 CI profile `spring-cloud-hoxton`、`spring-cloud-2020`、`spring-cloud-2021` 实际验证 | 根 `README.md` 分支表、`.github/workflows/maven-build.yml` |
| 这些 profile 选中的 Spring Boot 版本 | `spring-cloud-hoxton` → Spring Boot 2.3.12.RELEASE；`spring-cloud-2020` → Spring Boot 2.4.13；`spring-cloud-2021` → Spring Boot 2.6.15（默认 profile） | 由外部 `microsphere-spring-cloud-parent:0.1.26` POM 声明——**不在**本仓库内 |
| 本仓库内钉住的第三方版本 | Netflix Ribbon `2.7.18`（属性 `netflix.ribbon.version`，仅 `microsphere-multiactive-netflix`） | `microsphere-multiactive-netflix/pom.xml` |
| 必需的兄弟模块 | reactor 根 POM——`microsphere-multiactive-parent/pom.xml` 通过 `<relativePath>../pom.xml</relativePath>` 把 `microsphere-multiactive` 声明为自己的父工程 | `microsphere-multiactive-parent/pom.xml` 的 `<parent>` 块 |

> 确切的 Spring Boot、Spring Cloud、Eureka 与 Jackson 版本并没有写入本仓库。它们来自导入的
> `microsphere-spring-cloud-dependencies:0.1.26` BOM 以及外部父链。因此在本线上固定
> `microsphere-multiactive-parent` / BOM 到 `0.1.13`，就等价于固定了你应用的 Spring Boot 版本：
> 这是一个 Spring Boot 2.x 分支，而不是 Spring Boot 3.x 分支。

## 托管插件与构建 profile

`microsphere-multiactive-parent/pom.xml` **没有**声明自己的 `<build>` 段：没有插件管理、没有插件执行、没有
`<modules>`。它的全部内容就是一个 `<properties>` 段（`microsphere-spring-cloud.version=0.1.26`）和一个
`<dependencyManagement>` 导入。编译级别、surefire、sources/javadoc jar、Checkstyle、发布仓库与签名全部来自外部
`microsphere-spring-cloud-parent:0.1.26` 链，因此无法从本快照核实。

*可以在*此处核实的是 CI 按名称调用了哪些 profile：

| Profile | 由谁激活 | 在本分支中的作用 |
|---------|--------------|--------------------------------|
| `test` | `maven-build.yml` | 运行单元/集成测试 |
| `coverage` | `maven-build.yml` | 生成上传到 Codecov 的覆盖率报告（`slug: microsphere-projects/microsphere-multiactive`） |
| `spring-cloud-hoxton` / `spring-cloud-2020` / `spring-cloud-2021` | `maven-build.yml` 矩阵 | 选择被测的 Spring Cloud / Spring Boot 版本列车 |
| `publish` | `maven-publish.yml` | 构件的 staging/发布 |
| `ci` | `maven-publish.yml` | 部署期间的 CI 专用构建设置 |

## Revision 版本化

本 reactor 是 Maven 的 *CI 友好*（CI-friendly）多模块构建。本快照中可见的事实：

1. 根 `pom.xml` 声明 `<revision>0.1.14-SNAPSHOT</revision>`、`<version>${revision}</version>`，且每个模块都重复
   `<version>${revision}</version>`——包括父模块本身和 BOM——而不是字面版本。
2. 构建时在命令行覆盖该属性，因此无需修改任何文件即可改变版本：

```bash
# CI test run (maven-build.yml)
mvn --batch-mode --update-snapshots --file pom.xml -Drevision=0.0.1-SNAPSHOT \
    test --activate-profiles test,coverage,spring-cloud-2021

# CI release run (maven-publish.yml)
./mvnw --batch-mode --update-snapshots --file pom.xml -Drevision=${revision} \
    -Dgpg.skip=true deploy --activate-profiles publish,ci
```

3. `maven-publish.yml` 只针对 `release-1.x` 分支运行，用模式 `^[0-9]+\.[0-9]+\.[0-9]+$` 校验请求的版本（因此
   `-SNAPSHOT` 会被拒绝），打标签，把生成的说明追加到 `release-notes.md`，最后把根的 `<revision>` 改写为下一个
   patch 并带 `-SNAPSHOT` 后缀，再把 `release-1.x` 合并进 `dev-1.x`。这就是为什么本树携带 `0.1.14-SNAPSHOT`
   而 `0.1.13` 是本线最新已发布版本，也是为什么最后一次提交是
   `chore: bump version to next patch after publishing 0.1.13`。
4. `${microsphere-multiactive.version}` 是根 `README.md` 建议消费方在导入 BOM 时使用的属性名。

## 如何集成

你几乎不会直接依赖父工程。两种受支持的用法：

当你基于本项目构建自己的模块集合并希望使用相同的托管版本时，**继承它**：

```xml
<parent>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-multiactive-parent</artifactId>
    <version>0.1.13</version>
</parent>
```

**改为导入 BOM**（普通应用推荐做法）——参见
[microsphere-multiactive-dependencies.md](microsphere-multiactive-dependencies.md)。

若只在本地重建本模块：

```bash
./mvnw -pl microsphere-multiactive-parent -am install
```

## API 参考

无。本模块是纯 POM 模块，不提供任何 Java 类型。它使其可解析的类型在各模块文档中说明：
[commons](microsphere-multiactive-commons.md)、[spring](microsphere-multiactive-spring.md)、
[spring-boot](microsphere-multiactive-spring-boot.md)、[spring-cloud](microsphere-multiactive-spring-cloud.md)、
[aws](microsphere-multiactive-aws.md)、[netflix](microsphere-multiactive-netflix.md)。

## 相关文档

- [用户指南索引](README.md)
- [microsphere-multiactive-dependencies.md](microsphere-multiactive-dependencies.md) — 本线的 BOM
- 根 [README](../../../../README.md) 与[发布说明](../../../../release-notes.md)
- 开发者指南：[../../developer-guide/zh/microsphere-multiactive-parent.md](../../developer-guide/zh/microsphere-multiactive-parent.md)

## English

English version: [English](../microsphere-multiactive-parent.md)
