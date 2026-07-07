# Prefix Expressions

Prefix expressions parse and transform values. Chain with `|`.

Includes parsers from `DefaultPrefixExpressions` and plugin-specific parsers (SQL, Mongo, UI, etc.).

### attr:

- **Description**: Parses specified expression as context attribute.
- **Content type**: ATTRIBUTE

**Parameters:**

- `plugin` — If true, plugin session attributes will be accessed Default: `false`
- `pluginName` — When plugin attributes are being accessed this will be used. This is connection/driver/datasource name based on plugin Default: `null`
- `global` — During set if value is true, the attribute will be set at global level (get and remove will access local attributes only) Default: `false`

**Examples:**

*Default*

```
attr: attrName
```

*Using attr with type while setting attribute value. Below example sets value 10 (integer) on context with name intAttr1*

```
<s:set expression="attr(int): intAttr1" value="10"/>
```

*Using attr to access context attribute value*

```
<s:assert-not-null value="attr: intAttr" />
```


### bfile:

- **Description**: Parses specified expression as file path and loads it as binary data (byte array).As part of 'set' the value is expected to be byte[] which will be written to specified file.
- **Content type**: NONE

**Examples:**

*Default*

```
bfile: /tmp/data
```


### boolean:

- **Description**: Parses specified expression into boolean. If expression value is true (case insensitive), then result will be true.  In case of '$', current value's string value will be parsed.
- **Content type**: NONE

**Examples:**

*Default*

```
boolean: True
```

*Using to convert string into boolean and set Boolean object on context*

```
<s:set expression="booleanAttr" value="boolean: true"/>
```


### bres:

- **Description**: Parses specified expression as resource path and loads it as binary data (byte array).
- **Content type**: NONE

**Examples:**

*Default*

```
bres: /tmp/data.png
```


### condition:

- **Description**: Evaluates specified expression as condition and resultant boolean value will be returned
- **Content type**: FM_EXPRESSION

**Examples:**

*Default*

```
condition: (attr.flag == true)
```

*Using condition in assertion to check simple conditions*

```
<s:assert-true value="condition: attr.intAttr1 gt 5" />
```


### date:

- **Description**: Parses specified expression into date. In case of '$', current value's string value will be parsed.
- **Content type**: NONE

**Examples:**

*Default*

```
date: 21/3/2018, date(format=MM/dd/yyy): 3/21/2018
```


### double:

- **Description**: Parses specified expression into double. In case of '$', current value's string value will be parsed.
- **Content type**: NONE

**Examples:**

*Default*

```
double: 10.2
```


### expr:

- **Description**: Evaluates specified expression as freemarker expression and resultant value will be returned
- **Content type**: FM_EXPRESSION

**Examples:**

*Default*

```
expr: today()
```


### file:

- **Description**: Parses specified expression as file path and loads it as object. As part of 'set', the specified content will be converted to string and will be writtern to file. Supported object file types: xml, json, properties
- **Content type**: NONE

**Parameters:**

- `template` — If true, the loaded content will be parsed as freemarker template Default: `false`
- `text` — If true, the loaded content will be returned as text directly, without parsing into object. Default: `false`
- `propExpr` — If true, the property expressions #{} will be replaced with corresponding values. Default: `false`
- `jel` — If true, the json will be processed with Json expression language before object conversion. Applicable only for json content. Default: `false`
- `contextExpr` — Fmarker expression to context object for JEL (used only when jel = true). Default: `none`
- `expressions` — If true, then post parsing into object, values will be searched and processed as autox expressions Default: `false`

**Examples:**

*Default*

```
file: /tmp/data.json
```

*Using file to load file as object and extracting xpath value out of it*

```
<s:set expression="xpathAttr1" value="file:./src/test/resources/data/data1.json | xpath: //bean1/prop1"/>
```

*Loading file as simple text (instead of loading as object)*

```
<s:set expression="outputContent" value="file(text=true): ${attr.response}"/>
```


### float:

- **Description**: Parses specified expression into float. In case of '$', current value's string value will be parsed.
- **Content type**: NONE

**Examples:**

*Default*

```
float: 10.2
```


### int:

- **Description**: Parses specified expression into int. In case of '$', current value's string value will be parsed.
- **Content type**: NONE

**Examples:**

*Default*

```
int: 10
```


### json:

