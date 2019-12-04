ReadME

1. 欢迎来到rtw的世界，在这个世界中拥有五颗无限宝石，其中有一颗无限宝石就称之为RPC宝石。
2. 该宝石具有无限空间的能力，可以通过一个名叫Internet的魔法将共计传送到千里之外。
3. 掌握它，你就成为了一名 ==奇异博士==。





# 介绍

1. 项目使用技术：
   - 网络通讯依赖于Netty神器。
   - 分布式选举节点依赖于Zookeeper棋盘。因为ZK棋盘过于强大，只能借助于Curator外挂进行包装掌握。
   - 主结构依赖于SpringBoot牛皮糖。
2. 整个项目共有四个模块：
   - rtw-rpc-core： 核心模块，外部引用只需要引用该包，就可能称为RPC的提供者或者消费者
   - rtw-rpc-provide-api：服务端打包出来的API包，堆外提供RPC接口。
   - rtw-rpc-consumer：消费者Demo
   - rtw-rpc-web：提供者Demo



## 启动

1. rtw_空间宝石遵循新手村规则，简单易用，因此只需达到初级魔法师的等级就可以拿上该无级别橙装。

2. GIT下载至本地。

3. 本地安装ZK，并且在ZK创建路径 /dubbo, 即Constants中的数值

4. 启动ZK

5. 运行rtw-rpc-web的MyRpcWebApplication#main()。待输出

   > nettyServer 开始监听1024端口

   - 表示服务端提供者启动成功

6. 运行rtw-rpc-consumer的MyRpcConsumerApplicationTests##contextLoads。如果看到日志有

   > 调用远程提供方成功, 返回为=当前时间

   - 表示提供端启动成功

7. 完成以上两步，表示你已经成功掌握rtw_空间宝石的初级力量。

   

   

##Hello Word!!!!!!

















