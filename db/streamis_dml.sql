-- ----------------------------
-- Records of streamis_stream_configuration_config_key
-- ----------------------------
INSERT INTO `streamis_stream_configuration_config_key` VALUES (1, 'wds.linkis.flink.resource', '资源配置', '资源配置', NULL, 'None', NULL, 0, 0, 1, '资源配置', 1, 0, 1);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (2, 'wds.linkis.flink.taskmanager.num', 'Task Managers数量', 'Task Managers数量', '4', 'Regex', '^(?:[1-9]\\d?|[1234]\\d{2}|128)$', 0, 0, 2, '资源配置', 1, 1, 1);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (3, 'wds.linkis.flink.jobmanager.memory', 'JobManager Memory', 'JobManager Memory', '1.5', 'Regex', '^([1-9]\\d{0,2}|1000)(G|g)$', 0, 0, 2, '资源配置', 1, 2, 1);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (4, 'wds.linkis.flink.taskmanager.memory', 'TaskManager Memory', 'TaskManager Memory', '1.5', 'Regex', '^([1-9]\\d{0,2}|1000)(G|g)$', 0, 0, 2, '资源配置', 1, 3, 1);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (5, 'wds.linkis.flink.jobmanager.cpus', 'JobManager CPUs', 'JobManager CPUs', '1', 'Regex', '^(?:[1-9]\\d?|[1234]\\d{2}|128)$', 0, 0, 2, '资源配置', 1, 4, 1);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (6, 'wds.linkis.flink.taskManager.cpus', 'TaskManager CPUs', 'TaskManager CPUs', '1', 'Regex', '^(?:[1-9]\\d?|[1234]\\d{2}|128)$', 0, 0, 2, '资源配置', 1, 5, 1);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (7, 'wds.linkis.flink.custom', '自定义参数', '自定义参数', NULL, 'None', NULL, 0, 0, 1, '自定义参数', 2, 0, 1);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (8, 'wds.linkis.flink.produce', '生产配置', '生产配置', NULL, 'None', NULL, 0, 0, 1, '生产配置', 3, 0, 1);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (9, 'wds.linkis.flink.checkpoint.interval', 'Checkpoint间隔', 'Checkpoint间隔', NULL, NULL, NULL, 0, 0, 2, '生产配置', 3, 1, 1);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (10, 'wds.linkis.flink.reboot.strategy', '重启策略', '重启策略', '不重启,基于Checkpoint自动重启,无Checkpoint不重启', 'None', NULL, 0, 0, 2, '重启策略', 3, 2, 2);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (11, 'wds.linkis.flink.alert', '告警设置', '告警设置', NULL, 'None', NULL, 0, 0, 1, '告警设置', 4, 0, 1);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (12, 'wds.linkis.flink.alert.rule', '告警规则', '告警规则', '任务日志中出现ERROR/EXCEPTION,任务核心指标出现异常', 'None', NULL, 0, 0, 2, '告警规则', 4, 1, 2);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (13, 'wds.linkis.flink.alert.user', '告警用户', '告警用户', NULL, NULL, NULL, 0, 0, 2, '告警用户', 4, 3, 1);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (14, 'wds.linkis.flink.alert.leve', '告警级别', '告警级别', 'CLEARED,INDETERMINATE,WARNING,MINOR,MAJOR,CRITICAL', 'None', NULL, 0, 0, 2, '告警级别', 4, 2, 2);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (15, 'wds.linkis.flink.alert.failure.level', '失败时告警级别', '失败时告警级别', 'CLEARED,INDETERMINATE,WARNING,MINOR,MAJOR,CRITICAL', 'None', NULL, 0, 0, 2, '失败时告警级别', 4, 4, 2);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (16, 'wds.linkis.flink.alert.failure.user', '失败时告警用户', '失败时告警用户', NULL, 'None', NULL, 0, 0, 2, '失败时告警用户', 4, 5, 1);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (17, 'wds.linkis.flink.authority', '权限设置', '权限设置', NULL, 'None', NULL, 0, 0, 1, '权限设置', 5, 0, 1);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (18, 'wds.linkis.flink.authority.author', '授权模式', '授权模式', '私密,指定全员可见,指定人员可见', 'None', NULL, 0, 0, 2, '授权模式', 5, 1, 2);
INSERT INTO `streamis_stream_configuration_config_key` VALUES (19, 'wds.linkis.flink.authority.visible', '可见人员', '可见人员', NULL, 'None', NULL, 0, 0, 2, '可见人员', 5, 2, 1);
