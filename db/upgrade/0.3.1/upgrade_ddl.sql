DROP TABLE IF EXISTS `linkis_stream_error_code`;

CREATE TABLE `linkis_stream_error_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `error_code` varchar(50) COLLATE utf8_bin NOT NULL,
  `error_desc` varchar(1024) COLLATE utf8_bin NOT NULL,
  `error_regex` varchar(1024) COLLATE utf8_bin DEFAULT NULL,
  `error_type` int(3) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=143 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

ALTER TABLE linkis_stream_project_files ADD source varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL;

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1325 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `linkis_stream_register_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` bigint(20) DEFAULT NULL,
  `application_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `password` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `register_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `heartbeat_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


