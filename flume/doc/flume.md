## 第一节：Flume的简介

#### 1.1 大数据处理流程

在企业中，大数据的处理流程一般是：

```
- 1.数据采集
- 2.数据存储
- 3.数据清洗
- 4.数据分析
- 5.数据展示
```

参考下图：

![](flume-day01.assets/20191121151318.jpg)

在数据采集和搜集的工具中，Flume框架占有一定的市场份量。

#### 1.2 Flume的简介

Flume是一种分布式的，可靠的、高可用的服务，用于有效地收集，聚合和移动大量日志数据。它具有基于流数据流的简单灵活的体系结构。它具有可调整的可靠性机制以及许多故障转移和恢复机制，具有强大的功能和容错能力。它使用一个简单的可扩展数据模型，允许在线分析应用程序。 

> 参考官网： http://flume.apache.org/ 
>
> Flume is a distributed, reliable, and available service for efficiently collecting, aggregating, and moving large amounts of log data. It has a simple and flexible architecture based on streaming data flows. It is robust and fault tolerant with tunable reliability mechanisms and many failover and recovery mechanisms. It uses a simple extensible data model that allows for online analytic application.

flume 最开始是由 cloudera 开发的实时日志收集系统，受到了业界的认可与广泛应用。但随着 flume 功能的扩展，flume的代码工程臃肿、核心组件设计不合理、核心配置不标准等缺点渐渐暴露出来，尤其是在发行版本 0.9.4中，日志传输不稳定的现象尤为严重。

为了解决这些问题，2011 年 10 月 22 号，cloudera 对 Flume 进行了里程碑式的改动：重构核心组件、核心配置以及代码架构，并将 Flume 纳入 apache 旗下，从cloudera Flume 改名为 Apache Flume。

#### 1.3 版本区别

为了与之前版本区分开，重构后的版本统称为 **Flume NG（next generation）**，重构前的版本被统称为 **Flume OG（original generation）**，Flume目前只有Linux系统的启动脚本，没有Windows环境的启动脚本。

## 第二节：Flume的体系结构

#### 2.1 体系结构简介

Flume 运行的核心是 Agent。Flume是以agent为最小的独立运行单位。一个agent就是一个JVM。它是一个完整的数据收集工具，含有三个核心组件，分别是source、 channel、 sink。通过这些组件， Event 可以从一个地方流向另一个地方。如下图所示：

![](./flume-day01.assets/DevGuide_image00.png)

#### 2.2 组件及其作用

```
- Client：
	客户端，Client生产数据，运行在一个独立的线程中

- Event： 
	一个数据单元，消息头和消息体组成。（Events可以是日志记录、 avro 对象等。）

- Flow：
	Event从源点到达目的点的迁移的抽象。

- Agent： 
	一个独立的Flume进程，运行在JVM中，包含组件Source、 Channel、 Sink。
	每台机器运行一个agent，但是一个agent中可以包含多个sources和sinks。

- Source： 
	数据收集组件。source从Client收集数据，传递给Channel

- Channel： 
	管道，负责接收source端的数据，然后将数据推送到sink端。

- Sink： 
	负责从channel端拉取数据，并将其推送到持久化系统或者是下一个Agent。
	
- selector：
	选择器，作用于source端，然后决定数据发往哪个目标。

- interceptor：
	拦截器，flume允许使用拦截器拦截数据。允许使用拦截器链，作用于source和sink阶段。	
```

## 第三节：Flume的安装

#### 3.1 安装和配置环境变量

3.1.1 准备软件包

```
将apache-flume-1.6.0-bin.tar.gz 上传到linux系统中的/opt/software/目录中
```

3.1.2 解压软件包

```shell
[root@master software]# pwd
/opt/software
[root@master software]# tar -zxvf apache-flume-1.6.0-bin.tar.gz -C /opt/apps/
```

3.1.3 更名操作

```shell
[root@master software]# cd /opt/apps/
[root@master apps]# mv apache-flume-1.6.0-bin/ flume
```

3.1.4 配置环境变量，并source

```shell
[root@master apps]# vi /etc/profile
........省略..........
export FLUME_HOME=/usr/local/flume
export PATH=$FLUME_HOME/bin:$PATH

[root@master apps]# source /etc/profile
```

3.1.5 验证环境变量

```shell
[root@master apps]# flume-ng version
Flume 1.6.0
Source code repository: https://git-wip-us.apache.org/repos/asf/flume.git
Revision: 2561a23240a71ba20bf288c7c2cda88f443c2080
Compiled by hshreedharan on Mon May 11 11:15:44 PDT 2015
From source with checksum b29e416802ce9ece3269d34233baf43f
```

