# Ui Steps

Auto-generated reference for `Group.Ui` steps. Use step tags with the `s:` namespace.

### s:record-video

- **Title**: record video
- **Group**: Ui
- **Description**: Records the browser screen as video for all the steps under current step
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.RecordVideoStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `frames-per-sec` (mandatory) — Video speed in frames per second. Defaults to 1. Type: `java.lang.Integer`
- `name` (mandatory) — Name of the video file to be created. May get suffixed to create unique file. Type: `java.lang.String`
- `steps` (mandatory) — Group of steps/validations to be executed in loop. Type: `java.util.List<com.yukthitech.autox.IStep>`

### s:ui-click

- **Title**: ui click
- **Group**: Ui
- **Description**: Clicks the specified target
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.ClickStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `click-by-js` — Flag to enforce clicking using js instead of selenium Type: `boolean`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `locator` (mandatory) — Locator of the element to be clicked. Out of located elements, first element will be clicked. Type: `java.lang.String`
- `parent-element` — Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. If not specified, fetches globally. Type: `java.lang.Object`
- `post-hide-locator` — Post click the locator to be used to check for hidden. If this locator is visible, click will be retried till this locator is visible or timeout. Note: Polling for visibility will be done every 100 millis. Type: `java.lang.String`
- `post-verification-delay` — Time to wait to perform post verification in millis. Default: 2000 Type: `int`
- `post-visibility-locator` — Post click the locator to be used to check for visibility. If this locator is not visible, click will be retried till this locator is visible or timeout. Note: Polling for visibility will be done every 100 millis. Type: `java.lang.String`
- `retry-count` — Number of retries to happen. Default: 10 Type: `int`
- `retry-time-gap-millis` — Time gap between retries. Default: 1000 Type: `int`
- `try-js-on-error` — Default: true. When true, if click by selenium fails, js code will be tried to click the locator. Type: `boolean`

### s:ui-click-and-download

- **Title**: ui click and download
- **Group**: Ui
- **Description**: Clicks the specified target and download the result file. If no  file is downloaded, this will throw exception.
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.ClickAndDownloadStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `download-wait-time` (mandatory) — Time to wait for download to complete in millis. Default: 30000 Type: `long`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `extensions` — Comma separated supported extensions. If specified, once file is found with any of these extensions, the wait will end. Type: `java.lang.String`
- `locator` (mandatory) — Locator of the element to be triggered. Out of located elements, first element will be clicked. Type: `java.lang.String`
- `parent-element` — Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. If not specified, fetches globally. Type: `java.lang.Object`
- `path-name` (mandatory) — Attribute name which would be set with the downloaded file path. Type: `java.lang.String`
- `retry-count` — Number of retries to happen. Default: 5 Type: `int`
- `retry-time-gap-millis` — Time gap between retries. Default: 1000 Type: `int`

### s:ui-close-session

- **Title**: ui close session
- **Group**: Ui
- **Description**: Closes the current session (not only window but also driver).
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.CloseSessionStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `reset-driver` (mandatory) — To be set to true, if the driver needs to be reset. So that browser can be opened freshly. Default: false Type: `boolean`

### s:ui-close-window

- **Title**: ui close window
- **Group**: Ui
- **Description**: Closes the specified/current window. But does not close the session.
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.CloseWindowStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `name` — Name of the window. If none is specified, current window will be closed. Type: `java.lang.String`

### s:ui-dbl-click

