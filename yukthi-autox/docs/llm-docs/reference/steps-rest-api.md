# Rest_Api Steps

Auto-generated reference for `Group.Rest_Api` steps. Use step tags with the `s:` namespace.

### s:rest-invoke-delete

- **Title**: rest invoke delete
- **Group**: Rest_Api
- **Description**: Used to invoke DELETE api.
- **Java type**: `com.yukthitech.autox.plugin.rest.steps.InvokeDeleteStep`
- **Required plugins**: RestPlugin

**Attributes:**

- `base-url` — Base url to be used. If specified, this will be used instead of using base url from plugin. Type: `java.lang.String`
- `body` — Body to be set. If non-string is specified, object will be converted to json and content-type header will be set as JSON. Type: `java.lang.Object`
- `connection-name` — Name of connection to be used. Defaults to default connection Type: `java.lang.String`
- `content-charset` — Content char set to be used for body. Type: `java.lang.String`
- `content-type` — Request content type to be used. default: application/json Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `expected-response-type` — Expected response type. Default: java.lang.Object Type: `java.lang.Class<?>`
- `proxy-host-port` — Proxy host and port in host:port format. Type: `java.lang.String`
- `response-context-attribure` — Context attribute name on which the actaul rest response object will be placed. default: response Type: `java.lang.String` Default: `response`
- `result-context-attribute` — Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly<br/>3. headers (Map&lt;String, List&lt;String&gt;&gt;) - Response header. Note: a header can have multiple values.<br/>default: result Type: `java.lang.String` Default: `result`
- `uri` (mandatory) — Uri to be invoked. Type: `java.lang.String`

**Child elements:**

- `body` — Body of the request
- `form-field` (multiple) — Form field for current request
- `header` (multiple) — Http Header for the request
- `param` (multiple) — Request parameter of current request
- `path-variable` (multiple) — Path variable to be replaced in the URI
- `request-form-field` (multiple) — Form field for current request
- `request-header` (multiple) — Http Header for the request
- `request-param` (multiple) — Request parameter of current request
- `request-path-variable` (multiple) — Path variable to be replaced in the URI

### s:rest-invoke-get

- **Title**: rest invoke get
- **Group**: Rest_Api
- **Description**: Used to invoke GET api.
- **Java type**: `com.yukthitech.autox.plugin.rest.steps.InvokeGetStep`
- **Required plugins**: RestPlugin

**Attributes:**

- `base-url` — Base url to be used. If specified, this will be used instead of using base url from plugin. Type: `java.lang.String`
- `body` — Body to be set. If non-string is specified, object will be converted to json and content-type header will be set as JSON. Type: `java.lang.Object`
- `connection-name` — Name of connection to be used. Defaults to default connection Type: `java.lang.String`
- `content-charset` — Content char set to be used for body. Type: `java.lang.String`
- `content-type` — Request content type to be used. default: application/json Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `expected-response-type` — Expected response type. Default: java.lang.Object Type: `java.lang.Class<?>`
- `proxy-host-port` — Proxy host and port in host:port format. Type: `java.lang.String`
- `response-context-attribure` — Context attribute name on which the actaul rest response object will be placed. default: response Type: `java.lang.String` Default: `response`
- `result-context-attribute` — Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly<br/>3. headers (Map&lt;String, List&lt;String&gt;&gt;) - Response header. Note: a header can have multiple values.<br/>default: result Type: `java.lang.String` Default: `result`
- `uri` (mandatory) — Uri to be invoked. Type: `java.lang.String`

**Child elements:**

- `body` — Body of the request
- `form-field` (multiple) — Form field for current request
- `header` (multiple) — Http Header for the request
- `param` (multiple) — Request parameter of current request
- `path-variable` (multiple) — Path variable to be replaced in the URI
- `request-form-field` (multiple) — Form field for current request
- `request-header` (multiple) — Http Header for the request
- `request-param` (multiple) — Request parameter of current request
- `request-path-variable` (multiple) — Path variable to be replaced in the URI

### s:rest-invoke-get-file

- **Title**: rest invoke get file
- **Group**: Rest_Api
- **Description**: Used to invoke GET api and save response as file.
- **Java type**: `com.yukthitech.autox.plugin.rest.steps.InvokeGetFileStep`
- **Required plugins**: RestPlugin

**Attributes:**