#### 3.2 配置文件

```shell
[root@master apps]# cd flume/conf/
[root@master conf]# ll	  #查看里面是否有一个flume-env.sh.template文件
[root@master conf]# cp flume-env.sh.template flume-env.sh
[root@master conf]# vi flume-env.sh
........省略..........
export JAVA_HOME=/opt/apps/jdk
........省略..........
```

## 第四节：Flume的部署

#### 4.1 数据模型

```
- 单一数据模型
- 多数据流模型
```

**4.1.1 单一数据模型**

在单个 Agent 内由单个 Source, Channel, Sink 建立一个单一的数据流模型，如下图所示，整个数据流为 Web Server --> Source --> Channel --> Sink --> HDFS。 

![](./flume-day01.assets/DevGuide_image00.png)



**4.1.2 多数据流模型**

**1）**多 Agent 串行传输数据流模型

![](./flume-day01.assets/UserGuide_image03.png)

**2）**多 Agent 汇聚数据流模型

![](./flume-day01.assets/UserGuide_image02.png)

**3）**单 Agent 多路数据流模型

![](./flume-day01.assets/UserGuide_image01.png)

**4）**Sinkgroups 数据流模型

![](./flume-day01.assets/20150424221424789.jpg)

**4.1.3 小总结**

```properties
在flume提供的数据流模型中，几个原则很重要。

Source--> Channel
  1.单个Source组件可以和多个Channel组合建立数据流，既可以replicating 和 multiplexing。
  2.多个Sources可以写入单个 Channel

Channel-->Sink
  1.多个Sinks又可以组合成Sinkgroups从Channel中获取数据，既可以loadbalancing和failover机制。
  2.多个Sinks也可以从单个Channel中取数据。
  3.单个Sink只能从单个Channel中取数据

根据上述 5 个原则，你可以设计出满足你需求的数据流模型。
```



#### 4.2 配置介绍

4.2.1 定义组件名称

 要定义单个代理中的流，您需要通过通道链接源和接收器。您需要列出给定代理的源，接收器和通道，然后将源和接收器指向一个通道。一个源实例可以指定多个通道，但是一个接收器实例只能指定一个通道。格式如下： 

```properties
# list the sources, sinks and channels for the agent
<Agent>.sources = <Source>
<Agent>.sinks = <Sink>
<Agent>.channels = <Channel1> <Channel2>

# set channel for source
<Agent>.sources.<Source>.channels = <Channel1> <Channel2> ...

# set channel for sink
<Agent>.sinks.<Sink>.channel = <Channel1>
```

案例如下：

```properties
# list the sources, sinks and channels for the agent
agent_foo.sources = avro-appserver-src-1
agent_foo.sinks = hdfs-sink-1
agent_foo.channels = mem-channel-1

# set channel for source
agent_foo.sources.avro-appserver-src-1.channels = mem-channel-1

# set channel for sink
agent_foo.sinks.hdfs-sink-1.channel = mem-channel-1
```

4.2.2 配置组件属性

```properties
# properties for sources
<Agent>.sources.<Source>.<someProperty> = <someValue>

# properties for channels
<Agent>.channel.<Channel>.<someProperty> = <someValue>

# properties for sinks
<Agent>.sources.<Sink>.<someProperty> = <someValue>
```

案例如下：

```properties
agent_foo.sources = avro-AppSrv-source
agent_foo.sinks = hdfs-Cluster1-sink
agent_foo.channels = mem-channel-1

# set channel for sources, sinks

# properties of avro-AppSrv-source
agent_foo.sources.avro-AppSrv-source.type = avro
agent_foo.sources.avro-AppSrv-source.bind = localhost
agent_foo.sources.avro-AppSrv-source.port = 10000

# properties of mem-channel-1
agent_foo.channels.mem-channel-1.type = memory
agent_foo.channels.mem-channel-1.capacity = 1000
agent_foo.channels.mem-channel-1.transactionCapacity = 100

# properties of hdfs-Cluster1-sink
agent_foo.sinks.hdfs-Cluster1-sink.type = hdfs
agent_foo.sinks.hdfs-Cluster1-sink.hdfs.path = hdfs://namenode/flume/webdata

#...
```

#### 4.3 常用的source和sink种类

```
参考flume文档
```

4.3.1 **常用的flume sources**

