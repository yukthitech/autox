# AutoX LLM Documentation

This folder contains documentation for LLMs (Cursor, Claude, etc.) to author AutoX test automation XML correctly.

## How to use these docs

1. **Start here** when creating a new AutoX project or writing test XML.
2. Read the numbered guides (`01`–`14`) for concepts and patterns.
3. Look up step/validation details in `reference/` — split by [`Group`](../../src/main/java/com/yukthitech/autox/Group.java) enum (see [`reference/README.md`](reference/README.md)).
4. Use `doc-information.json` for programmatic lookup when you need the full schema.

## Guide index

| File | Topic |
|------|-------|
| [01-getting-started.md](01-getting-started.md) | Maven setup, project layout, `app.properties`, launch command |
| [02-app-configuration.md](02-app-configuration.md) | `app-configuration.xml`, REST and Selenium plugins |
| [03-test-suite-xml.md](03-test-suite-xml.md) | `<testData>`, global setup/cleanup, execution order |
| [04-test-cases.md](04-test-cases.md) | `<testCase>` anatomy, dependencies, groups, validations |
| [05-data-providers.md](05-data-providers.md) | List, default, and range data providers |
| [06-expressions.md](06-expressions.md) | Prefix expressions, FreeMarker, resource types |
| [07-rest-automation.md](07-rest-automation.md) | REST API test patterns (`Group.Rest_Api`) |
| [08-ui-automation.md](08-ui-automation.md) | Selenium UI test patterns (`Group.Ui`) |
| [09-functions-and-reuse.md](09-functions-and-reuse.md) | Reusable functions, beans, data-beans |
| [10-running-tests.md](10-running-tests.md) | CLI arguments, filtering, reports |
| [11-language-steps.md](11-language-steps.md) | Loops, if/else, try/catch (`Group.Lang`) |
| [12-sql-automation.md](12-sql-automation.md) | SQL/DDL/DML steps (`Group.Rdbms`) |
| [13-mongo-automation.md](13-mongo-automation.md) | MongoDB steps (`Group.Mongodb`) |
| [14-mock-server.md](14-mock-server.md) | HTTP mock server (`Group.Mock`) |

## Reference index (auto-generated)

Steps and validations are generated **per `Group` enum value** — see [`reference/README.md`](reference/README.md) for the full file list.

| File | Contents |
|------|----------|
| [reference/README.md](reference/README.md) | Index of all groups and reference files |
| [reference/steps-{group}.md](reference/README.md) | Steps per group (e.g. `steps-rest-api.md`, `steps-lang.md`) |
| [reference/validations-{group}.md](reference/README.md) | Validations per group |
| [reference/plugins.md](reference/plugins.md) | Plugin configuration and events |
| [reference/prefix-expressions.md](reference/prefix-expressions.md) | All prefix parsers in one file |
| [reference/freemarker-methods.md](reference/freemarker-methods.md) | `${...}` helper methods |
| [reference/ui-locators.md](reference/ui-locators.md) | UI locator types |

## Machine-readable index

- `doc-information.json` — full metadata for plugins, steps, validations, prefix expressions, FreeMarker methods, and UI locators.
- `autox-version.txt` — AutoX version these docs were generated from. Re-download when upgrading.

## Required XML conventions

Always use these namespaces in test suite XML:

```xml
<testData xmlns:s="http://autox.yukthitech.com/steps"
          xmlns:wrap="http://xmlbeanparser.yukthitech.com/wrap"
          xmlns:f="http://autox.yukthitech.com/functions">
```

Rules every test case must follow:

- `name` attribute is required and must not contain `#`.
- `<description>` is required.
- At least one step or validation is required.
- Step tags use the `s:` prefix (e.g. `s:rest-invoke-post`, `s:ui-click`).
- Data provider iteration data is accessed as `${attr.{providerName}.field}`.
- Look up step params in `reference/steps-{group-slug}.md` matching the step's `Group`.

## Setting up in a new project

See the root [README.md](https://github.com/yukthitech/autox/blob/main/README.md#using-autox-with-cursor-in-a-new-project) for steps to download these docs and the Cursor rule template into your project.
