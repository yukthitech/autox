# Rdbms Validations

Auto-generated reference for `Group.Rdbms` assertion/validation steps.

### s:sql-assert

- **Title**: sql assert
- **Group**: Rdbms
- **Description**: Executes specified query and validates expected data is returned
- **Java type**: `com.yukthitech.autox.plugin.sql.assertion.SqlAssert`
- **Required plugins**: DbPlugin

**Attributes:**

- `data-source-name` (mandatory) — Name of the data source to be used for query execution. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `expected-rows` (mandatory) — Expected rows of values from query result. Each row will have list of column (name-value pairs) Type: `java.util.List<com.yukthitech.autox.plugin.sql.assertion.SqlAssert$ExpectedRow>`
- `query` (mandatory) — Query to execute whose results needs to be validated. Type: `java.lang.String`

### s:sql-assert-value

- **Title**: sql assert value
- **Group**: Rdbms
- **Description**: Executes specified query and validates only single value (first row - first column value)
- **Java type**: `com.yukthitech.autox.plugin.sql.assertion.SqlAssertValue`
- **Required plugins**: DbPlugin

**Attributes:**

- `convert-expression` — Expression to be used on query result, before comparison. Default: null Type: `java.lang.String`
- `data-source-name` (mandatory) — Name of the data source to be used for query execution. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `expected-value` (mandatory) — Expected value. Which will compared with value from db. Type: `java.lang.Object`
- `query` (mandatory) — Query to execute whose results needs to be validated. Type: `java.lang.String`

