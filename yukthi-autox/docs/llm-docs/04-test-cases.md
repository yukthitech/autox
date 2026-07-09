# Test Cases

A `<testCase>` is a single test definition containing steps and/or validations.

## Required fields

| Field | Rule |
|-------|------|
| `name` attribute | Required; must not contain `#` |
| `<description>` | Required; shown in reports |
| Steps/validations | At least one step or validation required |

## Basic structure

```xml
<testCase name="testPost" dependencies="setupTest" groups="smoke,api"
          author="kranthi" parallelExecutionEnabled="false" sharedContext="false">
    <description>Test post api invocation</description>

    <tag name="priority">high</tag>
    <groups>smoke,api</groups>

    <wrap:steps>
        <s:rest-invoke-post uri="/emp/save">
            <body><![CDATA[{"name":"Emp1","address":"some address"}]]></body>
        </s:rest-invoke-post>
        <s:set expression="emp1_id" value="attr: response | xpath: //id"/>
        <s:assert-equals actual="prop: attr.result.statusCode" expected="int: 200"/>
    </wrap:steps>

    <wrap:validations>
        <s:assert-equals actual="xpath: /attr/response//name" expected="Emp1"/>
    </wrap:validations>
</testCase>
```

Steps can be placed directly under `<testCase>` or grouped in `<wrap:steps>` / `<wrap:validations>`.

For XML attribute vs text vs CDATA conventions, see [03-test-suite-xml.md](03-test-suite-xml.md#xml-value-specification).

## Attributes

| Attribute | Description |
|-----------|-------------|
| `dependencies` | Comma-separated test case names in the same suite; skipped if dependency failed |
| `groups` | Comma-separated groups for CLI filtering (`-tg`) or `<excludedGroups>` in app config |
| `author` | Comma-separated author names for reports |
| `parallelExecutionEnabled` | Run data-provider iterations in parallel |
| `sharedContext` | Share context across data-provider iterations (ignored when parallel) |

## Lifecycle hooks

| Element | When it runs |
|---------|--------------|
| `<setup>` | Before this test case's steps |
| `<cleanup>` | After this test case's steps |
| `<data-setup>` | Once before all data-provider iterations (only with data provider) |
| `<data-cleanup>` | Once after all data-provider iterations (only with data provider) |

## Expected exceptions

Assert that a specific exception is thrown:

```xml
<testCase name="testError">
    <description>Expects invalid argument exception</description>
    <s:invokeMethod method="throwError">
        <object s:beanRef="myBean"/>
    </s:invokeMethod>
    <expectedException type="com.yukthitech.utils.exceptions.InvalidArgumentException">
        <property name="message" value="MESSAGE"/>
    </expectedException>
</testCase>
```

With data providers, enable conditionally:

```xml
<expectedException enabled="${attr.extDataProvider.exception?c}"
    type="com.yukthitech.utils.exceptions.InvalidStateException">
    <property name="message" value="Value can not be less than zero: ${attr.extDataProvider.paramValue}"/>
</expectedException>
```

## Failure actions

Run diagnostic steps when a test fails:

```xml
<failureAction>
    <s:ui-log-screen-shot name="failure.png"/>
    <s:log message="Test failed, screenshot captured"/>
</failureAction>
```

## Best practices

- End tests with at least one validation (`s:assert-*` or `s:ui-assert-*`).
- Use `dependencies` to chain tests that share state (e.g. create entity then read it).
- Prefer `wrap:steps` and `wrap:validations` for readability in complex tests.
- Use descriptive `name` and `<description>` values — they appear in HTML reports.