```shell
# Avro source：
	avro
# Syslog TCP source：
	syslogtcp
# Syslog UDP Source：
	syslogudp
# HTTP Source：
	http	
# Exec source：
	exec
# JMS source：
	jms
# Thrift source：
	thrift	
# Spooling directory source：
	spooldir
# Kafka source：
	org.apache.flume.source.kafka,KafkaSource
.....	
```

4.3.2 **常用的flume channels**

```shell
# Memory Channel
	memory
# JDBC Channel
	jdbc
# Kafka Channel
	org.apache.flume.channel.kafka.KafkaChannel
# File Channel
	file
```

4.3.3 **常用的flume sinks**

```shell
# HDFS Sink
	hdfs
# HIVE Sink
	hive
# Logger Sink
	logger
# Avro Sink
	avro
# Kafka Sink
	org.apache.flume.sink.kafka.KafkaSink
```

## 第五节：案例演示

#### 5.1 案例演示：avro+memory+logger

5.1.1 编写采集方案

```properties
[root@master flume]# mkdir flumeconf
[root@master flume]# cd flumeconf
[root@master flumeconf]# vi avro-logger.conf
a1.sources=avro-sour1
a1.sources.avro-sour1.type=avro
a1.sources.avro-sour1.bind=master
a1.sources.avro-sour1.port=9090
a1.sources.avro-sour1.channels=mem-chan1

a1.channels=mem-chan1
a1.channels.mem-chan1.type=memory


a1.sinks=logger-sink1
a1.sinks.logger-sink1.type=logger
a1.sinks.logger-sink1.channel=mem-chan1
a1.sinks.logger-sink1.maxBytesToLog=100
```

5.1.2 启动Agent

```shell
[root@master flumeconf]# flume-ng agent -c ../conf -f ./avro-logger.conf -n a1 -Dflume.root.logger=INFO,console
```

5.1.3 测试数据

```shell
[root@master ~]# mkdir flumedata
[root@master ~]# cd flumedata/
[root@master flumedata]#
[root@master flumedata]# date >> test.data
[root@master flumedata]# cat test.data
2019年 11月 21日 星期四 21:22:36 CST
[root@master flumedata]# ping master >> test.data
^C[root@master flumedata]# cat test.data
....省略....
[root@master flumedata]# flume-ng avro-client -c /usr/local/flume-1.6.0/conf/ -H hadoop0001 -p 6666 -F ./test.dat 
```

#### 5.2 案例演示：exec+memory+logger

5.2.1 配置方案

```properties
a2.sources = r1 
a2.channels = c1
a2.sinks = s1

a2.sources.r1.type = exec
a2.sources.r1.command = tail -f /home/flume/log.01

a2.channels.c1.type=memory
a2.channels.c1.capacity=1000
a2.channels.c1.transactionCapacity=100
a2.channels.c1.keep-alive=3
a2.channels.c1.byteCapacityBufferPercentage=20
a2.channels.c1.byteCapacity=800000

a2.sinks.s1.type=logger
a2.sinks.s1.maxBytesToLog=30

a2.sources.r1.channels=c1
a2.sinks.s1.channel=c1
```

5.2.2 启动agent

```properties
flume-ng agent -c ./conf -f ./conf/exec.conf -n a2 -Dflume.root.logger=INFO,console
```

5.2.3 测试：

```properties
echo "nice" >> /home/flume/log.01
```

#### 5.3 案例演示：exec+memory+hdfs

5.3.1 配置方案

```properties
[root@master flumeconf]# vi exec-hdfs.conf
a1.sources=r1
a1.sources.r1.type=exec
a1.sources.r1.command=tail -F /root/flumedata/test.data

a1.sinks=k1
a1.sinks.k1.type=hdfs
a1.sinks.k1.hdfs.path=hdfs://supercluster/flume/tailout/%y-%m-%d/%H%M/
a1.sinks.k1.hdfs.filePrefix=events-
a1.sinks.k1.hdfs.round=true
a1.sinks.k1.hdfs.roundValue=10
a1.sinks.k1.hdfs.roundUnit=second
a1.sinks.k1.hdfs.rollInterval=3
a1.sinks.k1.hdfs.rollSize=20
a1.sinks.k1.hdfs.rollCount=5
a1.sinks.k1.hdfs.batchSize=1
a1.sinks.k1.hdfs.useLocalTimeStamp=true
a1.sinks.k1.hdfs.fileType=DataStream


a1.channels=c1
a1.channels.c1.type=memory
a1.channels.c1.capacity=1000
a1.channels.c1.transactionCapacity=100

a1.sources.r1.channels=c1
a1.sinks.k1.channel=c1
```

5.3.2 启动Agent

