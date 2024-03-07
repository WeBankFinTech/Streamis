ALTER TABLE linkis_stream_audit_log ADD cost_time_mills bigint(20) NULL COMMENT 'cost time';

ALTER TABLE linkis_stream_task ADD job_startup_config text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'streamis job startup config';
