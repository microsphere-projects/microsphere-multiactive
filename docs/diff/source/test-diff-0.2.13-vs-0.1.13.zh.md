# 源码对比（src/test）：0.2.13 vs 0.1.13

[English](test-diff-0.2.13-vs-0.1.13.md)

对比 git tag `0.2.13`（发布线 `0.2.x`，兼容 Spring Cloud 2022.0.x – 2025.0.x）与 `0.1.13`
（发布线 `0.1.x`，兼容 Spring Cloud Hoxton – 2021.0.x）之间所有 Maven 模块的 `src/test` 内容。

- 方法：逐模块执行 `git diff 0.2.13 0.1.13 -- '*/src/test/*'`，仅对比同名模块。
- 已按要求检查 Java 17+ 与 Java 8 语法/API 差异、Java EE（`javax.*`）与 Jakarta EE（`jakarta.*`）
  差异；**测试源码中两类差异均不存在**，本对 tag 无需过滤。

## 模块级汇总

| Maven 模块 | `src/test/java` 文件数 | `src/test` 差异 | 原因 |
|---|---|---|---|
| microsphere-multiactive-commons | 6 | 完全一致 | — |
| microsphere-multiactive-aws | 0（无测试源码） | 不适用 | — |
| microsphere-multiactive-netflix | 0（无测试源码） | 不适用 | — |
| microsphere-multiactive-spring | 5 | 完全一致 | — |
| microsphere-multiactive-spring-boot | 1 | 完全一致 | — |
| microsphere-multiactive-spring-cloud | 2 | 完全一致 | — |
| microsphere-multiactive-parent | 无（仅 POM） | 不适用 | — |
| microsphere-multiactive-dependencies | 无（仅 BOM） | 不适用 | — |

## 差异明细

无。`git diff 0.2.13 0.1.13 -- '*/src/test/*'` 输出为空。

## 结论

- 全部 14 个测试类在两条发布线之间逐字节一致。共享的测试套件验证了同一套实现
  在不同 Spring 平台版本下行为一致，这正是双发布线的设计意图。
- 测试覆盖面没有变化：有测试的模块（commons、spring、spring-boot、spring-cloud）在两个 tag 中
  测试文件相同；无测试的模块（aws、netflix）在两个 tag 中均没有测试。