- **Description**: Parses specified expression as json string and loads it as object. In case of '$', current value's string value will be parsed.
- **Content type**: NONE

**Parameters:**

- `template` — If true, the loaded content will be parsed as freemarker template Default: `false`
- `jel` — If true, the json will be processed with Json expression language before object conversion Default: `false`
- `contextExpr` — Fmarker expression to context object for JEL (used only when jel = true). Default: `none`
- `expressions` — If true, then post parsing into object, values will be searched and processed as autox expressions Default: `false`
- `javaType` — If specified, then json will be parsed to specified java type Default: `null`

**Examples:**

*Default*

```
json: {"a": 2, "b": 3}
```

*Converting json into object and setting it on context*

```
<s:set expression="beanForTest">
					<value>json:
						{
							"key1" : "value1",
							"key2" : "value2"
						}
					</value>
				</s:set>
```


### jsonWithType:

- **Description**: Parses specified expression as json (with types) string and loads it as object. In case of '$', current value's string value will be parsed.
- **Content type**: NONE

**Parameters:**

- `template` — If true, the loaded content will be parsed as freemarker template Default: `false`
- `jel` — If true, the json will be processed with Json expression language before object conversion Default: `false`
- `contextExpr` — Fmarker expression to context object for JEL (used only when jel = true). Default: `none`
- `expressions` — If true, then post parsing into object, values will be searched and processed as autox expressions Default: `false`

**Examples:**

*Default*

```
jsonWithType: {"a": 2, "b": 3}
```

*Converting json with type definitions into object (of specified type)*

```
<s:set expression="attrJson">
					<value>[
						"java.util.HashSet",
						[
							"value1",
							"value2"
						]
					]</value>
				</s:set>
	
				<s:assert-equals expected="jsonWithType: ${attr.attrJson}" actual="set: value1, value2">
				</s:assert-equals>
	
				<s:assert-equals expected="attr: attrJson | jsonWithType: $" actual="set: value1, value2">
				</s:assert-equals>
```


### list:

- **Description**: Parses specified expression into list of strings (using comma as delimiter). If type specified, strings will be converted to specified type. In case of '$', current value's string value will be parsed. If current value is collection, it will converted to list directly.
- **Content type**: NONE

**Examples:**

*Default*

```
list: val1, val2, val3
```

*Converting delimited string of values into list and asserting them*

```
<s:assert-equals actual="attr: empNames" expected="list: employee1, employee2"/>
```

*Converting delimited string of values into list of particular data type.
				Note: Generic types are not supported here.*

```
<s:assert-equals actual="attr: ids" expected="list(int): 1, 2"/>
```


### long:

- **Description**: Parses specified expression into long. In case of '$', current value's string value will be parsed.
- **Content type**: NONE

**Examples:**

*Default*

```
long: 10
```


### map:

- **Description**: Parses specified expression into map of strings (using comma as delimiter and = as delimiter for key and value). If types specified, strings will be converted to specified type. In case of '$', current value's string value will be parsed.
- **Content type**: NONE

**Examples:**

*Default*

```
map: key1 = val1, key2=val2, key3=val3
```


### mongo:

- **Description**: Used to fetch values using mongo json query.
- **Content type**: NONE

**Parameters:**

- `resource` — Mongo-resource on which specified query has to be executed. Default: `Default mongo-resource`
- `replaceExpressions` — Flag indicating if free-marker expressions has to be processed post json parsing. Default: `true`
- `jel` — If set to true, instead of standard json parsing, query will be processed as JEL Json. Default: `false`

**Examples:**

*Default*

```
mongo(resource=default): {"find": "ENVIRONMENT"}
```


### mongoJs:

- **Description**: Used to fetch values using mongo JS query (by yukhi-mongojs).
- **Content type**: NONE

**Parameters:**

- `resource` — Mongo-resource on which specified query has to be executed. Default: `Default mongo-resource`

**Examples:**

*Default*

```
mongoJs: db.ENVIRONMENT.find({})
```


### param:

- **Description**: Parses specified expression as parameter.
- **Content type**: NONE

**Examples:**

*Default*

```
param: paramName
```


### prop:

- **Description**: Parses specified expression as bean property on effective-context (context or current object in case of piping).
- **Content type**: NONE

**Parameters:**

- `add` — If true and if specified property indicates a list, during set-value instead of replacing existing element new element will be added/inserted Default: `false`

