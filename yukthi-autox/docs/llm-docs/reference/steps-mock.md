# Mock Steps

Auto-generated reference for `Group.Mock` steps. Use step tags with the `s:` namespace.

### s:mock-fetch-request

- **Title**: mock fetch request
- **Group**: Mock
- **Description**: Fetches mock request details with specified filter details
- **Java type**: `com.yukthitech.autox.plugin.mock.steps.MockFetchRequestStep`

**Attributes:**

- `attribute-name` (mandatory) — Attribute name to be used to store filtered-request on context. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `method-filter` — Method filter to be used to fetch mock request. Type: `java.lang.String`
- `name` (mandatory) — Name of the server. Type: `java.lang.String`
- `uri-filter` — Uri filter to be used to fetch mock request. Type: `java.lang.String`

### s:mock-response

- **Title**: mock response
- **Group**: Mock
- **Description**: Mocks the specified request (url + method) with specified response.
- **Java type**: `com.yukthitech.autox.plugin.mock.steps.MockResponseStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `method` (mandatory) — Http method of the request to be mocked Type: `java.lang.String`
- `name` (mandatory) — Name of the server where mocking should be done. Type: `java.lang.String`
- `response-body` (mandatory) — Body of the mocked response Type: `java.lang.String`
- `response-header` (mandatory) — Headers to be added to the mock response Type: `java.util.Map<java.lang.Stringjava.lang.String>`
- `response-status-code` — Status code to be sent as part of mock response Type: `java.lang.String`
- `times` — Number of times for which response should be available for given request. Default: Integer max value Type: `int`
- `uri` (mandatory) — Request uri to be mocked Type: `java.lang.String`
- `wait-config` — Wait configuration to be used before sending response. Type: `com.yukthitech.autox.plugin.mock.steps.WaitConfig`

### s:mock-server-reset

- **Title**: mock server reset
- **Group**: Mock
- **Description**: Resets specified mock server, that is cleaning up all mocked responses and requests..
- **Java type**: `com.yukthitech.autox.plugin.mock.steps.ResetMockServerStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `name` (mandatory) — Name of the server. Type: `java.lang.String`

### s:mock-server-start

- **Title**: mock server start
- **Group**: Mock
- **Description**: Starts Mock Server with specified name and port
- **Java type**: `com.yukthitech.autox.plugin.mock.steps.StartMockServerStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `name` (mandatory) — Name of the server. Type: `java.lang.String`
- `port` (mandatory) — Port Number on which mock server has to start Type: `java.lang.String`

### s:mock-server-stop

- **Title**: mock server stop
- **Group**: Mock
- **Description**: Stops specified mock server.
- **Java type**: `com.yukthitech.autox.plugin.mock.steps.StopMockServerStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `name` (mandatory) — Name of the server. Type: `java.lang.String`

