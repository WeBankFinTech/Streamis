UPDATE linkis_stream_job_config_def
SET `key`='wds.linkis.flink.alert.failure.user', name='失败时告警用户', `type`='INPUT', sort=0, description='失败时告警用户', validate_type='None', validate_rule=NULL, `style`='', visiable=1, `level`=1, unit=NULL, default_value='', ref_values='', parent_ref=8, required=0, is_temp=0
WHERE id=16;

INSERT INTO `linkis_stream_job_config_def` VALUES (38, 'linkis.ec.app.manage.mode', '管理模式', 'SELECT', 3, 'EngineConn管理模式', 'None', NULL, '', 1, 1, NULL, 'attach', 'detach,attach', 8, 0, 0);
