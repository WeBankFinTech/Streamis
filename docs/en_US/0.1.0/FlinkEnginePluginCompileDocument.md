# Flink Engine Compilation Guide

This doc is a step-by-step guidance of the compilation of flink engine in Linkis 1.0.2.

### 1. Configuration and compilation of Flink engine

The Flink version supported by *Linkis 1.0.2* is ***Flink 1.12.2***. Theoretically, *Linkis 1.x* is able to support various versions of Flink, however, the API difference between Flink versions is too big to ignore, so you may need to modify the code of the Flink engine in Linkis project code (according to the API changes) and recompile it.

### 2. Flink engineConn compilation and loading

The Flink engine will not be installed by default in *Linkis 1.0.2*, you need to compile and install it manually.

**Compile** the Flink engine separately:

```
${linkis_code_dir}/linkis-enginepconn-lugins/engineconn-plugins/flink/
mvn clean install
```

Once the compilation completes, you can get the Flink engine package <u>flink-engineconn.zip</u> in the ${linkis_code_dir}/linkis-enginepconn-lugins/engineconn-plugins/flink/target directory.

Then, **deploy** the <u>flink-engineconn.zip</u> package to

```
${LINKIS_HOME}/lib/linkis-engineplugins
```

and **unzip** it.

The directory structure of the <u>Flink engine package</u> is:

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

Next, **restart** linkis-engineplugin:

```
cd ${LINKIS_HOME}/sbin
sh linkis-daemon restart cg-engineplugin
```

For more detailed introduction of the engineplugin, please refer to [this article](https://github.com/apache/incubator-linkis/wiki/EngineConnPlugin%E5%BC%95%E6%93%8E%E6%8F%92%E4%BB%B6%E5%AE%89%E8%A3%85%E6%96%87%E6%A1%A3#22-%E7%AE%A1%E7%90%86%E5%8F%B0configuration%E9%85%8D%E7%BD%AE%E4%BF%AE%E6%94%B9%E5%8F%AF%E9%80%89).

Please note that once you **unzip** the <u>Flink engine package (flink-engineconn.zip)</u>, it is recommended to delete the <u>flink-engineconn.zip</u> immediately to avoid loading it into the library after the engine started.