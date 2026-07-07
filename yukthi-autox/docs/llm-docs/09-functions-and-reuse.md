# Functions and Reuse

AutoX supports reusable step groups via `<function>` elements and shared beans via `<data-bean>`.

## Functions

Define at file level (outside `<testSuite>`) or inside a suite:

```xml
<testData xmlns:s="http://autox.yukthitech.com/steps"
          xmlns:wrap="http://xmlbeanparser.yukthitech.com/wrap"
          xmlns:f="http://autox.yukthitech.com/functions">

    <function name="sumWithoutReturn">
        <s:set expression="sumResult" value="expr: param.a + param.b | int: $"/>
        <s:log message="Sum result was: ${attr.sumResult}"/>
    </function>

    <function name="condSimpleGroup">
        <s:if condition="attr.flag == 1">
            <then>
                <s:return value="if-then"/>
            </then>
            <else>
                <s:return value="if-else"/>
            </else>
        </s:if>
    </function>

    <testSuite name="lang-test-suites">
        <testCase name="testSimpleFunc">
            <description>Tests function without return</description>
            <f:sumWithoutReturn a="int: 10" b="int: 20"/>
            <s:assert-equals actual="attr: sumResult" expected="int: 30"/>
        </testCase>
    </testSuite>
</testData>
```

### Calling functions

Use the `f:` namespace with function name as element:

```xml
<f:sumWithoutReturn a="int: 10" b="int: 20"/>
<f:condSimpleGroup return-attr="ifExec"/>
```

Function parameters are passed as attributes. Access them inside the function as `param.{name}`.

### Return values

```xml
<function name="fetchProductOf10">
    <s:return value="expr: param.input * 10 | int: $"/>
</function>

<!-- In test case -->
<f:fetchProductOf10 input="int: 5" return-attr="product"/>
<s:assert-equals actual="attr: product" expected="int: 50"/>
```

Use `return-attr` on the call to capture the return value into a context attribute.

## Data beans

### App-level beans

In `app-configuration.xml`:

```xml
<wrap:data-beans>
    <data-bean s:beanType="com.example.MyHelper" id="myHelper"/>
</wrap:data-beans>
```

### Suite-level beans

```xml
<testSuite name="my-suite">
    <wrap:data-beans>
        <data-bean s:beanType="com.example.TestUtils" id="testUtils"/>
        <data-bean s:beanType="com.example.TestObject" id="beanFromTestSuite">
            <name>beanFromTestSuite</name>
        </data-bean>
    </wrap:data-beans>
</testSuite>
```

### Using beans in steps

```xml
<s:invokeMethod method="toText" paramTypes="com.example.NameBean">
    <object s:beanRef="beanFromTestSuite"/>
    <parameter s:beanType="com.example.NameBean" name="someName"/>
</s:invokeMethod>
```

### Bean copy (override properties)

```xml
<s:invokeMethod method="toText">
    <object s:beanCopy="beanFromTestSuite">
        <name>beanCopy</name>
    </object>
    <parameter s:beanType="com.example.NameBean" name="someName1"/>
</s:invokeMethod>
```

## Step groups via function-ref

```xml
<function name="simpleGroup">
    <s:log message="This message is from step group"/>
    <s:set expression="simpleGroupOutput" value="true"/>
    <s:assert-true value="attr: simpleGroupInput"/>
</function>

<!-- In test case -->
<s:function-ref name="simpleGroup" simpleGroupInput="true"/>
```

## REST session functions

Functions are commonly used for login/session setup referenced by plugin events:

```xml
<function name="login">
    <s:rest-invoke-post uri="/auth/login">
        <body><![CDATA[{"user":"test","password":"secret"}]]></body>
    </s:rest-invoke-post>
    <s:set expression="attr(plugin=sessionBased): authToken"
           value="attr: response | xpath: //token"/>
</function>
```

## Best practices

- Keep functions focused on one reusable operation.
- Prefer functions over copy-pasting step sequences.
- Use suite-level beans for test data helpers; app-level beans for shared services.
- Name functions clearly — they are callable from any suite in the same XML file tree.
