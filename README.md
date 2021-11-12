# Streamis

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

English | [中文](README-ZH.md)
## Introduction

 &nbsp; &nbsp; &nbsp; &nbsp;Streamis is an associated joint development application management system jointly established by WeBank Samoyed Financial Services and Xian Weng Technology.

 &nbsp; &nbsp; &nbsp; &nbsp;Based on DataSphere Studio’s Orchestrator orchestration mode, workflow micro-modules and framework capabilities, as well as the underlying docking Linkis’s Flink engine, using a workflow-style graphical drag-and-drop development experience, real-time streaming applications are based on Source nodes,
 The Dimension node, Transform node, Sink node and Visualis node are cascaded into a real-time workflow, allowing users to complete the development, debugging, release and production management of streaming applications with almost zero learning costs.

----

## Core features

#### Based on DSS, create a star-like workflow graphical drag-and-drop development experience.

#### Cover the whole process of streaming application development. Provides Source node, Dimension node, Transform node, Sink node and Visualis node, basically satisfying all usage scenarios of streaming applications.

#### Powerful debugging and data preview capabilities. Based on the cross-system and cross-user-level context capabilities of DSS, the flow workflow truly has the development capabilities of "development is debugging" and "debugging is preview".

#### Based on Linkis computing middleware, build a real-time production center with financial-level high concurrency, high availability, multi-tenant isolation, and resource management and control, and other full life cycle management and checkpoint capabilities.

----

## Comparison With Existing Systems

 &nbsp; &nbsp; &nbsp; &nbsp;Streamis is an open source project that leads the direction of streaming application development. There is no similar product in the open source community.
 
| Function module | describe | Streamis | 
 | :----: | :----: |-------|
 | UI | Integrated convenient management interface and monitoring window | Integrated |
 | Installation and deployment | Ease of deployment and third-party dependence | One-click deployment, relying on Linkis Flink engine |
 | open your heart | Streaming application editing | Support (not open source), integrated DSS | 
 |        | Seamlessly connect with DSS and support workflow | Support (not open source) |
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

