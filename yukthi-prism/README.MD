## AutoX

**AutoX** is a test automation framework in which all test cases and test suites are written in simple XML format (with different templating support). 

As part of test suites executions:

1. An automated detailed log report is generated with formatted queries. And these log reports are searchable.
2. Other than framework generated logs, the actual application log(s) can be tied to test cases. During each test case, the application log(s) during its execution can be viewed separately. Logs from remote machine can also be linked (based on sftp).   
3. Free marker templates can be used at different places making the test cases more dynamic.
4. Custom plugins or steps can be added very easily.
5. Web UI automation is also supported (backed by selenium). Support for taking screen shots, these screen shots will be part of log files.
6. In built support for REST APIs.


Detailed documentation can be found at [this autoX doc site!](http://autox.yukthitech.com).

**Future Plans**

1. Summary report generation, that can be used for mailing in CI/CD flows.
2. Eclipse plugin for editor and for executing highlighted test suites or test cases.
3. Auto screen shot for ui based test cases.
4. Some more steps related to collections.
