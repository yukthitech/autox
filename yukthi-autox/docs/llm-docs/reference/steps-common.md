# Common Steps

Auto-generated reference for `Group.Common` steps. Use step tags with the `s:` namespace.

### s:collection-add

- **Title**: collection add
- **Group**: Common
- **Description**: Adds the specified value to specified collection
- **Java type**: `com.yukthitech.autox.test.common.steps.CollectionAddStep`

**Attributes:**

- `collection` (mandatory) — Collection expression to which specified value needs to be added. Type: `java.lang.Object`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `value` — Value expression which needs to be added to specified collection. Default: null (null will be added to specified collection) Type: `java.lang.Object`

### s:collection-add-all

- **Title**: collection add all
- **Group**: Common
- **Description**: Adds the elements from specified new-collection to specified collection
- **Java type**: `com.yukthitech.autox.test.common.steps.CollectionAddAllStep`

**Attributes:**

- `collection` (mandatory) — Collection expression to which specified values needs to be added. Type: `java.lang.Object`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `new-collection` (mandatory) — Collection from which values needs to be added. Type: `java.lang.Object`

### s:collection-remove

- **Title**: collection remove
- **Group**: Common
- **Description**: Removes the specified value / key from specified collection or map
- **Java type**: `com.yukthitech.autox.test.common.steps.CollectionRemoveStep`

**Attributes:**

- `collection` (mandatory) — Collection or expression from which specified value (or key) needs to be removed. Type: `java.lang.Object`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `value` — Value (or key) expression which needs to be removed from specified collection. Default: null (null will be removed from specified collection) Type: `java.lang.Object`

### s:execute-command

- **Title**: execute command
- **Group**: Common
- **Description**: Executes specified OS command.
- **Java type**: `com.yukthitech.autox.test.common.steps.ExecuteCommandStep`

**Attributes:**

- `command` (mandatory) — Command to be executed. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `expected-exit-code` — Expected exit code of command. If specified, exit code will be validated with actual exit code. Type: `java.lang.Integer`
- `name` (mandatory) — Name of the command, which is used in generating log file. This name will be used to set context attribute with generate log file path. Type: `java.lang.String`
- `working-directory` — Directory in which command has to be executed. Type: `java.lang.String`

### s:invoke-method

- **Title**: invoke method
- **Group**: Common
- **Description**: Executes specified method on specified bean.
- **Java type**: `com.yukthitech.autox.test.jobj.steps.InvokeMethodStep`

**Attributes:**

- `deep-clone-object` — When set to false, object will not be deep cloned. Which means property expressions if any, will be processed only once.
Default false Type: `boolean`
- `deep-clone-params` — When set to false, parameters will not be deep cloned. Which means property expressions if any, will be processed only once.
Default false Type: `boolean`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `is-static` — Flag indicating if the method to be invoked is a static method or normal instance method. 
Defaults: false Type: `boolean`
- `method` (mandatory) — Name of the method to be invoked. Type: `java.lang.String`
- `object` — Object on which method needs to be invoked. For non-static method this is mandatory Type: `java.lang.Object`
- `object-type` — Object on which method needs to be invoked. For static method this is mandatory Type: `java.lang.Class<?>`
- `param-types` — List of method argument types delimited by comma. Needs to be used when particular method needs to be invoked.
If not specified, method which matches with specified arguments will be invoked. Type: `java.lang.String`
- `parameters` — List of parameters to be passed to method. Type: `java.util.List<java.lang.Object>`
- `result-parameter` — Context parameter name to be used to set the result on context. 
Default: returnValue Type: `java.lang.String` Default: `returnValue`

### s:log

