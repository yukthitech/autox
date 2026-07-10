# Test Suite XML Files

Test suite XML files are the root artifact for AutoX automation. They are parsed from the folder configured in `app-configuration.xml` `<testSuiteFolder>`.

## Root element

```xml
<testData xmlns:s="http://autox.yukthitech.com/steps"
          xmlns:wrap="http://xmlbeanparser.yukthitech.com/wrap"
          xmlns:f="http://autox.yukthitech.com/functions">
```

## Top-level elements

| Element | Description |
|---------|-------------|
| `<setup>` | Global setup — runs once before any suite (max one across all XML files) |
| `<cleanup>` | Global cleanup — runs once after all suites (max one across all XML files) |
| `<testSuite>` | Group of test cases |
| `<execution-suite>` | Selective execution of specific test cases from named suites |
| `<function>` | Reusable step groups callable via `<f:functionName>` or `<s:function-ref>` |
| `<custom-prefix-expression>` | Project-defined prefix for get/set via `c:name: ...` expressions |
| `<custom-ui-locator>` | Project-defined UI locator for application-specific widgets |

## Global setup and cleanup

```xml
<testData xmlns:s="http://autox.yukthitech.com/steps"
          xmlns:wrap="http://xmlbeanparser.yukthitech.com/wrap">
    <setup>
        <s:log message="Global setup"/>
        <!-- DDL, seed data, etc. -->
    </setup>

    <testSuite name="my-suite">
        <!-- test cases -->
    </testSuite>

    <cleanup>
        <s:log message="Global cleanup"/>
    </cleanup>
</testData>
```

Only one global `<setup>` and one global `<cleanup>` are allowed across all XML files in the test suite folder.

## Execution order

1. **Global setup** — from the single `<setup>` element
2. **Test suites** — generally alphabetical; dependency ordering applies when suites depend on each other
3. **Global cleanup** — from the single `<cleanup>` element

Within a suite: `<setup>` → `<beforeTestCase>` → test cases → `<afterTestCase>` → `<cleanup>`.

## Test suite element

```xml
<testSuite name="rest-test-suites" author="team" description="REST API tests"
           parallelExecutionEnabled="false">
    <setup><!-- suite-level setup --></setup>
    <beforeTestCase><!-- runs before each test case --></beforeTestCase>

    <wrap:data-beans>
        <data-bean s:beanType="com.example.Helper" id="helper"/>
    </wrap:data-beans>

    <testCase name="testPost">
        <description>Test post api</description>
        <!-- steps -->
    </testCase>

    <afterTestCase><!-- runs after each test case --></afterTestCase>
    <cleanup><!-- suite-level cleanup --></cleanup>
</testSuite>
```

## Execution suite (selective runs)

Run only specific test cases from named suites:

```xml
<execution-suite name="smokeTests">
    <entry testSuite="rest-test-suites">
        <test-case>testPost</test-case>
        <test-case>testGet</test-case>
    </entry>
    <entry testSuite="ui-test-suites">
        <!-- empty entry = run all test cases in that suite -->
    </entry>
</execution-suite>
```

## FreeMarker in test XML

String content in test suite XML supports FreeMarker `${...}` expressions. Property placeholders from `app.properties` use `#{prop.name}` and are resolved at parse time.

```xml
<s:set expression="testAttr" value="string: Value: #{test.app.prop}"/>
```

See [06-expressions.md](06-expressions.md) for expression details.

## Shared definitions in `common/`

Files under `test-suites/common/` (or any subfolder) are loaded with the rest of the test suite folder. Use this folder for definitions shared across the application:

- Global `<setup>` / `<cleanup>` (only one of each allowed across all files)
- `<custom-prefix-expression>` and `<custom-ui-locator>` definitions
- Shared `<function>` definitions

Example layout:

```
test-suites/
  common/
    common.xml                 # global setup/cleanup
    common-prefix-expr.xml     # custom prefixes and UI locators
  my-feature/
    feature-tests.xml
```

Custom prefixes registered in `common/` are available in every suite. See [06-expressions.md](06-expressions.md#custom-prefix-expressions).

## XML value specification

AutoX XML is parsed with **XmlBeanParser**, which maps elements to Java beans. For any property on any element (test suites, test cases, steps, data providers, data files), you can specify the value in three equivalent ways:

| Form | When to use | Example |
|------|-------------|---------|
| **Attribute** | Identifiers (`name`, `id`, `beanId`) and simple scalar values | `<testCase name="myTest" parallelExecutionEnabled="true">` |
| **Child text node** | Multi-line values | `<description>First line\nSecond line</description>` |
| **CDATA** | Values containing XML-special characters (`<`, `&`, etc.) | `<body><![CDATA[{"key": "<value>"}]]></body>` |

**Guidelines:**

- Prefer **attributes** for ids, names, flags, and short conditions.
- Prefer **child text** or **CDATA** for long or structured content (JSON bodies, SQL, descriptions).
- Attribute, child text, and CDATA are interchangeable for the same bean property — XmlBeanParser normalizes them to the same setter.

This applies across all AutoX XML: test suites, test cases, steps, validations, plugin configuration, and external data provider files.
