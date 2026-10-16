# Microsphere Multiactive Documentation

[简体中文](zh/README.md)

This directory contains the generated documentation for [Microsphere Multiactive](../README.md), a multi-active
zone-aware framework for distributed applications.

Documentation is organised along two independent axes:

- **Release line** — one directory per maintained release branch:
  - `current/` — the `release` branch (version line `0.2.x`, latest released `0.2.13`, in-development `0.2.14-SNAPSHOT`),
    compatible with Spring Cloud 2022.0.x – 2025.0.x
  - `1.x/` — the `release-1.x` branch (version line `0.1.x`, latest released `0.1.13`, in-development `0.1.14-SNAPSHOT`),
    compatible with Spring Cloud Hoxton – 2021.0.x
- **Guide type** — inside each release line:
  - `user-guide/` — API reference, integration steps, configuration properties, code examples, version and
    compatibility information
  - `developer-guide/` — architecture design, source-level analysis, design patterns, and Mermaid diagrams of the
    complex flows and module dependencies

Each guide keeps English at its primary path and a full Chinese mirror under its `zh/` subdirectory. One document
covers one Maven module (the parent POM and the BOM are documented as well).

## Documentation map

| Release line | Source branch | Version       | User Guide (EN)                                | User Guide (中文)                              | Developer Guide (EN)                                       | Developer Guide (中文)                                       |
|--------------|---------------|---------------|------------------------------------------------|------------------------------------------------|--------------------------------------------------------------|--------------------------------------------------------------|
| `current`    | `release`     | 0.2.x         | [Index](current/user-guide/README.md)          | [索引](current/user-guide/zh/README.md)        | [Index](current/developer-guide/README.md)                   | [索引](current/developer-guide/zh/README.md)                 |
| `1.x`        | `release-1.x` | 0.1.x         | [Index](1.x/user-guide/README.md)              | [索引](1.x/user-guide/zh/README.md)            | [Index](1.x/developer-guide/README.md)                       | [索引](1.x/developer-guide/zh/README.md)                     |

## Modules documented

Every release line documents the same eight Maven modules:

| Module                               | Document                                                                       |
|--------------------------------------|--------------------------------------------------------------------------------|
| microsphere-multiactive-parent       | [current](current/user-guide/microsphere-multiactive-parent.md) · [1.x](1.x/user-guide/microsphere-multiactive-parent.md) |
| microsphere-multiactive-dependencies | [current](current/user-guide/microsphere-multiactive-dependencies.md) · [1.x](1.x/user-guide/microsphere-multiactive-dependencies.md) |
| microsphere-multiactive-commons      | [current](current/user-guide/microsphere-multiactive-commons.md) · [1.x](1.x/user-guide/microsphere-multiactive-commons.md) |
| microsphere-multiactive-aws          | [current](current/user-guide/microsphere-multiactive-aws.md) · [1.x](1.x/user-guide/microsphere-multiactive-aws.md) |
| microsphere-multiactive-netflix      | [current](current/user-guide/microsphere-multiactive-netflix.md) · [1.x](1.x/user-guide/microsphere-multiactive-netflix.md) |
| microsphere-multiactive-spring       | [current](current/user-guide/microsphere-multiactive-spring.md) · [1.x](1.x/user-guide/microsphere-multiactive-spring.md) |
| microsphere-multiactive-spring-boot  | [current](current/user-guide/microsphere-multiactive-spring-boot.md) · [1.x](1.x/user-guide/microsphere-multiactive-spring-boot.md) |
| microsphere-multiactive-spring-cloud | [current](current/user-guide/microsphere-multiactive-spring-cloud.md) · [1.x](1.x/user-guide/microsphere-multiactive-spring-cloud.md) |

> All module documentation is generated from snapshots of the `release` and `release-1.x` branches; the `main` and
> `dev` working branches are out of scope.

## 中文

[简体中文版本](zh/README.md)
