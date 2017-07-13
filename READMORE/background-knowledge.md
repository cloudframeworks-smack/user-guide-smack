# 背景说明

* [关于Fast Data](#fast-data)
* [关于Data Pipeline](#data-pipeline)
* [关于Lambda Architecture](#lambda-architecture)

## <a name="fast-data"></a>关于Fast Data

客观的说，我们需要数据分析，但业务不一定真的会产生“足够大量”的数据，我们实际上更需要的是“快速的数据”，即**Fast Data**，以拉近与客户之间的距离、快速响应需求并提供个性化产品和服务。

Fast Data的核心在于“**让正确的信息在正确的时间通过正确的设备传递给正确的人**”，需要满足以下三点标准——

* **快速摄入（fast ingestion）**：摄取的目的在于获取数据源接口，以便进行更改并统一输入数据，分为直接摄入（direct ingestion，系统模块直接hook API generation，优点是简单，缺点是不灵活）和消息队列（message queue，通过broker访问数据生成API，优点是可以分区、复制、排序、根据组件压力管理pipeline）
* **流分析（analysis streaming）**：大量fast data导致流分析从后端转移到了流层，流分析能力对于实时处理非常重要
* **逐个事件处理（per event transaction）**：实时的逐个事件处理会产生巨大价值，例如节省将数据存储在磁盘上的成本、帮助业务实时作出决策（如何在pre event transcation上提取并获取价值也已成为现代大数据工程师的一大挑战，目前较为流行的是通过机器学习工具处理流数据）

## <a name="data-pipeline"></a>关于Data Pipeline

Data Pipeline即分布式系统里的数据管道，在大型互联网后端基础架构中扮演着举足轻重的角色。与早期的Sqoop、Flume不同的是，现代的Data Pipeline并非工具概念的技术，而是当作一个服务来运行，放在数据系统中去调度。服务化的Data Pipeline可以让原本复杂的数据传输工作变得优雅起来，这也是DevOps思想的一种延伸。

数据传递包括多个步骤，从复制数据到将数据从现有位置传递到云端，从重新格式化数据到数据源之间的联通……过去，这些步骤常需要不同工具来完成。而Data Pipeline则是这些步骤自动化的总和，保证这些步骤可以可靠的应用于所有数据。

Data Pipeline遵循的策略和原则如下——

* **异步消息传递（asynchronous message passing）**：Actor向进程（或actor）发送消息，并依赖进程和支持系统来选择并调用代码运行（Akka、Kafka、Spark相关）
* **[一致性算法](https://en.wikipedia.org/wiki/Consensus_algorithm)及[gossip protocol](https://en.wikipedia.org/wiki/Gossip_protocol)（consensus and gossip）**：（Akka、Cassandra相关）
* **数据局部性（data locality）**：分为temporal（时间序，短时间内数据复用）和Spatial（空间序，在相近存储位置使用数据）（Cassandra、Kafka相关）
* **故障探测（failure detection）**：Kafka中consumer注册成功后，coordinator将consumer添加到ping request scheduler的队列中，并尝试跟踪consumer是否仍然存在；Cassandra在本地确定node是up或down状态，本根据信息协调client访问；在Akka和Spark中使用网络变量相关的三个spark属性（`spark.akka.heartbeat.pauses`、`spark.akka.failure-detector.threshold`、`spark.akka.heartbeat.interval`）进行故障检测。（Cassandra、Spark、Akka、Kafka相关）
* **容错／无单节点故障（fault tolerance／no single point of failure）**：（Spark、Cassandra、Kafka相关）
* **隔离（isolation）**：（Spark、Cassandra、Kafka相关）
* **位置透明（location transparency）**：Spark、Cassandra、Kafka中，位置透明允许读写集群中的任何节点，而系统将读写信息复制到整个集群；在Akka中，actor的mailing address可以是路由位置，此位置对于开发者而言是透明的。（Akka、Spark、Cassandra、Kafka相关）
* **并行化（paralleism）**：Kafka分区并行；Cassandra数据并行；Spark和Akka任务并行；（Kafaka、Cassandra、Spark、Akka相关）
* **扩展分区（partition for scale）**：SMACK技术是网络拓扑感知的（Cassandra、Spark、Kafka、Akka相关）
* **故障点重播（replay for any point of failure）**：Spark中通过checkpointing实现，Kafka和Cassandra通过ZooKeeper实现，而Akka通过Akka persistence实现。（Spark、Cassandra、Kafka、Akka相关）
* **复制弹性（replicate for resiliency）**：Kafka通过调整服务器数量复制每个分区内的日志，集群内服务器发生故障时自动转移副本；Cassandra将副本存储在多个节点上以保障可靠性和容错；Spark通过HDFS实现；（Spark、Cassandra、Kafka相关）
* **可扩展的基础设施（Scalable infrastructure）**：（Spark、Cassandra、Kafka相关）
* **无共享架构（share nothing／masterless）**：节点独立（Cassandra、Akka相关）
* **Dynamo系统原则（Dynamo systems principles）**：Dynamo系统是一组技术，用来获得高可用性的键值分布式数据存储或结构化存储系，具有增量可扩展（incremental scalability）和对称性（symmetry）的特点。

在本项目中，主要包括如下Data Pipeline：

* Spark and Cassandra
* Akka and Kafka
* Akka and Cassandra
* Akka and Spark
* Kafka and Cassandra

## <a name="lambda-architecture"></a>关于Lambda Architecture

建立强大可扩展的大数据系统，需遵循Lambda Architecture的架构原则，服从`query = function(all data)`公式。

Lambda Architecture原则包括——

* **人为容错性（human fault-tolerance）** ：系统易数据丢失或数据损坏，大规模时可能是不可挽回的

* **数据不可变性（data immutability）** ：数据存储在它的最原始的形式不变的，永久的

* **重新计算（recomputation）** ：因为上面两个原则，运行函数重新计算结果是可能的

其**架构层次示例**如下：

<div align=center><img width="900" height="" src="../image/lambda-architecture.png"/></div>

* **批处理层（Batch Layer）**：批处理工具
* **服务层（Serving Layer）**：用于加载和现实数据库中的批处理视图，以便用户能够查询，不一定需要随机写，但是支持批更新和随机读
* **速度层（Speed Layer）**：处理新数据和服务层更新造成的高延迟补偿，利用流处理系统和随机read/write数据存储库来计算实时视图(HBase)



