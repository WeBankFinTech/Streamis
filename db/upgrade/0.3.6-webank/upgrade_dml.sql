
ALTER TABLE linkis_stream_task ADD server_instance varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT 'streamis server instance';

INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43051', 'streamis日志回调注册失败，请联系管理员处理', 'flink 应用请求注册streamis失败，即将退出', 0);


INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43052', 'streamis日志回调注册失败，请联系管理员处理', 'ExitCodeException exitCode=200', 0);