# Common Validations

Auto-generated reference for `Group.Common` assertion/validation steps.

### s:assert-deep-equals

- **Title**: assert deep equals
- **Group**: Common
- **Description**: Compares specified values for deep equality. This will not compare the java types, but compares only the structure.
- **Java type**: `com.yukthitech.autox.test.assertion.AssertDeepEqualsStep`

**Attributes:**

- `actual` (mandatory) — Actual value in comparison Type: `java.lang.Object`
- `check-equality` — If false, instead of checking for equality, check will be done for non equality. Default: true Type: `boolean`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `expected` (mandatory) — Expected value in comparison. Type: `java.lang.Object`
- `failed-path-attr` — Failed path, if any, will be set on context with this attribute. Default: failedPath Type: `java.lang.String` Default: `failedPath`
- `ignore-extra-properties` — If true, extra properties in actual will be ignored and will only ensure expected structure is found in actual object. Default: false Type: `boolean`
- `list-keys` — Comma separated key names that can be used to identify list objects in error paths. Type: `java.lang.String`

### s:assert-equals

- **Title**: assert equals
- **Group**: Common
- **Description**: Compares specified values for euqality.
- **Java type**: `com.yukthitech.autox.test.assertion.AssertEqualsStep`

**Attributes:**

- `actual` (mandatory) — Actual value in comparison Type: `java.lang.Object`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `expected` (mandatory) — Expected value in comparison. Type: `java.lang.Object`

**Example:**

*Any object reference can be used both for expected and actual*

```xml
<s:assert-equals expected="string:value" actual="string:value" />
				<s:assert-equals expected="int:10" actual="prop: attr.intAttr" />
```


### s:assert-false

- **Title**: assert false
- **Group**: Common
- **Description**: Asserts given value is either boolean false or string 'false'
- **Java type**: `com.yukthitech.autox.test.assertion.AssertFalseStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `value` (mandatory) — Value to be evaluated Type: `java.lang.Object`

**Examples:**

*Using free marker condition in the value*

```xml
<s:assert-false value="condition: attr.intAttr1 gt 5" />
```

*Any object reference expression can be used*

```xml
<s:assert-false value="prop: attr.flag" />
```


### s:assert-file-exists

- **Title**: assert file exists
- **Group**: Common
- **Description**: Validates specified path exists.
- **Java type**: `com.yukthitech.autox.test.lang.steps.AssertFileExists`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `path` (mandatory) — Path of file to check. Type: `java.lang.String`

**Example:**

*Assert specified file exists*

```xml
<s:assert-file-exists path="test.txt"/>
```


### s:assert-not-equals

- **Title**: assert not equals
- **Group**: Common
- **Description**: Compares specified values for non euqality.
- **Java type**: `com.yukthitech.autox.test.assertion.AssertNotEqualStep`

**Attributes:**

- `actual` (mandatory) — Actual value for comparison Type: `java.lang.Object`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `expected` (mandatory) — Expected value for comparison. Type: `java.lang.Object`

**Example:**

*Any object reference expression can be used*

```xml
<s:assert-not-equals expected="int:11" actual="prop:attr.intAttr" />
```


### s:assert-not-null

- **Title**: assert not null
- **Group**: Common
- **Description**: Asserts the specified value is not null.
- **Java type**: `com.yukthitech.autox.test.assertion.AssertNotNullStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `value` (mandatory) — Value to check. Type: `java.lang.Object`

**Example:**

*Any object reference expression can be used*

```xml
<s:assert-not-null value="attr: intAttr" />
```


### s:assert-not-same

- **Title**: assert not same
- **Group**: Common
- **Description**: Asserts specified values are not same reference.
- **Java type**: `com.yukthitech.autox.test.assertion.AssertNotSameStep`

**Attributes:**

- `actual` (mandatory) — Actual value in comparison Type: `java.lang.Object`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `expected` (mandatory) — Expected value in comparison. Type: `java.lang.Object`

**Example:**

*Any object reference expression can be used*

```xml
<s:assert-not-same expected="prop:attr.intAttr" actual="prop:attr.intAttr1" />
```


### s:assert-null

- **Title**: assert null
- **Group**: Common
- **Description**: Asserts the value to be null.
- **Java type**: `com.yukthitech.autox.test.assertion.AssertNullStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `value` (mandatory) — Value to check. Type: `java.lang.Object`

**Example:**

*Any object reference expression can be used*

```xml
<s:assert-null value="prop:attr.intAttrXyz" />
```


### s:assert-same

- **Title**: assert same
- **Group**: Common
- **Description**: Asserts specified values are same reference.
- **Java type**: `com.yukthitech.autox.test.assertion.AssertSameStep`

**Attributes:**

- `actual` (mandatory) — Actual value in comparison Type: `java.lang.Object`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `expected` (mandatory) — Expected value in comparison. Type: `java.lang.Object`

**Example:**

*Any object reference expression can be used*

```xml
<s:assert-same expected="prop:attr.intAttr" actual="prop:attr.intAttr" />
```


### s:assert-true

- **Title**: assert true
- **Group**: Common
- **Description**: Asserts given value is either boolean true or string 'true'
- **Java type**: `com.yukthitech.autox.test.assertion.AssertTrueStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `value` (mandatory) — Value to be checked. Type: `java.lang.Object`

**Example:**

*Any object reference expression can be used*

```xml
<s:assert-true value="condition: attr.intAttr1 gt 5" />
```


