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