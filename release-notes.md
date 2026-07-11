# Release Notes

## v0.2.0

# Release Notes - Version 0.2.0

## New Features
- **Contributor Code of Conduct:** Introduced a code of conduct for contributors. ([004d5cd](https://github.com/microsphere-projects/microsphere-multiactive/commit/004d5cd))
- **Detailed Documentation:** Replaced README with comprehensive project documentation. ([6fc98d6](https://github.com/microsphere-projects/microsphere-multiactive/commit/6fc98d6))
- **CI Workflows:** Added continuous integration workflows and automated wiki documentation generator. ([5070ed8](https://github.com/microsphere-projects/microsphere-multiactive/commit/5070ed8))
- **Dependabot Configuration:** Configured Dependabot for Maven dependency tracking and updates. ([f5f5e2e](https://github.com/microsphere-projects/microsphere-multiactive/commit/f5f5e2e))
- **Netflix Ribbon Update:** Upgraded `netflix.ribbon.version` from 2.3.0 to 2.7.18 for improved functionality. ([47f9791](https://github.com/microsphere-projects/microsphere-multiactive/commit/47f9791))

## Bug Fixes
- **Dependabot Formatting:** Fixed formatting issues with updates list in `dependabot.yml`. ([06cfbcb](https://github.com/microsphere-projects/microsphere-multiactive/commit/06cfbcb))

## Other Changes
- **Module Refactoring:** Removed `microsphere-multiactive-spring-cloud-netflix` module, refactored logging/IO utilities, and introduced `microsphere-multiactive-netflix` module for better modularization. ([2cd9e0b](https://github.com/microsphere-projects/microsphere-multiactive/commit/2cd9e0b), [0c1207d](https://github.com/microsphere-projects/microsphere-multiactive/commit/0c1207d), [c754172](https://github.com/microsphere-projects/microsphere-multiactive/commit/c754172))
- **Maven Updates:** Added and updated Maven wrapper to version 3.3.4. ([66c1f30](https://github.com/microsphere-projects/microsphere-multiactive/commit/66c1f30), [2f93093](https://github.com/microsphere-projects/microsphere-multiactive/commit/2f93093))
- **Miscellaneous Cleanup:** Removed unused code and deprecated modules. ([4c4a3e7](https://github.com/microsphere-projects/microsphere-multiactive/commit/4c4a3e7), [cb2af2f](https://github.com/microsphere-projects/microsphere-multiactive/commit/cb2af2f))

## v0.2.1

# Release Notes - Version 0.2.1 🎉

## Test Improvements
- Added comprehensive test coverage for commons and spring modules. ([#6](https://github.com/microsphere-projects/copilot/pull/6))
- Fixed `assertNull` usage in `ZoneResolverTest` based on code review feedback.

## Documentation
- Updated branch version details in `README`. 

## Build and Workflow Enhancements
- Improved release notes and GitHub release creation process.
- Updated dependency `microsphere-spring-cloud` to `0.2.11`.
- Bumped parent version to `0.2.11`.

---

Feel free to share your feedback or report any issues! 🌟

**Full Changelog**: https://github.com/microsphere-projects/microsphere-multiactive/compare/0.2.0...0.2.1## v0.2.2

# Release Notes - Version 0.2.2

## Build and Workflow Enhancements
- Added Maven server credentials to workflows. ([b61c962](#))
- Updated GitHub Actions Maven workflows and resolved EOF issues. ([c7f5bd8](#))

## Documentation
- Updated README to include branch table and version details. ([b6c2c7f](#))

## Dependency Updates
- Upgraded `microsphere-spring-cloud` to version 0.2.12. ([7288139](#))

## Other Changes
- Performed regular branch merges and version bump to prepare for the next patch cycle. ([chore commits](#))  

---

*For a detailed view of changes, refer to the commit history.*

**Full Changelog**: https://github.com/microsphere-projects/microsphere-multiactive/compare/0.2.1...0.2.2## v0.2.3

# Release Notes for Version 0.2.3

## Dependency Updates
- Updated `microsphere-spring-cloud-parent` to version `0.2.13`. ([#11](https://github.com/microsphere-projects/microsphere-spring-cloud))
- Updated `microsphere-spring-cloud-dependencies` to version `0.2.13`. ([#12](https://github.com/microsphere-projects/microsphere-spring-cloud))

## Build and Workflow Enhancements
- Merged main branch changes into release. (`[skip ci]`)
- Bumped version to next patch post `0.2.2`.

---

No new features, bug fixes, documentations, or test improvements were introduced in this release.

**Full Changelog**: https://github.com/microsphere-projects/microsphere-multiactive/compare/0.2.2...0.2.3## v0.2.4

# Release Notes for v0.2.4

## Dependency Updates
- **microsphere-spring-cloud**: Bumped to version `0.2.14`. (fbbc4da)

## Bug Fixes
- Removed duplicated line separators and trailing whitespace from Java source files to improve code cleanliness. (af199e6, #14)

## Documentation
- Updated branch version references in `README`. (e35e24a)

## Other Changes
- Internal merges and version bumps post-release. (a4386b8, dda1ee0, 904ab9b, f0856d6)

**Full Changelog**: https://github.com/microsphere-projects/microsphere-multiactive/compare/0.2.3...0.2.4## v0.2.5

# Release Notes - Version 0.2.5

## Dependency Updates
- **microsphere-spring-cloud** updated to `0.2.15`. ([416555c](#))

## Documentation
- Updated README with version bumps to `0.2.5` / `0.1.5`. ([c3520ef](#))

## Other Changes
- Version bumped to prepare for release and next patch development. ([ab18767](#))
- Routine merges: 
  - `main` into `release`. ([53de7a7](#), [94def6d](#))
  - `release` into `main`. ([5c8d6c4](#))

**Full Changelog**: https://github.com/microsphere-projects/microsphere-multiactive/compare/0.2.4...0.2.5## v0.2.6

# Release Notes - Version 0.2.6

## Documentation
- Updated README for improved clarity and accuracy.  
  - Adjusted branch references to reflect the latest versions.  
  - Bumped displayed version to 0.2.16.  

## Build and Workflow Enhancements
- Merged `main` into release branch to ensure alignment.  
- Updated versioning to prepare for patch release after 0.2.5.  

---

**Note**: No functional changes introduced in this release.  

**Full Changelog**: https://github.com/microsphere-projects/microsphere-multiactive/compare/0.2.5...0.2.6## v0.2.7

# Release Notes - Version 0.2.7

## Dependency Updates
- Bumped `microsphere-spring-cloud` to version `0.2.19`.

## Documentation
- Updated README to reflect version `0.2.7`/`0.1.7`.

## Other Changes
- Refactored to use `static getLogger` and cleaned up imports for improved code quality.
- Maintenance merges between `main` and `release` branches. 
- Updated versioning post-`0.2.6` release. 

**Full Changelog**: https://github.com/microsphere-projects/microsphere-multiactive/compare/0.2.6...0.2.7## v0.2.8

# Release Notes - Version 0.2.8

## New Features
- Add default feature mappings for improved configuration flexibility. ([d2903ab](https://github.com/your-repo/commit/d2903ab))

## Dependency Updates
- Bump Spring Cloud parent version to `0.2.20` for compatibility updates. ([7aabb95](https://github.com/your-repo/commit/7aabb95))

## Documentations
- Update README to reflect new release versions. ([be85cd9](https://github.com/your-repo/commit/be85cd9))

## Build and Workflow Enhancements
- Merged main branch changes into the release branch for synchronization. ([50413cd](https://github.com/your-repo/commit/50413cd), [1d24d51](https://github.com/your-repo/commit/1d24d51), [74a5840](https://github.com/your-repo/commit/74a5840))
- Bump version to next patch after publishing `0.2.7`. ([29ca7a4](https://github.com/your-repo/commit/29ca7a4))
- Merged release branch back into main for final updates. ([c3558a8](https://github.com/your-repo/commit/c3558a8))

--- 

**Full Changelog**: https://github.com/microsphere-projects/microsphere-multiactive/compare/0.2.7...0.2.8## v0.2.9

# Release Notes for v0.2.9

## Dependency Updates
- Bumped `microsphere-spring-cloud` to version **0.2.21**. (_e3563c2_)

## Documentation
- Updated `README` to reflect accurate branch version numbers. (_c8e1375_)

## Build and Workflow Enhancements
- Merged `main` into `release`. (_2619f70_)
- Merged `release` into `main`. (_a1fe87f_)
- Bumped version to next patch after publishing v0.2.8. (_7466b3f_)

---

**Full Changelog**: https://github.com/microsphere-projects/microsphere-multiactive/compare/0.2.8...0.2.9## v0.2.10

# Release Notes - Version 0.2.10

## New Features
- Added zone cloud auto-configuration integration test. ([f169be1](#))
- Enhanced availability-zone condition annotations and tests. ([68daaf2](#))
- Introduced zone constants annotations and associated tests. ([a9b4cad](#))

## Dependency Updates
- Upgraded `microsphere-spring-cloud` to 0.2.23. ([2c18604](#))
- Updated Spring Boot, Spring Cloud, Netflix, and AWS module dependencies for better alignment and optionality. ([73802c6](#), [55a3da1](#), [6842850](#), [8046780](#))
- Added optional Jackson dependency to the AWS module. ([195b3fd](#))
- Removed redundant SLF4J API dependency. ([09cba96](#))

## Test Improvements
- Refined test dependencies across various modules for efficiency and clarity. ([02f6f3c](#), [14c6091](#), [8046780](#))
- Added `spring-test` dependency for improved unit testing coverage. ([0efd0d5](#))
- Cleaned up and enhanced Spring Boot test class list syntax. ([73802c6](#))
- Simplified environment type in listener test. ([07b6993](#))

## Bug Fixes
- Downgraded non-critical zone logs to `trace` for noise reduction. ([39d91fd](#))
- Fixed trailing comma in class condition definition. ([74b29fb](#))

## Build and Workflow Enhancements
- Updated version numbers in `README.md`. ([2a633e3](#))

---

For a full list of changes, refer to the [Full Changelog](#).

**Full Changelog**: https://github.com/microsphere-projects/microsphere-multiactive/compare/0.2.9...0.2.10## v0.2.11

# Release Notes - Version 0.2.11

## Dependency Updates
- Bumped `microsphere-spring-cloud` to `0.2.24`. ([7355a9c](#))

## Documentation
- Updated README to reflect new branch version numbers. ([20c4d94](#))

## Build and Workflow Enhancements
- Merged `main` into `release` branches. [skip ci] ([b2d1929](#), [ced95ab](#))
- Merged `release` into `main`. [skip ci] ([b215d7a](#))
- Bumped version to next patch after publishing `0.2.10`. ([e2f2cfb](#))

**Full Changelog**: https://github.com/microsphere-projects/microsphere-multiactive/compare/0.2.10...0.2.11