**Examples:**

*Default*

```
prop: attr.bean.value1
```

*Accessing nested properties*

```
<s:set expression="propAttr1" value="prop: bean1.prop1"/>
```

*Accessing properties by key (same as nested properties). Added to be compatible with Apache Property Utils (which was used earlier).*

```
<s:set expression="propAttr1" value="prop: bean1(prop1)"/>
```

*Accessing list elements based on index.*

```
<s:set expression="propAttr1" value="prop: system.containers[0].name"/>
```

*Accessing list elements based on conditions.*

```
<s:set expression="propAttr1" value="prop: keys[name = USER_THRESHOLD_LIMIT].enumOptions[value = 5].label"/>
```

*Accessing list elements based on conditions with sub properties in condition.*

```
<s:set expression="propAttr1" value="prop: keys[cobMapProperty.processExpr = someVal].name"/><br/>
				<s:set expression="propAttr1" value="prop: keys[groupNames[0] = REFRESH-group].name"/>
```

*Using @this to access value of current element. Below example shows means to access "containers" element which has account-types list and has a value 'CMA'*

```
<s:set expression="propAttr1" value="system.containers[accountTypes[@this = CMA] = CMA].name"/>
```

*Using @this to remove an element with value 'HOLDER_DETAILS' from sublist with name 'supportedDatasetAttribs'*

```
<s:remove expression="system.products[1].supportedDatasetAttribs[@this = HOLDER_DETAILS]"/>
```


### res:

- **Description**: Parses specified expression as resource path and loads it as object. Supported file types: xml, json, properties
- **Content type**: NONE

**Parameters:**

- `template` — If true, the loaded content will be parsed as freemarker template Default: `false`
- `text` — If true, the loaded content will be returned as text directly, without parsing into object. Default: `false`
- `propExpr` — If true, the property expressions #{} will be replaced with corresponding values. Default: `false`
- `jel` — If true, the json will be processed with Json expression language before object conversion. Applicable only for json content. Default: `false`
- `contextExpr` — Fmarker expression to context object for JEL (used only when jel = true). Default: `none`
- `expressions` — If true, then post parsing into object, values will be searched and processed as autox expressions Default: `false`

**Examples:**

*Default*

```
res: /tmp/data.json
```

*Using res to load resource as object and extracting xpath value out of it*

```
<s:set expression="xpathAttr2" value="res:/data/data1.json | xpath: //bean1/subbean1/sprop1"/>
```

*Parsing resource as template file and then converting result into object*

```
<s:set expression="jsonObj" value="res(template=true):/data/data.json"/>
```


### set:

- **Description**: Parses specified expression into set of strings (using comma as delimiter). If type specified, strings will be converted to specified type. In case of '$', current value's string value will be parsed. If current value is collection, it will converted to set directly.
- **Content type**: NONE

**Examples:**

*Default*

```
set: val1, val2, val3
```


### sort:

- **Description**: Sorts the input collection and returns the result as list. As the input is expected to be collection this filtercan be used only with $. If no property/propertyExpr is specified, ordering will be done in natural order. Note if both property & propertyExpr is specified property only will be considered.
- **Content type**: NONE

**Parameters:**

- `propertyExpr` — A free marker expression which will be used to convert each object into string, post which sorting will be done. In orderto overcome default expression parsing, @{} expression format can be used instead of ${}. Default: `natural-ordering`
- `property` — Property which will be evaluated on each object. And this property value will be used for sorting. The value of property should be basic comparable values (like number, string, etc). If not error will be thrown. Default: `natural-ordering`
- `desc` — If set to true, the sorting will be done in reverse order. Default: `false`

**Examples:**

*Default*

```
attr: lstAttr | sort(propertyExpr=name): $
```

*Sorting by natural order*

```
<s:assert-equals expected="list: abc, bcd, def, fgh" actual="set: def, fgh, abc, bcd | sort: $"/>
```

*Sorting in natural order in descending order*

```
<s:assert-equals expected="list: fgh, def, bcd, abc" actual="set: def, fgh, abc, bcd | sort(desc=true): $"/>
```

*Sorting the objects using property expression*

```
<s:assert-equals expected="attr: sortedLst" actual="attr: unsortedLst | sort(propertyExpr=@{name}): $"/>
```

