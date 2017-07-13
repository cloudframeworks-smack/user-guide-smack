# 消息队列-Kafka

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


