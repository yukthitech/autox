# Running Tests

## Basic command

```bash
java com.yukthitech.autox.AutomationLauncher \
  ./src/test/resources/app-configuration.xml \
  -rf ./test-reports \
  -prop ./src/test/resources/app.properties
```

| Argument | Required | Description |
|----------|----------|-------------|
| `<app-config-file>` | Yes | Path to `app-configuration.xml` |
| `-rf`, `--reports-folder` | Yes | Output folder for HTML reports |
| `-prop`, `--property-file` | No | Path to `app.properties` |

## Filtering test execution

| Argument | Description |
|----------|-------------|
| `-ts`, `--test-suites` | Comma-separated suite names to run |
| `-tc`, `--test-cases` | Comma-separated test case names (include dependencies) |
| `-tg`, `--test-groups` | Comma-separated groups to run |
| `-es`, `--execution-suite` | Name of `<execution-suite>` to run |

Examples:

```bash
# Run specific suites
java com.yukthitech.autox.AutomationLauncher \
  ./src/test/resources/app-configuration.xml \
  -rf ./test-reports \
  -prop ./src/test/resources/app.properties \
  -ts rest-test-suites,ui-test-suites

# Run specific test cases
java com.yukthitech.autox.AutomationLauncher \
  ./src/test/resources/app-configuration.xml \
  -rf ./test-reports \
  -ts rest-test-suites \
  -tc testPost,testGet

# Run by group
java com.yukthitech.autox.AutomationLauncher \
  ./src/test/resources/app-configuration.xml \
  -rf ./test-reports \
  -tg smoke,api
```

## Excluding groups

In `app-configuration.xml`:

```xml
<excludedGroups>download,slow</excludedGroups>
```

Test cases tagged with these groups are skipped unless explicitly included via `-tg`.

## Reports

Reports are generated in the folder specified by `-rf`. Open `index.html` in a browser to view:

- Suite and test case results
- Step execution logs
- Screenshots (UI tests)
- Failure details and stack traces

## Maven execution

Add to `pom.xml`:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.1.0</version>
    <configuration>
        <mainClass>com.yukthitech.autox.AutomationLauncher</mainClass>
        <arguments>
            <argument>./src/test/resources/app-configuration.xml</argument>
            <argument>-rf</argument>
            <argument>./test-reports</argument>
            <argument>-prop</argument>
            <argument>./src/test/resources/app.properties</argument>
        </arguments>
    </configuration>
</plugin>
```

Run with: `mvn exec:java`

## Debugging

Use Prism IDE for breakpoints and step-through debugging. For CLI-only workflows, add logging steps:

```xml
<s:log message="Current emp id: ${attr.emp1_id}"/>
<s:ui-log-screen-shot name="debug-state.png"/>
```

## Parallel execution

Enable at suite or test case level:

```xml
<testSuite name="my-suite" parallelExecutionEnabled="true">
    <testCase name="dataDrivenTest" parallelExecutionEnabled="true">
```

Data provider iterations can also run in parallel with `parallelExecutionEnabled="true"` on the test case.

## Refreshing LLM docs

When upgrading AutoX, re-run the setup script to pull the latest `llm-docs` from GitHub. Compare `autox-version.txt` in your project's `docs/autox-llm/` with your Maven dependency version.
