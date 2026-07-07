# Mongodb Validations

Auto-generated reference for `Group.Mongodb` assertion/validation steps.

### s:assert-mongo

- **Title**: assert mongo
- **Group**: Mongodb
- **Description**: Executes specified mongo Query on specified mongo resource. Syntax of queries can be found at https://docs.mongodb.com/manual/reference/command. And the result is deep-compared with specified expecte object. Extra properties from result are ignored.
- **Java type**: `com.yukthitech.autox.plugin.mongo.AssertMongoStep`
- **Required plugins**: MongoPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `expected` (mandatory) — Expected value in comparison. Type: `java.lang.Object`
- `mongo-resource-name` (mandatory) — Mongo Resource to be used for query execution. Type: `java.lang.String`
- `query` (mandatory) — Query to execute. Type: `java.lang.String`
- `replace-expressions` — If true, fmarker expression should be processed in the query post json parsing. Type: `boolean` Default: `true`

