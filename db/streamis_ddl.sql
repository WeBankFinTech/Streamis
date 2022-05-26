
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for linkis_stream_configuration_config_key
-- ----------------------------
DROP TABLE IF EXISTS `linkis_stream_configuration_config_key`;
CREATE TABLE `linkis_stream_configuration_config_key`  (
  `id` bigint(20) NOT NULL,
  `key` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
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
-- Table structure for linkis_stream_configuration_config_value
-- ----------------------------
DROP TABLE IF EXISTS `linkis_stream_configuration_config_value`;
CREATE TABLE `linkis_stream_configuration_config_value`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `configkey_id` bigint(20) NULL DEFAULT NULL,
  `config_value` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `type` int(10) NULL DEFAULT NULL,
  `job_id` bigint(20) NULL DEFAULT NULL,
  `job_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `config_key` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `key`(`config_key`) USING BTREE,
  INDEX `keyid`(`configkey_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '配置信息' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of linkis_stream_configuration_config_value
-- ----------------------------


-- ----------------------------
-- Table structure for linkis_stream_job_alarm_send_history
-- ----------------------------
DROP TABLE IF EXISTS `linkis_stream_job_alarm_send_history`;
CREATE TABLE `linkis_stream_job_alarm_send_history`  (
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
-- Records of linkis_stream_job_alarm_send_history
-- ----------------------------

-- ----------------------------
-- Table structure for linkis_stream_job_checkpoints
-- ----------------------------
DROP TABLE IF EXISTS `linkis_stream_job_checkpoints`;
CREATE TABLE `linkis_stream_job_checkpoints`  (
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
-- Records of linkis_stream_job_checkpoints
-- ----------------------------

-- ----------------------------
-- Table structure for linkis_stream_job_role
-- ----------------------------
DROP TABLE IF EXISTS `linkis_stream_job_role`;
CREATE TABLE `linkis_stream_job_role`  (
  `id` bigint(20) NOT NULL,
  `job_id` bigint(20) NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `front_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `description` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of linkis_stream_job_role
-- ----------------------------
INSERT INTO `linkis_stream_job_role` VALUES (1, -1, '管理员', '管理员', '2021-04-07 20:57:09', NULL);


-- ----------------------------
-- Table structure for linkis_stream_job_user_role
-- ----------------------------
DROP TABLE IF EXISTS `linkis_stream_job_user_role`;
CREATE TABLE `linkis_stream_job_user_role` (
   `id` bigint(20) NOT NULL,
   `job_id` bigint(20) DEFAULT NULL,
   `user_id` bigint(20) DEFAULT NULL,
   `role_id` bigint(20) DEFAULT NULL,
   `username` varchar(100) DEFAULT NULL,
   PRIMARY KEY (`id`) USING BTREE
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='作业角色关系';

-- ----------------------------
-- Records of linkis_stream_job_user_role
-- ----------------------------

/*Table structure for table `linkis_stream_job` */

DROP TABLE IF EXISTS `linkis_stream_job`;

CREATE TABLE `linkis_stream_job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_name` varchar(100) DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `status` tinyint(1) DEFAULT '0' COMMENT '1:已完成 ，2:等待重启 ，3:告警 ，4:慢任务 ，5:运行中 ，6:失败任务',
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `label` varchar(200) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `job_type` varchar(30) DEFAULT NULL COMMENT '目前只支持flink.sql、flink.jar',
  `submit_user` varchar(100) DEFAULT NULL,
  `workspace_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY(`project_name`, `name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='作业表';

/*Table structure for table `linkis_stream_job_version` */

DROP TABLE IF EXISTS `linkis_stream_job_version`;

CREATE TABLE `linkis_stream_job_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(50) DEFAULT NULL,
  `version` varchar(20) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL COMMENT '这个版本的来源，比如：用户上传，由某个历史版本回退回来的',
  `job_content` text COMMENT '内容为meta.json',
  `comment` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_by` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY(`job_id`, `version`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='作业表';

/*Table structure for table `linkis_stream_job_version_files` */

DROP TABLE IF EXISTS `linkis_stream_job_version_files`;

CREATE TABLE `linkis_stream_job_version_files` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(50) NOT NULL,
  `job_version_id` bigint(20) NOT NULL,
  `file_name` varchar(500) DEFAULT NULL,
  `version` varchar(30) DEFAULT NULL COMMENT '文件版本号，由用户上传时指定的',
  `store_path` varchar(100) DEFAULT NULL COMMENT '如：{"resource":"22edar22", "version": "v0001"}',
  `store_type` varchar(20) DEFAULT NULL COMMENT '存储类型，一般就是bml',
  `create_time` datetime DEFAULT NULL,
  `create_by` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `linkis_stream_project` */

DROP TABLE IF EXISTS `linkis_stream_project`;

CREATE TABLE `linkis_stream_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` bigint(20) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `create_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='项目表';

/*Table structure for table `linkis_stream_project_files` */

DROP TABLE IF EXISTS `linkis_stream_project_files`;

CREATE TABLE `linkis_stream_project_files` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(500) DEFAULT NULL,
  `version` varchar(30) DEFAULT NULL COMMENT '文件版本号，由用户上传时指定的',
  `store_path` varchar(100) DEFAULT NULL COMMENT '如：{"resource":"22edar22", "version": "v0001"}',
  `store_type` varchar(20) DEFAULT NULL COMMENT '存储类型，一般就是bml',
  `project_name` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` varchar(32) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL COMMENT '说明',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='项目表';

/*Table structure for table `linkis_stream_task` */

DROP TABLE IF EXISTS `linkis_stream_task`;

CREATE TABLE `linkis_stream_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_version_id` bigint(20) NOT NULL,
  `job_id` varchar(200) DEFAULT NULL,
  `version` varchar(50) DEFAULT NULL,
  `status` int(3) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `err_desc` varchar(10240) DEFAULT NULL,
  `submit_user` varchar(50) DEFAULT NULL,
  `linkis_job_id` varchar(200) DEFAULT NULL,
  `linkis_job_info` mediumtext,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='任务表';

DROP TABLE IF EXISTS `linkis_stream_alert_record`;

CREATE TABLE `linkis_stream_alert_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `alert_level` varchar(20) NOT NULL DEFAULT 'critical' COMMENT '告警级别',
  `alert_user` varchar(20) NOT NULL COMMENT '告警用户',
  `alert_msg` varchar(200) NOT NULL COMMENT '告警信息',
  `job_id` bigint(20) NOT NULL,
  `job_version_id` bigint(20) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `status` bigint(2) DEFAULT '1' COMMENT '''1为成功，0为失败''',
  `error_msg` varchar(200) DEFAULT NULL COMMENT '告警发送失败后的错误信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `linkis_stream_project_privilege`;

CREATE TABLE `linkis_stream_project_privilege` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `user_name` varchar(100) NOT NULL,
  `privilege` tinyint(1) DEFAULT '0' NOT NULL COMMENT '1:发布权限 ，2:编辑权限 ，3:查看权限 ，4:所有权限 ，5:发布编辑权限 ，6:发布查看权限 ，7:编辑查看权限 ',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='项目权限表';


SET FOREIGN_KEY_CHECKS = 1;
