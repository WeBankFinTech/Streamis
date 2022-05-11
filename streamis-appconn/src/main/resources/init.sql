
delete from  `dss_appconn` where `appconn_name`='streamis';
INSERT INTO `dss_appconn` (`appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES ('streamis', 0, 1, NULL, 0, NULL, 'com.webank.wedatasphere.dss.appconn.streamis.StreamisAppConn', 'DSS_INSTALL_HOME_VAL/dss-appconns/streamis/lib', '');
