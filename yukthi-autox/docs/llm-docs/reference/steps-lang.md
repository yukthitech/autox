# Lang Steps

Auto-generated reference for `Group.Lang` steps. Use step tags with the `s:` namespace.

### s:break

- **Title**: break
- **Group**: Lang
- **Description**: Breaks current loop
- **Java type**: `com.yukthitech.autox.test.lang.steps.BreakStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`

**Example:**

*Breaking the loop*

```xml
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


### s:catch

- **Title**: catch
- **Group**: Lang
- **Description**: Represents steps to be executed on error. This step has to be preceeded by try-step.
- **Java type**: `com.yukthitech.autox.test.lang.steps.CatchStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `error-attr` (mandatory) — Attribute name for error. Default: error Type: `java.lang.String`

**Example:**

*Catching exceptions thrown from try block*

```xml
<s:try>
					<s:log message="This is from try block"/>
					<s:throw message="Error mssg" value="int: 100"/>
				</s:try>
				<s:catch errorAttr="ex">
					<s:log message="This is from catch block"/>
					<s:set expression="var" value="catch-${attr.ex.value!c}"/>
				</s:catch>
```


### s:continue

- **Title**: continue
- **Group**: Lang
- **Description**: Continues current loop
- **Java type**: `com.yukthitech.autox.test.lang.steps.ContinueStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`

**Example:**

*Skipping current loop iteration*

```xml
<s:for start="1" end="5">
					<s:if condition="attr.loopVar % 2 != 0">
						<s:continue/>
					</s:if>

					<s:set expression="res" value="${attr.res}|${attr.loopVar}"/>
				</s:for>
```


### s:else

- **Title**: else
- **Group**: Lang
- **Description**: Represents steps to be executed as part of else block.
- **Java type**: `com.yukthitech.autox.test.lang.steps.ElseStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`

**Example:**

*Else branch in stacked if conditions*

```xml
<s:if condition="param.input == 1">
					<s:return value="int: 10"/>
				</s:if>
				<s:else-if condition="param.input == 2">
					<s:return value="int: 20"/>
				</s:else-if>
				<s:else>
					<s:return value="int: 100"/>
				</s:else>
```


### s:else-if

- **Title**: else if
- **Group**: Lang
- **Description**: Represents steps to be executed based on condition when prior if condition fails.
- **Java type**: `com.yukthitech.autox.test.lang.steps.ElseIfStep`

**Attributes:**

- `condition` (mandatory) — Freemarker condition to be evaluated. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`

**Example:**

*Else-if branch in stacked if conditions*

```xml
<s:if condition="param.input == 1">
					<s:return value="int: 10"/>
				</s:if>
				<s:else-if condition="param.input == 2">
					<s:return value="int: 20"/>
				</s:else-if>
				<s:else>
					<s:return value="int: 100"/>
				</s:else>
```


### s:execute

- **Title**: execute
- **Group**: Lang
- **Description**: Execute specified freemarker expression.
- **Java type**: `com.yukthitech.autox.test.lang.steps.ExecuteStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `enclose` (mandatory) — If true, encloses the specified expression in ${}. Default: true Type: `boolean`
- `expression` (mandatory) — Expression to execute. Type: `java.lang.String`

**Example:**

*Executing the expression*

```xml
<s:execute expression="${attr.val + 1}"/>
```


### s:fail

- **Title**: fail
- **Group**: Lang
- **Description**: Fails the current test case by throwing fail exception.
- **Java type**: `com.yukthitech.autox.test.lang.steps.FailStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `message` — Message to the fail exception to be thrown Type: `java.lang.String`

**Example:**

*Failing the test case*

```xml
<s:if condition="attr.loopVar % 2 != 0">
					<s:fail/>
				</s:if>
```


### s:for

- **Title**: for
- **Group**: Lang
- **Description**: Loops through specified range of values and for each iteration executed underlying steps
- **Java type**: `com.yukthitech.autox.test.lang.steps.ForLoopStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `end` (mandatory) — Inclusive end of range. Type: `java.lang.Object`
- `loop-var` — Loop variable that will be used to set loop iteration object on context. Default: loopVar Type: `java.lang.String` Default: `loopVar`
- `start` (mandatory) — Inclusive start of range. Type: `java.lang.Object`

**Example:**

*Looping through range of numbers*

```xml
<s:for start="1" end="20">
					<s:if condition="attr.loopVar % 2 != 0">
						<s:continue/>
					</s:if>
					
					<s:set expression="res" value="${attr.res}|${attr.loopVar}"/>
				</s:for>
```


### s:for-each

- **Title**: for each
- **Group**: Lang
- **Description**: Loops through specified collection, map or string tokens and for each iteration executed underlying steps
- **Java type**: `com.yukthitech.autox.test.lang.steps.ForEachLoopStep`

**Attributes:**