- `base-url` — Base url to be used. If specified, this will be used instead of using base url from plugin. Type: `java.lang.String`
- `connection-name` — Name of connection to be used. Defaults to default connection Type: `java.lang.String`
- `content-type` — Request content type to be used. default: application/json Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `expected-response-type` — Expected response type. Default: java.lang.Object Type: `java.lang.Class<?>`
- `output-file` — Output file where response content should be stored. If not specified, temp file will be used. The output file path will be set as rest-result value Type: `java.lang.String`
- `proxy-host-port` — Proxy host and port in host:port format. Type: `java.lang.String`
- `response-context-attribure` — Context attribute name on which the actaul rest response object will be placed. default: response Type: `java.lang.String` Default: `response`
- `result-context-attribute` — Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly<br/>3. headers (Map&lt;String, List&lt;String&gt;&gt;) - Response header. Note: a header can have multiple values.<br/>default: result Type: `java.lang.String` Default: `result`
- `uri` (mandatory) — Uri to be invoked. Type: `java.lang.String`

**Child elements:**

- `form-field` (multiple) — Form field for current request
- `header` (multiple) — Http Header for the request
- `param` (multiple) — Request parameter of current request
- `path-variable` (multiple) — Path variable to be replaced in the URI
- `request-form-field` (multiple) — Form field for current request
- `request-header` (multiple) — Http Header for the request
- `request-param` (multiple) — Request parameter of current request
- `request-path-variable` (multiple) — Path variable to be replaced in the URI

### s:rest-invoke-multipart-delete

- **Title**: rest invoke multipart delete
- **Group**: Rest_Api
- **Description**: Used to invoke Multipart DELETE api.
- **Java type**: `com.yukthitech.autox.plugin.rest.steps.InvokeDeleteWithAttachmentStep`
- **Required plugins**: RestPlugin

**Attributes:**

- `attachments` — List of attachment objects. This is expected to be used when attachments has to be dynamic. Resultant json should map to list of attachments. Type: `java.lang.Object`
- `base-url` — Base url to be used. If specified, this will be used instead of using base url from plugin. Type: `java.lang.String`
- `connection-name` — Name of connection to be used. Defaults to default connection Type: `java.lang.String`
- `content-type` — Request content type to be used. default: application/json Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `expected-response-type` — Expected response type. Default: java.lang.Object Type: `java.lang.Class<?>`
- `parts` — List of part objects. This is expected to be used when parts has to be dynamic. Resultant json should map to list of parts. Type: `java.lang.Object`
- `proxy-host-port` — Proxy host and port in host:port format. Type: `java.lang.String`
- `response-context-attribure` — Context attribute name on which the actaul rest response object will be placed. default: response Type: `java.lang.String` Default: `response`
- `result-context-attribute` — Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly<br/>3. headers (Map&lt;String, List&lt;String&gt;&gt;) - Response header. Note: a header can have multiple values.<br/>default: result Type: `java.lang.String` Default: `result`
- `uri` (mandatory) — Uri to be invoked. Type: `java.lang.String`

**Child elements:**

- `attachment` (multiple) — List of files to be attached with this request. Values are resources
- `form-field` (multiple) — Form field for current request
- `header` (multiple) — Http Header for the request
- `param` (multiple) — Request parameter of current request
- `part` (multiple) — Parts to be set on the request. If non-string is specified, object will be converted to json and content-type of part will be set as JSON.
- `path-variable` (multiple) — Path variable to be replaced in the URI
- `request-form-field` (multiple) — Form field for current request
- `request-header` (multiple) — Http Header for the request
- `request-param` (multiple) — Request parameter of current request
- `request-path-variable` (multiple) — Path variable to be replaced in the URI

### s:rest-invoke-multipart-get

- **Title**: rest invoke multipart get
- **Group**: Rest_Api
- **Description**: Used to invoke Multipart GET api.
- **Java type**: `com.yukthitech.autox.plugin.rest.steps.InvokeGetWithAttachmentStep`
- **Required plugins**: RestPlugin

**Attributes:**

- `attachments` — List of attachment objects. This is expected to be used when attachments has to be dynamic. Resultant json should map to list of attachments. Type: `java.lang.Object`
- `base-url` — Base url to be used. If specified, this will be used instead of using base url from plugin. Type: `java.lang.String`
- `connection-name` — Name of connection to be used. Defaults to default connection Type: `java.lang.String`
- `content-type` — Request content type to be used. default: application/json Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `expected-response-type` — Expected response type. Default: java.lang.Object Type: `java.lang.Class<?>`
- `parts` — List of part objects. This is expected to be used when parts has to be dynamic. Resultant json should map to list of parts. Type: `java.lang.Object`
- `proxy-host-port` — Proxy host and port in host:port format. Type: `java.lang.String`
- `response-context-attribure` — Context attribute name on which the actaul rest response object will be placed. default: response Type: `java.lang.String` Default: `response`
- `result-context-attribute` — Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly<br/>3. headers (Map&lt;String, List&lt;String&gt;&gt;) - Response header. Note: a header can have multiple values.<br/>default: result Type: `java.lang.String` Default: `result`
- `uri` (mandatory) — Uri to be invoked. Type: `java.lang.String`

