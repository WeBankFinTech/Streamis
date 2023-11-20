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