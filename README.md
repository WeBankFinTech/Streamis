# Streamis

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

English | [中文](README-ZH.md)
## Introduction

 &nbsp; &nbsp; &nbsp; &nbsp;Streamis is a streaming application development and management system jointly built by **WeBank** in conjunction with **Ctyun**, **Xianweng Technology** and **Samoyed Cloud**.

 &nbsp; &nbsp; &nbsp; &nbsp;Based on the framework capabilities of [DataSphere Studio](https://github.com/WeBankFinTech/DataSphereStudio) and the underlying engine with the **Flink engine* of [Linkis](https://github.com/apache/incubator-linkis) *, Streamis allows users to complete the development, debugging, release and production management of streaming applications at low cost.
                             
 &nbsp; &nbsp; &nbsp; &nbsp;In the future, it is also planned to use a workflow-style graphical drag-and-drop development experience, and the streaming application will be formed as  a streaming workflow by connected series of the Source node, Dimension node, Transform node, Sink node and [Visualis](https://github.com/WeBankFinTech/Visualis) nodes, allowing users to develope, debug and publish streaming applications at a lower learning cost.

----

## Core features

#### Based on DataSphere Studio and DSS-Scriptis, build an industry-leading streaming application development management system.

#### Powerful streaming application development and debugging capabilities, based on DSS-Scriptis, provides streaming application development and debugging functions, and supports real-time debugging and result set display of FlinkSQL.

#### Powerful streaming application production center capabilities. Support multi-version management, full life cycle management, monitoring alarm, checkpoint and savepoint management capabilities of streaming jobs.

#### Based on Linkis computing middleware, build a financial-level streaming production center with high concurrency, high availability, multi-tenant isolation, and resource management and control capabilities.

----

## Comparison With Existing Systems

 | Function module | describe | Streamis | 
 | :----: | :----: |-------|
 | UI | Integrated convenient management interface and monitoring window | Integrated |
 | Installation and deployment | Ease of deployment and third-party dependence | One-click deployment, relying on Linkis Flink engine |
 | open your heart | support to develop and debug streaming applications | Support , integrated DSS | 
 |production center | Streaming application management operation and maintenance capabilities | support |
 |       | Reuse Linkis computing governance capabilities | support |
 |       |Support SQL and jar package release|support |
 | High service availability | Multiple services, failure does not affect the use | Application high availability | 
 | System Management | Node and resource management | support |
----

## Architecture

![架构](images/zh_CN/readme/architecture.png)

----

## Compile and install deployment
Please refer to [Compilation guidelines]() Flink engine compilation/Streamis compilation.

Please refer to [Install and deploy documentation]() Flink engine installation.


----
## Examples and usage guidelines
Please come [User documentation]() ,Learn how to use Streamis quickly.

----
## Communication contribution

![comminicate](images/zh_CN/readme/communication.png)

----

## License

 &nbsp; &nbsp; &nbsp; &nbsp;DSS is under the Apache 2.0 license. See the [License](LICENSE) file for details.

