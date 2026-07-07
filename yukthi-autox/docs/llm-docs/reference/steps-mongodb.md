# Mongodb Steps

Auto-generated reference for `Group.Mongodb` steps. Use step tags with the `s:` namespace.

### s:mongo-js

- **Title**: mongo js
- **Group**: Mongodb
- **Description**: Executes specified mongo script. Value of the last statement will be the result value of this script.
- **Java type**: `com.yukthitech.autox.plugin.mongo.MongoJsStep`
- **Required plugins**: MongoPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `mongo-resource-name` — Mongo Resource to be used for query execution. Type: `java.lang.String`
- `result-attribute` — Name of the attribute to be used to set the result. Value of the last statement will be the result value of this script. Type: `java.lang.String` Default: `result`
- `script` (mandatory) — Script to be executed. Type: `java.lang.String`

### s:mongo-multi-query

- **Title**: mongo multi query
- **Group**: Mongodb
- **Description**: Executes specified multiple mongo Query on specified mongo resource. Syntax of queries  can be found at https://docs.mongodb.com/manual/reference/command.
- **Java type**: `com.yukthitech.autox.plugin.mongo.MongoMultiQueryStep`
- **Required plugins**: MongoPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `mongo-resource-name` (mandatory) — Mongo Resource to be used for query execution. Type: `java.lang.String`
- `queries` (mandatory) — Query(ies) to execute. Query can be json-string or a map object. Type: `java.util.List<java.lang.Object>`
- `replace-expressions` — If true, fmarker expression should be processed in the query post json parsing. Type: `boolean` Default: `true`

### s:mongo-query

- **Title**: mongo query
- **Group**: Mongodb
- **Description**: Executes specified mongo Query on specified mongo resource. Syntax of queries  can be found at https://docs.mongodb.com/manual/reference/command.
- **Java type**: `com.yukthitech.autox.plugin.mongo.MongoQueryStep`
- **Required plugins**: MongoPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `mongo-resource-name` — Mongo Resource to be used for query execution. Type: `java.lang.String`
- `query` (mandatory) — Query to execute. Can be json string or a map object. Type: `java.lang.Object`
- `replace-expressions` — If true, fmarker expression should be processed in the query post json parsing. Type: `boolean` Default: `true`
- `result-attribute` — Name of the attribute to be used to set the result. Type: `java.lang.String` Default: `result`

