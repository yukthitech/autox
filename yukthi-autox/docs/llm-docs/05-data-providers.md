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
| `onFetchEval` on test case data | Re-evaluate FreeMarker on each data access |

## Custom data providers

Implement `com.yukthitech.autox.dataprovider.IDataProvider` or extend `AbstractDataProvider` for custom logic.