```shell
[root@master flumeconf]# flume-ng agent -c ../conf -f ./exec-hdfs.conf -n a1 -Dflume.root.logger=INFO,console
```

5.3.3 测试数据

```shell
[root@master flumedata]# ping master >> test.data
```

#### 5.4 案例演示：spool +file + hdfs

```properties
[root@master flumeconf]# vi spool-hdfs.conf
a1.sources = r1
a1.channels = c1
a1.sinks = k1

a1.sources.r1.type = spooldir
a1.sources.r1.spoolDir = /home/flume/input/06/25


a1.channels.c1.type = file
a1.channels.c1.checkpointDir = /home/flume/checkpoint
a1.channels.c1.dataDirs = /home/flume/data


a1.sinks.k1.type = hdfs
a1.sinks.k1.hdfs.path = hdfs://supercluster/flume/spooldir
a1.sinks.k1.hdfs.filePrefix = 
a1.sinks.k1.hdfs.round = true
a1.sinks.k1.hdfs.roundValue = 10
a1.sinks.k1.hdfs.roundUnit = minute
a1.sinks.k1.hdfs.fileSuffix= .log
a1.sinks.k1.hdfs.rollInterval=60
a1.sinks.k1.hdfs.fileType=DataStream
a1.sinks.k1.hdfs.writeFormat=Text


a1.sources.r1.channels = c1
a1.sinks.s1.channel = c1
```

5.4.2 启动Agent

```properties
[root@master flumeconf]# flume-ng agent -c ../conf/ -f ./spool-hdfs.conf -n a1 -Dflume.root.logger=INFO,console
```

#### 5.5 案例演示：spool+ mem+logger 

监控目录需要被先创建

```properties
a1.sources = r1  
a1.channels = c1
a1.sinks = s1

a1.sources.r1.type=spooldir
a1.sources.r1.spoolDir = /home/flume/spool
a1.sources.r1.fileSuffix = .COMPLETED
a1.sources.r1.deletePolicy=never
a1.sources.r1.fileHeader=false
a1.sources.r1.fileHeaderKey=file
a1.sources.r1.basenameHeader=false
a1.sources.r1.basenameHeaderKey=basename
a1.sources.r1.batchSize=100
a1.sources.r1.inputCharset=UTF-8
a1.sources.r1.bufferMaxLines=1000

a1.channels.c1.type=memory
a1.channels.c1.capacity=1000
a1.channels.c1.transactionCapacity=100
a1.channels.c1.keep-alive=3
a1.channels.c1.byteCapacityBufferPercentage=20
a1.channels.c1.byteCapacity=800000

a1.sinks.s1.type=logger
a1.sinks.s1.maxBytesToLog=16

a1.sources.r1.channels=c1
a1.sinks.s1.channel=c1
```

5.5.2 启动agent

```properties
flume-ng agent -c ./conf -f ./conf/spool.conf -n a1 -Dflume.root.logger=INFO,console
```

5.5.3 测试：

```properties
for i in `seq 1 10` ; do echo $i >> /home/flume/spool/$i;done
```

#### 5.6 案例演示：http+ mem+logger

```properties
a1.sources = r1  
a1.channels = c1
a1.sinks = s1

a1.sources.r1.type=http
a1.sources.r1.bind = hadoop01
a1.sources.r1.port = 6666
a1.sources.r1.handler = org.example.rest.RestHandler
a1.sources.r1.handler.nickname = random props

a1.channels.c1.type=memory
a1.channels.c1.capacity=1000
a1.channels.c1.transactionCapacity=100
a1.channels.c1.keep-alive=3
a1.channels.c1.byteCapacityBufferPercentage=20
a1.channels.c1.byteCapacity=800000

a1.sinks.s1.type=logger
a1.sinks.s1.maxBytesToLog=16

a1.sources.r1.channels=c1
a1.sinks.s1.channel=c1
```

5.6.2 启动agent的服务：

```sh
flume-ng agent -c ./conf -f ./conf/http.conf -n a1 -Dflume.root.logger=INFO,console
```

5.6.3 测试：

```sh
curl -X POST -d '[{"headers":{"para1":"aaa","para2":"ccc"},"body":"this is my content"}]' http://hadoop01:6666
```

#### 5.7 案例演示：Syslogtcp+mem+logger 

