select @old_dss_appconn_id:=id from `dss_appconn` where `appconn_name` = 'streamis';

delete from  `dss_workspace_menu_appconn` WHERE `appconn_id` = @old_dss_appconn_id;
delete from `dss_appconn_instance` where `appconn_id` = @old_dss_appconn_id;
delete from  `dss_appconn` where `appconn_name`='streamis';

INSERT INTO `dss_appconn` (`appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES ('streamis', 0, 1, 0, 0, NULL, 'com.webank.wedatasphere.streamis.dss.appconn.StreamisAppConn', NULL, NULL);

select @dss_appconn_id:=id from `dss_appconn` where `appconn_name` = 'streamis';

INSERT INTO `dss_workspace_menu_appconn`
(appconn_id, menu_id, title_en, title_cn, desc_en, desc_cn, labels_en, labels_cn, is_active, access_button_en, access_button_cn, manual_button_en, manual_button_cn, manual_button_url, icon, `order`, create_by, create_time, last_update_time, last_update_user, image)
VALUES(@dss_appconn_id, 1, 'Streamis', 'Streamis', 'Streaming application development and management', '流式应用开发和管理', 'streaming,realtime', '流式，实时', 1, 'enter Streamis', '进入Streamis', 'user manual', '用户手册', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO `dss_appconn_instance`
(appconn_id, label, url, enhance_json, homepage_uri)
VALUES(@dss_appconn_id, 'DEV', 'http://10.107.97.166:8088/', '', '#/workspaceHome?workspaceId=224');