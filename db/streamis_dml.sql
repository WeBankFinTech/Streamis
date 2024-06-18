-- ----------------------------
-- Records of linkis_stream_job_config_def
-- ----------------------------

INSERT INTO `linkis_stream_job_config_def` VALUES (1,'wds.linkis.flink.resource','资源配置','NONE',0,'资源配置','None',NULL,'',1,0,NULL,NULL,'',NULL,0,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (2,'wds.linkis.flink.app.parallelism','Parallelism并行度','NUMBER',0,'Parallelism并行度','Regex','^([1-9]\\d{0,1}|100)$','',1,1,NULL,'4','',1,1,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (3,'wds.linkis.flink.jobmanager.memory','JobManager Memory (M)','NUMBER',0,'JobManager Memory (M)','Regex','^([1-9]\\d{0,4}|100000)$','',1,1,'M','1024','',1,1,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (4,'wds.linkis.flink.taskmanager.memory','TaskManager Memory (M)','NUMBER',0,'JobManager Memory (M)','Regex','^([1-9]\\d{0,4}|100000)$','',1,1,'M','4096','',1,1,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (5,'wds.linkis.flink.taskmanager.numberOfTaskSlots','TaskManager Slot数量','NUMBER',0,'TaskManager Slot数量','Regex','^([1-9]\\d{0,1}|100)$','',1,1,NULL,'2','',1,1,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (6,'wds.linkis.flink.taskmanager.cpus','TaskManager CPUs','NUMBER',0,'TaskManager CPUs','Regex','^([1-9]\\d{0,1}|100)$','',1,1,NULL,'2','',1,1,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (7,'wds.linkis.flink.custom','Flink参数','NONE',0,'Flink自定义参数','None',NULL,'',1,0,NULL,NULL,'',NULL,0,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (8,'wds.linkis.flink.produce','生产配置','NONE',0,'生产配置','None',NULL,'',1,0,NULL,NULL,'',NULL,0,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (9,'wds.linkis.flink.checkpoint.switch','Checkpoint开关','SELECT',0,'Checkpoint开关',NULL,NULL,'',1,1,'','OFF','ON,OFF',8,0,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (10,'wds.linkis.flink.savepoint.path','快照(Savepoint)文件位置【仅需恢复任务时指定】','INPUT',4,'快照(Savepoint)文件位置','None',NULL,'',1,1,NULL,NULL,'',8,0,1);
INSERT INTO `linkis_stream_job_config_def` VALUES (11,'wds.linkis.flink.alert','告警设置','NONE',0,'告警设置','None',NULL,'',1,1,NULL,NULL,'',NULL,0,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (12,'wds.linkis.flink.alert.rule','告警规则','NONE',0,'告警规则','None',NULL,'',1,1,NULL,NULL,'',NULL,0,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (13,'wds.linkis.flink.alert.user','告警用户','NONE',0,'告警用户',NULL,NULL,'',1,1,NULL,NULL,'',NULL,0,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (14,'wds.linkis.flink.alert.level','告警级别','NONE',0,'告警级别','None',NULL,'',1,1,NULL,NULL,'',NULL,0,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (15,'wds.linkis.flink.alert.failure.level','失败时告警级别','NONE',0,'失败时告警级别','None',NULL,'',1,1,NULL,NULL,'',NULL,0,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (16, 'wds.linkis.flink.alert.failure.user', '失败时告警用户', 'INPUT', 0, '失败时告警用户', 'None', NULL, '', 1, 1, NULL, '', '', 8, 0, 0);
INSERT INTO `linkis_stream_job_config_def` VALUES (32,'wds.linkis.flink.authority','权限设置','NONE',0,'权限设置','None',NULL,'',1,0,NULL,NULL,'',NULL,0,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (33,'wds.linkis.flink.authority.visible','可见人员','INPUT',0,'可见人员','None',NULL,'',1,1,NULL,NULL,'',32,0,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (34,'wds.linkis.rm.yarnqueue','使用Yarn队列','INPUT',0,'使用Yarn队列','None',NULL,'',1,1,NULL,NULL,'',1,0,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (35,'wds.linkis.flink.app.fail-restart.switch','作业失败自动拉起开关','SELECT',1,'作业失败自动拉起开关','None',NULL,'',1,1,NULL,'OFF','ON,OFF',8,0,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (36,'wds.linkis.flink.app.start-auto-restore.switch','作业启动状态自恢复','SELECT',2,'作业启动状态自恢复','None',NULL,'',1,1,NULL,'ON','ON,OFF',8,0,0);
INSERT INTO `linkis_stream_job_config_def` VALUES (38, 'linkis.ec.app.manage.mode', '管理模式', 'SELECT', 3, 'EngineConn管理模式', 'None', NULL, '', 1, 1, NULL, 'detach', 'detach,attach', 8, 0, 0);

INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('01001', '您的任务没有路由到后台ECM，请联系管理员', 'The em of labels', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('01002', 'Linkis服务负载过高，请联系管理员扩容', 'Unexpected end of file from server', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('01003', 'Linkis服务负载过高，请联系管理员扩容', 'failed to ask linkis Manager Can be retried SocketTimeoutException', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('01004', '引擎在启动时被Kill，请联系管理员', ' [0-9]+ Killed', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('01005', '请求Yarn获取队列信息重试2次仍失败，请联系管理员', 'Failed to request external resourceClassCastException', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('01101', 'ECM资源不足，请联系管理员扩容', 'ECM resources are insufficient', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('01102', 'ECM 内存资源不足，请联系管理员扩容', 'ECM memory resources are insufficient', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('01103', 'ECM CPU资源不足，请联系管理员扩容', 'ECM CPU resources are insufficient', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('01004', 'ECM 实例资源不足，请联系管理员扩容', 'ECM Insufficient number of instances', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('01005', '机器内存不足，请联系管理员扩容', 'Cannot allocate memory', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('12001', '队列CPU资源不足，可以调整Spark执行器个数', 'Queue CPU resources are insufficient', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('12002', '队列内存资源不足，可以调整Spark执行器个数', 'Insufficient queue memory', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('12003', '队列实例数超过限制', 'Insufficient number of queue instances', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('12004', '全局驱动器内存使用上限，可以设置更低的驱动内存', 'Drive memory resources are insufficient', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('12005', '超出全局驱动器CPU个数上限，可以清理空闲引擎', 'Drive core resources are insufficient', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('12006', '超出引擎最大并发数上限，可以清理空闲引擎', 'Insufficient number of instances', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('12008', '获取Yarn队列信息异常,可能是您设置的yarn队列不存在', '获取Yarn队列信息异常', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('12009', '会话创建失败，%s队列不存在，请检查队列设置是否正确', 'queue (\\S+) is not exists in YARN', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('12010', '集群队列内存资源不足，可以联系组内人员释放资源', 'Insufficient cluster queue memory', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('12011', '集群队列CPU资源不足，可以联系组内人员释放资源', 'Insufficient cluster queue cpu', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('12012', '集群队列实例数超过限制', 'Insufficient cluster queue instance', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('12013', '资源不足导致启动引擎超时，您可以进行任务重试', 'wait for DefaultEngineConn', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('12014', '请求引擎超时，可能是因为队列资源不足导致，请重试', 'wait for engineConn initial timeout', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('13001', 'Java进程内存溢出，建议优化脚本内容', 'OutOfMemoryError', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('13002', '使用资源过大，请调优sql或者加大资源', 'Container killed by YARN for exceeding memory limits', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('13003', '使用资源过大，请调优sql或者加大资源', 'read record exception', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('13004', '引擎意外退出，可能是使用资源过大导致', 'failed because the engine quitted unexpectedly', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('13005', 'Spark app应用退出，可能是复杂任务导致', 'Spark application has already stopped', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('13006', 'Spark context退出，可能是复杂任务导致', 'Spark application sc has already stopped', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('13007', 'Pyspark子进程意外退出，可能是复杂任务导致', 'Pyspark process  has stopped', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('21001', '会话创建失败，用户%s不能提交应用到队列：%s，请联系提供队列给您的人员', 'User (\\S+) cannot submit applications to queue (\\S+)', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('21002', '创建Python解释器失败，请联系管理员', 'initialize python executor failed', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('21003', '创建单机Python解释器失败，请联系管理员', 'PythonSession process cannot be initialized', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('22001', '%s无权限访问，请申请开通数据表权限，请联系您的数据管理人员', 'Permission denied:\\s*user=[a-zA-Z0-9_]+,\\s*access=[A-Z]+\\s*,\\s*inode="([a-zA-Z0-9/_\\.]+)"', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('22003', '所查库表无权限', 'Authorization failed:No privilege', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('22004', '用户%s在机器不存在，请确认是否申请了相关权限', 'user (\\S+) does not exist', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('22005', '用户在机器不存在，请确认是否申请了相关权限', 'engineConnExec.sh: Permission denied', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('22006', '用户在机器不存在，请确认是否申请了相关权限', 'at com.sun.security.auth.UnixPrincipal', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('22007', '用户在机器不存在，请确认是否申请了相关权限', 'LoginException: java.lang.NullPointerException: invalid null input: name', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('22008', '用户在机器不存在，请确认是否申请了相关权限', 'User not known to the underlying authentication module', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('22009', '用户组不存在', 'FileNotFoundException: /tmp/?', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('22010', '用户组不存在', 'error looking up the name of group', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('30001', '库超过限制', 'is exceeded', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('31001', '用户主动kill任务', 'is killed by user', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('31002', '您提交的EngineTypeLabel没有对应的引擎版本', 'EngineConnPluginNotFoundException', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('41001', '数据库%s不存在，请检查引用的数据库是否有误', 'Database ''([a-zA-Z_0-9]+)'' not found', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('41001', '数据库%s不存在，请检查引用的数据库是否有误', 'Database does not exist: ([a-zA-Z_0-9]+)', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('41002', '表%s不存在，请检查引用的表是否有误', 'Table or view not found: ([`\\.a-zA-Z_0-9]+)', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('41002', '表%s不存在，请检查引用的表是否有误', 'Table not found ''([a-zA-Z_0-9]+)''', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('41002', '表%s不存在，请检查引用的表是否有误', 'Table ([a-zA-Z_0-9]+) not found', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('41003', '字段%s不存在，请检查引用的字段是否有误', 'cannot resolve ''`(.+)`'' given input columns', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('41003', '字段%s不存在，请检查引用的字段是否有误', ' Invalid table alias or column reference ''(.+)'':', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('41003', '字段%s不存在，请检查引用的字段是否有误', 'Column ''(.+)'' cannot be resolved', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('41004', '分区字段%s不存在，请检查引用的表%s是否为分区表或分区字段有误', '([a-zA-Z_0-9]+) is not a valid partition column in table ([`\\.a-zA-Z_0-9]+)', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('41004', '分区字段%s不存在，请检查引用的表是否为分区表或分区字段有误', 'Partition spec \\{(\\S+)\\} contains non-partition columns', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('41004', '分区字段%s不存在，请检查引用的表是否为分区表或分区字段有误', 'table is not partitioned but partition spec exists:\\{(.+)\\}', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('41004', '表对应的路径不存在，请联系您的数据管理人员', 'Path does not exist: viewfs', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('41005', '文件%s不存在', 'Caused by:\\s*java.io.FileNotFoundException', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42001', '括号不匹配，请检查代码中括号是否前后匹配', 'extraneous input ''\\)''', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42002', '非聚合函数%s必须写在group by中，请检查代码的group by语法', 'expression ''(\\S+)'' is neither present in the group by', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42002', '非聚合函数%s必须写在group by中，请检查代码的group by语法', 'grouping expressions sequence is empty,\\s?and ''(\\S+)'' is not an aggregate function', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42002', '非聚合函数%s必须写在group by中，请检查代码的group by语法', 'Expression not in GROUP BY key ''(\\S+)''', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42003', '未知函数%s，请检查代码中引用的函数是否有误', 'Undefined function: ''(\\S+)''', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42003', '未知函数%s，请检查代码中引用的函数是否有误', 'Invalid function ''(\\S+)''', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42004', '字段%s存在名字冲突，请检查子查询内是否有同名字段', 'Reference ''(\\S+)'' is ambiguous', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42004', '字段%s存在名字冲突，请检查子查询内是否有同名字段', 'Ambiguous column Reference ''(\\S+)'' in subquery', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42005', '字段%s必须指定表或者子查询别名，请检查该字段来源', 'Column ''(\\S+)'' Found in more than One Tables/Subqueries', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42006', '表%s在数据库%s中已经存在，请删除相应表后重试', 'Table or view ''(\\S+)'' already exists in database ''(\\S+)''', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42006', '表%s在数据库中已经存在，请删除相应表后重试', 'Table (\\S+) already exists', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42006', '表%s在数据库中已经存在，请删除相应表后重试', 'Table already exists', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42006', '表%s在数据库中已经存在，请删除相应表后重试', 'AnalysisException: (S+) already exists', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42007', '插入目标表字段数量不匹配,请检查代码！', 'requires that the data to be inserted have the same number of columns as the target table', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42008', '数据类型不匹配，请检查代码！', 'due to data type mismatch: differing types in', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42009', '字段%s引用有误，请检查字段是否存在！', 'Invalid column reference (S+)', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42010', '字段%s提取数据失败', 'Can''t extract value from (S+): need', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42011', '括号或者关键字不匹配，请检查代码！', 'mismatched input ''(\\S+)'' expecting', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42012', 'group by 位置2不在select列表中，请检查代码！', 'GROUP BY position (S+) is not in select list', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42013', '字段提取数据失败请检查字段类型', 'Can''t extract value from (S+): need struct type but got string', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42014', '插入数据未指定目标表字段%s，请检查代码！', 'Cannot insert into target table because column number/types are different ''(S+)''', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42015', '表别名%s错误，请检查代码！', 'Invalid table alias ''(\\S+)''', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42016', 'UDF函数未指定参数，请检查代码！', 'UDFArgumentException Argument expected', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42017', '聚合函数%s不能写在group by 中，请检查代码！', 'aggregate functions are not allowed in GROUP BY', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42018', '您的代码有语法错误，请您修改代码之后执行', 'SemanticException Error in parsing', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42019', '表不存在，请检查引用的表是否有误', 'table not found', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42020', '函数使用错误，请检查您使用的函数方式', 'No matching method', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42021', '您的sql代码可能有语法错误，请检查sql代码', 'FAILED: ParseException', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42022', '您的sql代码可能有语法错误，请检查sql代码', 'org.apache.spark.sql.catalyst.parser.ParseException', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42022', '您的sql代码可能有语法错误，请检查sql代码', 'org.apache.hadoop.hive.ql.parse.ParseException', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42023', '聚合函数不能嵌套', 'aggregate function in the argument of another aggregate function', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42024', '聚合函数不能嵌套', 'aggregate function parameters overlap with the aggregation', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42025', 'union 的左右查询字段不一致', 'Union can only be performed on tables', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42025', 'hql报错，union 的左右查询字段不一致', 'both sides of union should match', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42025', 'union左表和右表类型不一致', 'on first table and type', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42026', '您的建表sql不能推断出列信息', 'Unable to infer the schema', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42027', '动态分区的严格模式需要指定列，您可用通过设置set hive.exec.dynamic.partition.mode=nostrict', 'requires at least one static partition', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42028', '函数输入参数有误', 'Invalid number of arguments for function', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42029', 'sql语法报错，select * 与group by无法一起使用', 'not allowed in select list when GROUP BY  ordinal', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42030', 'where/having子句之外不支持引用外部查询的表达式', 'the outer query are not supported outside of WHERE', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42031', 'sql语法报错，group by 后面不能跟一个表', 'show up in the GROUP BY list', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42032', 'hql报错，窗口函数中的字段重复', 'check for circular dependencies', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42033', 'sql中出现了相同的字段', 'Found duplicate column', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42034', 'sql语法不支持', 'not supported in current context', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42035', 'hql语法报错，嵌套子查询语法问题', 'Unsupported SubQuery Expression', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('42036', 'hql报错，子查询中in 用法有误', 'in definition of SubQuery', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43037', '表字段类型修改导致的转型失败，请联系修改人员', 'cannot be cast to', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43038', 'select 的表可能有误', 'Invalid call to toAttribute on unresolved object', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43039', '语法问题，请检查脚本', 'Distinct window functions are not supported', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43040', 'Presto查询一定要指定数据源和库信息', 'Schema must be specified when session schema is not set', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43001', '代码中存在NoneType空类型变量，请检查代码', '''NoneType'' object', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43002', '数组越界', 'IndexError:List index out of range', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43003', '您的代码有语法错误，请您修改代码之后执行', 'SyntaxError', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43004', 'python代码变量%s未定义', 'name ''(S+)'' is not defined', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43005', 'python udf %s 未定义', 'Undefined function:s+''(S+)''', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43006', 'python执行不能将%s和%s两种类型进行连接', 'cannot concatenate ''(S+)'' and ''(S+)''', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43007', 'pyspark执行失败，可能是语法错误或stage失败', 'Py4JJavaError: An error occurred', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43008', 'python代码缩进对齐有误', 'unexpected indent', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43009', 'python代码缩进有误', 'unexpected indent', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43010', 'python代码反斜杠后面必须换行', 'unexpected character after line', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43011', '导出Excel表超过最大限制1048575', 'Invalid row number', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43012', 'python save as table未指定格式，默认用parquet保存，hive查询报错', 'parquet.io.ParquetDecodingException', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43013', '索引使用错误', 'IndexError', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43014', 'sql语法有问题', 'raise ParseException', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('46001', '找不到导入文件地址：%s', 'java.io.FileNotFoundException: (\\S+) \\(No such file or directory\\)', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('46002', '导出为excel时临时文件目录权限异常', 'java.io.IOException: Permission denied(.+)at org.apache.poi.xssf.streaming.SXSSFWorkbook.createAndRegisterSXSSFSheet', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('46003', '导出文件时无法创建目录：%s', 'java.io.IOException: Mkdirs failed to create (\\S+) (.+)', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('46004', '导入模块错误，系统没有%s模块，请联系运维人员安装', 'ImportError: No module named (S+)', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('91001', '找不到变量值，请确认您是否设置相关变量', 'not find variable substitution for', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('91002', '不存在的代理用户，请检查你是否申请过平台层（bdp或者bdap）用户', 'failed to change current working directory ownership', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('91003', '请检查提交用户在WTSS内是否有该代理用户的权限，代理用户中是否存在特殊字符，是否用错了代理用户，OS层面是否有该用户，系统设置里面是否设置了该用户为代理用户', '没有权限执行当前任务', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('91004', '平台层不存在您的执行用户，请在ITSM申请平台层（bdp或者bdap）用户', '使用chown命令修改', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('91005', '未配置代理用户，请在ITSM走WTSS用户变更单，为你的用户授权改代理用户', '请联系系统管理员为您的用户添加该代理用户', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('91006', '您的用户初始化有问题，请联系管理员', 'java: No such file or directory', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('91007', 'JobServer中不存在您的脚本文件，请将你的脚本文件放入对应的JobServer路径中', 'Could not open input file for reading%does not exist', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43042', '文件不存在: %s', 'Init executors error. Reason: FileNotFoundException: File (\\S+) does not exist', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43043', 'Yarn应用被意外关闭', 'The YARN application unexpectedly switched to state KILLED during deployment', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43044', 'Hadoop认证失败，请联系管理员处理', 'RuntimeException: Hadoop security with Kerberos', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43045', '缓存问题，请尝试重启', 'EngineConnServer Start Failed.java.lang.NoClassDefFoundError', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43046', 'hdfs资源文件下载失败，请尝试重启任务或者重新上传资源文件', 'Could not obtain block', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43047', 'ims 连接失败', 'Connection timed out (\\S+) at com.webank.ims.common', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43048', '缺少字段: %s', 'Object (\\S+) not found', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43049', 'topic没有权限', 'Not authorized to access topics', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43050', '请尝试同步一下物料', '应用不存在，请检查输入的appId', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43051', '文件没有权限: %s', 'FileNotFoundException: (\\S+) \\(Permission denied\\)', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43052', '队列 %s 不存在', 'Queue:(\\S+) does not exist in YARN', 0);
INSERT INTO linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43053', '用户jar包没有排除flink runtime 等包', 'NoSuchFieldError: AUTH_USER', 0);
INSERT INTO  linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43054', '用户程序代码错误，原因：用户Jar包启动异常，联系Jar包开发排查', '创建用户自定义方法失败', 0);
INSERT INTO  linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43055', ' RCS环境应用不存在', '应用不存在，请检查输入的appId', 0);
INSERT INTO  linkis_stream_error_code
(error_code, error_desc, error_regex, error_type)
VALUES('43056', '数据源RMB topic %S 不存在', 'RMBIllegalAccessException: Topic (\\S+) not exist  ', 0);