**Child elements:**

- `attachment` (multiple) — List of files to be attached with this request. Values are resources
- `form-field` (multiple) — Form field for current request
- `header` (multiple) — Http Header for the request
- `param` (multiple) — Request parameter of current request
- `part` (multiple) — Parts to be set on the request. If non-string is specified, object will be converted to json and content-type of part will be set as JSON.
- `path-variable` (multiple) — Path variable to be replaced in the URI
- `request-form-field` (multiple) — Form field for current request
- `request-header` (multiple) — Http Header for the request
- `request-param` (multiple) — Request parameter of current request
- `request-path-variable` (multiple) — Path variable to be replaced in the URI

### s:rest-invoke-multipart-patch

- **Title**: rest invoke multipart patch
- **Group**: Rest_Api
- **Description**: Used to invoke Multipart PATCH api.
- **Java type**: `com.yukthitech.autox.plugin.rest.steps.InvokePatchWithAttachmentStep`
- **Required plugins**: RestPlugin

**Attributes:**

- `attachments` — List of attachment objects. This is expected to be used when attachments has to be dynamic. Resultant json should map to list of attachments. Type: `java.lang.Object`
- `base-url` — Base url to be used. If specified, this will be used instead of using base url from plugin. Type: `java.lang.String`
- `connection-name` — Name of connection to be used. Defaults to default connection Type: `java.lang.String`
- `content-type` — Request content type to be used. default: application/json Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `expected-response-type` — Expected response type. Default: java.lang.Object Type: `java.lang.Class<?>`
- `parts` — List of part objects. This is expected to be used when parts has to be dynamic. Resultant json should map to list of parts. Type: `java.lang.Object`
- `proxy-host-port` — Proxy host and port in host:port format. Type: `java.lang.String`
- `response-context-attribure` — Context attribute name on which the actaul rest response object will be placed. default: response Type: `java.lang.String` Default: `response`
- `result-context-attribute` — Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly<br/>3. headers (Map&lt;String, List&lt;String&gt;&gt;) - Response header. Note: a header can have multiple values.<br/>default: result Type: `java.lang.String` Default: `result`
- `uri` (mandatory) — Uri to be invoked. Type: `java.lang.String`

**Child elements:**

- `attachment` (multiple) — List of files to be attached with this request. Values are resources
- `form-field` (multiple) — Form field for current request
- `header` (multiple) — Http Header for the request
- `param` (multiple) — Request parameter of current request
- `part` (multiple) — Parts to be set on the request. If non-string is specified, object will be converted to json and content-type of part will be set as JSON.
- `path-variable` (multiple) — Path variable to be replaced in the URI
- `request-form-field` (multiple) — Form field for current request
- `request-header` (multiple) — Http Header for the request
- `request-param` (multiple) — Request parameter of current request
- `request-path-variable` (multiple) — Path variable to be replaced in the URI

### s:rest-invoke-multipart-post

- **Title**: rest invoke multipart post
- **Group**: Rest_Api
- **Description**: Used to invoke Multipart POST api.
- **Java type**: `com.yukthitech.autox.plugin.rest.steps.InvokePostWithAttachmentStep`
- **Required plugins**: RestPlugin

**Attributes:**

- `attachments` — List of attachment objects. This is expected to be used when attachments has to be dynamic. Resultant json should map to list of attachments. Type: `java.lang.Object`
- `base-url` — Base url to be used. If specified, this will be used instead of using base url from plugin. Type: `java.lang.String`
- `connection-name` — Name of connection to be used. Defaults to default connection Type: `java.lang.String`
- `content-type` — Request content type to be used. default: application/json Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `expected-response-type` — Expected response type. Default: java.lang.Object Type: `java.lang.Class<?>`
- `parts` — List of part objects. This is expected to be used when parts has to be dynamic. Resultant json should map to list of parts. Type: `java.lang.Object`
- `proxy-host-port` — Proxy host and port in host:port format. Type: `java.lang.String`
- `response-context-attribure` — Context attribute name on which the actaul rest response object will be placed. default: response Type: `java.lang.String` Default: `response`
- `result-context-attribute` — Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly<br/>3. headers (Map&lt;String, List&lt;String&gt;&gt;) - Response header. Note: a header can have multiple values.<br/>default: result Type: `java.lang.String` Default: `result`
- `uri` (mandatory) — Uri to be invoked. Type: `java.lang.String`

