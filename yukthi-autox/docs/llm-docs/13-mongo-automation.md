# MongoDB Automation (Group.Mongodb)

MongoDB testing using the `mongo-plugin` and `s:mongo-*` steps.

**API reference:** [`reference/steps-mongodb.md`](reference/steps-mongodb.md), [`reference/validations-mongodb.md`](reference/validations-mongodb.md)

## Plugin configuration

```xml
<mongo-plugin defaultMongoResource="mongoResource">
    <mongo-resource name="mongoResource" replicas="localhost:27017" dbName="mydb"/>
</mongo-plugin>
```

## Suite setup — clear collection

```xml
<testSuite name="mongoTestSuite">
    <setup>
        <s:mongo-query mongoResourceName="mongoResource">
            <query>
                {
                    "delete": "AUTOX_TEST",
                    "deletes": [{"q": {}, "limit": 0}]
                }
            </query>
        </s:mongo-query>
    </setup>
</testSuite>
```

## Insert documents

```xml
<s:mongo-multi-query mongoResourceName="mongoResource">
    <query>
        {
            "insert": "AUTOX_TEST",
            "documents": [
                {"name": "test1", "entry": 1},
                {"name": "test2", "entry": 2}
            ]
        }
    </query>
</s:mongo-multi-query>
```

## Query and assert with prefix expressions

```xml
<s:assert-deep-equals ignoreExtraProperties="true">
    <actual>mongo:
        {
           "find": "AUTOX_TEST",
           "filter": {"name": "test1"}
        } | xpath: /cursor/firstBatch
    </actual>
    <expected>json: [{"name": "test1", "entry": 1}]</expected>
</s:assert-deep-equals>
```

## JavaScript query

```xml
<s:assert-deep-equals ignoreExtraProperties="true">
    <actual>mongoJs: return db.AUTOX_TEST.find({"name": "test1"}, {"_id": 0})</actual>
    <expected>json: [{"name": "test1", "entry": 1}]</expected>
</s:assert-deep-equals>
```

## Assert mongo command result

```xml
<s:assert-mongo mongoResourceName="mongoResource">
    <query>{ "count": "AUTOX_TEST" }</query>
    <expected>json: {"n": 3}</expected>
</s:assert-mongo>
```

## Prefix expressions

- `mongo:` — run Mongo command, access result
- `mongoJs:` — execute JavaScript against database

See [`reference/prefix-expressions.md`](reference/prefix-expressions.md).
