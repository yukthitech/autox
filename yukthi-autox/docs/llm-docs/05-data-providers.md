# Data Providers

Data providers run a test case multiple times, once per data item. The current iteration's data is accessed via `attr.{providerName}`.

## List data provider

Most common provider. Supports inline data or external files.

### Inline data

Each `<data>` element becomes one iteration. For simple values, `attr.{name}` is the string value directly.

```xml
<testCase name="testWithInlineData" parallelExecutionEnabled="true">
    <description>Runs once per data value</description>

    <list-data-provider name="dataString">
        <data>abc</data>
        <data>def</data>
        <data>ghi</data>
    </list-data-provider>

    <data-setup>
        <s:set expression="dataSetupAttr" value="data-setup-val"/>
    </data-setup>

    <wrap:steps>
        <s:log message="Processing: ${attr.dataString}"/>
        <s:assert-equals actual="attr: dataSetupAttr" expected="data-setup-val"/>
    </wrap:steps>

    <data-cleanup>
        <s:log message="Data iteration complete"/>
    </data-cleanup>
</testCase>
```

### External JSON data

```xml
<list-data-provider name="extDataProvider" stepDataList="res:/data/ext-data-provider.json"/>
```

`res:/data/ext-data-provider.json` format:

```json
[
    {
        "name": "case1",
        "value": {
            "paramValue": 10,
            "result": 5,
            "exception": false
        }
    },
    {
        "name": "case3",
        "value": {
            "paramValue": -10,
            "exception": true
        }
    }
]
```

Access fields: `${attr.extDataProvider.paramValue}`, `${attr.extDataProvider.result}`.

### External XML data

```xml
<list-data-provider name="extDataProvider" stepDataList="res:/data/ext-data-provider.xml"/>
```

`ext-data-provider.xml` format:

```xml
<test-data-list>
    <test-case-data name="case1">
        <dynamic-value paramValue="10" result="5" exception="false"/>
    </test-case-data>
</test-data-list>
```

## Default data provider

Supports multiple `stepDataList` sources and complex bean-based data (clone scenarios).

```xml
<default-data-provider name="dataProvider">
    <stepDataList>res:/data/def-data-provider-clone.xml</stepDataList>
</default-data-provider>

<!-- or as attribute -->
<default-data-provider name="extDataProvider" stepDataList="res:/data/onFetchExpr-data-provider.xml"/>
```

Uses `r:` reserved nodes (`r:register-bean`, `r:clone`, `r:property`) and evaluates data at XML parse time. For new projects, prefer **dynamic data provider** below.

## Dynamic data provider

Loads an external `test-data-list` XML file and evaluates each `<test-case-data>` entry **at runtime** when the iteration starts. Suited for payloads that vary per case and depend on properties declared earlier in the same data object.

```xml
<dynamic-data-provider name="regData" stepDataList="res:/data/dynamic-data-provider-basic.xml"/>
```

External file example (`dynamic-data-provider-basic.xml`):

```xml
<test-data-list>
    <bean id="payload">
        <value>json(jel=true): {
            "name": "test-user",
            "active": "@fmarker: ifTrue(_this.statusCode == 200, true, false)",
            "captcha": "@fmarker: _this.useValidCaptcha"
        }</value>
    </bean>

    <test-case-data name="successfulCase">
        <description>Basic dynamic data provider test with properties and clone</description>
        <value>
            <property name="useValidCaptcha" value="boolean: true"/>
            <property name="statusCode" value="int: 200"/>

            <clone beanId="payload" property="input"/>

            <property name="expected">
                <value>json: {
                    "name": "test-user",
                    "active": true,
                    "captcha": true
                }</value>
            </property>
        </value>
    </test-case-data>
</test-data-list>
```

Test case assertion (covered by `TAutomation.testSuccessCases` via `jobj-test-suite.xml`):

```xml
<testCase name="testDynamicDataProviderBasic">
    <dynamic-data-provider name="regData" stepDataList="res:/data/dynamic-data-provider-basic.xml"/>
    <s:assert-deep-equals actual="prop: attr.regData.input" expected="prop: attr.regData.expected"/>
</testCase>
```

### Evaluation model

| Element | Parse time | Runtime (`getValue()`) |
|---------|------------|------------------------|
| `<bean>` | Store raw value expression | Not evaluated directly |
| `<property name value="..."/>` | Store raw `value` attribute | Evaluated via prefix expressions (like `set` step) |
| `<clone beanId property="..."/>` | Store `beanId` + `property` | Reloads bean via `getValueByExpressionString()` (fresh object each time) |

Properties and clones run in **declaration order**. After each `<property>`, its result is added to a `LinkedHashMap` exposed as `_this` on `AutomationContext`. When a later `<clone>` evaluates its bean expression, **all properties declared before that clone** are available on `_this`.

### `_this` references

`_this` is the in-progress test-case-data map (not an `attr` — avoids name collisions with user data).

**In a later `<property>`** (via prefix expression):

```xml
<property name="derivedFlag" value="prop: _this.useValidCaptcha"/>
```

**In a `<bean>` loaded by `<clone>`** — use `json(jel=true)` with `@fmarker:` so values can reference `_this` and call FreeMarker methods:

```xml
<bean id="payload">
    <value>json(jel=true): {
        "active": "@fmarker: ifTrue(_this.statusCode == 200, true, false)",
        "captcha": "@fmarker: _this.useValidCaptcha"
    }</value>
</bean>
```

Alternatively, external template files work with `res(template=true):` and `${_this.field}` FreeMarker syntax.

After loading completes, iteration data is available as usual: `attr.{providerName}.field` and `attr.{providerName}$name`.

### Notes

- **No inline data** — external XML file only (`stepDataList`).
- **No `onFetchEval`** — data is always created at runtime per iteration.
- **No clone child elements** — use `_this` in bean expressions/templates instead of `r:clone` `<set>`/`<remove>`.
- Property values can be specified as a **`value` attribute** or child text/CDATA (XmlBeanParser supports both).
- Old `list-data-provider` / `default-data-provider` XML formats remain supported unchanged.

## Range data provider

Generates integer values from `start` to `end` inclusive.

```xml
<range-data-provider name="index" start="1" end="10"/>
```

Access current value: `${attr.index}`.

## REST example with data provider

```xml
<testCase name="testCreateEmployees" parallelExecutionEnabled="true">
    <description>Create multiple employees via REST</description>

    <list-data-provider name="empName">
        <data>dpEmp1</data>
        <data>dpEmp2</data>
    </list-data-provider>

    <s:rest-invoke-post uri="/emp/save">
        <body><![CDATA[{"name":"${attr.empName}","address":"some address"}]]></body>
    </s:rest-invoke-post>
    <s:assert-equals actual="prop: attr.result.statusCode" expected="int: 200"/>
</testCase>
```

## Attributes

| Attribute/Element | Description |
|-------------------|-------------|
| `parallelExecutionEnabled="true"` on test case | Run iterations in parallel |
| `sharedContext="true"` on test case | Share context between iterations (sequential only) |
| `<data-setup>` | Runs once before all iterations |
| `<data-cleanup>` | Runs once after all iterations |
| `onFetchEval` on test case data | Re-evaluate FreeMarker on each data access (legacy providers only) |

## Custom data providers

Implement `com.yukthitech.autox.dataprovider.IDataProvider` or extend `AbstractDataProvider` for custom logic. Test-case iteration data implements `com.yukthitech.autox.test.ITestCaseData` (`getName()`, `getDescription()`, `getValue()`).