```properties
a1.sources = r1

a1.channels = c1
a1.sinks = s1

a1.sources.r1.type=syslogtcp
a1.sources.r1.host = hadoop01
a1.sources.r1.port = 6666

a1.channels.c1.type=memory
a1.channels.c1.capacity=1000
a1.channels.c1.transactionCapacity=100
a1.channels.c1.keep-alive=3
a1.channels.c1.byteCapacityBufferPercentage=20
a1.channels.c1.byteCapacity=800000

a1.sinks.s1.type=logger
a1.sinks.s1.maxBytesToLog=16

a1.sources.r1.channels=c1
a1.sinks.s1.channel=c1
```

5.7.2 启动agent

```
flume-ng agent -c ./conf -f ./conf/systcp.conf -n a1 -Dflume.root.logger=INFO,console
```

5.7.3 测试：需要先安装nc

```
echo "hello world" | nc hadoop01 6666
```

#### 5.8 案例演示：Syslogtcp+mem+hdfs

```properties
a1.sources = r1  
a1.channels = c1
a1.sinks = s1

a1.sources.r1.type=syslogtcp
a1.sources.r1.host = hadoop01
a1.sources.r1.port = 6666

a1.channels.c1.type=memory
a1.channels.c1.capacity=1000
a1.channels.c1.transactionCapacity=100
a1.channels.c1.keep-alive=3
a1.channels.c1.byteCapacityBufferPercentage=20
a1.channels.c1.byteCapacity=800000

a1.sinks.s1.type=hdfs
a1.sinks.s1.hdfs.path=hdfs://hadoop01:9000/flume/%Y/%m/%d/%H%M
a1.sinks.s1.hdfs.filePrefix=flume-hdfs
a1.sinks.s1.hdfs.fileSuffix=.log
a1.sinks.s1.hdfs.inUseSuffix=.tmp
a1.sinks.s1.hdfs.rollInterval=60
a1.sinks.s1.hdfs.rollSize=1024
a1.sinks.s1.hdfs.rollCount=10
a1.sinks.s1.hdfs.idleTimeout=0
a1.sinks.s1.hdfs.batchSize=100
a1.sinks.s1.hdfs.fileType=DataStream
a1.sinks.s1.hdfs.writeFormat=Text
a1.sinks.s1.hdfs.round=true
a1.sinks.s1.hdfs.roundValue=1
a1.sinks.s1.hdfs.roundUnit=second
a1.sinks.s1.hdfs.useLocalTimeStamp=false

a1.sources.r1.channels=c1
a1.sinks.s1.channel=c1
```

启动agent的服务：

```sh
flume-ng agent -c ./conf -f ./conf/hdfs.conf -n a1 -Dflume.root.logger=INFO,console
```

测试：

```sh
echo "hello world hello qiangeng" | nc hadoop01 6666
```

#### 5.9 案例演示：Syslogtcp+file+hdfs

```properties
a1.sources = r1  
a1.channels = c1
a1.sinks = s1

a1.sources.r1.type=syslogtcp
a1.sources.r1.host = hadoop01
a1.sources.r1.port = 6666

a1.channels.c1.type=file
a1.channels.c1.dataDirs=/home/flume/filechannel/data
a1.channels.c1.checkpointDir=/home/flume/filechannel/point
a1.channels.c1.transactionCapacity=10000
a1.channels.c1.checkpointInterval=30000
a1.channels.c1.capacity=1000000
a1.channels.c1.keep-alive=3

a1.sinks.s1.type=hdfs
a1.sinks.s1.hdfs.path=hdfs://hadoop01:9000/flume/%Y/%m/%d/%H%M
a1.sinks.s1.hdfs.filePrefix=flume-hdfs
a1.sinks.s1.hdfs.fileSuffix=.log
a1.sinks.s1.hdfs.inUseSuffix=.tmp
a1.sinks.s1.hdfs.rollInterval=60
a1.sinks.s1.hdfs.rollSize=1024
a1.sinks.s1.hdfs.rollCount=10
a1.sinks.s1.hdfs.idleTimeout=0
a1.sinks.s1.hdfs.batchSize=100
a1.sinks.s1.hdfs.fileType=DataStream
a1.sinks.s1.hdfs.writeFormat=Text
a1.sinks.s1.hdfs.round=true
a1.sinks.s1.hdfs.roundValue=1
a1.sinks.s1.hdfs.roundUnit=second
a1.sinks.s1.hdfs.useLocalTimeStamp=true

a1.sources.r1.channels=c1
a1.sinks.s1.channel=c1
```

启动agent的服务：

```sh
flume-ng agent -c ./conf -f ./conf/fc.conf -n a1 -Dflume.root.logger=INFO,console
```

测试：

```sh
echo "hello world hello qiangeng" | nc hadoop01 6666
```

## 第六节：拦截器的使用

