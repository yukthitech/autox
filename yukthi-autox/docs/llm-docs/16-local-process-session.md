# Embedding AutoX in a Local Process

Use AutoX as an in-process library (not only via `AutomationLauncher` CLI). A host app starts an `AutoxSession` (or `AutoxDebugSession`), loads XML, runs functions / dynamic steps, and can pause for debug ops, screenshots, and context dumps.

**Related:** [09-functions-and-reuse.md](09-functions-and-reuse.md), [10-running-tests.md](10-running-tests.md), [08-ui-automation.md](08-ui-automation.md)

## When to use this

| Goal | API |
|------|-----|
| Run reusable flows from Java (same plugins/browser across calls) | `AutoxSession` |
| Breakpoints, step/resume, paused eval / steps | `AutoxDebugSession` |
| Full suite run + HTML report + `System.exit` | `AutomationLauncher` CLI ([10-running-tests.md](10-running-tests.md)) |

## Core rules

- **One active session per classloader.** `AutomationContext` is a process-level singleton. Close the current session before `start` again; a second `start` throws if a context is already active.
- Always **`close()`** the session (prefer try-with-resources). Closing resets plugins/browsers; it does **not** call `System.exit`.
- Session work is driven on a **single execution thread**. Do not call `runFunction` / `executeSteps` concurrently from multiple threads.
- `getPluginSession(...)` is only valid **on an execution thread** (during `runFunction` / `executeSteps`, or while paused steps run on that thread). Calling it from the host UI thread throws `NonExecutionThreadException`.

## Starting a session

```java
import com.yukthitech.autox.common.ContentStream;
import com.yukthitech.autox.session.AutoxSession;

File appConfig = new File("./src/test/resources/app-configuration.xml");
String[] args = {
    "-rf", "./output/embed-run",
    "-prop", "./src/test/resources/app.properties",
    "-rod", "true",                          // do not open report in browser
    "-flmt", "./src/test/resources/test-suites" // optional folder limit
};

try (AutoxSession session = AutoxSession.start(ContentStream.fromFile(appConfig), args))
{
    // load + run (see below)
}
```

`ContentStream` wraps file / string / stream content. For strings and streams, **`sourceName` is required** (used for location / debug attribution):

```java
ContentStream.fromString(xml, "host:checkout-flow.xml");
ContentStream.fromFile(appConfigFile);
```

Pass the same extended CLI args you would after the app-config path on the launcher (reports folder, properties, filters, etc.). See [10-running-tests.md](10-running-tests.md).

## Loading XML

Two ways to bring functions / suites into the session:

### 1. Load suite files from disk

```java
session.loadTestSuiteFiles();  // idempotent for this session
```

Uses `<testSuiteFolder>` from app config (and optional `-flmt` limits). Global functions defined in those files become available via `runFunction`.

### 2. Load XML from memory (host-provided)

```java
String xml =
    "<testData xmlns:s=\"http://autox.yukthitech.com/steps\">\n"
  + "  <function name=\"login\">\n"
  + "    <s:log message=\"logging in\"/>\n"
  + "  </function>\n"
  + "</testData>";

session.loadXml(ContentStream.fromString(xml, "host:login-flow.xml"));
```

Parse errors are thrown as `InvalidStateException` with the source name and error lines.

## Invoking functions

```java
// no parameters
Object result = session.runFunction("login");

// with parameters (become param.{name} inside the function)
Map<String, Object> params = new HashMap<>();
params.put("a", 10);
params.put("b", 20);
session.runFunction("sumWithoutReturn", params);

// function that returns (via <s:return>)
Object value = session.runFunction("sessionDynFunc");  // e.g. "ok"
```

Notes:

- Functions must already be loaded (`loadTestSuiteFiles` or `loadXml` / `executeSteps` that registered them).
- Multiple `runFunction` calls **without** `close()` reuse the same plugins and browser sessions.
- Per-invocation execution attributes are **not** automatically shared across calls. Use `session.getAutomationContext().setGlobalAttribute(name, value)` when you need cross-function sharing.
- If the function ends in an error status, `runFunction` throws `InvalidStateException`.
- Unknown function name → `InvalidArgumentException`.

## Executing dynamic step XML

Mode 2 — no caller source name; locations use a generated `interactive-steps-N.xml`:

```java
session.executeSteps("<s:log message=\"hello from host\"/>");

session.executeSteps(
    "<s:set expression=\"x\" value=\"int: 1\"/>"
  + "<s:assert-equals actual=\"attr: x\" expected=\"int: 1\"/>");
```

Snippets may also define functions / custom prefixes / UI locators; those are applied (reload) into the session. Example: define a function then call it:

