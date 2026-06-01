# Getting Started

<cite>
**Referenced Files in This Document**
- [pom.xml](file://pom.xml)
- [application.yml](file://src/main/resources/application.yml)
- [chat.sql](file://db/chat.sql)
- [Dockerfile](file://Dockerfile)
- [docker-compose.yml](file://docker-compose.yml)
- [ChatSimpleApplication.java](file://src/main/java/com/hch/chat_simple/ChatSimpleApplication.java)
- [WebMvcConfig.java](file://src/main/java/com/hch/chat_simple/config/WebMvcConfig.java)
- [MinioConfig.java](file://src/main/java/com/hch/chat_simple/config/MinioConfig.java)
- [MqProducerConfig.java](file://src/main/java/com/hch/chat_simple/mq/MqProducerConfig.java)
- [RedisUtil.java](file://src/main/java/com/hch/chat_simple/util/RedisUtil.java)
- [MinIOUtil.java](file://src/main/java/com/hch/chat_simple/util/MinIOUtil.java)
- [UserOpController.java](file://src/main/java/com/hch/chat_simple/controller/UserOpController.java)
- [ChatMsgController.java](file://src/main/java/com/hch/chat_simple/controller/ChatMsgController.java)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Installation and Setup](#installation-and-setup)
4. [Database Setup](#database-setup)
5. [Environment Configuration](#environment-configuration)
6. [Docker Deployment](#docker-deployment)
7. [First Run](#first-run)
8. [Quick Start Examples](#quick-start-examples)
9. [Troubleshooting](#troubleshooting)
10. [Conclusion](#conclusion)

## Introduction
Chat Simple is a modern chat application built with Spring Boot 3.3.6, Java 17, and designed for scalable real-time messaging. It integrates RocketMQ for asynchronous messaging, Redis for caching and session management, MySQL for persistent data storage, and MinIO for object storage. The application provides REST APIs for user management, chat messaging, friend relationships, and group communications.

## Prerequisites
Before installing Chat Simple, ensure your system meets the following requirements:

- **Java 17**: Required for building and running the application
- **Apache Maven**: Build tool for compiling the project
- **MySQL 5.7**: Database server for persistent data storage
- **RocketMQ 5.0.0**: Message queue for asynchronous operations
- **Redis**: In-memory data structure store for caching and session management
- **MinIO**: Object storage compatible with AWS S3 API

**Section sources**
- [pom.xml:29-34](file://pom.xml#L29-L34)
- [pom.xml:74-125](file://pom.xml#L74-L125)
- [pom.xml:127-153](file://pom.xml#L127-L153)
- [pom.xml:288-291](file://pom.xml#L288-L291)
- [pom.xml:312-315](file://pom.xml#L312-L315)

## Installation and Setup

### Install Java 17
1. Download and install OpenJDK 17 from the official Oracle website or adoptium.net
2. Verify installation: `java -version`
3. Set JAVA_HOME environment variable to point to your JDK 17 installation

### Install Apache Maven
1. Download Maven from https://maven.apache.org/download.cgi
2. Extract to a directory and add to PATH
3. Verify installation: `mvn -version`

### Install MySQL 5.7
1. Download MySQL Community Server 5.7
2. Install with default settings
3. Create database and user:
   ```sql
   CREATE DATABASE chat DEFAULT CHARACTER SET utf8mb4;
   CREATE USER 'chat_user'@'localhost' IDENTIFIED BY 'secure_password';
   GRANT ALL PRIVILEGES ON chat.* TO 'chat_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

### Install RocketMQ 5.0.0
1. Download RocketMQ 5.0.0 from Apache RocketMQ releases
2. Extract and navigate to the extracted directory
3. Start NameServer:
   ```bash
   nohup sh bin/mqnamesrv &
   ```
4. Start Broker:
   ```bash
   nohup sh bin/mqbroker -n localhost:9876 &
   ```
5. Verify installation:
   ```bash
   telnet localhost 9876
   ```

### Install Redis
1. Download and install Redis from https://redis.io/download/
2. Start Redis server:
   ```bash
   redis-server
   ```
3. Verify installation:
   ```bash
   redis-cli ping
   ```

### Install MinIO
1. Download MinIO server from https://min.io/download
2. Start MinIO server:
   ```bash
   ./minio server /data --console-address ":9001"
   ```
3. Access MinIO console at http://localhost:9001
4. Create bucket named "chat" and set anonymous read permissions

**Section sources**
- [docker-compose.yml:64-72](file://docker-compose.yml#L64-L72)
- [docker-compose.yml:81-96](file://docker-compose.yml#L81-L96)
- [docker-compose.yml:97-115](file://docker-compose.yml#L97-L115)

## Database Setup
The application requires a MySQL database with predefined schema and tables. Follow these steps:

### Create Database Schema
1. Connect to MySQL as root user
2. Execute the SQL script located at `db/chat.sql`:
   ```sql
   mysql -u root -p chat < db/chat.sql
   ```

### Database Schema Overview
The application creates the following tables:
- **user**: Stores user account information
- **friend_relationship**: Manages user friendships
- **chat_msg**: Contains chat messages and notifications
- **group_info**: Stores group chat information
- **group_member**: Tracks group membership
- **apply_friend**: Handles friendship requests

### Initial Data Loading
The schema script automatically creates the database structure. You can add initial users through the application's REST API endpoints.

**Section sources**
- [chat.sql:1-130](file://db/chat.sql#L1-L130)

## Environment Configuration
Configure the application by editing `src/main/resources/application.yml`. The configuration includes database connections, message queues, Redis settings, and MinIO integration.

### Database Configuration
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/chat?autoReconnect=true&useUnicode=true&characterEncoding=utf8&useSSL=false&rewriteBatchedStatements=true
    username: root
    password: 123456
```

### Redis Configuration
```yaml
spring:
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      database: 0
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
```

### RocketMQ Configuration
```yaml
rocketmq:
  name-server: 127.0.0.1:9876
  producer:
    group: async-producer-group
  consumer:
    group: async-consumer-group
    single: async-consumer-single
    composition: async-consumer-composition
mq:
  topic:
    multi-chat: muilt-chat
    single-chat: single-chat
    composition: composition
```

### MinIO Configuration
```yaml
minio:
  accessKey: minio
  secretKey: minio123
  bucketName: chat
  secure: false
  url: http://localhost:9000/
```

### Application Ports
```yaml
server: 
  port: 9001
chat:
  server:
    port: 7891
```

**Section sources**
- [application.yml:1-89](file://src/main/resources/application.yml#L1-L89)

## Docker Deployment
The application provides Docker support for containerized deployment with orchestration via docker-compose.

### Single Container Deployment
Build the application jar:
```bash
./mvnw clean package -DskipTests
```

Create Docker image:
```bash
docker build -t chat-simple:1.0.0 .
```

Run single container:
```bash
docker run -d \
  --name chat-simple \
  -p 9001:9001 \
  -p 7891:7891 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/chat \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e SPRING_DATA_REDIS_HOST=host.docker.internal \
  -e ROCKETMQ_NAME_SERVER=host.docker.internal:9876 \
  -e MINIO_URL=http://host.docker.internal:9000 \
  chat-simple:1.0.0
```

### Multi-Container Deployment with Compose
The docker-compose.yml file orchestrates all required services:

```yaml
version: '3'
services:
  openresty:
    image: openresty/openresty
    ports: 
      - "8001:80"
    depends_on:
      - chatsimple_1
      - chatsimple_2
      - chatsimple_3
  
  chatsimple_1:
    image: chat-simple:1.0.0
    ports:
      - "9001:9001"
      - "7891:7891"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/chat
      - SPRING_DATASOURCE_PASSWORD=root
      - SPRING_DATA_REDIS_HOST=redis
      - CHAT_TAG_CURRENT=chat_a
      - MINIO_URL=http://minio:9000/
      - ROCKETMQ_NAME_SERVER=namesrv:9876
    depends_on:
      - db
      - redis
      - broker
      - minio
  
  db:
    image: mysql:5.7
    environment:
      MYSQL_ROOT_PASSWORD: root
    volumes: 
      - ./db/chat.sql/:/docker-entrypoint-initdb.d/init.sql
    ports: 
      - "3306:3306"
  
  redis: 
    image: redis:latest
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
  
  namesrv:
    image: apache/rocketmq:5.0.0
    ports:
      - 9876:9876
    command: sh mqnamesrv
  
  broker:
    image: apache/rocketmq:5.0.0
    ports:
      - 10909:10909
      - 10911:10911
    environment:
      NAMESRV_ADDR: namesrv:9876
    depends_on:
      - namesrv
    command: sh mqbroker -n namesrv:9876 
  
  minio:
    image: minio/minio
    ports: 
      - 10000:9000
      - 10001:9001
    environment:
      - MINIO_ROOT_USER=minio
      - MINIO_ROOT_PASSWORD=minio123
    volumes:
      - ./data:/data
    command: server --console-address ':9001' /data
  
  mc:
    image: minio/mc
    depends_on:
      - minio
    entrypoint: >
      /bin/sh -c "
      sleep 10;
      /usr/bin/mc alias set myminio http://minio:9000 minio minio123;
      /usr/bin/mc mb myminio/chat;
      /usr/bin/mc anonymous set download myminio/chat;
      exit 0;
      "
```

**Section sources**
- [Dockerfile:1-12](file://Dockerfile#L1-L12)
- [docker-compose.yml:1-132](file://docker-compose.yml#L1-L132)

## First Run
After setting up all dependencies, start the application:

### Local Development
1. Ensure all services are running (MySQL, Redis, RocketMQ, MinIO)
2. Start the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Access the API documentation at http://localhost:9001/doc.html

### Docker Compose
1. Start all services:
   ```bash
   docker-compose up -d
   ```
2. Monitor logs:
   ```bash
   docker-compose logs -f
   ```
3. Access the application at http://localhost:9001

### Verification Steps
1. Check database connectivity:
   ```bash
   curl http://localhost:9001/user/searchUser -H "Content-Type: application/json" -d '{"username":"admin"}'
   ```
2. Verify message queue:
   ```bash
   curl http://localhost:9001/chatMsg/selectNotReadMsg
   ```
3. Test file upload:
   ```bash
   curl -X POST -F "file=@test.txt" http://localhost:9001/file/upload
   ```

**Section sources**
- [ChatSimpleApplication.java:16-22](file://src/main/java/com/hch/chat_simple/ChatSimpleApplication.java#L16-L22)
- [WebMvcConfig.java:8-27](file://src/main/java/com/hch/chat_simple/config/WebMvcConfig.java#L8-L27)

## Quick Start Examples
Test the basic functionality with these examples:

### User Registration
1. Register a new user:
   ```bash
   curl -X POST http://localhost:9001/user/insertUser \
     -H "Content-Type: application/json" \
     -d '{
       "username": "john_doe",
       "password": "secure_password",
       "name": "John Doe"
     }'
   ```

2. Login to get token:
   ```bash
   curl -X POST http://localhost:9001/user/login \
     -H "Content-Type: application/json" \
     -d '{
       "username": "john_doe",
       "password": "secure_password"
     }'
   ```

### Send Chat Message
1. Send a direct message:
   ```bash
   curl -X POST http://localhost:9001/chatMsg/sendMsg \
     -H "Content-Type: application/json" \
     -d '{
       "msgType": 2,
       "chatType": 0,
       "sendUserId": 1,
       "receiveUserId": 2,
       "content": "Hello there!",
       "contentType": 1
     }'
   ```

2. Check unread messages:
   ```bash
   curl -X POST http://localhost:9001/chatMsg/selectNotReadMsg
   ```

### Upload File
1. Upload a file to MinIO:
   ```bash
   curl -X POST -F "file=@document.pdf" http://localhost:9001/file/upload
   ```

2. Get file URL:
   ```bash
   curl -X GET http://localhost:9001/file/preview/{filename}
   ```

**Section sources**
- [UserOpController.java:106-111](file://src/main/java/com/hch/chat_simple/controller/UserOpController.java#L106-L111)
- [ChatMsgController.java:45-49](file://src/main/java/com/hch/chat_simple/controller/ChatMsgController.java#L45-L49)
- [MinIOUtil.java:98-122](file://src/main/java/com/hch/chat_simple/util/MinIOUtil.java#L98-L122)

## Troubleshooting

### Common Issues and Solutions

#### Database Connection Problems
**Issue**: Application cannot connect to MySQL
**Solution**:
1. Verify MySQL is running: `mysqladmin ping`
2. Check connection string in application.yml
3. Ensure database exists and credentials are correct
4. Test connection manually: `mysql -u root -p chat`

#### RocketMQ Connection Issues
**Issue**: Application cannot connect to RocketMQ
**Solution**:
1. Verify NameServer is running: `telnet localhost 9876`
2. Check RocketMQ version compatibility
3. Verify name-server address in application.yml
4. Restart RocketMQ services if needed

#### Redis Connectivity Problems
**Issue**: Redis connection refused
**Solution**:
1. Check Redis server status: `redis-cli ping`
2. Verify Redis host and port configuration
3. Check firewall settings
4. Ensure Redis has sufficient memory

#### MinIO Storage Issues
**Issue**: File upload fails to MinIO
**Solution**:
1. Verify MinIO server is running: `curl http://localhost:9000/minio/health/live`
2. Check bucket existence: `mc ls myminio/chat`
3. Verify bucket permissions are set to anonymous download
4. Check MinIO endpoint configuration

#### Port Conflicts
**Issue**: Application fails to start due to port conflicts
**Solution**:
1. Change server.port in application.yml (default 9001)
2. Modify chat.server.port for WebSocket (default 7891)
3. Update Docker port mappings accordingly

#### CORS Issues
**Issue**: Frontend cannot access API endpoints
**Solution**:
1. Verify CORS interceptor is configured in WebMvcConfig
2. Check allowed origins and methods
3. Ensure preflight requests are handled correctly

**Section sources**
- [WebMvcConfig.java:12-16](file://src/main/java/com/hch/chat_simple/config/WebMvcConfig.java#L12-L16)
- [MqProducerConfig.java:22-28](file://src/main/java/com/hch/chat_simple/mq/MqProducerConfig.java#L22-L28)
- [RedisUtil.java:19-26](file://src/main/java/com/hch/chat_simple/util/RedisUtil.java#L19-L26)
- [MinioConfig.java:28-31](file://src/main/java/com/hch/chat_simple/config/MinioConfig.java#L28-L31)

## Conclusion
Chat Simple provides a comprehensive foundation for building real-time chat applications with modern technologies. The application demonstrates best practices for microservice architecture, asynchronous messaging, and distributed systems. By following this guide, you can successfully deploy Chat Simple in development or production environments using either traditional deployment or containerized approaches.

Key benefits of the Chat Simple architecture include:
- Scalable message queuing with RocketMQ
- Efficient caching with Redis
- Reliable persistence with MySQL
- Flexible file storage with MinIO
- Container-ready deployment with Docker
- Comprehensive API documentation with Swagger

For production deployments, consider adding load balancers, monitoring solutions, and additional security measures as outlined in the Docker Compose configuration.