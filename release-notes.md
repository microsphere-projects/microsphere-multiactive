# Release Notes

## v0.1.0

# Release Notes - Version 0.1.0

## New Features
- **Modular Enhancements**: Added `microsphere-multiactive-netflix` module and implemented `DiscoveryClientServerList` and `DiscoveryClientServer`. [#0c1207d](https://github.com/microsphere-projects/microsphere-multiactive/commit/0c1207d)  
- **CI Improvements**: Added CI workflows, Maven Wrapper, Dependabot configuration, and wiki doc generator. [#3dbd5c2](https://github.com/microsphere-projects/microsphere-multiactive/commit/3dbd5c2), [#5070ed8](https://github.com/microsphere-projects/microsphere-multiactive/commit/5070ed8)  

## Bug Fixes
- **Code Cleanup**: Removed unused modules, deprecated GitHub Actions workflows, and old code. [#c754172](https://github.com/microsphere-projects/microsphere-multiactive/commit/c754172), [#cb2af2f](https://github.com/microsphere-projects/microsphere-multiactive/commit/cb2af2f)  
- **Refactoring**: Streamlined logging and I/O utilities. [#2cd9e0b](https://github.com/microsphere-projects/microsphere-multiactive/commit/2cd9e0b)  

## Other Changes
- Updated Maven Wrapper to version `3.3.4`. [#66c1f30](https://github.com/microsphere-projects/microsphere-multiactive/commit/66c1f30)  
- Enhanced documentation: Replaced README with detailed project docs. [#6fc98d6](https://github.com/microsphere-projects/microsphere-multiactive/commit/6fc98d6)  
- Removed deprecated modules: `microsphere-multiactive-spring-cloud-netflix`. [#c754172](https://github.com/microsphere-projects/microsphere-multiactive/commit/c754172)  

---

Version `0.1.0` sets a strong foundation with modular enhancements, CI optimizations, and comprehensive documentation.
## v0.1.1

# Release Notes: Version 0.1.1

## New Features
- Add comprehensive test coverage for commons and spring modules. ([cd9cda5](https://github.com/microsphere-projects/microsphere/commit/cd9cda5))

## Bug Fixes
- Fix `assertNull` usage in `ZoneResolverTest` as per code review. ([20e3155](https://github.com/microsphere-projects/microsphere/commit/20e3155))

## Documentation
- Add full changelog link and expand release notes. ([f240c5f](https://github.com/microsphere-projects/microsphere/commit/f240c5f))
- Update branch latest versions in `README.md`. ([b62ee43](https://github.com/microsphere-projects/microsphere/commit/b62ee43))

## Dependency Updates
- Bump `microsphere-spring-cloud` to version 0.1.11. ([71ad81d](https://github.com/microsphere-projects/microsphere/commit/71ad81d))

## Build and Workflow Enhancements
- Remove deprecated GitHub Actions workflows. ([8c8c68c](https://github.com/microsphere-projects/microsphere/commit/8c8c68c))
- Remove wiki docs generator script for simplification. ([853e533](https://github.com/microsphere-projects/microsphere/commit/853e533))

## Test Improvements
- Enhance code coverage by adding test cases for better quality assurance. ([cd9cda5](https://github.com/microsphere-projects/microsphere/commit/cd9cda5))

---

For the full changelog, refer to the [changelog](LINK_TO_CHANGELOG).

**Full Changelog**: https://github.com/microsphere-projects/microsphere-multiactive/compare/0.2.0...0.1.1## v0.1.2

# Release Notes - Version 0.1.2

## Build and Workflow Enhancements
- Added OSSRH credentials for publish workflow. ([a009f41](https://github.com/mercyblitz/microsphere-multiactive/commit/a009f41))
- Fixed Java setup and Maven usage in workflows. ([4b6340a](https://github.com/mercyblitz/microsphere-multiactive/commit/4b6340a))

## Dependency Updates
- Bumped `microsphere-spring-cloud` to version 0.1.12. ([9adb99d](https://github.com/mercyblitz/microsphere-multiactive/commit/9adb99d))

## Documentation
- Updated README with revised branch names and version information. ([e187bde](https://github.com/mercyblitz/microsphere-multiactive/commit/e187bde))

## Other Changes
- Synchronized `release-1.x` and `dev-1.x` branches with project updates. ([3cd95ce](https://github.com/mercyblitz/microsphere-multiactive/commit/3cd95ce), [e6c9c9f](https://github.com/mercyblitz/microsphere-multiactive/commit/e6c9c9f))
- Bumped version to next patch after publishing 0.1.1. ([a1721ca](https://github.com/mercyblitz/microsphere-multiactive/commit/a1721ca))

---

**Full Changelog**: [v0.1.1...v0.1.2](https://github.com/mercyblitz/microsphere-multiactive/compare/v0.1.1...v0.1.2)

**Full Changelog**: https://github.com/microsphere-projects/microsphere-multiactive/compare/0.1.1...0.1.2## v0.1.3

# Release Notes for v0.1.3

## Dependency Updates
- Bumped Microsphere Spring Cloud to version `0.1.13`. ([c73d7f2](https://github.com/mercyblitz/dev-1.x/commit/c73d7f2))

## Build and Workflow Enhancements
- Merged `release-1.x` into `dev-1.x` to keep branches in sync. ([66d9066](https://github.com/mercyblitz/dev-1.x/commit/66d9066))

## Other Changes
- Bumped version to next patch after publishing `v0.1.2`. ([3869325](https://github.com/mercyblitz/dev-1.x/commit/3869325))

**Full Changelog:** [v0.1.2...v0.1.3](https://github.com/mercyblitz/dev-1.x/compare/v0.1.2...v0.1.3)

**Full Changelog**: https://github.com/microsphere-projects/microsphere-multiactive/compare/0.1.2...0.1.3## v0.1.4

```markdown
# Release Notes - Version 0.1.4

## Dependency Updates
- Upgraded Microsphere Spring Cloud to `0.1.14`. (#243da3a)

## Documentation
- Updated `README` to reflect updated branch versions. (#65398d6)

## Bug Fixes
- Fixed formatting issues by removing duplicate blank lines and trailing whitespace. (#abe82ba)

## Build and Workflow Enhancements
- Synced `release-1.x` branch with `dev-1.x` for consistency. (#512d4bb)
- Post-release version bumped to prepare for the next patch release. (#a7b8b03)

## Other Changes
- Merged updates from `microsphere-projects:dev-1.x`. (#0a53365)
- Removed redundant line separators in code as part of cleanup. (#8d6b8e0)
```

**Full Changelog**: https://github.com/microsphere-projects/microsphere-multiactive/compare/0.1.3...0.1.4