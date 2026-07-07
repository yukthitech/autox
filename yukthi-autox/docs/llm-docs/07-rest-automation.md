# REST Automation

REST tests use `s:rest-*` steps with the `RestPlugin` configured in `app-configuration.xml`.

## Prerequisites

```xml
<rest-plugin>
    <baseUrl>#{base.url}</baseUrl>
    <connection name="sessionBased">
        <baseUrl>#{base.url}</baseUrl>
        <default-header name="Auth-Token">${(getPluginAttr('sessionBased').authToken)!''}</default-header>
    </connection>
</rest-plugin>
```

## POST with JSON body

```xml
<testCase name="testPost">
    <description>Test post api invocation</description>
    <wrap:steps>
        <s:rest-invoke-post uri="/emp/save">
            <body><![CDATA[
                {
                    "name" : "Emp1",
                    "address": "some address"
                }
            ]]></body>
        </s:rest-invoke-post>
        <s:set expression="emp1_id" value="attr: response | xpath: //id"/>
        <s:assert-equals actual="prop: attr.result.statusCode" expected="int: 200"/>
    </wrap:steps>
</testCase>
```

After REST calls, `attr.response` contains the response body and `attr.result` contains metadata (e.g. `statusCode`).

## POST form data

```xml
<s:rest-invoke-post uri="/emp/saveForm">
    <form-field name="name">Emp1</form-field>
    <form-field name="address">some address</form-field>
</s:rest-invoke-post>
```

## GET with path variables

```xml
<testCase dependencies="testPost" name="testGet">
    <description>Tests get api invocation</description>
    <s:rest-invoke-get uri="/emp/get/{id}">
        <pathVariable name="id">${attr.emp2_id}</pathVariable>
    </s:rest-invoke-get>
    <s:assert-equals actual="xpath: /attr/response//name" expected="Emp2"/>
</testCase>
```

## DELETE

```xml
<s:rest-invoke-delete uri="/emp/delete/{id}">
    <pathVariable name="id">${attr.emp2_id}</pathVariable>
</s:rest-invoke-delete>
<s:assert-equals actual="prop: attr.result.statusCode" expected="int: 200"/>
```

## Multipart upload

```xml
<s:rest-invoke-multipart-post uri="/emp/saveWithFile">
    <part name="details">
        <value>
            {
                "name" : "Emp3",
                "address": "some address"
            }
        </value>
    </part>
    <attachment file="file:./src/test/resources/testFile.txt" name="file"/>
</s:rest-invoke-multipart-post>
<s:set expression="emp3_id" value="attr: response | xpath: //id"/>
```

## Download file

```xml
<s:rest-invoke-get-file uri="/emp/getFile/{id}">
    <pathVariable name="id">${attr.emp3_id}</pathVariable>
</s:rest-invoke-get-file>
<s:set expression="outputContent" value="file(text=true): ${attr.response}"/>
<s:assert-equals actual="attr: outputContent" expected="Expected file content"/>
```

## Common child elements

| Element | Used in | Description |
|---------|---------|-------------|
| `<body>` | POST, PUT, PATCH | Request body (JSON/XML/text) |
| `<form-field>` | POST | Form field name/value |
| `<pathVariable>` | GET, DELETE, etc. | URI template variable |
| `<param>` | Any | Query parameter |
| `<header>` | Any | Request header |
| `<part>` | Multipart | Named multipart part |
| `<attachment>` | Multipart | File attachment |

## Session and auth events

`RestPlugin` supports plugin events for session management:

- `initialize` — called when creating a new session; set auth tokens here
- `unauthorized` — called on HTTP 401; return `true` to retry the request after re-auth

Define events in a `<function>` and reference from plugin config. See [reference/plugins.md](reference/plugins.md).

## Assertions

```xml
<s:assert-equals actual="prop: attr.result.statusCode" expected="int: 200"/>
<s:assert-equals actual="xpath: /attr/response//name" expected="Emp2"/>
<s:assert-equals actual="attr: response" expected="json: ${attr.expectedPayload}"/>
```

## Step reference

See [reference/steps-rest-api.md](reference/steps-rest-api.md) for all REST steps (`s:rest-invoke-get`, `s:rest-invoke-post`, `s:rest-invoke-put`, `s:rest-invoke-delete`, multipart variants, etc.).
