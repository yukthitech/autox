# Mock Server (Group.Mock)

HTTP mock server for stubbing REST APIs during tests.

**API reference:** [`reference/steps-mock.md`](reference/steps-mock.md)

## Basic flow

1. Start mock server
2. Register mock responses
3. Invoke REST calls against mock port
4. Fetch and validate received requests
5. Reset or stop server

```xml
<testCase name="basicMockTest">
    <description>Test mock server with REST call</description>

    <s:mock-server-start name="testMockServer" port="9944"/>

    <s:mock-response method="POST" name="testMockServer" responseStatusCode="200" uri="/test/job">
        <response-header name="Content-Type">application/json</response-header>
        <responseBody>
            {
                "code": 0,
                "message": "some test message from response"
            }
        </responseBody>
    </s:mock-response>

    <s:rest-invoke-post baseUrl="string: http://localhost:9944" responseContextAttribure="apiResult" uri="/test/job">
        <body>json:
            {
                "content": "Some test body",
                "reqId": 134
            }
        </body>
    </s:rest-invoke-post>

    <s:assert-equals actual="${attr.apiResult.message}" expected="some test message from response"/>
    <s:assert-equals actual="expr: attr.apiResult.code" expected="int: 0"/>
</testCase>
```

## Validate requests received by mock server

```xml
<s:mock-fetch-request name="testMockServer" attributeName="mockRequests"
    uriFilter="/test/job" methodFilter="POST"/>

<s:assert-equals actual="expr: (attr.mockRequests)?size" expected="int: 1"/>
<s:assert-equals actual="expr: attr.mockRequests[0].body | json: $">
    <expected>json: {"content": "Some test body", "reqId": 134}</expected>
</s:assert-equals>
```

## Reset mock server

Clears registered responses and request history:

```xml
<s:mock-server-reset name="testMockServer"/>

<s:mock-fetch-request name="testMockServer" attributeName="resetMockRequests"
    uriFilter="/test/job" methodFilter="POST"/>
<s:assert-equals actual="expr: (attr.resetMockRequests)?size" expected="int: 0"/>
```

After reset, unmocked endpoints return 404.

## Stop mock server

```xml
<s:mock-server-stop name="testMockServer"/>
```

## Key steps

| Step | Purpose |
|------|---------|
| `s:mock-server-start` | Start server on specified port |
| `s:mock-response` | Register URI/method response |
| `s:mock-fetch-request` | Retrieve captured requests |
| `s:mock-server-reset` | Clear mocks and request log |
| `s:mock-server-stop` | Shut down server |

Combine with `s:rest-invoke-*` steps using `baseUrl="string: http://localhost:{port}"` to hit the mock server.