#### 6.1 常用拦截器：

```
Timestamp Interceptor
Host Interceptor
Static Interceptor
```

#### 6.2 案例演示：Timestamp+Syslogtcp+file+hdfs

```properties
a1.sources = r1  
a1.channels = c1
a1.sinks = s1

a1.sources.r1.type=syslogtcp
a1.sources.r1.host = hadoop01
a1.sources.r1.port = 6666
a1.sources.r1.interceptors=i1 i2 i3
a1.sources.r1.interceptors.i1.type=timestamp
a1.sources.r1.interceptors.i1.preserveExisting=false
a1.sources.r1.interceptors.i2.type=host
a1.sources.r1.interceptors.i2.preserveExisting=false
a1.sources.r1.interceptors.i2.useIP=true
a1.sources.r1.interceptors.i2.hostHeader=hostname
a1.sources.r1.interceptors.i3.type=static
a1.sources.r1.interceptors.i3.preserveExisting=false
a1.sources.r1.interceptors.i3.key=hn
a1.sources.r1.interceptors.i3.value=hadoop01

a1.channels.c1.type=memory
a1.channels.c1.capacity=1000
a1.channels.c1.transactionCapacity=100
a1.channels.c1.keep-alive=3
a1.channels.c1.byteCapacityBufferPercentage=20
a1.channels.c1.byteCapacity=800000

a1.sinks.s1.type=hdfs
a1.sinks.s1.hdfs.path=hdfs://hadoop01:9000/flume/%Y/%m/%d/%H%M
a1.sinks.s1.hdfs.filePrefix=%{hostname}
a1.sinks.s1.hdfs.fileSuffix=.log
a1.sinks.s1.hdfs.inUseSuffix=.tmp
a1.sinks.s1.hdfs.rollInterval=60
a1.sinks.s1.hdfs.rollSize=1024
a1.sinks.s1.hdfs.rollCount=10
a1.sinks.s1.hdfs.idleTimeout=0
a1.sinks.s1.hdfs.batchSize=100
a1.sinks.s1.hdfs.fileType=DataStream
a1.sinks.s1.hdfs.writeFormat=Text
a1.sinks.s1.hdfs.round=true
a1.sinks.s1.hdfs.roundValue=1
a1.sinks.s1.hdfs.roundUnit=second
a1.sinks.s1.hdfs.useLocalTimeStamp=false

a1.sources.r1.channels=c1
a1.sinks.s1.channel=c1
```

启动agent的服务：sh

```sh
flume-ng agent -c ./conf -f ./conf/ts.conf -n a1 -Dflume.root.logger=INFO,console
```

测试：

```sh
echo "hello world hello qiangeng" | nc hadoop01 6666
```

#### 6.3 案例演示：regex+Syslogtcp+file+hdfs

```properties
a1.sources = r1  
a1.channels = c1
a1.sinks = s1

a1.sources.r1.type=syslogtcp
a1.sources.r1.host = hadoop01
a1.sources.r1.port = 6666
a1.sources.r1.interceptors=i1
a1.sources.r1.interceptors.i1.type=regex_filter
a1.sources.r1.interceptors.i1.regex=^[0-9].*$  #不要加引号包裹正则
a1.sources.r1.interceptors.i1.excludeEvents=false


a1.channels.c1.type=memory
a1.channels.c1.capacity=1000
a1.channels.c1.transactionCapacity=100
a1.channels.c1.keep-alive=3
a1.channels.c1.byteCapacityBufferPercentage=20
a1.channels.c1.byteCapacity=800000

a1.sinks.s1.type=hdfs
a1.sinks.s1.hdfs.path=hdfs://hadoop01:9000/flume/%Y/%m/%d/%H%M
a1.sinks.s1.hdfs.filePrefix=%{hostname}
a1.sinks.s1.hdfs.fileSuffix=.log
a1.sinks.s1.hdfs.inUseSuffix=.tmp
a1.sinks.s1.hdfs.rollInterval=60
a1.sinks.s1.hdfs.rollSize=1024
a1.sinks.s1.hdfs.rollCount=10
a1.sinks.s1.hdfs.idleTimeout=0
a1.sinks.s1.hdfs.batchSize=100
a1.sinks.s1.hdfs.fileType=DataStream
a1.sinks.s1.hdfs.writeFormat=Text
a1.sinks.s1.hdfs.round=true
a1.sinks.s1.hdfs.roundValue=1
a1.sinks.s1.hdfs.roundUnit=second
a1.sinks.s1.hdfs.useLocalTimeStamp=false

a1.sources.r1.channels=c1
a1.sinks.s1.channel=c1
```

