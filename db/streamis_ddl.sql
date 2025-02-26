
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

--
-- Table structure for table `linkis_stream_job_config_def`
--

DROP TABLE IF EXISTS `linkis_stream_job_config_def`;
CREATE TABLE `linkis_stream_job_config_def` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key` varchar(100) COLLATE utf8_bin NOT NULL,
  `name` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT 'Equals option',
  `type` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT 'NONE' COMMENT 'def type, NONE: 0, INPUT: 1, SELECT: 2',
  `sort` int(10) DEFAULT '0' COMMENT 'In order to sort the configurations that have the same level',
  `description` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT 'Description of configuration',
  `validate_type` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'Method the validate the configuration',
  `validate_rule` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT 'Value of validation rule',
  `style` varchar(200) COLLATE utf8_bin DEFAULT '' COMMENT 'Display style',
  `visiable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '0: hidden, 1: display',
  `level` tinyint(1) NOT NULL DEFAULT '1' COMMENT '0: root, 1: leaf',
  `unit` varchar(25) COLLATE utf8_bin DEFAULT NULL COMMENT 'Unit symbol',
  `default_value` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT 'Default value',
  `ref_values` varchar(200) COLLATE utf8_bin DEFAULT '',
  `parent_ref` bigint(20) DEFAULT NULL COMMENT 'Parent key of configuration def',
  `required` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'If the value of configuration is necessary',
  `is_temp` tinyint(1) DEFAULT '0' COMMENT 'Temp configuration',
  PRIMARY KEY (`id`),
  UNIQUE KEY `config_def_key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Table structure for table `linkis_stream_job_config`
--

DROP TABLE IF EXISTS `linkis_stream_job_config`;
CREATE TABLE `linkis_stream_job_config` (
  `job_id` bigint(20) NOT NULL,
  `job_name` varchar(200) COLLATE utf8_bin NOT NULL COMMENT 'Just store the job name',
  `key` varchar(100) COLLATE utf8_bin NOT NULL,
  `value` varchar(500) COLLATE utf8_bin NOT NULL,
  `ref_def_id` bigint(20) DEFAULT NULL COMMENT 'Refer to id in config_def table',
  PRIMARY KEY (`job_id`,`key`),
  KEY `config_def_id` (`ref_def_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

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
  `current_version` varchar(50) DEFAULT NULL,
  `enable` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY(`project_name`, `name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='作业表';

/*Table structure for table `linkis_stream_job_version` */

DROP TABLE IF EXISTS `linkis_stream_job_version`;

CREATE TABLE `linkis_stream_job_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(50) DEFAULT NULL,
  `version` varchar(20) DEFAULT NULL,
  `source` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '这个版本的来源，比如：用户上传，由某个历史版本回退回来的',
  `job_content` text COMMENT '内容为meta.json',
  `manage_mode` varchar(30) DEFAULT 'EngineConn' COMMENT 'Manage mode',
  `comment` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_by` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY  `job_id`(`job_id`, `version`)
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
  `create_time` datetime DEFAULT NULL,
  `last_update_by` varchar(50) DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `is_deleted` tinyint(3) unsigned DEFAULT 0,
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
  `update_time` datetime DEFAULT NULL,
  `md5` varchar(100) DEFAULT NULL COMMENT '文件md5',
  `source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='项目表';


/*Table structure for table `linkis_stream_project_job_template` */

DROP TABLE IF EXISTS `linkis_stream_project_job_template`;

CREATE TABLE `linkis_stream_project_job_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `store_path` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `meta_json` text COLLATE utf8_bin,
  `version` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `project_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `enable` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


/*Table structure for table `linkis_stream_register_info` */


DROP TABLE IF EXISTS `linkis_stream_register_info`;

CREATE TABLE `linkis_stream_register_info` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `job_id` bigint(20) DEFAULT NULL,
   `application_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
   `password` varchar(255) COLLATE utf8_bin DEFAULT NULL,
   `register_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `heartbeat_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=171425 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


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
  `server_instance` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'streamis server instance',
  `job_start_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'streamis job startup config',
  `solution` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'error code solution',
  `template_id` bigint(20) DEFAULT NULL,
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
  `privilege` tinyint(1) DEFAULT '0' NOT NULL COMMENT '1:发布权限 ，2:编辑权限 ，3:查看权限',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='项目权限表';


DROP TABLE IF EXISTS `linkis_stream_error_code`;

CREATE TABLE `linkis_stream_error_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `error_code` varchar(50) COLLATE utf8_bin NOT NULL,
  `error_desc` varchar(1024) COLLATE utf8_bin NOT NULL,
  `error_regex` varchar(1024) COLLATE utf8_bin DEFAULT NULL,
  `error_type` int(3) DEFAULT '0',
  `solution` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'error code solution',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS `linkis_stream_audit_log`;

CREATE TABLE `linkis_stream_audit_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `proxy_user` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `api_name` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `api_desc` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `api_type` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `operate_time` datetime DEFAULT NULL,
  `input_parameters` text COLLATE utf8_bin,
  `output_parameters` text COLLATE utf8_bin,
  `project_name` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `client_ip` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `cost_time_mills` bigint(20) NULL,
  `job_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



SET FOREIGN_KEY_CHECKS = 1;
