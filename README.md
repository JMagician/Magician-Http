<h1> 
    <a href="https://magician-io.com">Magician</a> ·
    <img src="https://img.shields.io/badge/licenes-MIT-brightgreen.svg"/>
    <img src="https://img.shields.io/badge/jdk-11+-brightgreen.svg"/>
    <img src="https://img.shields.io/badge/maven-3.5.4+-brightgreen.svg"/>
    <img src="https://img.shields.io/badge/release-master-brightgreen.svg"/>
</h1>

Magician 是一个基于Netty开发的小型 HTTP服务包，可以非常方便的启动一个http服务，同时也支持WebSocket，注解式Handler配置


## 运行环境

JDK11+

---

中央库的Jar包 最低支持JDK11，但是源码最低可以支持jdk8，如果您需要在8上运行，可以下载最新的tag，自行编译

## 文档

这个版本的文档还没出，尽请期待，不过可以跟着示例玩一下试试
[https://magician-io.com](https://magician-io.com)

## 示例

### 导入依赖
```xml
<dependency>
    <groupId>com.github.yuyenews</groupId>
    <artifactId>Magician</artifactId>
    <version>2.0</version>
</dependency>

<!-- 这是日志包，必须有，不然控制台看不到东西，支持任意可以看slf4j桥接的日志包 -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-jdk14</artifactId>
    <version>1.7.12</version>
</dependency>
```

### 创建http服务

创建一个 Handler

```java
@HttpHandler(path="/")
public class DemoHandler implements HttpBaseHandler {

    @Override
    public void request(MagicianRequest magicianRequest, MagicianResponse response) {
        // response data
        magicianRequest.getResponse()
                .sendJson(200, "{'status':'ok'}");
    }
}
```

创建http服务

```java
Magician.createHttp()
                    .scan("handler所在的包名")
                    .bind(8080);
```

### 创建 WebSocket
Just add a handler when creating the http service
```java
@WebSocketHandler(path = "/websocket")
public class DemoSocketHandler implements WebSocketBaseHandler {
   
    @Override
    public void onOpen(WebSocketSession webSocketSession) {
     
    }
   
    @Override
    public void onClose(WebSocketSession webSocketSession) {
        
    }

    @Override
    public void onMessage(String message, WebSocketSession webSocketSession) {

    }
}
```

## 更多组件

可以利用这些组件方便的开发web项目

[Magician-Web](https://github.com/yuyenews/Magician-Web) | 
[Magician-JDBC](https://github.com/yuyenews/Magician-JDBC)