启动agent的服务：

```shell
flume-ng agent -c ./conf -f ./conf/ts.conf -n a1 -Dflume.root.logger=INFO,console
```

测试：

```shell
echo "hello world hello qiangeng" | nc hadoop01 6666
```

## 第七节：选择器的使用

#### 7.1 说明

```
selector：选择器
作用于source阶段，决定一条数据去往哪一个channel、sink
```

#### 7.2 案例演示：replicating selector 

```properties
a1.sources = r1  
a1.channels = c1 c2
a1.sinks = s1 s2

a1.sources.r1.type=syslogtcp
a1.sources.r1.host = hadoop01
a1.sources.r1.port = 6666
a1.sources.r1.selector.type=replicating


a1.channels.c1.type=memory
a1.channels.c1.capacity=1000
a1.channels.c1.transactionCapacity=100
a1.channels.c1.keep-alive=3
a1.channels.c1.byteCapacityBufferPercentage=20
a1.channels.c1.byteCapacity=800000

a1.channels.c2.type=memory
a1.channels.c2.capacity=1000
a1.channels.c2.transactionCapacity=100


a1.sinks.s1.type=hdfs
a1.sinks.s1.hdfs.path=hdfs://hadoop01:9000/flume/%Y/%m/%d/rep
a1.sinks.s1.hdfs.filePrefix=s1sink
a1.sinks.s1.hdfs.fileSuffix=.log
a1.sinks.s1.hdfs.inUseSuffix=.tmp
a1.sinks.s1.hdfs.rollInterval=60
a1.sinks.s1.hdfs.rollSize=1024
a1.sinks.s1.hdfs.rollCount=10
a1.sinks.s1.hdfs.idleTimeout=0
a1.sinks.s1.hdfs.batchSize=100
a1.sinks.s1.hdfs.fileType=DataStream
a1.sinks.s1.hdfs.writeFormat=Text
a1.sinks.s1.hdfs.round=true
a1.sinks.s1.hdfs.roundValue=1
a1.sinks.s1.hdfs.roundUnit=second
a1.sinks.s1.hdfs.useLocalTimeStamp=true

a1.sinks.s2.type=hdfs
a1.sinks.s2.hdfs.path=hdfs://hadoop01:9000/flume/%Y/%m/%d/rep
a1.sinks.s2.hdfs.filePrefix=s2sink
a1.sinks.s2.hdfs.fileSuffix=.log
a1.sinks.s2.hdfs.inUseSuffix=.tmp
a1.sinks.s2.hdfs.rollInterval=60
a1.sinks.s2.hdfs.rollSize=1024
a1.sinks.s2.hdfs.rollCount=10
a1.sinks.s2.hdfs.idleTimeout=0
a1.sinks.s2.hdfs.batchSize=100
a1.sinks.s2.hdfs.fileType=DataStream
a1.sinks.s2.hdfs.writeFormat=Text
a1.sinks.s2.hdfs.round=true
a1.sinks.s2.hdfs.roundValue=1
a1.sinks.s2.hdfs.roundUnit=second
a1.sinks.s2.hdfs.useLocalTimeStamp=true

a1.sources.r1.channels=c1 c2
a1.sinks.s1.channel=c1
a1.sinks.s2.channel=c2
```

启动agent的服务：

```shell
flume-ng agent -c ./conf -f ./conf/rep.conf -n a1 -Dflume.root.logger=INFO,console
```

测试：

```sh
echo "hello world hello qiangeng" | nc hadoop01 6666
```



#### 7.3 案例演示：Multiplexing selector 

