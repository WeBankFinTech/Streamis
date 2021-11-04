# Streamis安装部署文档

## 1.模块介绍
----------

Streamis有多个模块，具体如表1-1，每个模块都可以独立提供服务，也可以合并成为一个服务对外提供能力。

| 模块名              | 模块作用                                                                                                       | 是否必须                                                |
|---------------------|----------------------------------------------------------------------------------------------------------------|---------------------------------------------------------|
| streamis-jobmanager | 1.发布流式应用 2.管理流式应用(例如启停) 3.设置流式应用参数 4.流式应用监控                                      | 1.必须，使用Streamis必须要部署jobmanager                |
| streamis-datasource | 1.流式数据源管理，与Linkis数据源打通 2.流式数据源导入导出 3.流式数据源转换成Flink执行代码 4.流式数据源支持执行 | 1.如果需要使用流式工作流，则必须 2.搭建流式数仓，则必须 |
| streamis-workflow   | 1.可视化编辑流式工作流 2.流式工作流执行                                                                        | 1.使用流式工作流，必须                                  |
| streamis-plugins    | 1.与DSS集成相关组件 2.流式工作流转成流式应用                                                                   | 1.如果需要和DSS集成，则必须                             |

## 2.版本选择
----------

&emsp;&emsp;我们将Streamis的安装版本分成两个版本,如下

1. 精简版，该版本只需要包含StreamisJobManager,用户可以通过上传物料包将流式应用发布到JobManager，然后对流式应用进行启停、管理和监控等，因为安装简单，可以作为一个流式应用管理平台。

2. 实时数仓版本。该版本会将流式工作流、jobmanager以及数据源等内容统一成一个服务，对外会提供流式工作流编辑、执行，以及一键发布到jobmanager，并且支持数据源的管理和分享，同时也支持集成到DSS。

## 3.代码编译
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


## 4.安装准备
### 4.1基础环境安装
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;下面的软件必须安装：

- MySQL (5.5+)，[如何安装MySQL](https://www.runoob.com/mysql/mysql-install.html)
- JDK (1.8.0_141以上)，[如何安装JDK](https://www.runoob.com/java/java-environment-setup.html)

### 4.2Linkis环境
Streamis的执行依赖于Linkis，并且需要在1.0.2以上的版本，所以您需要安装1.0.2以上的Linkis

### 4.3软件包准备
从第三步骤中获取软件包，并上传到服务器的安装目录,如 /appcom/Install/streamis
```bash
cd /appcom/Install/streamis
tar -xvf wedatasphere-streamis-${streamis-version}-dist.tar.gz
```

### 4.4修改数据库配置
```bash
vi conf/db.sh
#配置基础的数据库信息
```

### 4.5修改基础配置文件

```bash
vi conf/config.sh
#配置服务端口信息
#配置Linkis服务信息
```
## 5.安装和启动
----------
### 5.1 精简版

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
![components](../images/zh_CN/eureka_streamis.png)



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




### 5.2实时数仓版本
