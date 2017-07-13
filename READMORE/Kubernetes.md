# 容器-Kubernetes

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

