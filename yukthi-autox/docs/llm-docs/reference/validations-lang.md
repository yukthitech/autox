# Lang Validations

Auto-generated reference for `Group.Lang` assertion/validation steps.

### s:poll-and-check

- **Title**: poll and check
- **Group**: Lang
- **Description**: Used to execute polling steps till check condition is met with specified interval gap. Validation will fail if required condition is not met or exceeds timeout.
- **Java type**: `com.yukthitech.autox.test.lang.steps.PollAndCheckStep`

**Attributes:**

- `check-condition` (mandatory) — Check Freemarker condition to be evaluated. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `polling-interval` (mandatory) — Polling interval duration. Type: `java.lang.Object`
- `polling-interval-unit` — Polling interval time unit. Defaults to millis. Type: `java.util.concurrent.TimeUnit`
- `time-out` (mandatory) — Timout till which check condition will be tried. After this time, this validation will fail. Type: `java.lang.Object`
- `time-out-unit` — Time out time unit. Defaults to millis. Type: `java.util.concurrent.TimeUnit`

**Child elements:**

- `poll` — Used to specify polling steps.

**Example:**

*Polling and checking for condition*

```xml
<s:pollAndCheck checkCondition="attr.checkVar gte 5" timeOut="20" timeOutUnit="SECONDS" pollingInterval="500">
					<poll>
						<s:set expression="checkVar" value="int: ${attr.checkVar + 1}"/>
					</poll>
				</s:pollAndCheck>
```


