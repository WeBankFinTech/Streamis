INSERT INTO linkis_ps_error_code (error_code, error_desc, error_regex, error_type)
VALUES('43042', '文件不存在: %s', 'Init executors error. Reason: FileNotFoundException: File (\\S+) does not exist', 0);
INSERT INTO linkis_ps_error_code
(error_code, error_desc, error_regex, error_type)
VALUES( '43043', 'Yarn应用被意外关闭', 'The YARN application unexpectedly switched to state KILLED during deployment', 0);
INSERT INTO linkis_ps_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43044', 'Hadoop认证失败，请联系管理员处理', 'Hadoop securith with Kerberos is enabled but the login user does not have Kerberos credentials or delegation tokens', 0);
INSERT INTO linkis_ps_error_code
(error_code, error_desc, error_regex, error_type)
VALUES( '43045', '缓存问题，请尝试重启', 'EngineConnServer Start Failed.java.lang.NoClassDefFoundError', 0);