- `delimiter` — If expression evaluated to string, delimiter to be used to split the string. Default Value: comma (\s*\,\s*) Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `expression` (mandatory) — Expression which will be evaluated to collection or map or String Type: `java.lang.Object`
- `ignore-error` — Ignores error during iteration and continues to next iteration. Type: `boolean`
- `loop-idx-var` — Loop index variable that will be used to set loop iteration index on context. Default: loopIdxVar Type: `java.lang.String` Default: `loopIdxVar`
- `loop-var` — Loop variable that will be used to set loop iteration object on context. Default: loopVar Type: `java.lang.String` Default: `loopVar`

**Examples:**

*Looping through list elements*

```xml
<s:for-each expression="attr: checkBoxes" loopVar="cbox">
					<s:set expression="values" value="${attr.values},${uiElemAttr('value', attr.cbox, null)}"/>
				</s:for-each>
```

*Looping through string tokens using delimter*

```xml
<s:for-each expression="string: a,b,c,d, e,f" delimiter="\s*\,\s*">
					<s:if condition="attr.loopVar == 'b'">
						<s:continue/>
					</s:if>
					
					<s:set expression="res" value="${attr.res}|${attr.loopVar}"/>
				</s:for-each>
```

*Looping through map. Each iteration using loop variable, key and value can be accessed as shown in this example.*

```xml
<s:forEach expression="prop: data.dataMap">
					<s:set expression="res" value="${attr.res}|${attr.loopVar.key}=${attr.loopVar.value}"/>
				</s:forEach>
```


### s:function-ref

- **Title**: function ref
- **Group**: Lang
- **Description**: Reference step to execute target function with specified parameters.
- **Java type**: `com.yukthitech.autox.test.FunctionRef`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `name` (mandatory) — Name of the function to execute. Type: `java.lang.String`
- `params` — Parameters to be passed to function. Type: `java.util.Map<java.lang.Stringcom.yukthitech.autox.test.FunctionParam>`
- `return-attr` — Attribute name to be used to specify return value. If not specified, return value will be ignored. Default: null Type: `java.lang.String`

### s:if

- **Title**: if
- **Group**: Lang
- **Description**: Evaluates specified condition and if evaluates to true execute 'then' otherwise execute 'else'. For ease 'if' supports direct addition of steps which would be added to then block.
- **Java type**: `com.yukthitech.autox.test.lang.steps.IfConditionStep`

**Attributes:**

- `condition` (mandatory) — Freemarker condition to be evaluated. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`

**Child elements:**

- `else` — Used to group steps to be executed when this if condition is false.
- `then` — Used to group steps to be executed when this if condition is true.

**Examples:**

*If to check condition and execute steps*

```xml
<s:if condition="attr.loopVar % 2 != 0">
					<s:continue/>
				</s:if>
```

*If with else block*

```xml
<s:if condition="attr.flag == 1">
					<then>
						<s:set expression="ifExec" value="if-then"/>
					</then>
					<else>
						<s:set expression="ifExec" value="if-else"/>
					</else>
				</s:if>
```


### s:return

- **Title**: return
- **Group**: Lang
- **Description**: Returns from current execution. Currently this is supported only in step-group.
- **Java type**: `com.yukthitech.autox.test.lang.steps.ReturnStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `value` — Value to be returned. Type: `java.lang.Object`

**Example:**

*Returning from a step group*

```xml
<step-group name="condSimpleGroup">
					<s:if condition="attr.returnFlag??">
						<s:set expression="ifExec">
							<value>string: returnFlag: ${attr.returnFlag}</value>
						</s:set>
						
						<s:return/>
					</s:if>
				</step-group>
```


### s:throw

- **Title**: throw
- **Group**: Lang
- **Description**: Fails the current test case by throwing fail exception.
- **Java type**: `com.yukthitech.autox.test.lang.steps.ThrowStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `message` — Message to be used to throw as part of error Type: `java.lang.String`
- `value` — Value to be sent along with error. Type: `java.lang.Object`

**Example:**

*Throwing exception with message and value*

```xml
<s:throw message="Error mssg" value="int: 100"/>
```


### s:try

- **Title**: try
- **Group**: Lang
- **Description**: Used to enclose steps to catch errors.
- **Java type**: `com.yukthitech.autox.test.lang.steps.TryStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`

**Example:**

*Executing steps in try block*

```xml
<s:try>
					<s:log message="This is from try block"/>
					<s:set expression="var" value="try"/>
				</s:try>
				<s:catch errorAttr="ex">
					<s:set expression="var" value="catch-${attr.ex.value!c}"/>
				</s:catch>
```


### s:while

- **Title**: while
- **Group**: Lang
- **Description**: Loops till specified condition is evaluated to true executed underlying steps
- **Java type**: `com.yukthitech.autox.test.lang.steps.WhileLoopStep`

**Attributes:**

- `condition` (mandatory) — Freemarker condition to be evaluated. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`

**Example:**

*Looping till condition is meeting*

```xml
<s:while condition="attr.i lt 5">
					<s:set expression="res" value="${attr.res}|${attr.i}"/>
					<s:set expression="i" value="int: ${attr.i + 1}"/>
				</s:while>
```