- **Title**: ui dbl click
- **Group**: Ui
- **Description**: Double Clicks the specified target
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.StepDoubleClick`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `locator` (mandatory) — Locator of the element to be double-cicked. Type: `java.lang.String`
- `parent-element` — Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. If not specified, fetches globally. Type: `java.lang.Object`
- `post-hide-locator` — Post click the locator to be used to check for hidden. If this locator is visible, click will be retried till this locator is visible or timeout. Note: Polling for visibility will be done every 100 millis. Type: `java.lang.String`
- `post-verification-delay` — Time to wait to perform post verification in millis. Default: 2000 Type: `int`
- `post-visibility-locator` — Post click the locator to be used to check for visibility. If this locator is not visible, click will be retried till this locator is visible or timeout. Note: Polling for visibility will be done every 100 millis. Type: `java.lang.String`
- `retry-count` — Number of retries to happen. Default: 5 Type: `int`
- `retry-time-gap-millis` — Time gap between retries. Default: 1000 Type: `int`

### s:ui-drag-and-drop

- **Title**: ui drag and drop
- **Group**: Ui
- **Description**: Drags the specified element to specified target
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.DragAndDropStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `destination` (mandatory) — Locator of element on which source element should be dropped Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `parent-element` — Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. If not specified, fetches globally. Type: `java.lang.Object`
- `source` (mandatory) — Locator of element which needs to be dragged Type: `java.lang.String`

### s:ui-execute-js

- **Title**: ui execute js
- **Group**: Ui
- **Description**: Can be used to execute js code. If the result needs to be set on context, from js code 'return' should be used to return approp value.
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.UiExecuteJsStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `result-attribute` — If specified, the result of the js execution (returned by 'return' statement) will be stored with this name on context. Default: null Type: `java.lang.String`
- `script` (mandatory) — Script to execute Type: `java.lang.String`

### s:ui-fill-form

- **Title**: ui fill form
- **Group**: Ui
- **Description**: Fills the form with specified data
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.FillFormStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `data` (mandatory) — Data to populate in the form Type: `java.lang.Object`
- `delay` — Delay in millis time between field to field filling. Useful in forms, in which field options are fetched based on other field values. Default: 10 Type: `int`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `locator` — Html locator of the parent form or container (like DIV) enclosing the input elements. Type: `java.lang.String`
- `parent-element` — Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. If not specified, fetches globally. Type: `java.lang.Object`

**Child elements:**

- `data` — Data to fill

### s:ui-get-elements

- **Title**: ui get elements
- **Group**: Ui
- **Description**: Fetches value of specified ui element
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.UiGetElementsStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `locator` (mandatory) — Locator of the elements to be fetched Type: `java.lang.String`
- `name` (mandatory) — Name of the attribute to set. Type: `java.lang.String`
- `parent-element` — Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. If not specified, fetches globally. Type: `java.lang.Object`

### s:ui-get-value

- **Title**: ui get value
- **Group**: Ui
- **Description**: Fetches value of specified ui element
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.UiGetValueStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `display-value` — If set to true, instead of value, display value will be fetched (currently non-select fields will return value itself). Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `locator` (mandatory) — Locator of the element for which value needs to be fetched Type: `java.lang.String`
- `name` (mandatory) — Name of the attribute to set. Type: `java.lang.String`
- `parent-element` — Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. If not specified, fetches globally. Type: `java.lang.Object`

### s:ui-goto-page

- **Title**: ui goto page
- **Group**: Ui
- **Description**: Loads page with specified uri
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.GotoPageStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `uri` (mandatory) — URI of the page to load Type: `java.lang.String`

### s:ui-goto-url

- **Title**: ui goto url
- **Group**: Ui
- **Description**: Loads page with specified url
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.GotoUrlStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `url` (mandatory) — URL of the page to load Type: `java.lang.String`

### s:ui-handle-alert

- **Title**: ui handle alert
- **Group**: Ui
- **Description**: Used to validate and click ok of alert prompt.
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.HandleAlertStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `expected-message` — Messaged expected in alert. If specified, alert message will be validated with this message. Type: `java.lang.String`

### s:ui-handle-confirm

- **Title**: ui handle confirm
- **Group**: Ui
- **Description**: Used to validate and click ok/cancel of confirm prompt.
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.HandleConfirmStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `accept` (mandatory) — Flag used to accept or cancel confirm box. Default: true Type: `boolean`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `expected-message` — Messaged expected in alert. If specified, alert message will be validated with this message. Type: `java.lang.String`

### s:ui-handle-prompt

- **Title**: ui handle prompt
- **Group**: Ui
- **Description**: Used to validate, feed and accept/cancel prompt.
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.HandlePromptStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `accept` (mandatory) — Flag used to accept or cancel confirm box. Default: true Type: `boolean`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `expected-message` — Messaged expected in alert. If specified, alert message will be validated with this message. Type: `java.lang.String`
- `text` — If specified, feeds the specified text to the prompt Type: `java.lang.String`

### s:ui-is-visible

- **Title**: ui is visible
- **Group**: Ui
- **Description**: Fetches flag indicating if target element is visible or not
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.IsVisibleStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `locator` (mandatory) — Locator of the element for which value needs to be fetched Type: `java.lang.String`
- `name` (mandatory) — Name of the attribute to set. Type: `java.lang.String`
- `parent-element` — Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. If not specified, fetches globally. Type: `java.lang.Object`

### s:ui-load-cookies

- **Title**: ui load cookies
- **Group**: Ui
- **Description**: Loads cookies from specified file into current session cookies.
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.LoadCookiesStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `path` — Path of the file where cookies should be loaded from. Default: ./cookies.ser Type: `java.lang.String`

### s:ui-log-screen-shot

- **Title**: ui log screen shot
- **Group**: Ui
- **Description**: Takes current screen snapshot and adds to the log
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.LogScreenShotStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `level` — Logging level. Default Value: DEBUG Type: `com.yukthitech.autox.exec.report.LogLevel`
- `message` — Message to be logged along with image Type: `java.lang.String`
- `name` (mandatory) — Name of the screenshot image file to be created Type: `java.lang.String`

### s:ui-maximize-browser

- **Title**: ui maximize browser
- **Group**: Ui
- **Description**: Maximizes the current browser window.
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.MaximizeBrowserStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`

