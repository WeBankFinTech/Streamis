# Streamis

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

[English](README.md) | 中文

## 引言

 &nbsp; &nbsp; &nbsp; &nbsp;Streamis 是微众银行联合萨摩耶金服和仙翁科技联合共建的一站式流式应用开发管理系统。

 &nbsp; &nbsp; &nbsp; &nbsp;基于DataSphere Studio的Orchestrator编排模式、工作流微模块和框架化能力，以及底层对接Linkis的Flink引擎，以工作流式的图形化拖拽开发体验，将实时流式应用以Source节点、
Dimension节点、Transform节点、Sink节点和Visualis节点串连成一条实时工作流，让用户几乎0学习成本完成流式应用的开发、调试、发布和生产管理。

----

## 核心特点

#### 基于DSS，打造业界领先的工作流式图形化拖拽开发体验。

#### 覆盖流式应用开发全流程。提供Source节点、Dimension节点、Transform节点、Sink节点和Visualis节点，基本满足流式应用的所有使用场景。

#### 强大的调试和数据预览能力。基于DSS的跨系统、跨用户级上下文能力，让流式工作流真正具备“开发即调试”、“调试即预览”的开发能力。

#### 基于Linkis计算中间件，打造金融级高并发、高可用、多租户隔离和资源管控等全生命周期管理与checkpoint能力的实时生产中心。

----

## 与类似系统对比

 &nbsp; &nbsp; &nbsp; &nbsp;Streamis是一个引领流式应用开发方向的开源项目，开源社区目前尚没有同类型产品。
 
| 功能模组 | 描述 | Streamis | 
 | :----: | :----: |-------|
 | UI | 集成便捷的管理界面和监控窗口| 已集成 |
 | 安装部署 | 部署难易程度和第三方依赖 | 一键部署，依赖Linkis Flink引擎 |
 | 开发中心| 流式应用编辑 | 支持（未开源），集成DSS | 
 |        | 与DSS无缝衔接，支持工作流 | 支持 （未开源） |
 |生产中心 | 流式应用管理运维能力 | 支持 |
 |       | 复用Linkis计算治理能力 | 支持 |
 |       |支持SQL和jar包等方式发布|支持 |
 | 服务高可用 | 服务多点，故障不影响使用| 应用高可用 | 
 | 系统管理 | 节点、资源管理 | 支持 |
----

## 架构

![架构](images/zh_CN/readme/architecture.png)

----

## 编译和安装部署
请参照 [编译指引]() Flink引擎编译/Streamis编译。

请参考 [安装部署文档]() Flink引擎安装。


----
## 示例和使用指引
请到 [用户使用文档]() ，了解如何快速使用Streamis。

----
## 交流贡献

![交流](images/zh_CN/readme/communication.png)

----

## License

 &nbsp; &nbsp; &nbsp; &nbsp;DSS is under the Apache 2.0 license. See the [License](LICENSE) file for details.

