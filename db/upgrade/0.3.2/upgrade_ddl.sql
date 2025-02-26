
ALTER TABLE linkis_stream_audit_log
    ADD COLUMN `job_name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL;

ALTER TABLE linkis_stream_error_code
    ADD COLUMN solution TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'error code solution';

ALTER TABLE linkis_stream_job
    ADD COLUMN enable tinyint(1) DEFAULT 1;

ALTER TABLE linkis_stream_task
    ADD COLUMN `solution` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'error code solution';

ALTER TABLE linkis_stream_task
    ADD COLUMN `template_id` bigint(20) DEFAULT NULL;