- **Title**: log
- **Group**: Common
- **Description**: Logs specified message. Multiple messages can be specified in single log statement. If non-string or non-primitive values are specified they are converted to json before printing.
- **Java type**: `com.yukthitech.autox.test.common.steps.LogStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `level` — Logging level. Default Value: DEBUG Type: `com.yukthitech.autox.exec.report.LogLevel`
- `message` (mandatory) — Message(s)/object(s) to log Type: `java.util.List<java.lang.Object>`

**Examples:**

*Logging simple message at default level (DEBUG)*

```xml
<s:log message="This message is from step group"/>
```

*Logging multiple messages and by using expressions*

```xml
<s:log>
					<message>Invoking method using object from app config: </message>
					<message>prop: data.beanFromApp</message>
				</s:log>
```

*Logging at specific level*

```xml
<s:log level="SUMMARY" message="Time taken during test was: 100"/>
```


### s:map-put

- **Title**: map put
- **Group**: Common
- **Description**: Adds the specified key-value entry to specified map.
- **Java type**: `com.yukthitech.autox.test.common.steps.MapPutStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `key` — Key expression which needs to be added to specified collection. Default: null (null will be added) Type: `java.lang.Object`
- `map` (mandatory) — Map expression to which specified entry needs to be added. Type: `java.lang.Object`
- `value` — Value expression which needs to be added to specified collection. Default: null (null will be added) Type: `java.lang.Object`

### s:map-put-all

- **Title**: map put all
- **Group**: Common
- **Description**: Adds all the entries from the specified new-map to specified map.
- **Java type**: `com.yukthitech.autox.test.common.steps.MapPutAllStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `map` (mandatory) — Map expression to which specified entries needs to be added. Type: `java.lang.Object`
- `new-map` (mandatory) — New map whose entries has to be added. Type: `java.lang.Object`

### s:remove

- **Title**: remove
- **Group**: Common
- **Description**: Removes the specified context attribute or values matching with specified path.
- **Java type**: `com.yukthitech.autox.test.common.steps.RemoveStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `expression` — Expression to be used to remove the values. Currently supported expressions: xpath, attr, store Type: `java.lang.String`
- `name` — Name of the attribute to remove. Type: `java.lang.String`

**Examples:**

*Removing context attribute*

```xml
<s:remove name="tmpDir"/>
```

*Removing using xpath expression - key in a map.*

```xml
<s:set expression="bean">
					<value>
						json:
						{
							"key1" : "value1",
							"key2" : "value2",
							"arr": [1, 2, 3, 4, 5],
							"strArr": ["one", "two", "three"]
						}
					</value>
				</s:set>

				<s:remove expression="xpath: /attr/bean/key2"/>
```

*Removing using xpath expression - to remove element in a list using index. Note: In xpath idex starts with 1 (not zero).*

```xml
<s:remove expression="xpath: /attr/bean/arr[2]"/>
```

*Removing using xpath expression - to remove element in a list using value.*

```xml
<s:remove expression="xpath: /attr/bean/strArr[contains(., 'two')]"/>
```


### s:send-mail

- **Title**: send mail
- **Group**: Common
- **Description**: Step to send mail, useful to send specific notifications from test cases.
- **Java type**: `com.yukthitech.autox.test.mail.steps.SendMailStep`

**Attributes:**

- `content` (mandatory) — Content to be used for sending mail. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `from-address` (mandatory) — Email-Id from which notification should be marked as sent. Type: `java.lang.String`
- `password` — Smtp password to be used for authentication. If not specified, authentication will not be done. Type: `java.lang.String`
- `smptp-host` (mandatory) — Smtp host to be used for sending mails Type: `java.lang.String`
- `smptp-port` (mandatory) — Smtp port to be used for sending mails. Type: `java.lang.Integer` Default: `587`
- `subject` (mandatory) — Subject to be used for sending mail. Type: `java.lang.String`
- `to-address-list` (mandatory) — Space separated address list to which notification should be sent. Type: `java.lang.String`
- `ttls-enabled` — Flag indicating ttls enabled or not. Type: `boolean` Default: `false`
- `user-name` — Smtp user name to be used for authentication. If not specified, authentication will not be done. Type: `java.lang.String`

