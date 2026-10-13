# UI Automation (Playwright)

UI tests use `s:ui-*` steps with the **`playwright-plugin`** configured in `app-configuration.xml`.

`yukthi-autox-all` includes Playwright by default. Locator syntax and most step names match the earlier Selenium UI API so existing suites stay readable; configure `<playwright-plugin>` instead of `<selenium-plugin>`.

## Prerequisites

```xml
<playwright-plugin maxSessions="3">
    <base-url>#{base.url}</base-url>
    <wrap:drivers>
        <driver name="autoxChrome" browser-type="chromium" default="true" headless="false"/>
    </wrap:drivers>
</playwright-plugin>
```

Driver attributes:

| Attribute | Description |
|-----------|-------------|
| `name` | Driver id used by steps (`driver-name`) |
| `browser-type` | `chromium` (default), `firefox`, or `webkit` |
| `default` | Use as the default driver when unset |
| `headless` | Launch headless (default `false`) |
| `channel` | Chromium channel (`chrome`, `msedge`, …) |
| `user-data-dir` | Persistent profile directory |
| `download-folder` | Expected download directory |
| `extra-arguments` | Comma-separated browser args |
| `slow-mo` | Slow down actions by N milliseconds |

No external chromedriver binary is required. Optional CLI: `-wd` / `--webdriver` to select a named driver.

**Do not** put both `yukthi-autox-ui-playwright` and `yukthi-autox-ui-selenium` on the classpath. For Selenium instead, see [15-module-structure.md](15-module-structure.md).

## Locator format

Locators use `type:value` syntax:

```
id: button
xpath: //input[@name='statusFld']
css: div>a
class: errElem
name: saveButton
tag: button
```

See [reference/ui-locators.md](reference/ui-locators.md) for all types. Prefer the types above; `js:` is not supported by the Playwright plugin.

## Navigate and click

```xml
<testCase name="button_Click">
    <description>Validates button click action</description>
    <wrap:steps>
        <s:ui-goto-page uri="/index.html"/>
        <s:ui-click locator="id: button"/>
        <s:ui-get-value locator="xpath: //input[@name='statusFld']" name="fldValue"/>
    </wrap:steps>
    <wrap:validations>
        <s:ui-assert-value locator="xpath: //input[@name='statusFld']" value="button Clicked"/>
        <s:assert-equals actual="attr: fldValue" expected="button Clicked"/>
    </wrap:validations>
</testCase>
```

`uri` is relative to `base-url` in the playwright plugin config.

## Visibility and waits

```xml
<s:ui-is-visible locator="id: testLayer" name="visibFlag"/>
<s:ui-click locator="id: clickButton1" postVisibilityLocator="id: clickButton1Res"/>
<s:ui-assert-visibility locator="id: clickButton1Res"/>
<s:sleep time="1000"/>
```

## Form filling

**Prefer `<s:ui-fill-form>`** to fill entire forms in one step. This keeps test cases compact and easy to read compared to many individual `ui-set-value` / `ui-click` calls.

```xml
<s:ui-goto-page uri="/form.html"/>
<s:ui-fill-form locator="id:sampleForm">
    <data>
        {
            "name": "kranthi",
            "id:genderDropDown": "male",
            "address": "line1\nline2",
            "color": ["red", "green", "blue"],
            "c:srchDropDown:vehicle": "Boat",
            "c:srchDropDown:ranking": "Nine"
        }
    </data>
</s:ui-fill-form>
<s:ui-assert-value locator="xpath: //input[@name='name']" value="kranthi"/>
```

Field keys can be:

- Element **names** (`"name"`, `"address"`)
- Standard **locators** (`"id:genderDropDown"`)
- **Custom UI locators** (`"c:srchDropDown:vehicle"`) — for application-specific widgets; see [06-expressions.md](06-expressions.md#custom-ui-locators)

Array values populate multi-select fields. Use `\n` in strings for multi-line text areas.

For a single non-standard widget outside a form fill, `s:ui-set-value` with a custom locator is fine:

```xml
<s:ui-set-value locator="c:srchDropDown : vehicle" value="Rocket"/>
```

## Set and get values

```xml
<s:ui-set-value locator="id: hiddenFld" value="newValue"/>
<s:ui-get-value locator="xpath: //input[@name='statusFld']" name="fldValue"/>
```

## Alerts and dialogs

Playwright queues dialogs. Alerts may be accepted as they appear so the triggering action is not blocked; confirm/prompt stay pending until you handle them.

```xml
<s:ui-click locator="id: alertBut"/>
<s:ui-handle-alert expectedMessage="Test Alert Message!!!"/>

<s:ui-click locator="id: confirmBut"/>
<s:ui-handle-confirm expectedMessage="Choose your button..." accept="true"/>

<s:ui-click locator="id: promptBut"/>
<s:ui-handle-prompt expectedMessage="Provide a value..." accept="true" text="Kranthi"/>
```

## Windows and tabs

```xml
<s:ui-open-window url="/index.html" name="Index"/>
<s:ui-switch-window locator="Index"/>
<s:ui-close-window name="Index"/>
<s:ui-switch-window/>
```

## Screenshots, MHTML, video, and tracing

```xml
<!-- PNG screenshot in the execution log -->
<s:ui-log-screen-shot name="test.png"/>

<!-- MHTML page snapshot (CDP; best on Chromium). Playwright-only. -->
<s:ui-log-html-snapshot name="page-snapshot.mhtml"/>

<!-- Screen recording of nested steps -->
<s:record-video name="form-filling">
    <s:ui-goto-page uri="/form.html"/>
    <s:ui-fill-form locator="id:sampleForm">
        <data>{"name": "kranthi"}</data>
    </s:ui-fill-form>
</s:record-video>

<!-- Playwright tracing zip (screenshots + DOM snapshots + sources). Playwright-only. -->
<s:ui-capture-tracing name="filledForm-1.zip">
    <s:ui-goto-page uri="/form.html"/>
    <s:ui-fill-form locator="id:sampleForm">
        <data>{"name": "kranthi", "id:genderDropDown": "male"}</data>
    </s:ui-fill-form>
</s:ui-capture-tracing>
```

Open a tracing zip with the [Playwright Trace Viewer](https://playwright.dev/docs/trace-viewer).

### Playwright-only steps

| Step | Purpose |
|------|---------|
| `s:ui-log-html-snapshot` | Save current page as MHTML in the log (`name`, optional `message`, `level`) |
| `s:ui-capture-tracing` | Wrap child steps; write a Playwright `.zip` trace to the log (`name`) |

## Network monitor (Java API)

For lightweight **network-only** capture (APIs during a click/navigation, crawl idle detection), use `PlaywrightPluginSession.startNetworkMonitor()` — not an XML step. Prefer this over `s:ui-capture-tracing` when you only need request/response analysis, not a full Trace Viewer zip.

Listeners are registered on the **browser context** (all pages/tabs of that driver).

```java
PlaywrightPluginSession ui = AutomationContext.getInstance()
		.getExecutionContextManager()
		.getPluginSession(PlaywrightPlugin.class);

try(NetworkMonitor monitor = ui.startNetworkMonitor()) // or startNetworkMonitor(driverName)
{
	// perform UI action (click, navigate, nested steps, etc.)

	// Optional: wait until traffic quiets (unknown sites / SPA)
	long deadline = System.currentTimeMillis() + 15_000;
	while(System.currentTimeMillis() < deadline)
	{
		Thread.sleep(500);
		if(monitor.getInProgressCount() == 0)
		{
			break;
		}
	}

	System.out.println("requests=" + monitor.getRequestCount()
			+ " responses=" + monitor.getResponseCount()
			+ " inFlight=" + monitor.getInProgressCount()
			+ " failed=" + monitor.getFailedRequestCount());

	for(NetworkMonitor.NetworkCall call : monitor.getApiCalls()) // xhr + fetch only
	{
		System.out.println(call.getMethod() + " " + call.getUrl()
				+ " status=" + call.getStatusCode()
				+ " ok=" + call.isSuccessful());

		// Live Playwright objects — read headers/body while context is alive
		Map<String, String> reqHeaders = call.getRequest().headers();
		if(call.getResponse() != null)
		{
			String body = call.getResponse().text();
		}
	}
} // close() unregisters listeners; does not clear NetworkCall refs
```

| Method | Meaning |
|--------|---------|
| `getInProgressCount()` | Requests not yet finished/failed |
| `getRequestCount()` / `getResponseCount()` | Totals since monitor start |
| `getFailedRequestCount()` | Request failed event, or HTTP status `>= 400` |
| `getApiCalls()` | `xhr` / `fetch` only; each `NetworkCall` has `getRequest()` / `getResponse()` |
| `close()` | Unregister listeners (idempotent) |

**Success** = response status in `200–399`. Inspect live `Request`/`Response` **before** driver reset/quit.

In-process / host embedding: call only from an **execution thread** — see [16-local-process-session.md](16-local-process-session.md).

## Download

```xml
<s:ui-click-and-download locator="id: downloadLink" pathName="filePath"/>
<s:assert-file-exists path="${attr.filePath}"/>
```

## JavaScript execution

```xml
<s:ui-execute-js>
    <script>
        document.getElementById("changeByJs").click()
    </script>
</s:ui-execute-js>
```

## Suite cleanup

Always quit the browser session in suite cleanup:

```xml
<cleanup>
    <s:ui-quit-session/>
</cleanup>
```

## Step reference

See [reference/steps-ui.md](reference/steps-ui.md) for all UI steps and [reference/validations-ui.md](reference/validations-ui.md) for assertions.