*Sorting the objects using complex property expression and in descending order*

```
<s:assert-equals expected="attr: sortedLst" actual="attr: unsortedLst | sort(propertyExpr=@{name}-@{val}, desc=true): $"/>
```

*Sorting the objects with simple property (non string value sorting)*

```
<s:assert-equals expected="attr: sortedLst" actual="attr: unsortedLst | sort(property=name): $"/>
```

*Sorting the objects with nested property (non string value sorting)*

```
<s:assert-equals expected="attr: sortedLst" actual="attr: unsortedLst | sort(property=subprop.name): $"/>
```


### sqlColumnList:

- **Description**: Used to fetch first column values as list.
- **Content type**: NONE

**Parameters:**

- `dataSource` — Data-source on which specified query has to be executed. Default: `Default data-source`

**Examples:**

*Default*

```
sqlColumnList: select NAME from emp
```


### sqlMap:

- **Description**: Used to fetch map from specified key column and value column (each row makes entry into result map).
- **Content type**: NONE

**Parameters:**

- `dataSource` — Data-source on which specified query has to be executed. Default: `Default data-source`
- `keyColumn` (mandatory) — Mandatory Param. Key column to be used.
- `valueColumn` (mandatory) — Mandatory Param. Value column to be used.

**Examples:**

*Default*

```
sqlMap(keyColumn=ID, valueColumn=NAME): select ID, NAME from emp
```


### sqlRowMaps:

- **Description**: Used to fetch rows as maps (Column name will be used as key). Result will be list of maps.
- **Content type**: NONE

**Parameters:**

- `dataSource` — Data-source on which specified query has to be executed. Default: `Default data-source`
- `processAllRows` — Defaults to true, which means all rows will be processed. If made false, then only first row will be processed. Default: `true`

**Examples:**

*Default*

```
sqlRowMaps: select ID, NAME, ADDRESS, MANAGER from emp
```


### sqlVal:

- **Description**: Used to fetch single value using sql query
- **Content type**: NONE

**Parameters:**

- `dataSource` — Data-source on which specified query has to be executed. Default: `Default data-source`

**Examples:**

*Default*

```
sqlVal(dataSource=default): select count(*) from emp
```


### store:

- **Description**: Parses specified expression as value on/from store.
- **Content type**: NONE

**Examples:**

*Default*

```
store: key1
```

*Using store to set the value into the store*

```
<s:set expression="store: testStoreKey" value="value1"/>
```

*Using store to fetch the value of the specified key from store*

```
<s:set expression="ctxAttrKey" value="store: testStoreKey"/>
```


### string:

- **Description**: Returns specified expression as stirng value after triming. In case of '$', current value will be converted to string. In case input object Blob/Clob, string value will be extracted from it.
- **Content type**: NONE

**Examples:**

*Default*

```
string: str
```

*Using string to escape expression formats*

```
<s:assert-equals actual="attr: returnValue" expected="string: beanFromApp:someName"/>
```


### template:

- **Description**: Parses specified value for free marker expressions and returns the result.
- **Content type**: NONE

**Examples:**

*Default*

```
template: Value=${someAttr}
```


### uiVal:

- **Description**: Used to set/fetch value from ui element.
- **Content type**: NONE

**Parameters:**

- `driver` — Driver to be used to access ui browser. Default: `Default driver`
- `parentElement` — Parent locator under which current locator should be accessed. Default: `<none>`
- `pressEnter` — If true, post value population, enter key will be pressed on the element. Default: `false`
- `displayValue` — If set to true, instead of value, display value will be fetched (currently non-select fields will return value itself). Default: `false`

**Examples:**

*Default*

```
uiVal(driver=default): xpath: //input[@name='statusFld']
```


### xpath:

- **Description**: Parses specified expression as xpath on effective-context (context or current object in case of piping).
- **Content type**: NONE

**Parameters:**

- `multi` — If true, list of matches will be returned Default: `false`

**Examples:**

*Default*

```
xpath: /attr/bean/value1
```

*Using xpath to access single property value*

```
<s:set expression="xpathAttr1" value="file:./src/test/resources/data/data1.json | xpath: //bean1/prop1"/>
```

*Using xpath to access multiple values matching specified path*

```
<s:set expression="xpathAttr1" value="xpath(multi=true): /attr/bean1//prop1"/>
```


