# 引擎-Spark

Spark基于内存设计，采用分布式计算Master-Slave模型，支持包括Scala、Java、Python在内的多种语言，提供Map Reduce操作、SQL查询、流数据、机器学习和图表数据处理等多种能力。Spark并不是要取代Hadoop，二是为管理不同性质大数据用例的统一框架。

完整的Spark架构生态由Spark Core（核心API）及Spark SQL、Spark Streaming、Spark MLib、Spark Graphx等附加库构成。

<div align=center><img width="900" height="" src="../image/spark-framework.png"/></div>

**Spark Core**：Spark Core是大规模并行和分布式数据处理的基础引擎（Spark项目基础），负责内存管理和故障恢复、调度分发监控集群作业、与存储系统进行交互等。Spark采用RDD基础数据结构，可通过在外部存储系统中引用数据集（[Actions](http://spark.apache.org/docs/1.2.1/programming-guide.html#actions)）或通过在现有RDD转换（map、filter、reducer、join等）来创建（[Transformations](http://spark.apache.org/docs/1.2.1/programming-guide.html#transformations)）。

**Spark SQL**：Spark SQL通过JDBC API暴露Spark数据集，支持传统BI和可视化工具在Spark数据上执行SQL查询，同时对不同格式数据（JSON、Parquet、数据库等）执行ETL病暴露给特定查询。

**Spark Streaming**：Spark Streaming以微批量的方式计算和处理实时数据流，它使用DStream，简单来说就是一个弹性分布式数据集（RDD）系列，处理实时数据。

**Spark MLib**：Spark MLib（Machine Learning Library）是Spark可扩展的机器学习库，由二元分类、线性回归、聚类、协同过滤、梯度下降以及底层优化原语通用的学习算法和工具组成。

**Spark GraphX**：GraphX是用于图计算和并行图计算的Spark API，通过弹性分布式属性图（Resilient Distributed Property Graph）扩展Spark RDD。GraphX通过暴露基础操作符集合（如subgraph，joinVertices和aggregateMessages）和经过优化的Pregel API变体以支持图计算，并提供简化图分析任务的图算法和构建器集合。

**Read more [Apache Spark Official Documentation](https://spark.apache.org/docs/latest/)**