**Child elements:**

- `attachment` (multiple) — List of files to be attached with this request. Values are resources
- `form-field` (multiple) — Form field for current request
- `header` (multiple) — Http Header for the request
- `param` (multiple) — Request parameter of current request
- `part` (multiple) — Parts to be set on the request. If non-string is specified, object will be converted to json and content-type of part will be set as JSON.
- `path-variable` (multiple) — Path variable to be replaced in the URI
- `request-form-field` (multiple) — Form field for current request
- `request-header` (multiple) — Http Header for the request
- `request-param` (multiple) — Request parameter of current request
- `request-path-variable` (multiple) — Path variable to be replaced in the URI

### s:rest-invoke-multipart-put

- **Title**: rest invoke multipart put
- **Group**: Rest_Api
- **Description**: Used to invoke Multipart PUT api.
- **Java type**: `com.yukthitech.autox.plugin.rest.steps.InvokePutWithAttachmentStep`
- **Required plugins**: RestPlugin

**Attributes:**

- `attachments` — List of attachment objects. This is expected to be used when attachments has to be dynamic. Resultant json should map to list of attachments. Type: `java.lang.Object`
- `base-url` — Base url to be used. If specified, this will be used instead of using base url from plugin. Type: `java.lang.String`
- `connection-name` — Name of connection to be used. Defaults to default connection Type: `java.lang.String`
- `content-type` — Request content type to be used. default: application/json Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `expected-response-type` — Expected response type. Default: java.lang.Object Type: `java.lang.Class<?>`
- `parts` — List of part objects. This is expected to be used when parts has to be dynamic. Resultant json should map to list of parts. Type: `java.lang.Object`
- `proxy-host-port` — Proxy host and port in host:port format. Type: `java.lang.String`
- `response-context-attribure` — Context attribute name on which the actaul rest response object will be placed. default: response Type: `java.lang.String` Default: `response`
- `result-context-attribute` — Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly<br/>3. headers (Map&lt;String, List&lt;String&gt;&gt;) - Response header. Note: a header can have multiple values.<br/>default: result Type: `java.lang.String` Default: `result`
- `uri` (mandatory) — Uri to be invoked. Type: `java.lang.String`

**Child elements:**

- `attachment` (multiple) — List of files to be attached with this request. Values are resources
- `form-field` (multiple) — Form field for current request
- `header` (multiple) — Http Header for the request
- `param` (multiple) — Request parameter of current request
- `part` (multiple) — Parts to be set on the request. If non-string is specified, object will be converted to json and content-type of part will be set as JSON.
- `path-variable` (multiple) — Path variable to be replaced in the URI
- `request-form-field` (multiple) — Form field for current request
- `request-header` (multiple) — Http Header for the request
- `request-param` (multiple) — Request parameter of current request
- `request-path-variable` (multiple) — Path variable to be replaced in the URI

### s:rest-invoke-patch

- **Title**: rest invoke patch
- **Group**: Rest_Api
- **Description**: Used to invoke PATCH api.
- **Java type**: `com.yukthitech.autox.plugin.rest.steps.InvokePatchStep`
- **Required plugins**: RestPlugin

**Attributes:**

- `base-url` — Base url to be used. If specified, this will be used instead of using base url from plugin. Type: `java.lang.String`
- `body` — Body to be set. If non-string is specified, object will be converted to json and content-type header will be set as JSON. Type: `java.lang.Object`
- `connection-name` — Name of connection to be used. Defaults to default connection Type: `java.lang.String`
- `content-charset` — Content char set to be used for body. Type: `java.lang.String`
- `content-type` — Request content type to be used. default: application/json Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `expected-response-type` — Expected response type. Default: java.lang.Object Type: `java.lang.Class<?>`
- `proxy-host-port` — Proxy host and port in host:port format. Type: `java.lang.String`
- `response-context-attribure` — Context attribute name on which the actaul rest response object will be placed. default: response Type: `java.lang.String` Default: `response`
- `result-context-attribute` — Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly<br/>3. headers (Map&lt;String, List&lt;String&gt;&gt;) - Response header. Note: a header can have multiple values.<br/>default: result Type: `java.lang.String` Default: `result`
- `uri` (mandatory) — Uri to be invoked. Type: `java.lang.String`

