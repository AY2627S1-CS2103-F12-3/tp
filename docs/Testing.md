---
  layout: default.md
  title: "Testing guide"
  pageNav: 3
---

# Testing guide

<!-- * Table of Contents -->
<page-nav-print />

<!-- -------------------------------------------------------------------------------------------------------------------- -->

## Running tests

You can run tests in two ways.

* **Method 1: Using IntelliJ JUnit test runner**
  * To run all tests, right-click on the `src/test/java` folder and choose `Run 'All Tests'`
  * To run a subset of tests, you can right-click on a test package,
    test class, or a test and choose `Run 'ABC'`
* **Method 2: Using Gradle**
  * Open a console and run the command `gradlew clean test` (Mac/Linux: `./gradlew clean test`)

<box type="info" seamless>

**Link**: Read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html) to learn more about using Gradle.
</box>

--------------------------------------------------------------------------------------------------------------------

## Types of tests

This project has three types of tests:

1. *Unit tests* target the lowest-level methods and classes.<br>
   For example: `seedu.address.commons.StringUtilTest`
1. *Integration tests* check how multiple code units work together; the individual units are assumed to work.<br>
   For example: `seedu.address.storage.StorageManagerTest`
1. *Hybrid tests* combine unit and integration testing. These tests check both the individual units and how they work together.<br>
   For example: `seedu.address.logic.LogicManagerTest`

## Assertions and role workflow diagnostics

Gradle already enables Java assertions for both `run` and `test`. Enable `-ea` in an IDE application run configuration as well. For a packaged development copy, use `java -ea -jar addressbook.jar`; compare normal command outcomes with `java -da -jar addressbook.jar` using the same disposable fixture. Neither input validation nor a required mutation may depend on assertions being enabled.

Run `./gradlew test --tests '*RoleWorkflowDiagnosticsTest'` to check diagnostic levels and messages, preserved candidate details and unchanged state after rejection. The tests attach an in-memory handler to the existing logger and restore its level and handler afterwards. They do not configure a second log file. Assertion tests deliberately exercise two programmer-contract violations: a missing internal status and a lookup collaborator returning the wrong existing role. These are not examples of invalid recruiter commands; parser and command tests continue to cover user errors through their existing exceptions.

Recovery diagnostic checks extend `MainAppTest` and `RecruitmentDataRecoveryTest`. They verify the loading warning, actual backup and replacement paths, failure severity and the absence of success messages for stages that failed, while retaining the existing byte-preservation and blocked-startup checks. Run them with `./gradlew test --tests '*MainAppTest' --tests '*RecruitmentDataRecoveryTest'`.

These tests share the `LogCapture` utility with role workflow tests instead of duplicating logging setup. It records messages in memory, provides source-class and severity checks, and restores the previous level and handler configuration when closed. `LogCaptureTest` verifies restoration even when the test body throws and checks that record snapshots cannot be modified. Logging configuration is process-wide, so these configuration-sensitive tests must run sequentially, as they do under the current Gradle configuration.
