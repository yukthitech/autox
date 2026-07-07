# Ui Validations

Auto-generated reference for `Group.Ui` assertion/validation steps.

### s:ui-assert-form-fields

- **Title**: ui assert form fields
- **Group**: Ui
- **Description**: Validates specified form fields are present
- **Java type**: `com.yukthitech.autox.plugin.ui.assertion.AssertFormFields`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `locator` (mandatory) — Locator of the form to be validated. Type: `java.lang.String`

**Child elements:**

- `field` (multiple) — Form field to validate

### s:ui-assert-value

- **Title**: ui assert value
- **Group**: Ui
- **Description**: Validates specified element has specified value/text
- **Java type**: `com.yukthitech.autox.plugin.ui.assertion.UiAssertValue`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `display-value` — If set to true, instead of value, display value will be fetched (currently non-select fields will return value itself). Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `locator` (mandatory) — Locator of the element to be validated. Type: `java.lang.String`
- `parent-element` — Name of the parent element under which locator needs to be searched. If not specified, fetches globally. Type: `java.lang.String`
- `value` (mandatory) — Expected value of the element. Type: `java.lang.String`

### s:ui-assert-visibility

- **Title**: ui assert visibility
- **Group**: Ui
- **Description**: Validates specified element is visible/hidden
- **Java type**: `com.yukthitech.autox.plugin.ui.assertion.AssertVisibility`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `enabled` — Enables/disables current validation.
Default: true Type: `java.lang.String`
- `locator` (mandatory) — Locator of the element to validate Type: `java.lang.String`
- `message` — Message expected in the target element. Type: `java.lang.String`
- `parent-element` — Name of the parent element under which locator needs to be searched. If not specified, fetches globally. Type: `java.lang.String`
- `retry-count` — Number of retries to happen. Default: 5 Type: `int`
- `retry-time-gap-millis` — Time gap between retries. Default: 1000 Type: `int`
- `visible` — Flag indicating if the validation is for visibility or invisibility.
Default: true Type: `java.lang.String`