### s:ui-move-mouse

- **Title**: ui move mouse
- **Group**: Ui
- **Description**: Moves the mouse to specified target and optionally clicks the element.
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.MoveMouseStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `parent-element` — Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. If not specified, fetches globally. Type: `java.lang.Object`
- `xoffset` (mandatory) — Mouse mouse in x-direction by specified amount. Type: `int`
- `yoffset` (mandatory) — Mouse mouse in y-direction by specified amount. Type: `int`

### s:ui-move-to

- **Title**: ui move to
- **Group**: Ui
- **Description**: Moves the view to specified target and optionally clicks the element.
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.MoveToStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `align-to-top` — Before moving the element, the element will be aligned to document. This flag indicates if alignment should be to top or bottom. Default: true Type: `boolean`
- `click` — If true, moves the mouse to target and clicks the element. Default: false Type: `boolean`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `locator` (mandatory) — Locator of the element to which view needs to be moved. Type: `java.lang.String`
- `parent-element` — Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. If not specified, fetches globally. Type: `java.lang.Object`
- `retry-count` — Number of retries to happen. Default: 5 Type: `int`
- `retry-time-gap-millis` — Time gap between retries. Default: 1000 Type: `int`
- `time-gap` — Time gap (in millis) which will be used before clicking and after moving the mouse over the element. Default: 10 Type: `long`

### s:ui-open-window

- **Title**: ui open window
- **Group**: Ui
- **Description**: Opens new window with specifie name and url.
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.OpenWindowStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `name` (mandatory) — Name of the window being opened. Type: `java.lang.String`
- `url` (mandatory) — Url to be opened. Type: `java.lang.String`

### s:ui-quit-session

- **Title**: ui quit session
- **Group**: Ui
- **Description**: Quits the driver. In order to user driver again it has to be initialized.
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.QuitSessionStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`

### s:ui-refresh

- **Title**: ui refresh
- **Group**: Ui
- **Description**: Refreshes the current page. This step uses 2 min post-verification delay by default.
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.RefreshStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `parent-element` — Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. If not specified, fetches globally. Type: `java.lang.Object`
- `post-hide-locator` — Post click the locator to be used to check for hidden. If this locator is visible, click will be retried till this locator is visible or timeout. Note: Polling for visibility will be done every 100 millis. Type: `java.lang.String`
- `post-verification-delay` — Time to wait to perform post verification in millis. Default: 2000 Type: `int`
- `post-visibility-locator` — Post click the locator to be used to check for visibility. If this locator is not visible, click will be retried till this locator is visible or timeout. Note: Polling for visibility will be done every 100 millis. Type: `java.lang.String`
- `retry-count` — Number of retries to happen. Default: 10 Type: `int`
- `retry-time-gap-millis` — Time gap between retries. Default: 1000 Type: `int`

### s:ui-reset-session

- **Title**: ui reset session
- **Group**: Ui
- **Description**: Resets the driver for usage.
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.ResetSessionStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`