```java
session.executeSteps(
    "<function name=\"sessionDynFunc\">"
  + "<s:return value=\"string: ok\"/>"
  + "</function>");

Assert.assertEquals(session.runFunction("sessionDynFunc"), "ok");
```

Empty step XML or no executable steps (and no reload) → `InvalidArgumentException`. Errored status → `InvalidStateException`.

## Accessing context and plugins

```java
AutomationContext ctx = session.getAutomationContext();

// After / during execution (attributes set by steps)
Object attr = ctx.getAttribute("sumResult");
ctx.setGlobalAttribute("sharedFlag", true);

// Only from an execution thread:
PlaywrightPluginSession ui =
    session.getPluginSession(PlaywrightPlugin.class);
```

## Screenshots and HTML dump (UI)

Prefer AutoX steps when you only need artifacts in the execution log:

```java
session.executeSteps(
    "<s:ui-goto-page uri=\"/index.html\"/>"
  + "<s:ui-log-screen-shot name=\"state.png\"/>"
  + "<s:ui-log-html-snapshot name=\"page.mhtml\"/>");
```

For **raw bytes / MHTML string** in the host process, call the Playwright plugin session **from an execution thread** (inside a running function/steps, or via paused `executeStepsWhilePaused` that uses a helper function). On `PlaywrightPluginSession`:

| Method | Returns |
|--------|---------|
| `takeScreenshot()` / `takeScreenshot(driverName)` | PNG `byte[]` |
| `captureHtmlSnapshot()` / `captureHtmlSnapshot(driverName)` | MHTML `String` |

Example pattern — capture PNG during a function and store it on a global attribute for the host to read after `runFunction` returns:

```xml
<function name="capturePage">
    <s:ui-goto-page uri="/form.html"/>
    <!-- Prefer s:ui-log-screen-shot / s:ui-log-html-snapshot for report files.
         For PNG byte[] in Java, call PlaywrightPluginSession.takeScreenshot()
         only from an execution thread (during runFunction / executeSteps /
         or executeStepsWhilePaused). -->
</function>
```

While **paused** in a debug session, use `executeStepsWhilePaused` (below) with `s:ui-log-screen-shot` / `s:ui-log-html-snapshot` so capture runs on the paused execution thread.

See [08-ui-automation.md](08-ui-automation.md) for step details.

## Context attribute dump

When execution pauses, `ServerMssgExecutionPaused` carries a serializable dump of context attributes and function params:

```java
if (event instanceof ServerMssgExecutionPaused)
{
    ServerMssgExecutionPaused paused = (ServerMssgExecutionPaused) event;

    Map<String, KeyValue> attrs = paused.getContextAttr();
    Map<String, KeyValue> params = paused.getParams();

    for (KeyValue kv : attrs.values())
    {
        String name = kv.getName();
        String type = kv.getType();
        Integer size = kv.getSize();          // for list/map
        Object value = SerializationUtils.deserialize(kv.getValue());
        // present dump to host UI / logs
    }
}
```

After **paused step execution**, `ServerMssgStepExecuted.getContextAttr()` provides an updated dump for that request.

You can also inspect live attributes from the host **after** a non-debug `runFunction` / `executeSteps` via `AutomationContext.getAttribute` / `getAttributeMap` (execution context for that call may be gone depending on cleanup — prefer globals or return values when the host needs results).

## Debug session (`AutoxDebugSession`)

Extends `AutoxSession` and enables in-process debug mode with an `IDebugEventSink` (default: `InProcessDebugEventSink`).

### Start with a sink listener

Pause events are delivered **asynchronously** on the sink dispatcher thread. Call resume / step / eval from that listener (or another host thread) — **never** from the paused execution thread (deadlock).

```java
InProcessDebugEventSink sink = new InProcessDebugEventSink();
AtomicReference<AutoxDebugSession> sessionRef = new AtomicReference<>();

sink.addListener(event ->
{
    if (event instanceof ServerMssgExecutionPaused)
    {
        ServerMssgExecutionPaused paused = (ServerMssgExecutionPaused) event;
        String executionId = paused.getExecutionId();

        // dump: paused.getContextAttr(), paused.getParams()
        // then continue:
        sessionRef.get().resume(executionId);
    }
    else if (event instanceof ServerMssgExecutionReleased)
    {
        // optional: notice release
    }
});

try (AutoxDebugSession session = AutoxDebugSession.start(
        ContentStream.fromFile(appConfig), args, sink))
{
    sessionRef.set(session);
    session.loadXml(ContentStream.fromString(xml, "host:flow.xml"));

    Function fn = session.getAutomationContext().getGlobalFunction("dbgPauseFn");
    IStep first = fn.getSteps().get(0);
    session.addDebugPoint(new DebugPoint(
            first.getLocation().getPath(), first.getLineNumber(), null));

    Object result = session.runFunction("dbgPauseFn");
}
```

