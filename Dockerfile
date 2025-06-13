FROM openjdk:17-jdk-slim

LABEL maintainer="1769412357@qq.com"

COPY target/chat_simple-0.0.1-SNAPSHOT.jar app.jar

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 这里可以改变端口，以及--chat.tag.list=["127.0.0.1:9001", "127.0.0.1:9002", ...], --chat.tag.local=false,两个配置来修改多实例启动
ENTRYPOINT ["java", "-jar", "/app.jar", "--server.port=9001", "--chat.server.port=7891"]