# Web服务脚手架
Java + SpringBoot + MySQL + Redis + ActiveMQ + Quartz + ...

# 一键部署Web服务
https://github.com/jextop/StarterDeploy

# 开发调试
## 依赖环境
JRE或者JDK 8

## 开发环境
IntelliJ IDEA, Maven

# 启动依赖服务
## 安装Docker
- https://docs.docker.com/install/linux/docker-ce/ubuntu/
- https://docs.docker.com/docker-for-windows/install/

## 拉取镜像
./pull.sh

## 启动服务
./up.sh

## 查看日志
./logs.sh

## 停止服务
./down.sh

# 服务信息

| 开发运行环境     | URL:Port                                |  备注              |
| ------------     | --------------------------------------  | :----------------- |
| MySQL数据库      | http://localhost:3306, root/root        | |
| MySQL Admin      | http://localhost:3006                   | |
| Redis缓存        | http://localhost:6379                   | |
| ActiveMQ消息队列 | http://localhost:8161, admin/admin      | launchActiveMQ.bat |

| 管理工具         | URL:Port                                |  备注              |
| ------------     | --------------------------------------  | :----------------- |
| API服务检查      | http://localhost:8011/chk               | |
| Swagger接口文档  | http://localhost:8011/swagger-ui.html   | |
| Actuator服务监控 | http://localhost:8011/actuator          | |

![](https://github.com/jextop/StarterApi/blob/master/img/architect.png)
![](https://github.com/jextop/StarterApi/blob/master/img/postman.png)
![](https://github.com/jextop/StarterApi/blob/master/img/swagger.png)
