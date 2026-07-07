# Rdbms Steps

Auto-generated reference for `Group.Rdbms` steps. Use step tags with the `s:` namespace.

### s:sql-ddl-query

- **Title**: sql ddl query
- **Group**: Rdbms
- **Description**: Executes specified DDL query on specified data source.
- **Java type**: `com.yukthitech.autox.plugin.sql.steps.DdlQueryStep`
- **Required plugins**: DbPlugin

**Attributes:**

- `data-source-name` — Data source to be used for sql execution. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `ignore-errors` (mandatory) — If set to true, exceptions during query execution will be ignored. Helpful to rest the db without assuming initial state. Type: `boolean`
- `query` (mandatory) — DDL query to execute Type: `java.lang.String`

### s:sql-dml-query

- **Title**: sql dml query
- **Group**: Rdbms
- **Description**: Executes specified DML Query on specified data source.
- **Java type**: `com.yukthitech.autox.plugin.sql.steps.DmlQueryStep`
- **Required plugins**: DbPlugin

**Attributes:**

- `commit-at-end` — If true, calls commit at end. Default: true Type: `boolean`
- `count-attribute` — If specified, number of rows affected will be set on the context Type: `java.lang.String`
- `data-source-name` — Data source to be used for sql execution. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `fail-on-no-update` — If true, error will be thrown if no rows are affected by specified DML.
Default Value: false Type: `boolean`
- `query` (mandatory) — Query to execute. Type: `java.lang.String`

### s:sql-fetch-value-query

- **Title**: sql fetch value query
- **Group**: Rdbms
- **Description**: Fetches single value (first row, first column value) from the results of specified query.
- **Java type**: `com.yukthitech.autox.plugin.sql.steps.FetchValueQueryStep`
- **Required plugins**: DbPlugin

**Attributes:**

- `context-attribute` (mandatory) — Name of the attribute which should be used to keep the result value. Type: `java.lang.String`
- `data-source-name` — Name of the data source to be used for query execution. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `query` (mandatory) — Query to execute, the result's first column will be used to create list. Type: `java.lang.String`

### s:sql-load-query-column-list

- **Title**: sql load query column list
- **Group**: Rdbms
- **Description**: Executes specified query and loads the result first column values as list on the context. 
In case of zero results empty list will be kept on context.
- **Java type**: `com.yukthitech.autox.plugin.sql.steps.LoadQueryColumnListStep`
- **Required plugins**: DbPlugin

**Attributes:**

- `context-attribute` (mandatory) — Name of the attribute which should be used to keep the result map on the context. Type: `java.lang.String`
- `data-source-name` — Name of the data source to be used for query execution. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `query` (mandatory) — Query to execute, the result's first column will be used to create list. Type: `java.lang.String`

### s:sql-load-query-map

- **Title**: sql load query map
- **Group**: Rdbms
- **Description**: Executes specified query and loads the results as map on context. 
In case of zero results empty map will be kept on context. 
Per row new entry will be added.
- **Java type**: `com.yukthitech.autox.plugin.sql.steps.LoadQueryMapStep`
- **Required plugins**: DbPlugin

**Attributes:**

- `context-attribute` (mandatory) — Name of the attribute which should be used to keep the result map on the context. Type: `java.lang.String`
- `data-source-name` — Name of the data source to be used for query execution. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `key-column` (mandatory) — Results column name whose values should be used as key in result map. Type: `java.lang.String`
- `query` (mandatory) — Query to execute, the results will be used to create map. Type: `java.lang.String`
- `value-column` (mandatory) — Results column name whose values should be used as value in result map. Type: `java.lang.String`

### s:sql-load-query-row-bean

- **Title**: sql load query row bean
- **Group**: Rdbms
- **Description**: Executes specified query and loads the results as bean(s) on context. 
In case of zero results empty map will be kept on context. 
Per row new bean will be created.
- **Java type**: `com.yukthitech.autox.plugin.sql.steps.LoadQueryRowBeanStep`
- **Required plugins**: DbPlugin

**Attributes:**

- `bean-type` (mandatory) — Type of bean to which rows should be converted. Type: `java.lang.Class<?>`
- `context-attribute` (mandatory) — Name of the attribute which should be used to keep the result bean on the context. Type: `java.lang.String`
- `data-source-name` — Name of the data source to be used for query execution. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `process-all-rows` — If false, only first row will be processed into bean. If true, per row new map will be created and loads of this beans into context.
Default: true Type: `boolean`
- `query` (mandatory) — Query to execute, the results will be used to create bean. Type: `java.lang.String`

### s:sql-load-query-row-map

- **Title**: sql load query row map
- **Group**: Rdbms
- **Description**: Executes specified query and loads the results as map(s) on context. 
In case of zero results empty map will be kept on context. 
Per row new map will be created.
- **Java type**: `com.yukthitech.autox.plugin.sql.steps.LoadQueryRowMapStep`
- **Required plugins**: DbPlugin

**Attributes:**

- `column-transformations` — Column transformation expressions. This can be used to transform column values using specified expressions before loading result on context.<br>In this expression current column/row details can be accessed using attribute specified by 'transformAttrName'. Type: `java.util.Map<java.lang.Stringjava.lang.String>`
- `context-attribute` (mandatory) — Name of the attribute which should be used to keep the result map on the context. Type: `java.lang.String`
- `data-source-name` — Name of the data source to be used for query execution. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `process-all-rows` — If false, only first row will be processed into map. If true, per row new map will be created and loads of this maps into context.
Default: true Type: `boolean`
- `query` (mandatory) — Query to execute, the results will be used to create map. Type: `java.lang.String`
- `transform-attr-name` (mandatory) — Name of the attribute which should be used to set trasnform details on context while evaluating transform expression. Defaults to: result Type: `java.lang.String`

### s:sql-multi-dml-query

- **Title**: sql multi dml query
- **Group**: Rdbms
- **Description**: Executes specified multiple DML queries in single transaction
- **Java type**: `com.yukthitech.autox.plugin.sql.steps.MultiDmlQueryStep`
- **Required plugins**: DbPlugin

**Attributes:**

- `data-source-name` — Data source to be used for sql execution. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `fail-on-no-update` — If true, error will be thrown if no rows are affected by specified DML.
Default Value: false Type: `boolean`

**Child elements:**

- `query` (multiple) — DML Query to execute.

