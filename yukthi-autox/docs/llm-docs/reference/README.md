# AutoX API Reference Index

Auto-generated index of LLM reference files. Steps and validations are split by the [`Group`](../../src/main/java/com/yukthitech/autox/Group.java) enum declared on each `@Executable` step/validation.

## Steps and validations by group

| Group | Steps | Validations |
|-------|-------|-------------|
| `Common` | [steps-common.md](steps-common.md) | [validations-common.md](validations-common.md) |
| `Io` | [steps-io.md](steps-io.md) | [validations-io.md](validations-io.md) |
| `Lang` | [steps-lang.md](steps-lang.md) | [validations-lang.md](validations-lang.md) |
| `Mail` | [steps-mail.md](steps-mail.md) | [validations-mail.md](validations-mail.md) |
| `Mock` | [steps-mock.md](steps-mock.md) | [validations-mock.md](validations-mock.md) |
| `Mongodb` | [steps-mongodb.md](steps-mongodb.md) | [validations-mongodb.md](validations-mongodb.md) |
| `Rdbms` | [steps-rdbms.md](steps-rdbms.md) | [validations-rdbms.md](validations-rdbms.md) |
| `Rest_Api` | [steps-rest-api.md](steps-rest-api.md) | [validations-rest-api.md](validations-rest-api.md) |
| `Ssh` | [steps-ssh.md](steps-ssh.md) | [validations-ssh.md](validations-ssh.md) |
| `Store` | [steps-store.md](steps-store.md) | [validations-store.md](validations-store.md) |
| `Ui` | [steps-ui.md](steps-ui.md) | [validations-ui.md](validations-ui.md) |

## Other reference files

| Topic | File |
|-------|------|
| Plugins | [plugins.md](plugins.md) |
| Prefix expressions (`attr:`, `xpath:`, `json:`, etc.) | [prefix-expressions.md](prefix-expressions.md) |
| FreeMarker methods (`${...}`) | [freemarker-methods.md](freemarker-methods.md) |
| UI locator types | [ui-locators.md](ui-locators.md) |
| Full machine-readable schema | [../doc-information.json](../doc-information.json) |

## All Group enum values

Defined in `com.yukthitech.autox.Group`: Lang, Common, Io, Rdbms, Mongodb, Ui, Mock, Rest_Api, Ssh, Store, Mail

Files are generated only for groups that have at least one step or validation.

## Notes

- `proxy-host-port` is a REST step attribute, not a separate group.
- Prefix expressions from `DefaultPrefixExpressions` and plugin parsers are documented in a single file.
- For how-to patterns see the numbered guides in the parent folder (`07-rest-automation.md`, `11-language-steps.md`, etc.).
