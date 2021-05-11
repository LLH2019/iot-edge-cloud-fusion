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

#### 项目总体设计介绍

边缘端 采用akka-cluster 部署

如下图所示，主要存在以下几类Actor

![image](https://user-images.githubusercontent.com/46324430/117657194-daaa3f80-b1cb-11eb-8134-e2e7e7be12e0.png)

- Pod Actor， 对应于每一个运行时环境存在一个，初始化时创建
    - 主要功能如下
        - 创建 DeviceActor,实现方式为根据接收到的kafka消息创建相应的DeviceActor
        - 监控运行时环境消耗资源情况 （待完成）
    - 交互情况
        - 接受来自于 EdgeKafkaConnectInActor 接收的消息，topic 为 edge.edge-pod-[no]
- Device Actor，对应于监控设备的Actor
    - 主要功能如下：
        - 接收相应设备发布的Mqtt消息，并根据对应物模型做相应的处理
        - 接受来自于上层的指令消息，通过发Mqtt消息给相应设备下发指令 （待完成）
    - 交互情况
        - 接收来自于 EdgeKafkaConnectInActor 接收的消息，topic 为 edge.[name].[no]
        - 发布消息到kafka中， topic为 cloud.[name].[no]
        - 接收来自于 Mqtt Broker 中的消息， topic 为 device/up/[name]/[no]
        - 发布消息到 Mqtt Broker 中， topic 为 device/down/[name]/[no]
- EdgeKafkaConnectInActor，每一个运行时环境存在一个，初始化时新建
    - 主要功能如下：
        - 接收来自于 kafka 中间件消息，根据中间件消息对应 topic，然后转发给其他的 Actor
- EdgeMqttConnectInActor，每一个运行时环境存在一个，初始化时新建
    - 主要功能如下：
        - 接收来自于 Mqtt Broker 消息，然后根据 topic 转发给不同的 Device Actor 做相应处理

云端

如下图所示，主要存在以下几类actor

![image](https://user-images.githubusercontent.com/46324430/117658962-ef87d280-b1cd-11eb-87ae-40dcfa73fb54.png)

- Brain Actor， 云端的主要控制actor，运行时环境中只存在一个
    - 主要作用如下：
        - 接受MongoDB查询物模型数据，然后创建DeviceCloudControlActor，以及发布消息到Kafka，边缘端接收到消息将创建对应的Device Actor
        - 监控云端运行时环境 （待完成）
- MongoDBConnActor ，用于对MongoDB 进行相应的操作
    - 插入数据
    - 查询数据
- DeviceCloudControlActor，作为端设备在云端的控制节点
    - 主要功能如下：
        - 将通过KafkaConnctInActor得到对应Device Actor发来的消息，然后做进一步处理
- CloudKafkaConnctInActor， 类似于边缘端
- 全局保存环境，由于如果每次查询需要到数据库中进行，对于频繁更新的前端查询将会造成数据库很大的负载，因此设置一个全局保存变量，主要保存相关需要向前端展示的数据内容，该环境中数据直接根据kafka中传来的数据实时更新


#### 由于云、边、端之间主要靠消息进行通信，因此基于此，设计了以下一些规则用于规范

边以及端通过Mqtt进行通信，基于此

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


#### 物模型实现介绍
- Profile 设备静态属性信息
- Property 设备属性，为设备主动上传信息
- Event 设备接收指令，由用户下发给相应的设备，这里将直接生成 Mqtt 数据


#### 前端页面数据介绍
- /device-list 
    - 将展示有多少个设备连入
    - 显示每一个设备的基本信息，暂时只包括名称
    - 每一个设备连入情况 （活跃、待机）  待完成
![image](https://user-images.githubusercontent.com/46324430/117676546-3979b400-b1e0-11eb-80a4-1dfd4732d1d5.png)

- /device-info/{cloud.[name].[no]}
    - 将显示相应设备的状态信息，包括名称，最近上传属性值，能够向下发布的指令
![image](https://user-images.githubusercontent.com/46324430/117748324-59da5a80-b242-11eb-9da8-5c5b46a7969a.png)
![image](https://user-images.githubusercontent.com/46324430/117748293-4dee9880-b242-11eb-9b7b-dcbd677a2c2a.png)


- /device-control/{name.no.event}
    - 向相应设备发布相应的指令 

## 工具操作

#### mongodb 操作


#### kafka 操作

kafka采用docker 进行安装
- docker ps 
    - 查看 kafka 对应docker id
- docker exec -it [id] /bin/bash
    - 进入相应 docker 容器内 
- 一些常见kafka脚本命令
    - 模拟消费者消费
        - kafka-console-consumer --bootstrap-server [host]:[port] --topic [topic] --from-beginning
    - 模拟生产者生产
        - kafka-console-producer --broker-list [host]:[port] --topic [topic]


## 遇到问题
* 边缘端无法将消息发送到云端的 kafka 中

## 后期计划

- 消息通信规则比较死板，有没有替换方案
- 设备接入之后，能够自发现
- 

## 项目反馈
