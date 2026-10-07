# Getting Started with AutoX

AutoX is a Java-based XML-driven automation framework for REST, UI (Playwright), SQL, and general integration testing.

## Maven dependency

Add the Yukthi repository, the launcher, and the batteries-included aggregator to your `pom.xml`:

```xml
<repository>
    <id>yukthitech</id>
    <name>yukthitech</name>
    <url>https://oss.sonatype.org/content/groups/public</url>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>

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

`yukthi-autox-all` pulls in language, REST, SQL, MongoDB, mail, SSH, mock, webutils, and **Playwright UI** modules.

For a slimmer classpath, keep `yukthi-automation` and add only the feature modules you need (for example `yukthi-autox-lang` + `yukthi-autox-rest`). For UI, add **either** `yukthi-autox-ui-playwright` **or** `yukthi-autox-ui-selenium` — never both (duplicate `ui-*` step names).

See the root [README.md](https://github.com/yukthitech/autox/blob/main/README.md#maven-dependencies-for-applications) and [15-module-structure.md](15-module-structure.md) for purpose-specific POM examples.

## Recommended project layout

```
my-automation-project/
  docs/autox-llm/              # LLM docs (downloaded from autox repo)
  src/test/resources/
    app.properties             # Environment-specific values
    app-configuration.xml      # Framework configuration
    test-suites/               # Test suite XML files (recursive scan)
      common/                  # Shared setup, custom prefixes, functions
    data/                      # External data for data providers
  test-reports/                # Generated reports (output)
```

Playwright manages browsers itself; you do not need a local `chromedriver` when using `<playwright-plugin>`.

## Application properties

Create `src/test/resources/app.properties` with environment-specific values:

```properties
base.url=http://localhost:8080/myapp
db.url=jdbc:mysql://localhost:3306/test
db.user=test
db.password=test
```

Properties are injected into `app-configuration.xml` using `#{property.name}` expressions.

System properties and environment variables use prefixes:

- `#{system.PROP_NAME}` — JVM `-D` arguments
- `#{env.ENV_VAR}` — environment variables

## Application configuration

Create `src/test/resources/app-configuration.xml`. See [02-app-configuration.md](02-app-configuration.md) for full details.

Minimum required elements:

```xml
<app xmlns:ccg="http://xmlbeanparser.yukthitech.com/reserved"
     xmlns:wrap="http://xmlbeanparser.yukthitech.com/wrap">
    <report-name>My Automation</report-name>
    <dateFomat>dd-MMM-yyyy</dateFomat>
    <testSuiteFolder>./src/test/resources/test-suites</testSuiteFolder>

    <wrap:plugins>
        <rest-plugin>
            <baseUrl>#{base.url}</baseUrl>
        </rest-plugin>
    </wrap:plugins>
</app>
```

For UI tests, also configure `<playwright-plugin>` (see [08-ui-automation.md](08-ui-automation.md)).

## Test suites

Create XML files under `test-suites/`. All `.xml` files are scanned recursively.

```xml
<testData xmlns:s="http://autox.yukthitech.com/steps"
          xmlns:wrap="http://xmlbeanparser.yukthitech.com/wrap">
    <testSuite name="my-suite">
        <testCase name="myTest">
            <description>My first test</description>
            <s:log message="Hello AutoX"/>
        </testCase>
    </testSuite>
</testData>
```

## Running tests

```bash
java com.yukthitech.autox.AutomationLauncher \
  ./src/test/resources/app-configuration.xml \
  -rf ./test-reports \
  -prop ./src/test/resources/app.properties
```

See [10-running-tests.md](10-running-tests.md) for CLI filtering options.

## Next steps

- [02-app-configuration.md](02-app-configuration.md) — configure REST/UI plugins
- [03-test-suite-xml.md](03-test-suite-xml.md) — test suite file structure
- [07-rest-automation.md](07-rest-automation.md) or [08-ui-automation.md](08-ui-automation.md) — write your first real tests
- [15-module-structure.md](15-module-structure.md) — Maven modules and Playwright vs Selenium
