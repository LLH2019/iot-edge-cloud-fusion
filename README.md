# iot-edge-cloud-fusion
利用actor model实现云、边对端设备的控制，以及一些高容错、高可靠方面的支持


## 项目简介

- 此项目是一个利用基于JVM的akka框架编写的跨云、边对设备进行控制的一体化框架项目

![image](https://user-images.githubusercontent.com/46324430/117656288-c154c380-b1ca-11eb-8563-182c5c0415e4.png)

![image](https://user-images.githubusercontent.com/46324430/117656927-8737f180-b1cb-11eb-990f-5f651978b3f7.png)



主要涉及组件：

- HTTP 客户端： 客户可通过HTTP客户端获取相应设备信息状况

- MongoDB 数据库，作为文档型数据库，MongoDB能够很好的保存json数据，在此将主要用于保存物模型数据

- MySQL 数据库，主要保存常规数据

- Kafka 中间件，在此作为云端和边缘端通信媒介

- MQTT 中间件，在此作为边缘端和设备通信媒介

## 项目配置运行

1、下载项目

git clone [git@github.com](mailto:git@github.com):LLH2019/iot-edge-cloud-fusion.git

2、配置运行云端中间件

cd cloud

docker-compose up

3、配置运行云端运行时环境

mvn compile exec:exec

4、配置运行边缘端运行时环境

cd edge

mvn compile exec:exec

## 项目详细介绍

项目总体设计介绍

边缘端 采用akka-cluster 部署

如下图所示，主要存在以下几类Actor

- Pod Actor， 对应于每一个运行时环境存在一个，主要功能如下：
    - 创建Device Actor
    - 监控运行时环境消耗资源情况 （待完成）
- Device Actor，对应于监控设备的Actor，主要功能如下：
    - 接收相应设备发布的Mqtt消息，并根据对应物模型做相应的处理
    - 接受来自于上层的指令消息，通过发Mqtt消息给相应设备下发指令 （待完成）
- KafkaConnectInActor，每一个运行时环境存在一个，主要功能如下：
    - 接收来自于kafka中间件消息
    - 根据中间件消息对应topic，然后转发给其他的actor
- MqttConnectIn，在此处并未声明为Actor，后面进行完善，此处为监听Mqtt发布消息，然后转发给其他的actor


云端

如下图所示，主要存在以下几类actor

- Brain Actor， 云端的主要控制actor，主要作用如下：
    - 接收MongoDB查询物模型数据，然后创建DeviceControlActor，以及发布消息到Kafka，边缘端接收到消息将创建对应的Device Actor
- MongoDBConnActor ，用于对MongoDB 进行相应的操作
    - 插入数据
    - 查询数据
- DeviceControlActor，作为端设备在云端的控制节点，将通过KafkaConnctInActor得到对应Device Actor发来的消息，然后做进一步处理
- KafkaConnctInActor， 类似于边缘端


由于云、边、端之间主要靠消息进行通信，因此基于此，设计了以下一些规则用于规范,边以及端通过Mqtt进行通信，基于此
- 若云向端发送消息
    - topic 规则为 device/down/[name]/[no]
    - value 规则为 key:value 形式， key为属性，value为相应值

- 若端向云发送消息
    - topic 规则为 device/up/[name]/[no]
    - value 规则为 key:value 形式， key为属性，value为相应值

云和边之间采用kafka进行通信
- 云端监听 消息 cloud.*
- 边缘监听 消息 edge.*

云发布消息到边，规则如下
- 若该消息发给Pod Actor
    - topic 为 edge.edge_pod.[no]
- 若该消息发给Device Actor
    - topic 为 edge.[name].[no]
    - value 为 属性:值

边发布消息到云，规则如下
- topic 为 cloud.[name].[no]
- value 为 属性:值

物模型实现介绍

## 遇到问题

## 后期计划

- 消息通信规则比较死板，有没有替换方案
- 设备接入之后，能够自发现
- 

## 项目反馈
