ALTER TABLE linkis_stream_job ADD COLUMN `current_version` varchar(50);
ALTER TABLE linkis_stream_job_version ADD COLUMN IF NOT EXISTS `manage_mode` varchar(30) NOT NULL DEFAULT 'EngineConn';