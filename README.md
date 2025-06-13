### 基于springboot3 + netty 实现的聊天工具 

#### 1. 框架使用 

使用springboot3 + netty + redis + rocketmq + mysql 搭建的聊天后端应用，netty + ws支持消息后端推送；redis作为单节点存储用户id map netty会话id,以及缓存系统；为了支持多实例部署，用rocketmq进行消息投递，首先用户id进行hash到不同实例，支持单聊准确服务投递，群聊多实例投递，根据netty会话多实例广播，每个实例再根据当前存在的用户会话，发送消息到用户； mysql支持消息存储

#### 2. 系统功能

2.1 登录部分

2.2 好友添加 + 好友搜索

2.3 群聊添加 + 好友群聊邀请

2.4 单聊消息同步及未读消息标记 + 用户登录后拉取所有未读消息（单聊未读消息拉取）

2.5 群聊消息同步 + 根据群聊拉取最后一条消息id之后的所有消息（群未读消息拉取）

#### 3.主要设计特点

3.1 考虑水平部署如何保持ws会话的问题

1. 单台实例的netty + ws 的会话会有上限，怎么处理会话多的问题，思路是将不用用户根据id，hash到不同的实例上，这样会有两个问题

   a)、如何在建立会话的时候就转发请求到指定实例上，这样能很大程度确保每个实例的数量是平分了用户数的，解决方案： openresty nginx 基于lua脚本实现自定义实例映射； openresty的安装：本次开发基于windwos， 直接访问下载：https://openresty.org/，查找对应系统released 版本； openresty 当前最新版使用v1.27.1.2，启动命令通nginx，修改配置文件后直接启动

   b)、如何在发送消息的时候，找到接收人所在的实例上，解决方案：根据nginx层实例映射的规则，将ip+port映射值，配置为rocketmq的tag，rocketmq在发送消息的时候就判断接收人所在tag，直接发送到对应的实例进行消费

3.2 针对多人数在线压力处理，可以水平多实例部署，单聊和群聊的时候消息发送处理

1. 服务器的压力主要涉及单聊和群聊消息的发送，其他接口都是正常的http发送，也就是不区分实例的；单聊的话，服务器要想推送消息到接收人上面，必须找到绑定netty + ws会话的实力上，最初设想的是将所有netty + ws的会话id存到redis中，这样每次接收人的id取缓存中找对应的实例，然后还需要将（消息推送）请求发送到指定的实例上，进行操作；这样有一些不好的地方，缓存首先会存在单点故障，丢失大量的会话信息，在一个每次单聊发送消息，都要走一次远程获取缓存；为了优化，改为使用缓存存储实例信息（ip+port）映射实例（部署）的数，例如部署了172.0.1.1:9001，这时实例数为1，继续部署172.0.1.2:9001, 这时实例数是2，这样两个实例映射保存到redis中，然后根据接收人id%实例数量 + 1，找到对应的实例上；具体怎么发送消息，需要借助rocketmq，这样单聊的时候，一个接收人，我们在实例创建时候，注入消费对象，topic:实力数，这样可以根据topic:tag进行指定路由，然后消费端推送到接收人上；群聊只是接收人变成所有群成员，在生成端查询了所有成员，然后取模实例数后，分组到不同到tag上，这样就完成了群聊；

2. 部署的时候有些难点，暂未解决：

   主要是缓存实例和tag的映射关系，必须所有实例正确部署，且tag映射为1,2,3...自增才行，服务在spring上下文关闭的时候，也做了清理映射关系，但是仍有可能映射关系保留了旧的数据，在实例启动完后，应该检查每个实例的映射是否重复，是否正确，这样才能保证消息的路由是正确的；这个可以在一开始进行环境配置，将不同的ip+port 映射指定的tag上，这样在示例创建的时候，以及网关配置的时候，直接固定配置读取即可

3. rocketmq订阅消息的时候，需要注意，订阅关系一致性，消息是根据topic和tag进行区分，但是相同消费组订阅的topic+tag，以及消费逻辑是要保持一致的，所以我们要根据消息路由到一个topic下不同的tag，需要用不同的消费组进行订阅；具体可参考官方文档:https://rocketmq.apache.org/zh/docs/4.x/bestPractice/07subscribe/

4. 以下简单做了单聊和群聊的消息路由示例图

   ![image-create](https://github.com/liftp/chat_simple/blob/main/image/image-create.png)

   ![image-send-msg](https://github.com/liftp/chat_simple/blob/main/image/image-send-msg.png)

   ![image-flow](https://github.com/liftp/chat_simple/blob/main/image/image-flow.png)

3.3 针对消息存储，及未读消息做了以下处理

1. 单聊和群聊时候，都是在生产端保存了消息，并返回消息id,这样客户端就知道本次消息的id, 而且数据库涉及为自增，后续可以根据id排序，拉取客户端最新id之后的所有消息

#### 4. 数据库表

4.1. 见项目chat.sql

#### 5.后端部署

```shell
# 先编译项目, 前置需要了jdk17 maven环境,再项目根目录下
maven install -DskipTests
# 使用Dockerfile构建
docker build -t chat/chat_simple:1.0.0 .
# 注意项目下的openresty_nginx_conf/nginx.conf, 修改ws代理路径为，docker宿主容器的ip，这样就可以直接运行docker compose
# 直接使用docker compose 启动,后面加-d参数，后台运行
docker compose up 
# 启动完成后，前端项目运行，需要修改对应vite的代理请求路径
```



#### 6. 存在缺陷

6.1 缺少头像展示， 缺少用户添加，及个人备注修改功能

6.2 暂未增加通知类消息推送（如群聊成员添加消息推送及展示）

6.3 暂未实现压力测试，只是基于设计思想进行了实现

#### 7. 后续可能追加实现

7.1 文件传输； 语音发送；安卓端聊天支持（可能使用react native）实现

#### 8. 前端框架

8.1 前端使用electron + vue3 + nedb 等实现桌面端应用，windows作为开发系统，mac 系统暂未测试，具体前端项目详见：

[基于electron + vue3 + nedb的前端聊天]: https://github.com/liftp/chat_front


