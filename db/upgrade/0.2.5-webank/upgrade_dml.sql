UPDATE linkis_stream_job j SET current_version = (SELECT version FROM linkis_stream_job_version v WHERE v.job_id = j.id ORDER BY id DESC limit 1);