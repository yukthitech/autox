# SQL Automation (Group.Rdbms)

Database testing using the `db-plugin` and `s:sql-*` steps.

**API reference:** [`reference/steps-rdbms.md`](reference/steps-rdbms.md), [`reference/validations-rdbms.md`](reference/validations-rdbms.md)

## Plugin configuration

```xml
<db-plugin defaultDataSource="dataSource">
    <dataSource name="dataSource" ccg:beanType="org.apache.commons.dbcp2.BasicDataSource">
        <driverClassName>com.mysql.jdbc.Driver</driverClassName>
        <url>#{db.url}</url>
        <username>#{db.user}</username>
        <password>#{db.password}</password>
    </dataSource>
</db-plugin>
```

## Global DDL setup

Use global `<setup>` for schema creation (runs once before all suites):

```xml
<testData xmlns:s="http://autox.yukthitech.com/steps"
          xmlns:wrap="http://xmlbeanparser.yukthitech.com/wrap">
    <setup>
        <s:sql-ddl-query dataSourceName="dataSource" ignoreErrors="true">
            <query>DROP TABLE EMPLOYEE</query>
        </s:sql-ddl-query>
        <s:sql-ddl-query dataSourceName="dataSource">
            <query>
                CREATE TABLE EMPLOYEE (
                    ID      INT,
                    NAME    VARCHAR(100)
                )
            </query>
        </s:sql-ddl-query>
    </setup>
</testData>
```

- `ignoreErrors="true"` — useful for DROP when table may not exist

## DML (insert/update/delete)

```xml
<testCase name="insertEmployee">
    <description>Insert employee record</description>
    <s:sql-dml-query dataSourceName="dataSource">
        <query>INSERT INTO EMPLOYEE (ID, NAME) VALUES (1, 'Emp1')</query>
    </s:sql-dml-query>
</testCase>
```

## Fetch single value

```xml
<s:sql-fetch-value-query dataSourceName="dataSource" name="empName">
    <query>SELECT NAME FROM EMPLOYEE WHERE ID = 1</query>
</s:sql-fetch-value-query>
<s:assert-equals actual="attr: empName" expected="Emp1"/>
```

## Load query results

```xml
<!-- As list of row maps -->
<s:sql-load-query-row-maps dataSourceName="dataSource" name="employees">
    <query>SELECT ID, NAME FROM EMPLOYEE</query>
</s:sql-load-query-row-maps>

<!-- As single row bean -->
<s:sql-load-query-row-bean dataSourceName="dataSource" name="employee" beanType="com.example.Employee">
    <query>SELECT ID, NAME FROM EMPLOYEE WHERE ID = 1</query>
</s:sql-load-query-row-bean>
```

## Prefix expressions for SQL

Use in assertions and data loading:

- `sqlVal:` — single SQL value
- `sqlRowMaps:` — multiple rows as maps
- `sqlMap:` — key-value from two columns
- `sqlColumnList:` — single column as list

See [`reference/prefix-expressions.md`](reference/prefix-expressions.md).

## FreeMarker SQL helpers

See [`reference/freemarker-methods.md`](reference/freemarker-methods.md) for SQL-related `${...}` methods in `SqlFreeMarkerFunctions`.
