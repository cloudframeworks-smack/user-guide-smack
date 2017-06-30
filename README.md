# [云框架]SMACK大数据架构

![](https://img.shields.io/badge/Release-v0.1-green.svg)
[![](https://img.shields.io/badge/Producer-elvis2002-orange.svg)](CONTRIBUTORS.md)
![](https://img.shields.io/badge/License-Apache_2.0-blue.svg)

跟硅谷大数据工程师谈笑风声？Spark、Storm、Pig、Hive……还是Hadoop？那些使用大数据技术的前沿公司会告诉你——**SMACK** is the new buzzword！并非一项单一技术，SMACK由Spark、Mesos、Akka、Cassandra、Kafka组成的大数据架构，适用于广泛的数据处理场景，可完成低延迟扩展及数据复制、统一管理异构负载集群，并通过单一平台满足不同架构设计和不同应用的需求。（[A Brief History of the SMACK Stack](https://chiefscientist.org/a-brief-history-of-the-smack-stack-f382547e91fe)）

在面对数据源数量急剧增加、数据样本获取难度升高、数据分析时效性差、数据分析投资回报率低等一系列大数据带来的挑战时，SMACK可以解决Hadoop等我们熟知的大数据技术无法解决的诸多问题，特别是物联网化、API化趋势下big data向[fast data](http://www.infoworld.com/article/2608040/big-data/fast-data--the-next-step-after-big-data.html)转变所带来的一系列新需求。

进一步讲，SMACK可以看作是一种广义上的框架组合思想，其中技术可以被更适合的技术替代，以更好的完成我们处理流式大数据的目标。本篇[云框架](README.md)即**使用Flink替代了Spark作为引擎层**，**使用Kubernetes替换Mesos作为容器层**，并以以海量网站数据为例，提供SMACK大数据框架的最佳实践，包括数据接入、SMACK核心、数据分析等一系列完整框架内容。

# 内容概览

* [快速部署](#快速部署)
* [框架说明-业务](#框架说明-业务)
* [框架说明-SMACK核心](#smack核心)
    * [引擎-Flink](#引擎-Flink)
    * [容器-Kubernetes](#容器-Kubernetes)
    * [模型-Akka](#模型-akka)
    * [存储-Cassandra](#存储-cassandra)
    * [消息队列-Kafka](#消息队列-kafka)
    * [Data Pipeline](#data-pipeline)
* [框架说明-数据接入](#数据接入)
* [框架说明-数据分析与监控](#数据分析与监控)
* [如何变成自己的项目](#如何变成自己的项目)
* [更新计划](#更新计划)
* [社群贡献](#社群贡献)

# <a name="快速部署"></a>快速部署 @ELVIS2002

# <a name="框架说明-业务"></a>框架说明-业务 @ELVIS2002

<div align=center><img width="900" height="" src="./image/business-architecture.png"/></div>

# <a name="smack核心"></a>框架说明-SMACK核心

[云框架]SMACK大数据框架整体结构如下:

<div align=center><img width="900" height="" src="./image/smack-architecture.png"/></div>

基于Flink（Spark）、Kubernetes（Mesos）、Akka、Cassandra、Kafka这样的组合，利用其成熟的技术、易用性、组合自由性、自动化程度优势等特性去对应不同场景，所构建平台灵活性很难被击败。

**需要注意的是，Flink、Kubernetes、Akka、Cassandra、Kafka比较复杂，本项目不做详细解读，仅对基本要点进行介绍以便理解SMACK大数据架构，更多内容建议通过官方文档进行具体学习。**

## <a name="引擎-Flink"></a>引擎-Flink

Flink是针对流数据和批数据（流数据的一种极限特例）的分布式处理引擎，采用Java编写，支持Scala、Java、Python的API。Flink的最大特点在于将所有任务当作“流”来处理，支持快速本地迭代及环形迭代任务。相比Spark，Flink并没有将内存完全交给应用层，由此降低了out of memory的发生（Spark在1.5版本后所有DataFrame操作直接作用于[Tungsten](https://community.hortonworks.com/articles/72502/what-is-tungsten-for-apache-spark.html)的二进制数据上）。

<div align=center><img width="900" height="" src="./image/flink-architecture.png"/></div>

* Client将任务提交给JobManager，JobManager分发任务给TaskManager，TaskManager执行任务并向JobManager汇报任务状态（心跳） 
* TaskManager之间通过“流”来传递数据，TaskManager内部及TaskManager之间均有数据传递
* Flink允许多级Shuffle

Flink具备以下特性：

* 仅需少量配置即可实现高吞吐率和低延迟
* 通过Event Time简化乱序到达事件计算
* 通过checkpoint机制避免故障发生时状态受到影响
* 流式窗口（时间窗口、统计窗口、Session窗口、数据驱动窗口等）可通过灵活的触发条件定制
* Flink streaming运行时，慢的数据sink节点会backpressure快的数据源（sources）
* Flink的容错机制保证了系统在拥有高吞吐率的同时还能提供强一致性的保障
* 流处理和批处理基于同一个运行时（Flink Runtime）
* Flink在JVM实现了自己的内存管理
* Flink支持迭代计算和增量迭代
* [DataStream API](https://ci.apache.org/projects/flink/flink-docs-release-1.2/dev/datastream_api.html) & [DataSet API](https://ci.apache.org/projects/flink/flink-docs-release-1.2/dev/batch/index.html)
* Flink Stack

<div align=center><img width="900" height="" src="./image/flink-stack.png"/></div>

**Read more [Apache Flink Documentation](https://ci.apache.org/projects/flink/flink-docs-release-1.3/)**

## <a name="容器-Kubernetes"></a>容器-Kubernetes

Kubernetes是谷歌开源的自动化容器集群管理系统，在Docker基础上为容器化应用提供部署运行、资源调度、服务发现和弹性伸缩等功能特性，无需用户进行复杂的设置工作。轻量、易用、可拓展、自修复是Kubernetes的主要特点，也使得Kubernetes成为目前最为流行的容器编排工具，其架构如下：

<div align=center><img width="900" height="" src="./image/kubernetes-architecture.png"/></div>

**Master node**：Master node负责Kubernetes集群的管理，编排实际服务运行的节点，是所有管理任务的entry point。

* API server是用于控制集群的REST命令的entry point，处理并验证REST请求，执行绑定的业务逻辑，并将结果状态存储至etcd
* etcd作为配置中心和存储服务，保存所有组件的定义及状态，并支持Kubernetes多组件之间的交互
* scheduler是插件式调度器，主要用来监听etcd中的pod目录变更并分配node，同时调用API server的bind接口关联node和pod
* controller manager承担IaaS交互、node管理、pod管理及replication、service、namespace管理等master node主要功能，通过监听etcd、registry、event对应事件实现

**Worker node**：pod运行在worker node之上，负责管理容器间的网络、与主节点之间的通信及为容器分配资源等

* docker在worker node上运行，并执行配置的pod
* kubelet本身即是REST服务，和pod相关命令操作通过接口实现，负责容器管理、镜像管理、volume管理等任务，支持volume和network的扩展
* kube-proxy用于单个work node服务的网络代理及负载均衡，实现kubernetes的service机制（TCP和UDP流转发）
* kubectl与API service通讯的命令行工具，向master node发送命令

**Read more [Kubernetes Documentation](https://kubernetes.io/docs/home/)**

## <a name="模型-akka"></a>模型-Akka

Akka是基于Actor模型的并发框架，能够在JVM上简化并行和分布式应用的构建，具有很强的扩展性，可以在系统内轻松创建数千个实例，也能够便捷的从单一节点进程扩展到一个集群的机器中，无需修改代码即可远程运行失败恢复和错误处理。简单来说，Akka是用来开发容错、分布式、并发应用的框架。

<div align=center><img width="900" height="" src="./image/actor-system.png"/></div>

* **Actor**是Akka的核心所在，指封装状态和行为的对象，通过交换消息来通讯（不涉及共享内存），由Erlang语言编写，是面向对象编程最严格的形式。
* Actor自然形成**层次结构**，负责某一功能的Actor会将任务拆解成更小的任务（子Actor），并委托唯一的监管者（supervisor）来监督，直到任务小到可以被完整处理（每一个Actor都是其子Actor的监管者）。这样一来任务本身结构被清晰拆解，同时在Actor无法处理某一任务或状况时，会向监管者发送失败消息以寻求帮助，使失败能够在正确的层次得到处理。
* Akka中每一个Actor都是其子Actor的监管者，每一个Actor都定义了**容错**策略，该策略为Actor系统结构的一部分，可以自己定制，但定制后不能修改。
* 在Actor系统中，**监管**描述actor之间的依赖关系，即监管者（Actor）将任务委托给下属（子Actor）并响应下属失败状况。基于所监管的工作的性质和失败的性质，监管者参考函数配置可以恢复（保持下属当前内部状态）、重启（清楚下属内部状态）、停止、升级（向上级传递失败状况）。需要注意，应始终把Actor视为整个系统中的一部分，即以上前三种操作会同时对Actor所有子Actor生效。
* Actor模型是一个具有**自适应**和初步智能的软件智能体（Agent）模型，Actor集群则是软件多智能体（Multi-Agent）。将Actor的功能进一步扩充可形成适用于各种业务的自主，协同和学习的分布式多智能体系统。
* Akka中消息传递方式分为Fire and Forget和Send and Receive，两种模式均为**异步消息模式**，即消息一旦发送，方法立刻返回，不会阻塞主线程。
* Akka的**持久性**能够使得有状态的Actor实例保存它的内部状态（非当前状态，以追加方式存储），在Actor重启后能够更快的进行恢复。有状态的Actor通过重放（Replay）持久化的状态来快速恢复，重建内部状态。

**Read more [Akka Offical Documentation](http://akka.io/docs/)**

## <a name="存储-cassandra"></a>存储-Cassandra

Cassandra是一个分布式的NoSQL数据库，它最大的特点就是完全去中心化，不像MySQL、MongoDB主从备份的模式，也不像HBase、HDFS有不同类型的节点。整个Cassandra集群就是一个由P2P协议组织起来的网络，消除了所有的单点故障。

<div align=center><img width="900" height="" src="./image/cassandra-architecture.png"/></div>

* 数据存储在**node**
* **Data center**为一组配置在一个集群中用于复制和负载隔离的相关节点
* **Cluster**为一组用于存储数据的节点，包含一个或多个数据中心
* **commit log**为cassandra崩溃恢复机制，数据将先写入commit log用于持久化
* **memtable**是存储器驻留的数据结构
* **SStable**是Cassandra周期性写入memtales数据的不变的数据文件，当sstable内容达到阀值时，数据从memtable中刷新
* **bloom filter**用于检索一个元素是否在一个集合中

Cassandra具有以下特性：

* 具备**可扩展性**，允许添加更多硬件以适应大量客户和数据的需求
* 支持多数据中心，**没有单点故障**，且在多节点故障时仍然何用，适合连续用于不能承担故障的关键业务应用
* 线程可扩展（增加集群节点数量以提高吞吐量）以保证**响应时间**
* 数据建模非常灵活，支持结构化、半结构化和非结构化多种数据格式，并且可以**动态适应数据结构变化**
* 支持自动和可配置的数据复制，在需要时灵活**分发数据**
* 可以在低级硬件上执行**快速写入**，在不牺牲读取效率的前提下存储大量数据

**Read more [Cassandra Official Documentation](http://cassandra.apache.org/doc/latest/)**

## <a name="消息队列-kafka"></a>消息队列-Kafka

Kafka发源于LinkedIn，是一款基于发布／订阅的分布式消息系统，于2011年成为Apache的孵化项目，随后于2012年成为Apache的主要项目之一。Kafka使用Scala和Java进行编写，因其快速、可扩展的、高吞吐、可容错的特点而逐渐成为一项广泛使用的技术，适合在messaging、website activity tracking、log aggregation等大规模消息处理场景中使用。

<div align=center><img width="900" height="" src="./image/kafka-architecture.png"/></div>

* Kafka集群通常由多个broker（Kafka服务实例）组成以实现负载均衡，这些broker是无状态的，通过ZooKeeper来维护集群状态并完成broker的leader election
* ZooKeeper用于管理和协调broker。Zookeeper服务通常负责通知producer（生产消息到topic的一方，topic为消息存放的目录）和customer（订阅topic消息消费的一方）在系统存在的broker或是broker失败，而producer和customer根据通知决定并开始与其他broker协调任务执行
* Producer将数据推送给broker，当新的broker开始工作，所有的producer都会搜索它并自动向该broker发送信息。Producer本身并不等待broker的反馈确认，其发送消息的速度取决于broker的处理能力
* 因为Kafka的broker是无状态的，所以consumer必须使用partition offset（存储在ZooKeeper中）来记录消费了多少数据。如果一个consumer指定了一个topic的offset，意味着该consumer已经消费了该offset之前的所有数据，并可以通过指定offset，从topic的指定位置开始消费数据。

Kafka具有以下特性：

* 支持自动代理故障切换
* 高性能分布式消息传递
* 可在群集节点之间进行分区和分发
* 数据管线去耦
* 大量消费者得到支持
* 大量的数据加载处理

**Read more [Kafka Official Documentation](https://kafka.apache.org/documentation.html)**

## <a name="data-pipeline"></a>Data Pipeline

Data Pipeline即分布式系统里的数据管道，在大型互联网后端基础架构中扮演着举足轻重的角色。与早期的Sqoop、Flume不同的是，现代的Data Pipeline并非工具概念的技术，而是当作一个服务来运行，放在数据系统中去调度。服务化的Data Pipeline可以让原本复杂的数据传输工作变得优雅起来，这也是DevOps思想的一种眼神。

在本篇云框架中Data Pipeline整体结构如下：

<div align=center><img width="900" height="" src="./image/smack-data-pipeline.png"/></div>

# <a name="数据接入"></a>数据接入

`TODO`

# <a name="数据分析与监控"></a>数据分析与监控

`TODO`

# <a name="如何变成自己的项目"></a>如何变成自己的项目

`TODO`

# <a name="更新计划"></a>更新计划

* `文档` 
* `CODE` 

# <a name="社群贡献"></a>社群贡献

+ QQ群: 614186010
+ [参与贡献](CONTRIBUTING.md)
+ [联系我们](mailto:info@goodrain.com)

---- 

[云框架](ABOUT.md)系列主题，遵循[APACHE LICENSE 2.0](LICENSE.md)协议发布。

