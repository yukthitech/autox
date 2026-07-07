# Mail Steps

Auto-generated reference for `Group.Mail` steps. Use step tags with the `s:` namespace.

### s:delete-emails

- **Title**: delete emails
- **Group**: Mail
- **Description**: Step to delete mails.
- **Java type**: `com.yukthitech.autox.test.mail.steps.DeleteEmailsStep`
- **Required plugins**: EmailPlugin

**Attributes:**

- `delete-count-attribute` — Name of attribute to which delete count should be populated. Default: deleteCount Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `email-server-name` — Email server from which mails has to be deleted. If not specified, default server will be used. Type: `java.lang.String`
- `from-address-pattern` — Email-Id pattern of mails to be deleted. Type: `java.lang.String`
- `subject-pattern` (mandatory) — Subject pattern of mails to be deleted. Type: `java.lang.String`

### s:read-emails

- **Title**: read emails
- **Group**: Mail
- **Description**: Step to read mails.
- **Java type**: `com.yukthitech.autox.test.mail.steps.ReadEmailsStep`
- **Required plugins**: EmailPlugin

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `email-server-name` — Email server from which mails has to be read. If not specified, default server will be used. Type: `java.lang.String`
- `from-address-pattern` — Email-Id pattern of mails to be read. Type: `java.lang.String`
- `mail-attribute` — Attribute name to which read mails has to be set. Default: readMails Type: `java.lang.String`
- `subject-pattern` — Subject pattern of mails to be read. Type: `java.lang.String`

### s:send-email

- **Title**: send email
- **Group**: Mail
- **Description**: Step to send mail, useful to send specific notifications from test cases.
- **Java type**: `com.yukthitech.autox.test.mail.steps.SendEmailStep`
- **Required plugins**: EmailPlugin

**Attributes:**

- `content` (mandatory) — Content to be used for sending mail. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `email-server-name` — Email server from which mail has to be sent. If not specified, default server will be used. Type: `java.lang.String`
- `from-address` — Email-Id from which notification should be marked as sent. Type: `java.lang.String`
- `subject` (mandatory) — Subject to be used for sending mail. Type: `java.lang.String`
- `to-address-list` (mandatory) — Space separated address list to which notification should be sent. Type: `java.lang.String`

