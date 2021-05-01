<br/>

<div align=center>
<img width="260px;" src="http://magician-io.com/img/logo-black.png"/>
</div>

<br/>

<div align=center>

<img src="https://img.shields.io/badge/licenes-MIT-brightgreen.svg"/>
<img src="https://img.shields.io/badge/jdk-11+-brightgreen.svg"/>
<img src="https://img.shields.io/badge/maven-3.5.4+-brightgreen.svg"/>
<img src="https://img.shields.io/badge/release-master-brightgreen.svg"/>

</div>
<br/>

<div align=center>
一个异步非阻塞的网络协议解析包
</div>


## 项目简介

Magician 是一个异步非阻塞的网络协议解析包，支持Http, WebSocket, UDP等协议

## 运行环境

JDK11+

## 导入依赖
```xml
<dependency>
    <groupId>com.github.yuyenews</groupId>
    <artifactId>Magician</artifactId>
    <version>最新版</version>
</dependency>

<!-- 这个是日志包，支持任意可以跟slf4j桥接的包 -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-jdk14</artifactId>
    <version>1.7.12</version>
</dependency>
```

## 创建TCP服务(默认为http解码器)
### 创建Handler
```java
public class DemoHandler implements MagicianHandler<MagicianRequest> {

    @Override
    public void request(MagicianRequest magicianRequest) {
        // 响应数据
        magicianRequest.getResponse()
                .sendJson(200, "{'status':'ok'}");
    }
}
```

### 创建服务(默认线程池配置)
```java
Magician.createTCPServer()
                    .httpHandler("/", new DemoHandler())
                    .bind(8080);
```

### 创建服务(自定义线程池配置)
```java
EventGroup ioEventGroup = new EventGroup(1, Executors.newCachedThreadPool());
EventGroup workerEventGroup = new EventGroup(10, Executors.newCachedThreadPool());

// 当前EventRunner没任务的时候，允许从其他EventRunner窃取任务
workerEventGroup.setSteal(EventEnum.STEAL.YES);

Magician.createTCPServer(ioEventGroup, workerEventGroup)
                    .httpHandler("/", new DemoHandler())
                    .bind(8080);
                                                                            .bind(8080);
```

### 创建服务(监听多端口)
```java
// 监听几个端口，ioEventGroup的第一个参数就写几
EventGroup ioEventGroup = new EventGroup(2, Executors.newCachedThreadPool());
EventGroup workerEventGroup = new EventGroup(10, Executors.newCachedThreadPool());

// 当前EventRunner没任务的时候，允许从其他EventRunner窃取任务
workerEventGroup.setSteal(EventEnum.STEAL.YES);

TCPServer tcpServer = Magician
                         .createTCPServer(ioEventGroup, workerEventGroup)
                         .httpHandler("/", new DemoHandler())

tcpServer.bind(8080);
tcpServer.bind(8088);
```

## 创建WebSocket
只需要在创建http服务的时候加一个handler即可
```java
Magician.createTCPServer()
                    .httpHandler("/", new DemoHandler())
                    .webSocketHandler("/websocket", new DemoSocketHandler())
                    .bind(8080);
```

## 创建UDP服务
```java
Magician.createUdpServer()
                .handler(outputStream -> {
                    // outputStream 是ByteArrayOutputStream类型的
                    // 它是客户端发过来的数据，自行解析即可
                }).bind(8088);
```
除了这种写法，也可以单独创建handler，在这里add进去

## 开发资源
- 开发文档: [http://magician-io.com/docs/index.html](http://magician-io.com/docs/index.html)
- 使用示例: [https://github.com/yuyenews/Magician-Example](https://github.com/yuyenews/Magician-Example)