**Child elements:**

- `body` — Body of the request
- `form-field` (multiple) — Form field for current request
- `header` (multiple) — Http Header for the request
- `param` (multiple) — Request parameter of current request
- `path-variable` (multiple) — Path variable to be replaced in the URI
- `request-form-field` (multiple) — Form field for current request
- `request-header` (multiple) — Http Header for the request
- `request-param` (multiple) — Request parameter of current request
- `request-path-variable` (multiple) — Path variable to be replaced in the URI

### s:rest-invoke-post

- **Title**: rest invoke post
- **Group**: Rest_Api
- **Description**: Used to invoke POST api.
- **Java type**: `com.yukthitech.autox.plugin.rest.steps.InvokePostStep`
- **Required plugins**: RestPlugin

**Attributes:**

- `base-url` — Base url to be used. If specified, this will be used instead of using base url from plugin. Type: `java.lang.String`
- `body` — Body to be set. If non-string is specified, object will be converted to json and content-type header will be set as JSON. Type: `java.lang.Object`
- `connection-name` — Name of connection to be used. Defaults to default connection Type: `java.lang.String`
- `content-charset` — Content char set to be used for body. Type: `java.lang.String`
- `content-type` — Request content type to be used. default: application/json Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `expected-response-type` — Expected response type. Default: java.lang.Object Type: `java.lang.Class<?>`
- `proxy-host-port` — Proxy host and port in host:port format. Type: `java.lang.String`
- `response-context-attribure` — Context attribute name on which the actaul rest response object will be placed. default: response Type: `java.lang.String` Default: `response`
- `result-context-attribute` — Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly<br/>3. headers (Map&lt;String, List&lt;String&gt;&gt;) - Response header. Note: a header can have multiple values.<br/>default: result Type: `java.lang.String` Default: `result`
- `uri` (mandatory) — Uri to be invoked. Type: `java.lang.String`

**Child elements:**

- `body` — Body of the request
- `form-field` (multiple) — Form field for current request
- `header` (multiple) — Http Header for the request
- `param` (multiple) — Request parameter of current request
- `path-variable` (multiple) — Path variable to be replaced in the URI
- `request-form-field` (multiple) — Form field for current request
- `request-header` (multiple) — Http Header for the request
- `request-param` (multiple) — Request parameter of current request
- `request-path-variable` (multiple) — Path variable to be replaced in the URI

### s:rest-invoke-put

- **Title**: rest invoke put
- **Group**: Rest_Api
- **Description**: Used to invoke PUT api.
- **Java type**: `com.yukthitech.autox.plugin.rest.steps.InvokePutStep`
- **Required plugins**: RestPlugin

**Attributes:**

- `base-url` — Base url to be used. If specified, this will be used instead of using base url from plugin. Type: `java.lang.String`
- `body` — Body to be set. If non-string is specified, object will be converted to json and content-type header will be set as JSON. Type: `java.lang.Object`
- `connection-name` — Name of connection to be used. Defaults to default connection Type: `java.lang.String`
- `content-charset` — Content char set to be used for body. Type: `java.lang.String`
- `content-type` — Request content type to be used. default: application/json Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `expected-response-type` — Expected response type. Default: java.lang.Object Type: `java.lang.Class<?>`
- `proxy-host-port` — Proxy host and port in host:port format. Type: `java.lang.String`
- `response-context-attribure` — Context attribute name on which the actaul rest response object will be placed. default: response Type: `java.lang.String` Default: `response`
- `result-context-attribute` — Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly<br/>3. headers (Map&lt;String, List&lt;String&gt;&gt;) - Response header. Note: a header can have multiple values.<br/>default: result Type: `java.lang.String` Default: `result`
- `uri` (mandatory) — Uri to be invoked. Type: `java.lang.String`

**Child elements:**

- `body` — Body of the request
- `form-field` (multiple) — Form field for current request
- `header` (multiple) — Http Header for the request
- `param` (multiple) — Request parameter of current request
- `path-variable` (multiple) — Path variable to be replaced in the URI
- `request-form-field` (multiple) — Form field for current request
- `request-header` (multiple) — Http Header for the request
- `request-param` (multiple) — Request parameter of current request
- `request-path-variable` (multiple) — Path variable to be replaced in the URI

