# Expressions

AutoX uses two expression systems: **prefix expressions** (typed value parsers) and **FreeMarker** (`${...}`).

## Prefix expressions

Prefix expressions transform values. Chain them with `|`:

```xml
<s:set expression="emp1_id" value="attr: response | xpath: //id"/>
<s:assert-equals actual="prop: attr.result.statusCode" expected="int: 200"/>
```

Common prefixes:

| Prefix | Example | Description |
|--------|---------|-------------|
| `attr:` | `attr: response` | Context attribute value |
| `prop:` | `prop: attr.result.statusCode` | Java bean property access |
| `xpath:` | `xpath: //id` | XPath on XML/string |
| `json:` | `json: ${attr.data}` | Parse as JSON |
| `int:` | `int: 200` | Parse as integer |
| `string:` | `string: hello` | String value |
| `expr:` | `expr: param.a + param.b \| int: $` | Evaluate expression |
| `condition:` | `condition: attr.flag == 1` | Boolean condition |
| `file:` | `file:./path/to/file.txt` | Load file content |
| `file(text=true):` | `file(text=true): ${attr.response}` | Load file as text |
| `res:` | `res:/data/test.json` | Classpath resource |
| `uiVal:` | `uiVal: xpath: //input[@name='statusFld']` | UI element value |
| `bean:` | `bean: myBean` | Bean reference |

Prefix `raw-` variants skip expression parsing inside content (e.g. `raw-file:`, `raw-res:`).

See [reference/prefix-expressions.md](reference/prefix-expressions.md) for the full list.

## Resource types

Used in resource parameters and data provider `stepDataList`:

| Type | Example | Description |
|------|---------|-------------|
| `file:` | `file:./src/test/resources/data.json` | File system path |
| `res:` | `res:/data/ext-data-provider.json` | Classpath resource |
| `string:` | `string:{"name":"value"}` | Inline string content |
| `property:` | `property:attr.response` | Property converted to string |

## FreeMarker expressions

Use `${...}` in attribute values and text content:

```xml
<s:rest-invoke-get uri="/emp/get/{id}">
    <pathVariable name="id">${attr.emp2_id}</pathVariable>
</s:rest-invoke-get>

<s:assert-equals actual="${uiValue('id:alertRes', null)}" expected="Okay"/>
```

### Property placeholders (parse time)

`#{prop.name}` in XML is resolved from `app.properties` at parse time (not runtime FreeMarker):

```xml
<s:set expression="testAttr2" value="string: Value: #{test.app.prop}"/>
```

### Common FreeMarker patterns

```xml
<!-- Null-safe default -->
${(getPluginAttr('sessionBased').authToken)!''}

<!-- Boolean for XML attribute -->
enabled="${attr.extDataProvider.exception?c}"

<!-- Existence check -->
<s:if condition="attr.returnFlag??">
```

See [reference/freemarker-methods.md](reference/freemarker-methods.md) for available methods like `uiValue()`, `uiIsVisible()`, `getPluginAttr()`.

## Setting context attributes

```xml
<!-- Simple set -->
<s:set expression="testAttr" value="SimpleValue"/>

<!-- With prefix expression -->
<s:set expression="emp1_id" value="attr: response | xpath: //id"/>

<!-- Global attribute (shared across suites) -->
<s:set expression="attr(global=true): someGlobalAttr" value="int: 100"/>

<!-- Multi-line value element -->
<s:set expression="testAttr3">
    <value>string: Value: #{test.app.prop} ${attr.testAttr}</value>
</s:set>
```

## Custom prefix expressions

Projects can register custom prefix parsers. Example custom locator prefix:

```xml
<s:ui-set-value locator="c:srchDropDown : vehicle" value="Rocket"/>
```

See [reference/prefix-expressions.md](reference/prefix-expressions.md) for built-in parsers and [reference/ui-locators.md](reference/ui-locators.md) for standard locator types.
