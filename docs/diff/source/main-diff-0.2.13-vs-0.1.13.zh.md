# 源码对比（src/main）：0.2.13 vs 0.1.13

[English](main-diff-0.2.13-vs-0.1.13.md)

对比 git tag `0.2.13`（发布线 `0.2.x`，兼容 Spring Cloud 2022.0.x – 2025.0.x）与 `0.1.13`
（发布线 `0.1.x`，兼容 Spring Cloud Hoxton – 2021.0.x）之间所有 Maven 模块的 `src/main` 内容。

- 方法：逐模块执行 `git diff 0.2.13 0.1.13 -- '*/src/main/*'`，仅对比同名模块，任一 tag 中缺失的模块忽略
  （本对 tag 模块集合完全一致，无需忽略）。
- 已按要求检查并排除 Java 17+ 与 Java 8 的语法/API 差异、Java EE（`javax.*`）与 Jakarta EE（`jakarta.*`）
  差异。**两个 tag 的全部源码均未引用 `javax.*` / `jakarta.*`，也不存在语言版本差异**，因此本对 tag
  无需过滤此类差异。

## 模块级汇总

| Maven 模块 | `src/main/java` 文件数 | `src/main` 差异 | 原因 |
|---|---|---|---|
| microsphere-multiactive-commons | 6 | 完全一致 | — |
| microsphere-multiactive-aws | 3 | 完全一致 | — |
| microsphere-multiactive-netflix | 6 | 完全一致 | — |
| microsphere-multiactive-spring | 7 | 完全一致 | — |
| microsphere-multiactive-spring-boot | 3 | 0.1.13 的 `META-INF/spring.factories` 增加了 `EnableAutoConfiguration` 条目 | API 兼容（旧版自动装配注册机制） |
| microsphere-multiactive-spring-cloud | 5 | `META-INF/spring.factories`（含 `EnableAutoConfiguration` 条目）仅存在于 0.1.13 | API 兼容（旧版自动装配注册机制） |
| microsphere-multiactive-parent | 无（仅 POM） | 除 parent 版本属性 `microsphere-spring-cloud` 由 `0.2.26` → `0.1.26` 外一致 | 发布线依赖锚定 |
| microsphere-multiactive-dependencies | 无（仅 BOM） | 完全一致 | — |

根 `pom.xml` 也仅差 `<revision>`（`0.2.13-SNAPSHOT` vs `0.1.13-SNAPSHOT`）与 parent
`microsphere-spring-cloud-parent` 版本（`0.2.26` vs `0.1.26`）。

## 差异明细

### 1. microsphere-multiactive-spring-boot — `META-INF/spring.factories`

`src/main/resources/META-INF/spring.factories`

```diff
+org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
+io.microsphere.multiple.active.zone.spring.boot.autoconfigure.ZoneAutoConfiguration
+
 io.microsphere.multiple.active.zone.spring.ZoneLocator=\
 io.microsphere.multiple.active.zone.spring.DefaultZoneLocator
```

**解释 —— API 兼容。** 两个 tag 均带有内容相同的
`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
（Spring Boot 2.7+/3.x 的自动装配注册机制）。0.1.x 线额外在 `spring.factories` 中保留旧版
`EnableAutoConfiguration=` 键，因为该发布线必须兼容 2.7 之前的 Spring Boot / Spring Cloud
（Hoxton – 2021.0.x），那些版本不会读取 `.imports` 文件。`ZoneLocator=` 键是两条发布线共用的
自定义 SPI 约定，未发生变化。

### 2. microsphere-multiactive-spring-cloud — `META-INF/spring.factories`

该文件在 0.1.13 中整体新增：

```diff
+org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
+io.microsphere.multiple.active.zone.spring.cloud.autoconfigure.ZoneCloudAutoConfiguration,\
+io.microsphere.multiple.active.zone.spring.cloud.loadbalancer.CustomizedLoadBalancerAutoConfiguration
```

**解释 —— API 兼容。** 这两个自动配置类在两个 tag 中都通过 `.imports` 文件注册；0.1.x 只是以旧版
`spring.factories` 格式重复注册一次，使其能在 Spring Boot < 2.7 上完成自动装配。
`CustomizedLoadBalancerAutoConfiguration` 负责 Spring Cloud LoadBalancer 路径，在 Boot 2.7+ 上
不会冲突，因为 Spring 会对重复注册的自动配置类去重。

## 结论

- 两个 tag 的 `src/main` Java 源码逐字节一致：0.1.x 与 0.2.x 发布线刻意共享同一套实现，
  依赖保持 API 稳定的代码（无 `javax`/`jakarta` 引用、无语言版本专属语法）。
- 唯一的功能性差异是为 2.7 之前的 Spring Boot 发现机制做的自动装配双重注册——纯属兼容性处理，
  并非功能分叉。
- 因此两条发布线的行为差异来自上游依赖锚定版本
  （`microsphere-spring-cloud-parent` / `microsphere-spring-cloud` 0.2.26 vs 0.1.26），
  而非本仓库源码本身。
