# Streamis Compilation Guide

This doc is a step-by-step compilation guidance of Streamis, a streaming application platform, including front-end web and back-end server.

### 1. Streamis code compilation

Get the source code from [github](https://github.com/WeBankFinTech/Streamis), and compile it:

#### Back-end server:

```
cd ${STREAMIS_CODE_HOME}
mvn -N install
mvn clean install
```

Once the compilation completes, you can get the installation package <u>wedatasphere-streamis-0.1.0-dist.tar.gz</u> in the *${STREAMIS_CODE_HOME}/assembly/target* directory.

#### Front-end server:

```
cd ${STREAMIS_CODE_HOME}/web
npm i
npm run build
```

Once the compilation completes, you can get the installation package <u>dist.zip</u> in the *${STREAMIS_CODE_HOME}/web* directory.

