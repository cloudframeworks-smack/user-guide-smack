# 容器-Mesos

集群管理平台Mesos是一种分布式操作系统内核，总体架构由Mesos-master、Mesos-slave（即Mesos-agent）、frameworks（即Mesos applications）及executor组成。

<div align=center><img width="900" height="" src="../image/mesos_architecture.png"/></div>

* **Mesos-master**是整个架构的核心，管理接入Mesos的slave和Framework，并按照策略为framework分配slave上的资源。

* **Mesos-slave**接收并执行Mesos-master命令、管理节点上的Mesos-task并为task分配资源。该过程为：Mesos-slave将自身资源量（CPU及内存）发送给Mesos-master，由Mesos-master Allocator模块决定资源分配给哪个framework。用户提交作业时指定每个任务所需CPU个数及内存量，Mesos-slave即可将任务放至包含固定资源的容器中运行，并达到资源隔离的效果。

* **Framework**指Hadoop等外部计算框架。计算框架通过注册方式接入Mesos，并由Mesos统一管理和分配资源。Mmesos采用双层调度框架，第一层为Mesos将资源分配给Framework，第二层为Framework利用自己的调度器模块将资源分配给Framework内部的任务。

* Mesos支持C++、Java、Python三种语言编写的Framework调度器模块，而其内部采用C++实现调度器驱动（Mesos Scheduler Driver），Framework的调度器可调用该driver中的接口与Mesos-master交互，完成注册、资源分配等功能。

* **Executor**用于启动Framework内部task。新Framework接入Mesos时，我们需要编写对应Executor告诉Mesos如何启动该Framework内的task。与调度器类似，Mesos采用C++实现执行器驱动（Mesos Executor Driver）以便Framework告知Mesos如何启动task。

**[Apache Aurora](http://aurora.apache.org/documentation/latest/)**：是执行长期运行服务、cron作业、ad-hoc作业的Mesos框架。Aurora支持自动更新与回滚、资源分配和多用户支持、复杂DSL、服务注册等功能特性。

**[Chronos](https://mesos.github.io/chronos/)**：Chronos是用于执行基于容器定时任务的Mesos框架。Chronos以[ISO 8601](https://en.wikipedia.org/wiki/ISO_8601)时间规范作为定时任务的执行时间配置，任务可选Docker或Mesos两种执行容器形式，支持重叠并发及任务依赖配置。

**[Marathon](https://mesosphere.github.io/marathon/docs/)**:是用于执行长时间运行任务的Mesos框架，如web应用和服务等。Marathon可用于集群的多进程管理、为部署提供REST API服务、SSL与基础认证、配置约束、HAProxy、DNS实现服务发现和负载均衡、Health Check以及可定制化监控策略实现Task自动伸缩等。Marathon常与Mesos和Chronos一起运行，使Chronos可以在Mesos内部运行，并直接管理Chronos及长期运行的web应用和服务。

**[Kubernetes on Mesos](https://kubernetes.io/docs/getting-started-guides/mesos/)**：Mesos允许Kubernetes动态分享集群资源，当Kubernetes运行在Mesos上（相当于一个Framework），你可以轻松将Kubernetes上运行的任务在任意云平台之间转移。

**[Mesos Docker Containerizer](http://mesos.apache.org/documentation/latest/docker-containerizer/)**将Docker迁入Mesos集群可解决去烧故障监控、资源的调度、故障转移平台、缺少user-friendly管理界面和相对完整API、网络管理不够完善等Docker在生产环境中的部分不足。

**Read more [Mesos Official Documentation](http://mesos.apache.org/documentation/latest/)**