### s:ui-right-click

- **Title**: ui right click
- **Group**: Ui
- **Description**: Right clicks the specified target
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.RightClickStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `locator` (mandatory) — Locator of the element to be triggered. Out of located elements, first element will be clicked. Type: `java.lang.String`
- `parent-element` — Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. If not specified, fetches globally. Type: `java.lang.Object`
- `retry-count` — Number of retries to happen. Default: 5 Type: `int`
- `retry-time-gap-millis` — Time gap between retries. Default: 1000 Type: `int`

### s:ui-set-style

- **Title**: ui set style
- **Group**: Ui
- **Description**: Used to manipulate the style of the element.
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.SetStyleStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `locator` (mandatory) — Locator of the element whose style needs to be modified. Type: `java.lang.String`
- `parent-element` — Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. If not specified, fetches globally. Type: `java.lang.Object`
- `styles` (mandatory) — Styles to be modified. Type: `java.util.Map<java.lang.Stringjava.lang.String>`

### s:ui-set-value

- **Title**: ui set value
- **Group**: Ui
- **Description**: Populates specified field with specified value
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.UiSetValueStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `locator` (mandatory) — Locator of the element to be populated Type: `java.lang.String`
- `parent-element` — Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. If not specified, fetches globally. Type: `java.lang.Object`
- `press-enter-at-end` — If true, an enter-key press will be simulated on target element after populating value. Default: false Type: `boolean`
- `value` — Value to be filled with. Defaults to empty string. Type: `java.lang.String`

### s:ui-store-cookies

- **Title**: ui store cookies
- **Group**: Ui
- **Description**: Stores the current session cookies into specified file.
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.StoreCookiesStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `path` — Path of the file where cookies should be persisted. Default: ./cookies.ser Type: `java.lang.String`

### s:ui-switch-frame

- **Title**: ui switch frame
- **Group**: Ui
- **Description**: Helps in switching the frames
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.SwitchFrame`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `index` — Index of the frame. Either locator or index is mandatory. Type: `java.lang.Integer`
- `locator` — Locator of the frame. Either locator or index is mandatory. Type: `java.lang.String`

### s:ui-switch-window

- **Title**: ui switch window
- **Group**: Ui
- **Description**: Helps in switching between windows
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.SwitchWindow`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `locator` — Locator of the window. If none is specified, main window will be selected. Type: `java.lang.String`

### s:ui-wait-for

- **Title**: ui wait for
- **Group**: Ui
- **Description**: Waits for (at least one) specified element to become visible/hidden
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.WaitForStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `hidden` — If true, this step waits for element with specified locator gets removed or hidden.
Default: false Type: `java.lang.String`
- `locators` (mandatory) — Locator(s) of the element to be waited for Type: `java.util.List<java.lang.String>`
- `parent-element` — Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. If not specified, fetches globally. Type: `java.lang.Object`
- `retry-count` — Number of retries to happen. Default: 5 Type: `int`
- `retry-time-gap-millis` — Time gap between retries. Default: 1000 Type: `int`

### s:ui-wait-for-conditions

- **Title**: ui wait for conditions
- **Group**: Ui
- **Description**: Waits for all specified conditions to be true
- **Java type**: `com.yukthitech.autox.plugin.ui.steps.WaitForConditionsStep`
- **Required plugins**: SeleniumPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `driver-name` — Name of the driver to be used for the step. Defaults to default driver. Type: `java.lang.String`
- `parent-element` — Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. If not specified, fetches globally. Type: `java.lang.Object`

**Child elements:**

- `title-is` (multiple) — Condition to check page title