```properties
a1.sources = r1  
a1.channels = c1 c2
a1.sinks = s1 s2

a1.sources.r1.type=http
a1.sources.r1.bind = hadoop01
a1.sources.r1.port = 6666
a1.sources.r1.selector.type=multiplexing
a1.sources.r1.selector.header = state
a1.sources.r1.selector.mapping.USER = c1
a1.sources.r1.selector.mapping.ORDER = c2
a1.sources.r1.selector.default = c1

a1.channels.c1.type=memory
a1.channels.c1.capacity=1000
a1.channels.c1.transactionCapacity=100
a1.channels.c1.keep-alive=3
a1.channels.c1.byteCapacityBufferPercentage=20
a1.channels.c1.byteCapacity=800000

a1.channels.c2.type=memory
a1.channels.c2.capacity=1000
a1.channels.c2.transactionCapacity=100


a1.sinks.s1.type=hdfs
a1.sinks.s1.hdfs.path=hdfs://hadoop01:9000/flume/%Y/%m/%d/mul
a1.sinks.s1.hdfs.filePrefix=s1sink
a1.sinks.s1.hdfs.fileSuffix=.log
a1.sinks.s1.hdfs.inUseSuffix=.tmp
a1.sinks.s1.hdfs.rollInterval=60
a1.sinks.s1.hdfs.rollSize=1024
a1.sinks.s1.hdfs.rollCount=10
a1.sinks.s1.hdfs.idleTimeout=0
a1.sinks.s1.hdfs.batchSize=100
a1.sinks.s1.hdfs.fileType=DataStream
a1.sinks.s1.hdfs.writeFormat=Text
a1.sinks.s1.hdfs.round=true
a1.sinks.s1.hdfs.roundValue=1
a1.sinks.s1.hdfs.roundUnit=second
a1.sinks.s1.hdfs.useLocalTimeStamp=true

a1.sinks.s2.type=hdfs
a1.sinks.s2.hdfs.path=hdfs://hadoop01:9000/flume/%Y/%m/%d/mul
a1.sinks.s2.hdfs.filePrefix=s2sink
a1.sinks.s2.hdfs.fileSuffix=.log
a1.sinks.s2.hdfs.inUseSuffix=.tmp
a1.sinks.s2.hdfs.rollInterval=60
a1.sinks.s2.hdfs.rollSize=1024
a1.sinks.s2.hdfs.rollCount=10
a1.sinks.s2.hdfs.idleTimeout=0
a1.sinks.s2.hdfs.batchSize=100
a1.sinks.s2.hdfs.fileType=DataStream
a1.sinks.s2.hdfs.writeFormat=Text
a1.sinks.s2.hdfs.round=true
a1.sinks.s2.hdfs.roundValue=1
a1.sinks.s2.hdfs.roundUnit=second
a1.sinks.s2.hdfs.useLocalTimeStamp=true

a1.sources.r1.channels=c1 c2
a1.sinks.s1.channel=c1
a1.sinks.s2.channel=c2
```

启动agent的服务：

```shell
flume-ng agent -c ./conf -f ./conf/mul.conf -n a1 -Dflume.root.logger=INFO,console
```

测试：

```shell
curl -X POST -d '[{"headers":{"state":"ORDER"},"body":"this my multiplex to c2"}]' http://hadoop01:6666
curl -X POST -d '[{"headers":{"state":"ORDER"},"body":"this is my content"}]' http://hadoop01:6666
```

#### 7.4 flume的集群

其实slave配置差不多。

配置192.168.216.121：

```properties
a1.sources=r1
a1.channels=c1
a1.sinks=s1

a1.sources.r1.type=syslogtcp
a1.sources.r1.host=192.168.216.121
a1.sources.r1.port=6666

a1.channels.c1.type=memory

a1.sinks.s1.type=avro
a1.sinks.s1.hostname=192.168.216.123
a1.sinks.s1.port=6666

a1.sources.r1.channels=c1
a1.sinks.s1.channel=c1
```

配置192.168.216.122：

```properties
a1.sources=r1
a1.channels=c1
a1.sinks=s1

a1.sources.r1.type=http
a1.sources.r1.bind=192.168.216.122
a1.sources.r1.port=6666

a1.channels.c1.type=memory

a1.sinks.s1.type=avro
a1.sinks.s1.hostname=192.168.216.123
a1.sinks.s1.port=6666

a1.sources.r1.channels=c1
a1.sinks.s1.channel=c1
```

配置192.168.216.123：

```properties
a1.sources=r1
a1.channels=c1
a1.sinks=s1

a1.sources.r1.type=avro
a1.sources.r1.bind=192.168.216.123
a1.sources.r1.port=6666

a1.channels.c1.type=memory

a1.sinks.s1.type=logger

a1.sources.r1.channels=c1
a1.sinks.s1.channel=c1
```

启动master：

```shell
flume-ng agent -c ./conf -f ./conf/master.conf -n a1 -Dflume.root.logger=INFO,console
```

再启动slave：

```shell
flume-ng agent -c ./conf -f ./conf/slave1.conf -n a1 -Dflume.root.logger=INFO,console
flume-ng agent -c ./conf -f ./conf/slave2.conf -n a1 -Dflume.root.logger=INFO,console
```

测试：

```properties
echo "i am slave1" | nc 192.168.216.121 6666
curl -X POST -d '[{"headers":{"param1":"kkk"},"body":"i am slave2"}]' http://192.168.216.122:6666
```

