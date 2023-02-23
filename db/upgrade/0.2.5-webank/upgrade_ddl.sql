alter table linkis_stream_job add column `current_version` varchar(50);
alter table linkis_stream_job_version add column `manage_mode` varchar(30) default `EngineConn`;