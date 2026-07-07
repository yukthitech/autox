# Io Steps

Auto-generated reference for `Group.Io` steps. Use step tags with the `s:` namespace.

### s:io-create-temp-dir

- **Title**: io create temp dir
- **Group**: Io
- **Description**: Creates temporary directory within the work folder.
- **Java type**: `com.yukthitech.autox.test.io.steps.CreateTempDirStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `path-attr` (mandatory) — Name of the attribute to use to set the generated file path. Type: `java.lang.String`
- `prefix` — Prefix to be used for generated file. Default: temp Type: `java.lang.String`

### s:io-create-temp-file

- **Title**: io create temp file
- **Group**: Io
- **Description**: Creates temporary file with specified content.
- **Java type**: `com.yukthitech.autox.test.io.steps.CreateTempFileStep`

**Attributes:**

- `content` — Content (in string format) to be written to the file being created. If not specified empty file will be created. Type: `java.lang.Object`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `folder` — Folder under which file should be created. If not specified, file will be created in work folder. Type: `java.lang.String`
- `path-attr` (mandatory) — Name of the attribute to use to set the generated file path. Type: `java.lang.String`
- `prefix` — Prefix to be used for generated file. Default: temp Type: `java.lang.String`
- `suffix` — Suffix to be used for generated file. Default: .txt Type: `java.lang.String`

### s:io-delete-dir

- **Title**: io delete dir
- **Group**: Io
- **Description**: Deletes specified directory.
- **Java type**: `com.yukthitech.autox.test.io.steps.DeleteDirStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `path` (mandatory) — Path of directory to delete. Type: `java.lang.String`

### s:io-delete-file

- **Title**: io delete file
- **Group**: Io
- **Description**: Deletes specified file.
- **Java type**: `com.yukthitech.autox.test.io.steps.DeleteFileStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `path` (mandatory) — Path of file to delete. Type: `java.lang.String`

### s:io-list-files

- **Title**: io list files
- **Group**: Io
- **Description**: Fetches file list from specified directory.
- **Java type**: `com.yukthitech.autox.test.io.steps.ListFilesStep`

**Attributes:**

- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `files` — Flag indicating files has to be included. Default true Type: `boolean`
- `folder` (mandatory) — Folder under which files list to be fetched. Type: `java.lang.String`
- `folders` — Flag indicating folders has to be included. Default true Type: `boolean`
- `list-attr` — Name of attribute to which file list has to be set. Default: fileList Type: `java.lang.String`
- `pattern` — File regex pattern to be matche Type: `java.lang.String`

### s:io-mkdir

- **Title**: io mkdir
- **Group**: Io
- **Description**: Creates a directory.
- **Java type**: `com.yukthitech.autox.test.io.steps.MkDirStep`

**Attributes:**

- `absolute-path` — Flag indicating if the specified path is absolute or not. Not absolute folders will be created with-in work folder. Default: false Type: `boolean`
- `disable-logging` — Flag indicating if logging has to be disabled for current step. Default: false Type: `boolean`
- `name` (mandatory) — Context attribute to which result folder path will be set Type: `java.lang.String`
- `path` (mandatory) — Directory path to create. Type: `java.lang.String`

