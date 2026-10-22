# Microsphere Multiactive 文档

[English](README.md)

本目录包含 [Microsphere Multiactive](../../README.md)（面向分布式应用的多活 Zone 感知框架）的生成文档。

文档沿两个相互独立的维度组织：

- **发布线（Release line）** —— 每个受维护的发布分支对应一个目录：
  - `current/` —— `release` 分支（版本线 `0.2.x`，最新已发布 `0.2.13`，开发中 `0.2.14-SNAPSHOT`），
    兼容 Spring Cloud 2022.0.x – 2025.0.x
  - `1.x/` —— `release-1.x` 分支（版本线 `0.1.x`，最新已发布 `0.1.13`，开发中 `0.1.14-SNAPSHOT`），
    兼容 Spring Cloud Hoxton – 2021.0.x
- **文档集（Guide type）** —— 每条发布线内部：
  - `user-guide/` —— API 说明、集成步骤、配置属性、代码示例、版本信息与兼容范围
  - `developer-guide/` —— 架构设计、源码级分析、设计模式，以及复杂流程与模块依赖的 Mermaid 流程图

每个文档集将英文放在主路径下，并在其 `zh/` 子目录中提供完整的中文镜像。一个 Maven 模块对应一篇文档
（parent POM 与 BOM 同样单独成文）。

## 文档地图

| 发布线 | 源分支 | 版本 | 用户文档（EN） | 用户文档（中文） | 开发者文档（EN） | 开发者文档（中文） |
|--------------|---------------|---------------|------------------------------------------------|------------------------------------------------|--------------------------------------------------------------|--------------------------------------------------------------|
| `current`    | `release`     | 0.2.x         | [Index](../current/user-guide/README.md)          | [索引](../current/user-guide/zh/README.md)        | [Index](../current/developer-guide/README.md)                   | [索引](../current/developer-guide/zh/README.md)                 |
| `1.x`        | `release-1.x` | 0.1.x         | [Index](../1.x/user-guide/README.md)              | [索引](../1.x/user-guide/zh/README.md)            | [Index](../1.x/developer-guide/README.md)                       | [索引](../1.x/developer-guide/zh/README.md)                     |

## 已文档化的模块

每条发布线都覆盖相同的八个 Maven 模块：

| 模块 | 文档                                                                       |
|--------------------------------------|--------------------------------------------------------------------------------|
| microsphere-multiactive-parent       | [current](../current/user-guide/microsphere-multiactive-parent.md) · [1.x](../1.x/user-guide/microsphere-multiactive-parent.md) |
| microsphere-multiactive-dependencies | [current](../current/user-guide/microsphere-multiactive-dependencies.md) · [1.x](../1.x/user-guide/microsphere-multiactive-dependencies.md) |
| microsphere-multiactive-commons      | [current](../current/user-guide/microsphere-multiactive-commons.md) · [1.x](../1.x/user-guide/microsphere-multiactive-commons.md) |
| microsphere-multiactive-aws          | [current](../current/user-guide/microsphere-multiactive-aws.md) · [1.x](../1.x/user-guide/microsphere-multiactive-aws.md) |
| microsphere-multiactive-netflix      | [current](../current/user-guide/microsphere-multiactive-netflix.md) · [1.x](../1.x/user-guide/microsphere-multiactive-netflix.md) |
| microsphere-multiactive-spring       | [current](../current/user-guide/microsphere-multiactive-spring.md) · [1.x](../1.x/user-guide/microsphere-multiactive-spring.md) |
| microsphere-multiactive-spring-boot  | [current](../current/user-guide/microsphere-multiactive-spring-boot.md) · [1.x](../1.x/user-guide/microsphere-multiactive-spring-boot.md) |
| microsphere-multiactive-spring-cloud | [current](../current/user-guide/microsphere-multiactive-spring-cloud.md) · [1.x](../1.x/user-guide/microsphere-multiactive-spring-cloud.md) |

## 源码对比

对两条发布线最新 tag 之间逐模块的 `src/main` 与 `src/test` 内容对比（结论中已排除 Java 语言版本
与 Java EE / Jakarta EE API 差异）：

| Tag 对 | src/main（EN · 中文） | src/test（EN · 中文） |
|----------|----------------------|----------------------|
| 0.2.13 vs 0.1.13 | [main-diff-0.2.13-vs-0.1.13.md](../diff/source/main-diff-0.2.13-vs-0.1.13.md) · [中文](../diff/source/main-diff-0.2.13-vs-0.1.13.zh.md) | [test-diff-0.2.13-vs-0.1.13.md](../diff/source/test-diff-0.2.13-vs-0.1.13.md) · [中文](../diff/source/test-diff-0.2.13-vs-0.1.13.zh.md) |

> 所有模块文档均基于 `release` 与 `release-1.x` 分支的快照生成；`main` 与 `dev` 工作分支不在分析范围内。

## English

[English version](README.md)
