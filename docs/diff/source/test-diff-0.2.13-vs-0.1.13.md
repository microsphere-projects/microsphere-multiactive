# Source Diff (src/test): 0.2.13 vs 0.1.13

[简体中文](test-diff-0.2.13-vs-0.1.13.zh.md)

Comparison of the `src/test` content of every Maven module between git tags `0.2.13`
(release line `0.2.x`, Spring Cloud 2022.0.x – 2025.0.x) and `0.1.13`
(release line `0.1.x`, Spring Cloud Hoxton – 2021.0.x).

- Method: `git diff 0.2.13 0.1.13 -- '*/src/test/*'`, module by module, with matched module names only.
- Java 17 + vs Java 8 syntax/API differences and Java EE (`javax.*`) vs Jakarta EE (`jakarta.*`)
  differences were checked; **the test sources contain neither**, so nothing was filtered out
  in this release pair.

## Module-level summary

| Maven module | Test files under `src/test/java` | Difference in `src/test` | Reason |
|---|---|---|---|
| microsphere-multiactive-commons | 6 | Identical | — |
| microsphere-multiactive-aws | 0 (no test sources) | n/a | — |
| microsphere-multiactive-netflix | 0 (no test sources) | n/a | — |
| microsphere-multiactive-spring | 5 | Identical | — |
| microsphere-multiactive-spring-boot | 1 | Identical | — |
| microsphere-multiactive-spring-cloud | 2 | Identical | — |
| microsphere-multiactive-parent | n/a (POM only) | n/a | — |
| microsphere-multiactive-dependencies | n/a (BOM only) | n/a | — |

## Detailed differences

None. `git diff 0.2.13 0.1.13 -- '*/src/test/*'` is empty.

## Conclusions

- All 14 test classes are byte-identical across the two release lines. The shared test suite validates
  that the single implementation behaves the same regardless of the Spring platform version, which is
  the design intent of the two release lines.
- Test coverage is unchanged: the modules with tests (commons, spring, spring-boot, spring-cloud)
  have the same test files in both tags, and modules without tests (aws, netflix) have none in either.
