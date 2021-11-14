# Streamis安装部署文档

## 1.组件介绍
----------

Streamis0.1.0提供了Streamis-JobManager组件,组件的作用有<br>
1.发布流式应用<br>
2.设置流式应用参数,如Flink的Slot数量,checkpoint相关参数等<br> 
3.管理流式应用(例如启停)<br> 
4.流式应用监控<br>


## 2.代码编译
----------
如果已经获取到安装包，可以跳过此步骤<br>
后台编译方式如下
```
cd ${STREAMIS_CODE_HOME}
mvn -N install
mvn clean install
```
前端编译方式如下
```bash
cd ${STREAMIS_CODE_HOME}/web
npm i
npm run build
```


## 3.安装准备
### 3.1基础环境安装
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;下面的软件必须安装：

- MySQL (5.5+)，[如何安装MySQL](https://www.runoob.com/mysql/mysql-install.html)
- JDK (1.8.0_141以上)，[如何安装JDK](https://www.runoob.com/java/java-environment-setup.html)

### 3.2Linkis环境
Streamis的执行依赖于Linkis，并且需要在1.0.3及以上的版本，所以您需要安装1.0.3以上的Linkis,并且保证Flink引擎可以正常使用,具体的,您可以在Scriptis上新建编辑一个flinksql脚本
并执行。如果flinksql能正确执行，表示linkis1.0.3环境是正常的。

### 3.3软件包准备
从第三步骤中获取软件包，并上传到服务器的安装目录,如 /appcom/Install/streamis
```bash
cd /appcom/Install/streamis
tar -xvf wedatasphere-streamis-${streamis-version}-dist.tar.gz
```

### 3.4修改数据库配置
```bash
vi conf/db.sh
#配置基础的数据库信息
```

### 3.5修改基础配置文件

```bash
vi conf/config.sh
#配置服务端口信息
#配置Linkis服务信息
```
## 4.安装和启动
----------

- 后台安装
```bash
sh bin/install.sh
```

- install.sh脚本会询问您是否需要初始化数据库并导入元数据。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;因为担心用户重复执行install.sh脚本，把数据库中的用户数据清空，所以在install.sh执行时，会询问用户是否需要初始化数据库并导入元数据。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**第一次安装**必须选是。


- 启动
```bash
sh bin/start-streamis.sh
```

- 启动验证
验证方式，因为Streamis和Linkis同用一套Eureka,所以您需要检查Linkis的Eureka页面是否已经包含了Streamis的服务，如图, 
![components](../../images/zh_CN/eureka_streamis.png)



- 前端部署

1.安装nginx
 
```bash
sudo yum install -y nginx
```
2.部署前端包
```
mkdir ${STREAMIS_FRONT_PATH}
cd ${STREAMIS_FRONT_PATH}
#放置前端包
unzip streamis-web.zip
```
3.修改nginx配置文件<br>

```bash
cd /etc/nginx/conf.d
vi streamis.conf
# 复制下面的模板并根据实际情况进行修改
```
```
server {
    listen       9088;# 访问端口
    server_name  localhost;
    location / {
        root   ${STREAMIS_FRONT_PAH}; # 请修改成Streamis恰当南的静态文件目录
    index  index.html index.html;
    }
    location /api {
    proxy_pass http://${Linkis_GATEWAY_IP}:${LINKIS_GATEWY_PORT}; #后端Linkis的地址,请修改成Linkis网关的ip和端口
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header x_real_ipP $remote_addr;
    proxy_set_header remote_addr $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_http_version 1.1;
    proxy_connect_timeout 4s;
    proxy_read_timeout 600s;
    proxy_send_timeout 12s;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection upgrade;
    }

    #error_page  404              /404.html;
    # redirect server error pages to the static page /50x.html
    #
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
    root   /usr/share/nginx/html;
    }
}
```
4.重启nginx
```bash
sudo systemctl restart nginx
```

## 5.接入DSS
Streamis0.1.0版本接入DSS只实现了一级规范，也就是能够在DSS的页面进行免密跳转到Streamis前端，具体的您需要将streamis的基本信息配置到DSS的数据库中，具体如下,你需要替换sql首行的ip和端口即可。
```roomsql
SET @STREAMIS_INSTALL_IP_PORT='127.0.0.1:9088'; 
SET @URL = replace('http://STREAMIS_IP_PORT', 'STREAMIS_IP_PORT', @STREAMIS_INSTALL_IP_PORT);
SET @HOMEPAGE_URL = replace('http://STREAMIS_IP_PORT', 'STREAMIS_IP_PORT', @STREAMIS_INSTALL_IP_PORT);
SET @PROJECT_URL = replace('http://STREAMIS_IP_PORT', 'STREAMIS_IP_PORT', @STREAMIS_INSTALL_IP_PORT);


delete from `dss_application` WHERE `name` = 'STREAMIS';
INSERT INTO `dss_application`(`name`,`url`,`is_user_need_init`,`level`,`user_init_url`,`exists_project_service`,`project_url`,`enhance_json`,`if_iframe`,`homepage_url`,`redirect_url`) VALUES ('STREAMIS', @URL, 0, 1, NULL, 0, @PROJECT_URL, '', 1, @HOMEPAGE_URL, @REDIRECT_URL);

select @dss_STREAMIS_applicationId:=id from `dss_application` WHERE `name` = 'STREAMIS';

select @dss_onestop_menu_id:=id from `dss_onestop_menu` where `name` = '应用开发';

delete from `dss_onestop_menu_application` WHERE title_en = 'STREAMIS';
INSERT INTO `dss_onestop_menu_application` (`application_id`, `onestop_menu_id`, `title_en`, `title_cn`, `desc_en`, `desc_cn`, `labels_en`, `labels_cn`, `is_active`, `access_button_en`, `access_button_cn`, `manual_button_en`, `manual_button_cn`, `manual_button_url`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`, `image`) 
VALUES(@dss_STREAMIS_applicationId, @dss_onestop_menu_id, 'STREAMIS','实时计算平台','Streamis is a streaming application development management system. Based on DataSphereStudio framing capabilities and the underlying docking of Linkis Flink engine, users can complete the development, debugging, release and production management of streaming applications at low cost','Streamis是微众银行联合天翼云、仙翁科技和萨摩耶云共建的流式应用开发管理系统。基于DataSphereStudio的框架化能力，以及底层对接Linkis的Flink引擎，让用户低成本完成流式应用的开发、调试、发布和生产管理。','Streamis','实时计算平台','1','Enter Streamis','进入Streamis','user manual','用户手册','http://127.0.0.1:8088/wiki/scriptis/manual/workspace_cn.html','shujujiaohuan-logo',NULL,NULL,NULL,NULL,NULL,'shujujiaohuan-icon');
```
执行完sql之后，您可能需要重新启动一下dss-framework-project-server进行一下内存刷新。