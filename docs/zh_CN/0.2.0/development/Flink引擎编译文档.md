# Flink引擎编译文档

本文主要介绍在Linkis1.0.2中，Flink引擎的编译

### 1、Flink引擎的配置和部署

Linkis1.0.2支持的Flink版本是Flink1.12.2,理论上Linkis1.x 可以支持各个版本的Flink,但是Flink个版本之间的API差异太大，可能需要您按照API的变化修改Linkis中Flink引擎的代码并重新编译。

### 2、Flink engineConn的部署和加载

Linkis Flink引擎默认在Linkis1.0.2中是不会安装的，需要您手动进行编译并安装。

单独编译Flink引擎的方式

```
${linkis_code_dir}/linkis-enginepconn-lugins/engineconn-plugins/flink/
mvn clean install
```

编译完成后，您可以在${linkis_code_dir}/linkis-enginepconn-lugins/engineconn-plugins/flink/target目录下获取到Flink引擎的引擎包flink-engineconn.zip

然后将其部署到

```
${LINKIS_HOME}/lib/linkis-engineplugins
```

并将其解压，Flink引擎包的目录结构

```
flink
├── dist
│   └── v1.12.2
│       ├── conf
│       ├── conf.zip
│       ├── lib
│       └── lib.zip
└── plugin
    └── 1.12.2
        └── linkis-engineconn-plugin-flink-1.0.2.jar
```

并重启linkis-engineplugin

```
cd ${LINKIS_HOME}/sbin
sh linkis-daemon restart cg-engineplugin
```

engineplugin更详细的介绍可以参看下面的文章。
https://github.com/apache/incubator-linkis/wiki/EngineConnPlugin%E5%BC%95%E6%93%8E%E6%8F%92%E4%BB%B6%E5%AE%89%E8%A3%85%E6%96%87%E6%A1%A3#22-%E7%AE%A1%E7%90%86%E5%8F%B0configuration%E9%85%8D%E7%BD%AE%E4%BF%AE%E6%94%B9%E5%8F%AF%E9%80%89

需要注意的是，当您解压完Flink引擎包后，建议将flink-engineconn.zip删除，避免引擎启动后，将flink-engineconn.zip加载进物料库中。

