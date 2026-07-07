# Application Configuration

The file `app-configuration.xml` configures the AutoX runtime: report settings, test suite folder, plugins, beans, and persistence.

## Root element

```xml
<app xmlns:ccg="http://xmlbeanparser.yukthitech.com/reserved"
     xmlns:wrap="http://xmlbeanparser.yukthitech.com/wrap">
```

## Core elements

| Element | Required | Description |
|---------|----------|-------------|
| `<report-name>` | Yes | Title shown in generated reports |
| `<dateFomat>` | No | Date format in reports (note spelling) |
| `<testSuiteFolder>` | Yes | Folder scanned recursively for test suite XML |
| `<wrap:basePackages>` | No | Extra packages to scan for custom steps (default: `com.yukthitech`) |
| `<wrap:plugins>` | No | Plugin configurations |
| `<wrap:data-beans>` | No | Global beans available in all suites |
| `<wrap:logMonitors>` | No | File/browser log monitors |
| `<excludedGroups>` | No | Test groups to skip at runtime |
| `<storage-repository-factory>` | No | Persistence for reports and execution history |

## Property injection

Use `#{...}` in this file to reference `app.properties`:

```xml
<rest-plugin>
    <baseUrl>#{base.url}</baseUrl>
</rest-plugin>
```

FreeMarker `${...}` can also be used in plugin configuration for dynamic values.

## REST plugin

Required for `s:rest-*` steps.

```xml
<rest-plugin>
    <baseUrl>http://localhost:8080/app/api</baseUrl>

    <!-- Optional named connections with per-connection headers -->
    <connection name="sessionBased">
        <baseUrl>http://localhost:8080/app/api</baseUrl>
        <default-header name="Auth-Token">${(getPluginAttr('sessionBased').authToken)!''}</default-header>
    </connection>
</rest-plugin>
```

- `baseUrl` on `<rest-plugin>` sets the default connection.
- Named `<connection>` elements support session-based auth via plugin events (`initialize`, `unauthorized`). See [reference/plugins.md](reference/plugins.md).

## Selenium (UI) plugin

Required for `s:ui-*` steps.

```xml
<selenium-plugin maxSessions="3">
    <base-url>#{base.url}</base-url>
    <wrap:drivers>
        <driver name="autoxChrome" class-name="com.yukthitech.autox.config.selenium.AutoxChromeDriver">
            <system-property name="webdriver.chrome.driver">./drivers/chromedriver.exe</system-property>
            <profile-option name="chrome.binary">./drivers/chrome.exe</profile-option>
            <extraArguments>--remote-allow-origins=*</extraArguments>
        </driver>
    </wrap:drivers>
</selenium-plugin>
```

- `base-url` is required; prepended to `s:ui-goto-page` URI values.
- `maxSessions` limits concurrent browser sessions.

## Database plugin

Required for SQL steps.

```xml
<db-plugin defaultDataSource="dataSource">
    <dataSource name="dataSource" ccg:beanType="org.apache.commons.dbcp2.BasicDataSource">
        <driverClassName>com.mysql.jdbc.Driver</driverClassName>
        <url>#{db.url}</url>
        <username>#{db.user}</username>
        <password>#{db.password}</password>
    </dataSource>
</db-plugin>
```

## Global data beans

Beans declared here are available in all test suites via `s:beanRef`:

```xml
<wrap:data-beans>
    <data-bean s:beanType="com.example.MyHelper" id="myHelper"/>
</wrap:data-beans>
```

Suite-level beans override or extend app-level beans. See [09-functions-and-reuse.md](09-functions-and-reuse.md).

## Custom steps

If your project defines custom steps with `@Executable`, add your package to `<wrap:basePackages>`:

```xml
<wrap:basePackages>
    <basePackage>com.yukthitech</basePackage>
    <basePackage>com.mycompany.autox.steps</basePackage>
</wrap:basePackages>
```

Regenerate `doc-information.json` with your packages to include custom steps in LLM reference docs.
