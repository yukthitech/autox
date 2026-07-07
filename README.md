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
- **LLM documentation** for authoring tests with Cursor: [`yukthi-autox/docs/llm-docs/`](yukthi-autox/docs/llm-docs/) — guides, step reference, and Cursor setup template.
- Review `yukthi-autox/src/test/resources/test-suites` and `yukthi-autox/src/test/java` for practical XML flow examples.
- Review `yukthi-prism/src/main/java` for IDE architecture and `yukthi-prism/src/test/java` for XML editor/parser behavior.

## Using AutoX with Cursor in a new project

Use the LLM docs hosted in this repo so Cursor can author correct AutoX XML for REST and UI automation.

### 1. Create a Maven automation project

Add the Yukthi repository and `yukthi-automation` dependency to your `pom.xml`. See [`yukthi-autox/docs/llm-docs/01-getting-started.md`](yukthi-autox/docs/llm-docs/01-getting-started.md) for the full project layout.

Create these files under `src/test/resources/`:

- `app.properties` — environment values (base URL, credentials, etc.)
- `app-configuration.xml` — plugins and test suite folder (see [`02-app-configuration.md`](yukthi-autox/docs/llm-docs/02-app-configuration.md))
- `test-suites/` — your test suite XML files

### 2. Download LLM docs and Cursor rule

From your **project root**, run one of the setup scripts (requires `git`):

**Windows (PowerShell):**

```powershell
git clone --depth 1 https://github.com/yukthitech/autox.git $env:TEMP\autox-setup
& "$env:TEMP\autox-setup\yukthi-autox\docs\llm-docs\cursor-template\setup.ps1" -TargetDir .
Remove-Item -Recurse -Force $env:TEMP\autox-setup
```

Or, if you already have this repo cloned locally:

```powershell
.\path\to\autox\yukthi-autox\docs\llm-docs\cursor-template\setup.ps1 -TargetDir .
```

**Linux / macOS:**

```bash
git clone --depth 1 https://github.com/yukthitech/autox.git /tmp/autox-setup
bash /tmp/autox-setup/yukthi-autox/docs/llm-docs/cursor-template/setup.sh .
rm -rf /tmp/autox-setup
```

Optional flags: `--branch main`, `--repo yukthitech/autox` (shell script only).

This installs:

- `docs/autox-llm/` — guides, reference markdown, and `doc-information.json`
- `.cursor/rules/autox-automation.mdc` — Cursor rule for test XML authoring

### 3. Configure plugins

Enable the plugins you need in `app-configuration.xml`:

- **REST** — `<rest-plugin>` with `<baseUrl>`
- **UI** — `<selenium-plugin>` with `<base-url>` and driver config

See [`07-rest-automation.md`](yukthi-autox/docs/llm-docs/07-rest-automation.md) and [`08-ui-automation.md`](yukthi-autox/docs/llm-docs/08-ui-automation.md).

### 4. Write tests with Cursor

Open the project in Cursor. When asking the agent to create or edit test suites, it will use `docs/autox-llm/` for step names, parameters, and patterns.

Example prompt: *"Create a REST test suite that POSTs an employee and validates the response status is 200."*

### 5. Run tests

```bash
java com.yukthitech.autox.AutomationLauncher \
  ./src/test/resources/app-configuration.xml \
  -rf ./test-reports \
  -prop ./src/test/resources/app.properties
```

See [`10-running-tests.md`](yukthi-autox/docs/llm-docs/10-running-tests.md) for filtering by suite, test case, or group.

### 6. Refresh docs when upgrading AutoX

Re-run the setup script after upgrading the `yukthi-automation` dependency. Compare `docs/autox-llm/autox-version.txt` with your dependency version to confirm docs are current.

### Custom steps

If your project defines custom `@Executable` steps, add your package to `<basePackage>` in `app-configuration.xml` and regenerate docs locally:

```bash
mvn -pl yukthi-autox -q compile exec:java \
  -Dexec.mainClass="com.yukthitech.autox.doc.DocGenerator" \
  -Dexec.args="com.yukthitech,com.mycompany.autox ./docs/autox-llm"
```


## Driver download

Chrome driver can be downloaded from [Chrome Driver](https://autox.yukthitech.com/downloads/chrome-driver-149.0.7827.53.zip).