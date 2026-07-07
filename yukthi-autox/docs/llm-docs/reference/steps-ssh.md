# Ssh Steps

Auto-generated reference for `Group.Ssh` steps. Use step tags with the `s:` namespace.

### s:ssh-close-session

- **Title**: ssh close session
- **Group**: Ssh
- **Description**: Closes the specified remote session.
- **Java type**: `com.yukthitech.autox.test.ssh.steps.CloseSessionStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `session` (mandatory) — Name of the session to be closed. Type: `java.lang.String`

### s:ssh-execute-command

- **Title**: ssh execute command
- **Group**: Ssh
- **Description**: Executes specified command on specified session and stores the output on context.
- **Java type**: `com.yukthitech.autox.test.ssh.steps.ExecuteCommandStep`

**Attributes:**

- `command` (mandatory) — Command to be executed. Type: `java.lang.String`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `exit-status-var` — Name of the context attribute on which exit status to be set. Default: exitStatus Type: `java.lang.String` Default: `exitStatus`
- `output-var` — Name of the output context attribute on which output needs to be set. Default: output Type: `java.lang.String` Default: `output`
- `session` (mandatory) — Name of the session on which command needs to be executed. Type: `java.lang.String`

### s:ssh-start-session

- **Title**: ssh start session
- **Group**: Ssh
- **Description**: Starts a new session with specified details. The session can be accessed in other ssh steps with specified name.
- **Java type**: `com.yukthitech.autox.test.ssh.steps.StartSessionStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `host` (mandatory) — Remote host to be connected. Type: `java.lang.String`
- `name` (mandatory) — Name for the session being started. Type: `java.lang.String`
- `password` — Password for login. Either of password or private-key is mandatory. If both are provided, password will be given higher preference. Type: `java.lang.String`
- `port` — Remote host's ssh port. Default: 22 Type: `int`
- `private-key-path` — Private key to be used for login. Either of password or private-key is mandatory. If both are provided, password will be given higher preference. Type: `java.lang.String`
- `user` (mandatory) — User name for login. Type: `java.lang.String`

