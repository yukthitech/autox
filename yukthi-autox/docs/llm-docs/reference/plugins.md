# Plugins

Auto-generated reference for AutoX plugins. Configure these in `app-configuration.xml`.

## DbPlugin

- **Description**: Plugin related to db related steps or validators.
- **Java type**: `com.yukthitech.autox.plugin.sql.DbPlugin`

### Parameters

- `data-source-map` (mandatory) — Name to data source mapping for different data sources that are available in this app automation. Type: `java.util.Map<java.lang.Stringjavax.sql.DataSource>`
- `default-data-source` — Name of the default data source name. Type: `java.lang.String`
- `max-sessions` (mandatory) — Maximum number of sessions that can be opened simultaneously. Defaults to 10. Type: `int`

## EmailPlugin

- **Description**: Plugin related to email reading or sending.
- **Java type**: `com.yukthitech.autox.plugin.mail.EmailPlugin`

### Parameters

- `default-email-server` — Name of the default email server. Type: `java.lang.String`
- `email-settings-map` (mandatory) — Name to setting mapping for different email servers. Type: `java.util.Map<java.lang.Stringcom.yukthitech.autox.plugin.mail.EmailServerSettings>`
- `max-sessions` (mandatory) — Maximum number of sessions that can be opened simultaneously. Defaults to 10. Type: `int`

## PlaywrightPlugin

- **Description**: Plugin needed by playwright/ui-automation based steps or validators.
- **Java type**: `com.yukthitech.autox.plugin.ui.PlaywrightPlugin`

### Parameters

- `base-url` (mandatory) — Base url to be used for ui automation Type: `java.lang.String`
- `drivers` (mandatory) — Name to basic configuration to be used for different drivers. Like - name, browser-type, headless, etc. Type: `java.util.Map<java.lang.Stringcom.yukthitech.autox.plugin.ui.PlaywrightDriverConfig>`
- `max-sessions` (mandatory) — Maximum number of sessions that can be opened simultaneously. Defaults to 10. Type: `int`

## RestPlugin

- **Description**: Plugin for REST based steps and validations.
- **Java type**: `com.yukthitech.autox.plugin.rest.RestPlugin`

### Parameters

- `max-sessions` (mandatory) — Maximum number of sessions that can be opened simultaneously. Defaults to 10. Type: `int`

### Events

#### initialize

- **Description**: Invoked when creating new session. Authentication and setting default token header can be done here

**Event parameters:**

- `connectionName` — Name of current active connection.

#### unauthorized

- **Description**: Invoked when unauthorized response is received with http-status 401. Which may occur during session timeout. This event can be used to reset session with new token without disturbing testcases
- **Return**: Boolean value (true/false). True indicates session is reset successfully and current request (which caused unauthoirzed response) will be reinvoked (this retry logic is executed only once). If null or non-true value is returned, no action will be taken.

**Event parameters:**

- `connectionName` — Name of current active connection.
- `request` — Rest Request which resulted in unauthorized status. Test cases can send special values in form of headers or params, when session invalidation is being simulated.
- `result` — Current rest result which has unauthorized status.