### Breakpoints

```java
session.addDebugPoint(new DebugPoint(filePath, lineNumber, condition));
session.setDebugPoints(collection);
session.removeDebugPoint(filePath, lineNumber);
Collection<DebugPoint> points = session.getDebugPoints();
```

`DebugPoint` fields: `filePath`, `lineNumber`, optional FreeMarker/condition string, `singlePauseOnly`.

File path must match the location used when XML was loaded (e.g. path from `IStep.getLocation()` after `loadXml` with a given `sourceName`).

### Debug operations

All take the `executionId` from `ServerMssgExecutionPaused`. Optional `ignoreError` flag for error-points:

| Method | Effect |
|--------|--------|
| `resume(executionId)` | Continue to next breakpoint |
| `stepOver(executionId)` | Next step at same level |
| `stepInto(executionId)` | Step into nested callable |
| `stepReturn(executionId)` | Step out to caller |
| `dropToFrame(executionId, stackElementId)` | Drop to stack frame (`stackElementId` from pause stack) |

Underlying ops map to `DebugOp`: `RESUME`, `STEP_OVER`, `STEP_INTO`, `STEP_RETURN`.

### Eval while paused

```java
String requestId = session.evalWhilePaused(executionId, "int: 21");
// Result arrives as ServerMssgEvalExprResult on the sink
// Correlate with requestId; deserialize result bytes
```

```java
if (event instanceof ServerMssgEvalExprResult)
{
    ServerMssgEvalExprResult result = (ServerMssgEvalExprResult) event;
    if (requestId.equals(result.getRequestId()) && result.isSuccessful())
    {
        Object value = SerializationUtils.deserialize(result.getResult());
        session.resume(executionId);
    }
}
```

Expression syntax is the same prefix-expression language used in steps (see [06-expressions.md](06-expressions.md)).

### Execute steps while paused

Same interactive step path as Prism; **`sourceName` is required**:

```java
String requestId = session.executeStepsWhilePaused(
    executionId,
    "<s:ui-log-screen-shot name=\"paused.png\"/>"
  + "<s:set expression=\"debugNote\" value=\"string: from-host\"/>",
    "host:paused-steps.xml");

// Completion: ServerMssgStepExecuted on the sink (requestId + contextAttr dump)
```

Returns `null` when the snippet only reloaded definitions (no steps).

## Typical embed patterns

### Mode 1 — load once, run many functions

```java
try (AutoxSession session = AutoxSession.start(ContentStream.fromFile(appConfig), args))
{
    session.loadXml(ContentStream.fromString(suiteXml, "host:checkout-flow.xml"));
    session.runFunction("login");
    Object orderId = session.runFunction("checkout", checkoutParams);
}
```

### Mode 2 — ad-hoc steps from the host

```java
session.executeSteps("<s:rest-invoke-get uri=\"/health\"/>");
session.executeSteps("<s:assert-equals actual=\"prop: attr.result.statusCode\" expected=\"int: 200\"/>");
```

### Mode 3 — interactive debug / agent host

Use `AutoxDebugSession` + sink: set breakpoints → `runFunction` → on pause dump attrs / screenshot / eval → `resume` / step ops.

## Error and lifecycle checklist

| Situation | Behavior |
|-----------|----------|
| Second `AutoxSession.start` without close | `InvalidStateException` (context already active) |
| Call after `close()` | `InvalidStateException` (session closed) |
| `getPluginSession` off execution thread | `NonExecutionThreadException` |
| Function / steps errored | `InvalidStateException` with status message |
| Unknown function | `InvalidArgumentException` |

After close, `AutomationContext.getInstance()` is cleared so another session can start.

## Packages cheat sheet

| Type | Package |
|------|---------|
| `AutoxSession`, `AutoxDebugSession` | `com.yukthitech.autox.session` |
| `ContentStream` | `com.yukthitech.autox.common` |
| `InProcessDebugEventSink` | `com.yukthitech.autox.debug` |
| `DebugPoint`, `DebugOp`, pause/eval messages | `com.yukthitech.autox.debug.common` |
| `PlaywrightPlugin` / `PlaywrightPluginSession` | `com.yukthitech.autox.plugin.ui` |

## Tests as examples

In the AutoX repo:

- `TAutoxSession` — start, `loadXml`, `runFunction`, `executeSteps`, single-session guard
- `TAutoxDebugSession` — in-process pause/resume and `evalWhilePaused`
