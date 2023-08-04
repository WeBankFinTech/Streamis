# Streamis Configuration and Script Upgrade Documentation

## Configuration changes

not involved

## SQL changes

A structure modification involving two data tables and a data modification of one data table have been added to the project version branch and included when packaging

![](../../../images/0.3.1/upgrade/upgrade-to-0.3.1.jpg)

### Base script changes

#### 1、streamis_ddl.sql

```yaml
# Add two fields update_time and md5 to the linkis_stream_project_files table
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
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='项目表';
 
```

#### 2、streamis_dml.sql

not involved

### upgrade script

#### 1、ddl upgrade script

```yaml
ALTER TABLE linkis_stream_project_files ADD update_time datetime NULL;
ALTER TABLE linkis_stream_project_files ADD md5 varchar(100) NULL;
```

#### 2、dml upgrade script

```yaml
UPDATE linkis_stream_job_config_def
SET `key`='wds.linkis.flink.alert.failure.user', name='失败时告警用户', `type`='INPUT', sort=0, description='失败时告警用户', validate_type='None', validate_rule=NULL, `style`='', visiable=1, `level`=1, unit=NULL, default_value='', ref_values='', parent_ref=8, required=0, is_temp=0
WHERE id=16;

INSERT INTO `linkis_stream_job_config_def` VALUES (38, 'linkis.ec.app.manage.mode', '管理模式', 'SELECT', 3, 'EngineConn管理模式', 'None', NULL, '', 1, 1, NULL, 'attach', 'detach,attach', 8, 0, 0);

```

