# Microsphere Multiactive

> Microsphere Projects for Multiactive

[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/microsphere-projects/microsphere-multiactive)
[![Maven Build](https://github.com/microsphere-projects/microsphere-multiactive/actions/workflows/maven-build.yml/badge.svg)](https://github.com/microsphere-projects/microsphere-multiactive/actions/workflows/maven-build.yml)
[![Codecov](https://codecov.io/gh/microsphere-projects/microsphere-multiactive/branch/main/graph/badge.svg)](https://app.codecov.io/gh/microsphere-projects/microsphere-multiactive)
![Maven](https://img.shields.io/maven-central/v/io.github.microsphere-projects/microsphere-multiactive.svg)
![License](https://img.shields.io/github/license/microsphere-projects/microsphere-multiactive.svg)

Microsphere Multiactive is a comprehensive framework for implementing multi-active zone-aware deployments in
distributed applications. The project enables applications to automatically discover their deployment zones and
implement intelligent routing that prefers same-zone instances while providing fallback capabilities for cross-zone
scenarios.

## Modules

| **Module**                               | **Purpose**                                                            |
|------------------------------------------|------------------------------------------------------------------------|
| **microsphere-multiactive-parent**       | Defines the parent POM with dependency management and version profiles |
| **microsphere-multiactive-dependencies** | Centralizes dependency management for all project modules              |
| **microsphere-multiactive-commons**      | Core zone management abstractions                                      |
| **microsphere-multiactive-aws**          | AWS zone discovery                                                     |
| **microsphere-multiactive-netflix**      | Netflix OSS integration                                                |
| **microsphere-multiactive-spring**       | Spring Framework Intergation                                           |
| **microsphere-multiactive-spring-boot**  | Spring Boot Intergation                                                |
| **microsphere-multiactive-spring-cloud** | Spring Cloud Intergation                                               |

## Getting Started

The easiest way to get started is by adding the Microsphere Multiactive BOM (Bill of Materials) to your project's
pom.xml:

```xml

<dependencyManagement>
    <dependencies>
        ...
        <!-- Microsphere Multiactive Dependencies -->
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-multiactive-dependencies</artifactId>
            <version>${microsphere-multiactive.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        ...
    </dependencies>
</dependencyManagement>
```

`${microsphere-multiactive.version}` has two branches:

| **Branches** | **Purpose**                                      | **Latest Version** |
|--------------|--------------------------------------------------|--------------------|
| **0.2.x**    | Compatible with Spring Cloud 2022.0.x - 2025.0.x | 0.2.0              |
| **0.1.x**    | Compatible with Spring Cloud Hoxton - 2021.0.x   | 0.1.0              |

## Building from Source

You don't need to build from source unless you want to try out the latest code or contribute to the project.

To build the project, follow these steps:

1. Clone the repository:

```bash
git clone https://github.com/microsphere-projects/microsphere-multiactive.git
```

2. Build the source:

- Linux/MacOS:

```bash
./mvnw package
```

- Windows:

```powershell
mvnw.cmd package
```

## Contributing

We welcome your contributions! Please read [Code of Conduct](./CODE_OF_CONDUCT.md) before submitting a pull request.

## Reporting Issues

* Before you log a bug, please search
  the [issues](https://github.com/microsphere-projects/microsphere-multiactive/issues)
  to see if someone has already reported the problem.
* If the issue doesn't already
  exist, [create a new issue](https://github.com/microsphere-projects/microsphere-multiactive/issues/new).
* Please provide as much information as possible with the issue report.

## Documentation

### User Guide

[DeepWiki Host](https://deepwiki.com/microsphere-projects/microsphere-multiactive)

### Wiki

[Github Host](https://github.com/microsphere-projects/microsphere-multiactive/wiki)

### JavaDoc

- [microsphere-multiactive-commons](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-multiactive-commons)
- [microsphere-multiactive-aws](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-multiactive-aws)
- [microsphere-multiactive-netflix](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-multiactive-netflix)
- [microsphere-multiactive-spring](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-multiactive-spring)
- [microsphere-multiactive-spring-boot](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-multiactive-spring-boot)
- [microsphere-multiactive-spring-cloud](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-multiactive-spring-cloud)

## License

The Microsphere Spring is released under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).