# AutoX Monorepo

This repository contains AutoX (XML-driven automation framework) and Prism (Java desktop IDE for authoring and running AutoX projects), along with related support modules.

## What is AutoX?

AutoX is a Java-based automation framework where test flows are authored in XML and executed by the AutoX runtime.

- XML test data is parsed into strongly typed models (`testData`, `testSuite`, `testCase`, `setup`, `cleanup`, `function`, step tags).
- Step tags are mapped to Java executables via `@Executable` annotations and package scanning.
- Execution is hierarchical: suite-group -> suite -> test-case -> steps, with setup/cleanup hooks at multiple levels.
- Test cases support dependency ordering, data-driven expansion, and optional parallel execution.
- Runtime context supports expressions, attributes/params, plugin session data, and reporting.
- Plugin architecture enables integrations like Playwright-based UI automation, REST, and SQL operations.

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

- `yukthi-autox-parent`: Parent POM and reactor for all AutoX library modules.
- `yukthi-autox-common` / `yukthi-autox-lang` / `yukthi-autox-rest` / …: Split AutoX libraries (see `yukthi-autox/docs/llm-docs/15-module-structure.md`).
- `yukthi-autox-ui-playwright`: Default UI automation (Playwright). Opt-in alternative: `yukthi-autox-ui-selenium`.
- `yukthi-autox-all`: Batteries-included aggregator (includes Playwright UI).
- `yukthi-autox`: Artifact `yukthi-automation` — launcher/core jar and framework tests.
- `yukthi-prism`: Prism IDE (UI, editor, run/debug integration, help/search).
- `docs`: AutoX documentation and configuration references.

## Getting oriented

- Start with `docs` for concepts and configuration.
- **LLM documentation** for authoring tests with Cursor: [`yukthi-autox/docs/llm-docs/`](yukthi-autox/docs/llm-docs/) — guides, step reference, and Cursor setup template.
- Review `yukthi-autox/src/test/resources/test-suites` and `yukthi-autox/src/test/java` for practical XML flow examples.
- Review `yukthi-prism/src/main/java` for IDE architecture and `yukthi-prism/src/test/java` for XML editor/parser behavior.

## Maven dependencies for applications

Current AutoX version: **`4.0.0-SNAPSHOT`** (`groupId`: `com.yukthitech`).

Add the Yukthi repository (required for snapshots):

```xml
<repository>
    <id>yukthitech</id>
    <name>yukthitech</name>
    <url>https://oss.sonatype.org/content/groups/public</url>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
```

### Recommended: full stack (Playwright UI)

Use the launcher plus the batteries-included aggregator:

```xml
<!-- Runtime / AutomationLauncher -->
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-automation</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>

<!-- Lang, REST, SQL, Mongo, Playwright UI, mail, SSH, mock, webutils -->
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-autox-all</artifactId>
    <version>4.0.0-SNAPSHOT</version>
    <type>pom</type>
</dependency>
```

`yukthi-autox-all` includes Playwright UI (`yukthi-autox-ui-playwright`), not Selenium.

### Pick modules by purpose

Always include the launcher, then add only the feature modules you need:

```xml
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-automation</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
```

| Purpose | Artifact | Plugin / notes |
|---------|----------|----------------|
| Control flow (`if`, `for`, `try`, …) | `yukthi-autox-lang` | Always useful; pulled by most other modules’ consumers via common usage |
| REST API tests | `yukthi-autox-rest` | `<rest-plugin>` |
| SQL / RDBMS | `yukthi-autox-sql` | `<db-plugin>` |
| MongoDB | `yukthi-autox-mongo` | `<mongo-plugin>` |
| UI (Playwright, default) | `yukthi-autox-ui-playwright` | `<playwright-plugin>` |
| UI (Selenium, opt-in) | `yukthi-autox-ui-selenium` | `<selenium-plugin>` — **do not** combine with Playwright |
| Email | `yukthi-autox-mail` | `<email-plugin>` |
| SSH | `yukthi-autox-ssh` | SSH plugin / steps |
| HTTP mock server | `yukthi-autox-mock` | Mock server steps |
| Webutils helpers | `yukthi-autox-webutils` | Shared web helpers |

Example — REST + Playwright UI only:

```xml
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-automation</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-autox-lang</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-autox-rest</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-autox-ui-playwright</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
```

Example — REST + SQL (no UI):

```xml
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-automation</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-autox-lang</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-autox-rest</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-autox-sql</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
```

Example — Selenium UI instead of Playwright (exclude Playwright from the classpath):

```xml
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-automation</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-autox-lang</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-autox-ui-selenium</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
```

Do **not** put both `yukthi-autox-ui-playwright` and `yukthi-autox-ui-selenium` on the classpath (duplicate `ui-*` step names).

Feature modules transitively depend on `yukthi-autox-common`. Full module map: [`15-module-structure.md`](yukthi-autox/docs/llm-docs/15-module-structure.md).

## Using AutoX with Cursor in a new project

Use the LLM docs hosted in this repo so Cursor can author correct AutoX XML for REST and UI automation.

### 1. Create a Maven automation project

Add the Yukthi repository and the [Maven dependencies](#maven-dependencies-for-applications) above (typically `yukthi-automation` + `yukthi-autox-all`). See [`yukthi-autox/docs/llm-docs/01-getting-started.md`](yukthi-autox/docs/llm-docs/01-getting-started.md) for the full project layout.

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
- **UI** — `<playwright-plugin>` with `<base-url>` and driver config (`browser-type`, etc.)

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

Re-run the setup script after upgrading the `yukthi-autox-all` dependency. Compare `docs/autox-llm/autox-version.txt` with your dependency version to confirm docs are current.

### Custom steps

If your project defines custom `@Executable` steps, add your package to `<basePackage>` in `app-configuration.xml` and regenerate docs locally:

```bash
# From yukthi-autox-parent; use an absolute out path so docs land in your project
mvn -pl ../yukthi-autox -q -DskipTests exec:java \
  -Dexec.classpathScope=test \
  -Dexec.mainClass="com.yukthitech.autox.doc.DocGenerator" \
  -Dexec.args="com.yukthitech,com.mycompany.autox /absolute/path/to/docs/autox-llm"
```


## Browser drivers

Playwright (default UI module) installs and manages browsers itself — no separate chromedriver download is required for `<playwright-plugin>`.

If you opt into Selenium (`yukthi-autox-ui-selenium`), Chrome driver can be downloaded from [Chrome Driver](https://autox.yukthitech.com/downloads/chrome-driver-149.0.7827.53.zip).