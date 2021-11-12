# Streamis编译文档

本文主要介绍Streamis流式应用平台的编译，包括前端web和后端server。

### 1、Streamis代码编译

​	从github上面获取源代码

​	后台编译方式如下：

```
cd ${STREAMIS_CODE_HOME}
mvn -N install
mvn clean install
```

执行成功后会在工程的${STREAMIS_CODE_HOME}/assembly/target目录下生成安装包wedatasphere-streamis-0.1.0-dist.tar.gz	

 前端编译方式如下

```
cd ${STREAMIS_CODE_HOME}/web
npm i
npm run build
```

编译成功后

在${STREAMIS_CODE_HOME}/web目录下生成dist.zip