### s:set

- **Title**: set
- **Group**: Common
- **Description**: Sets the specified value using specified expression
- **Java type**: `com.yukthitech.autox.test.common.steps.SetStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `expression` (mandatory) — Expression to be used to set the value. Type: `java.lang.String`
- `value` — Value of the attribute to set. Default: empty string Type: `java.lang.Object`

**Examples:**

*Setting context attribute with type*

```xml
<s:set expression="intAttr" value="int: 10" />
				<s:set expression="attr(int): intAttr1" value="10"/>
```

*Using piped expressions for setting the value*

```xml
<s:set expression="propAttr1" value="file:./src/test/resources/data/data1.json | prop: bean1.prop1"/>
```

*Setting property (instead of default context attribute)*

```xml
<s:set expression="prop: attr.beanForTest.key1" value="newValue1" />
```

*Setting property using json*

```xml
<s:set expression="beanForTest">
					<value>json:
						{
							"key1" : "value1",
							"key2" : "value2"
						}
					</value>
				</s:set>
```


### s:sleep

- **Title**: sleep
- **Group**: Common
- **Description**: Sleeps for specified amount of time.
- **Java type**: `com.yukthitech.autox.test.common.steps.SleepStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `time` (mandatory) — Time to sleep. Type: `java.lang.Object`
- `time-unit` — Units of time specified. Default: millis Type: `java.util.concurrent.TimeUnit`

**Examples:**

*Sleep for specified number of millis (default time units)*

```xml
<s:sleep time="5000"/>
```

*Sleep using non-default time units*

```xml
<s:sleep time="10" timeUnit="SECONDS"/>
```


### s:start-timer

- **Title**: start timer
- **Group**: Common
- **Description**: Starts time tracking with specified name. Stopping timer would keep elaspsed time on context which can used for logging.
- **Java type**: `com.yukthitech.autox.test.common.steps.StartTimerStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `name` (mandatory) — Name of the timer. Type: `java.lang.String`

**Example:**

*Starting/stopped timer with specified name*

```xml
<s:start-timer name="timeTaken"/>
				<s:sleep time="10" timeUnit="SECONDS"/>
				<s:stop-timer name="timeTaken"/>
```


### s:stop-timer

- **Title**: stop timer
- **Group**: Common
- **Description**: Stops the timer and keeps the elapsed time on context.
- **Java type**: `com.yukthitech.autox.test.common.steps.StopTimerStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `name` (mandatory) — Name of the timer. Type: `java.lang.String`

**Example:**

*Starting/stopped timer with specified name*

```xml
<s:start-timer name="timeTaken"/>
				<s:sleep time="10" timeUnit="SECONDS"/>
				<s:stop-timer name="timeTaken"/>
```


### s:unzip

- **Title**: unzip
- **Group**: Common
- **Description**: Unzips specified file or resource to specified directory.
- **Java type**: `com.yukthitech.autox.test.common.steps.UnzipStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `out-folder` (mandatory) — Folder path in which unzip should happen. Type: `java.lang.String`
- `zip-file` — Zip file to be unzipped. Either of zipFile or ZipResouce is mandatory. Type: `java.lang.String`
- `zip-resource` — Zip resource to be unzipped. Either of zipFile or ZipResouce is mandatory. Type: `java.lang.String`

### s:zip

- **Title**: zip
- **Group**: Common
- **Description**: Zips specified file(s) into specified zip file.
- **Java type**: `com.yukthitech.autox.test.common.steps.ZipStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `file-names` — Collection of file names. If specified, only these files from root folder will be zipped. Type: `java.lang.Object`
- `root-folder` (mandatory) — Root folder whose files will be zipped. Type: `java.lang.String`
- `zip-file` — Zip file to be created. If not specified, a temp file will be created. Type: `java.lang.String`
- `zip-file-attr` — Attribute name to be set with generated zip file path. Default: zipFilePath Type: `java.lang.String`

