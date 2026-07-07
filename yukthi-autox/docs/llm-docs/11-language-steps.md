# Language Steps (Group.Lang)

Control-flow and language constructs: loops, conditions, try/catch, return, and context manipulation.

**API reference:** [`reference/steps-lang.md`](reference/steps-lang.md)

## If / else-if / else

```xml
<s:set expression="flag" value="int: 1"/>

<s:if condition="attr.flag == 1">
    <then>
        <s:set expression="result" value="if-then"/>
    </then>
    <else>
        <s:set expression="result" value="if-else"/>
    </else>
</s:if>

<s:assert-equals actual="attr: result" expected="if-then"/>
```

Use `s:else-if` for additional branches between `s:if` and `s:else`.

## For loop (numeric range)

```xml
<s:set expression="res" value=""/>

<s:for start="1" end="20">
    <s:if condition="attr.loopVar % 2 != 0">
        <s:continue/>
    </s:if>
    <s:set expression="res" value="${attr.res}|${attr.loopVar}"/>
    <s:if condition="attr.loopVar gte 10">
        <s:break/>
    </s:if>
</s:for>
```

- `attr.loopVar` — current iteration value (inclusive `start` to `end`)
- `s:continue` — skip to next iteration
- `s:break` — exit loop

## For-each (collection)

```xml
<s:set expression="someLst" value="json: [1, 2, 3, 4]"/>

<s:forEach expression="attr: someLst" loopVar="item">
    <s:log message="Item: ${attr.item}"/>
</s:forEach>
```

## Try / catch

```xml
<s:try>
    <s:invokeMethod method="throwError">
        <object s:beanRef="myBean"/>
    </s:invokeMethod>
    <s:catch error-attr="error">
        <s:log message="Caught: ${attr.error.message}"/>
    </s:catch>
</s:try>
```

## Set context attributes

```xml
<s:set expression="testAttr" value="SimpleValue"/>
<s:set expression="sumResult" value="expr: param.a + param.b | int: $"/>
<s:set expression="attr(global=true): globalAttr" value="int: 100"/>
```

## Return from functions

Inside a `<function>`, use `s:return` to exit with a value:

```xml
<function name="fetchProductOf10">
    <s:if condition="param.input == 1">
        <s:return value="int: 10"/>
    </s:if>
    <s:return value="expr: param.input * 10 | int: $"/>
</function>
```

Call with `return-attr` to capture the result:

```xml
<f:fetchProductOf10 input="int: 5" return-attr="product"/>
<s:assert-equals actual="attr: product" expected="int: 50"/>
```

## Function reference

```xml
<s:function-ref name="simpleGroup" simpleGroupInput="true"/>
```

## Other Lang steps

See [`reference/steps-lang.md`](reference/steps-lang.md) for: `s:execute`, `s:execute-command`, `s:fail`, `s:remove`, `s:sleep`, `s:return`.
