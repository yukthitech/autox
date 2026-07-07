# AutoX Monorepo

This repository contains AutoX (XML-driven automation framework) and Prism (Java desktop IDE for authoring and running AutoX projects), along with related support modules.

## What is AutoX?

AutoX is a Java-based automation framework where test flows are authored in XML and executed by the AutoX runtime.

- XML test data is parsed into strongly typed models (`testData`, `testSuite`, `testCase`, `setup`, `cleanup`, `function`, step tags).
- Step tags are mapped to Java executables via `@Executable` annotations and package scanning.
- Execution is hierarchical: suite-group -> suite -> test-case -> steps, with setup/cleanup hooks at multiple levels.
- Test cases support dependency ordering, data-driven expansion, and optional parallel execution.
- Runtime context supports expressions, attributes/params, plugin session data, and reporting.
- Plugin architecture enables integrations like Selenium-based UI automation and SQL operations.

Primary implementation lives under `yukthi-autox`.

## What is Prism?

Prism is the dedicated Java Swing IDE for AutoX projects, implemented in `yukthi-prism`.

- XML-focused editor for AutoX test files with syntax support, parse diagnostics, and completion.
- AutoX-aware run/debug actions (project/suite/test-case/function scope).
- Breakpoints and step-debug controls integrated with AutoX execution.
- Console/report integration with file-line navigation back to sources.
- Project explorer and execution environment management for multiple runs/debug sessions.
- Embedded searchable help built from AutoX documentation metadata.

Prism is intentionally specialized for AutoX workflows rather than being a generic IDE.

## Key modules

- `yukthi-autox`: AutoX framework core, parser, execution engine, plugins, and tests.
- `yukthi-prism`: Prism IDE (UI, editor, run/debug integration, help/search).
- `docs`: AutoX documentation and configuration references.
- `yukthi-autox-parent`: Parent/build aggregation module.

## Getting oriented

- Start with `docs` for concepts and configuration.
- Review `yukthi-autox/src/test/resources/test-suites` and `yukthi-autox/src/test/java` for practical XML flow examples.
- Review `yukthi-prism/src/main/java` for IDE architecture and `yukthi-prism/src/test/java` for XML editor/parser behavior.

## Driver download

Chrome driver can be downloaded from [Chrome Driver](https://autox.yukthitech.com/downloads/chrome-driver-149.0.7827.53.zip).