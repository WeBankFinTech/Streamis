
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for streamis_stream_bml
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_bml`;
CREATE TABLE `streamis_stream_bml`  (
  `id` bigint(20) NOT NULL,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `bml_type` tinyint(1) NULL DEFAULT NULL,
  `org_identification` bigint(20) NULL DEFAULT NULL,
  ` latest_version` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of streamis_stream_bml
-- ----------------------------

-- ----------------------------
-- Table structure for streamis_stream_bml_version
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_bml_version`;
CREATE TABLE `streamis_stream_bml_version`(
  `id` bigint(20) NOT NULL,
  `bml_id` bigint(20) NULL DEFAULT NULL,
  `version` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `storage_path` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attribute` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '物料版本' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of streamis_stream_bml_version
-- ----------------------------

-- ----------------------------
-- Table structure for streamis_stream_cluster
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_cluster`;
CREATE TABLE `streamis_stream_cluster`  (
  `id` int(11) NOT NULL,
  `yarn_conf_dir` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `hdfs_conf_dir` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `resource_manager_url` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `savepoint_dir` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'flink 集群信息' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of streamis_stream_cluster
-- ----------------------------

-- ----------------------------
-- Table structure for streamis_stream_configuration_config_key
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_configuration_config_key`;
CREATE TABLE `streamis_stream_configuration_config_key`  (
  `id` bigint(20) NOT NULL,
  `key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `description` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `default_value` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `validate_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `validate_range` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `is_hidden` tinyint(1) NULL DEFAULT NULL,
  `is_advanced` tinyint(1) NULL DEFAULT NULL,
  `level` tinyint(1) NULL DEFAULT NULL,
  `treename` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `type` int(10) NULL DEFAULT NULL,
  `sort` int(10) NULL DEFAULT NULL,
  `status` tinyint(10) NULL DEFAULT NULL COMMENT '1 custom , 2 selected ',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `key_index`(`key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '配置信息' ROW_FORMAT = Compact;


-- ----------------------------
-- Table structure for streamis_stream_configuration_config_value
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_configuration_config_value`;
CREATE TABLE `streamis_stream_configuration_config_value`  (
  `id` bigint(20) NOT NULL,
  `configkey_id` bigint(20) NULL DEFAULT NULL,
  `config_value` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `type` int(10) NULL DEFAULT NULL,
  `job_id` bigint(20) NULL DEFAULT NULL,
  `job_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `config_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `key`(`config_key`) USING BTREE,
  INDEX `keyid`(`configkey_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '配置信息' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of streamis_stream_configuration_config_value
-- ----------------------------

-- ----------------------------
-- Table structure for streamis_stream_frame_version
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_frame_version`;
CREATE TABLE `streamis_stream_frame_version`  (
  `id` bigint(20) NOT NULL,
  `frame` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `version` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `java_version` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '框架信息' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of streamis_stream_frame_version
-- ----------------------------

-- ----------------------------
-- Table structure for streamis_stream_job
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_job`;
CREATE TABLE `streamis_stream_job`  (
  `id` bigint(20) NOT NULL,
  `project_id` bigint(20) NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `type` tinyint(1) NULL DEFAULT NULL,
  `current_task_id` bigint(20) NULL DEFAULT NULL,
  `current_version` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `current_release_time` datetime NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '1:已完成 ，2:等待重启 ，3:告警 ，4:慢任务 ，5:运行中 ，6:失败任务',
  `org_identification` bigint(20) NULL DEFAULT NULL,
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `label` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `current_released` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `description` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '作业表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of streamis_stream_job
-- ----------------------------

-- ----------------------------
-- Table structure for streamis_stream_job_alarm_send_history
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_job_alarm_send_history`;
CREATE TABLE `streamis_stream_job_alarm_send_history`  (
  `id` bigint(20) NOT NULL,
  `job_id` bigint(20) NULL DEFAULT NULL,
  `task_id` bigint(20) NULL DEFAULT NULL,
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `type` tinyint(1) NULL DEFAULT NULL,
  `rule_type` tinyint(1) NULL DEFAULT NULL,
  `content` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '报警历史信息' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of streamis_stream_job_alarm_send_history
-- ----------------------------

-- ----------------------------
-- Table structure for streamis_stream_job_checkpoints
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_job_checkpoints`;
CREATE TABLE `streamis_stream_job_checkpoints`  (
  `id` bigint(20) NOT NULL,
  `config_value_id` bigint(20) NULL DEFAULT NULL,
  `path` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `size` int(20) NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT NULL,
  `trigger_timestamp` datetime NULL DEFAULT NULL,
  `latest_ack_timestamp` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of streamis_stream_job_checkpoints
-- ----------------------------

-- ----------------------------
-- Table structure for streamis_stream_job_code_resource
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_job_code_resource`;
CREATE TABLE `streamis_stream_job_code_resource`  (
  `id` bigint(20) NOT NULL,
  `job_version_id` bigint(20) NULL DEFAULT NULL,
  `bml_version_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '其他代码' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of streamis_stream_job_code_resource
-- ----------------------------

-- ----------------------------
-- Table structure for streamis_stream_job_role
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_job_role`;
CREATE TABLE `streamis_stream_job_role`  (
  `id` bigint(20) NOT NULL,
  `job_id` bigint(20) NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `front_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `description` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of streamis_stream_job_role
-- ----------------------------
INSERT INTO `streamis_stream_job_role` VALUES (1, -1, '管理员', '管理员', '2021-04-07 20:57:09', NULL);

-- ----------------------------
-- Table structure for streamis_stream_job_sql_resource
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_job_sql_resource`;
CREATE TABLE `streamis_stream_job_sql_resource`  (
  `id` bigint(20) NOT NULL,
  `job_version_id` bigint(20) NULL DEFAULT NULL,
  `execute_sql` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of streamis_stream_job_sql_resource
-- ----------------------------

-- ----------------------------
-- Table structure for streamis_stream_job_user_role
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_job_user_role`;
CREATE TABLE `streamis_stream_job_user_role`  (
  `id` bigint(20) NOT NULL,
  `job_id` bigint(20) NULL DEFAULT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  `role_id` bigint(20) NULL DEFAULT NULL,
  `type` tinyint(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '作业角色关系' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of streamis_stream_job_user_role
-- ----------------------------

-- ----------------------------
-- Table structure for streamis_stream_job_version
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_job_version`;
CREATE TABLE `streamis_stream_job_version`  (
  `id` bigint(20) NOT NULL,
  ` job_id` bigint(20) NULL DEFAULT NULL,
  `version` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `program_arguments` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `bml_version` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `resource_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of streamis_stream_job_version
-- ----------------------------

-- ----------------------------
-- Table structure for streamis_stream_project
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_project`;
CREATE TABLE `streamis_stream_project`  (
  `id` bigint(20) NOT NULL,
  `workspace_id` bigint(20) NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '项目表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of streamis_stream_project
-- ----------------------------

-- ----------------------------
-- Table structure for streamis_stream_task
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_task`;
CREATE TABLE `streamis_stream_task`  (
  `id` bigint(20) NOT NULL,
  `job_version_id` bigint(20) NULL DEFAULT NULL,
  `version` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `stream_task_identification` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `status` tinyint(10) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of streamis_stream_task
-- ----------------------------

-- ----------------------------
-- Table structure for streamis_stream_user
-- ----------------------------
DROP TABLE IF EXISTS `streamis_stream_user`;
CREATE TABLE `streamis_stream_user`  (
  `id` bigint(20) NOT NULL,
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of streamis_stream_user
-- ----------------------------
INSERT INTO `streamis_stream_user` VALUES (1, 'hdfs', 'hdfs');

SET FOREIGN_KEY_CHECKS = 1;
