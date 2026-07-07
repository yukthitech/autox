# UI Locators

Locator format: `<type>:<value>`

### class

- **Description**: Used to find ui elements using css class
- **Example**: `class: ...`

### css

- **Description**: Used to find ui elements using css locator.
- **Example**: `css: ...`

### id

- **Description**: Used to find ui elements using id.
- **Example**: `id: ...`

**Examples:**

```
<s:ui-click locator="id: button"/>
```


### js

- **Description**: Used to find ui elements using js expression.
- **Example**: `js: ...`

### name

- **Description**: Used to find ui elements using name of the element
- **Example**: `name: ...`

### tag

- **Description**: Used to find ui elements using tag name
- **Example**: `tag: ...`

### xpath

- **Description**: Used to find ui elements using xpath.
- **Example**: `xpath: ...`

**Examples:**

```
<s:ui-get-value locator="xpath: //input[@name='statusFld']" name="fldValue"/>
```


