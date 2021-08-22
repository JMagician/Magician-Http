<br/>

<div align=center>
<img width="260px;" src="https://user-images.githubusercontent.com/39583360/127732354-23e3dfbf-de92-450a-890b-2b491de30fd8.png"/>
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
An asynchronous non-blocking network protocol analysis package
</div>


## Project Description

Magician is an asynchronous non-blocking network protocol analysis package, supports TCP, UDP protocol, built-in Http, WebSocket decoder

## Run environment

JDK11+

---

If you want to use it on a lower version of the JDK, you can download the source code of this repository and compile it yourself.

## Import dependencies
```xml
<dependency>
    <groupId>com.github.yuyenews</groupId>
    <artifactId>Magician</artifactId>
    <version>last version</version>
</dependency>

<!-- This is the log package, which supports any package that can be bridged with slf4j -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-jdk14</artifactId>
    <version>1.7.12</version>
</dependency>
```

## 1. create a TCP service (using http decoder by default)
### Create Handler
```java
@TCPHandler(path="/")
public class DemoHandler implements TCPBaseHandler<MagicianRequest> {

    @Override
    public void request(MagicianRequest magicianRequest) {
        // response data
        magicianRequest.getResponse()
                .sendJson(200, "{'status':'ok'}");
    }
}
```

### Create TCP Server (Default thread pool configuration)
```java
Magician.createTCPServer()
                    .scan("The package name of the handler")
                    .bind(8080);
```

### Create TCP Server (custom thread pool configuration)
```java
EventGroup ioEventGroup = new EventGroup(1, Executors.newCachedThreadPool());
EventGroup workerEventGroup = new EventGroup(10, Executors.newCachedThreadPool());

// When the current EventRunner has no tasks, it is allowed to steal tasks from other EventRunners
workerEventGroup.setSteal(EventEnum.STEAL.YES);

Magician.createTCPServer(ioEventGroup, workerEventGroup)
                    .scan("The package name of the handler")
                    .bind(8080);
```

### Create TCP Server (Listen on multiple ports)
```java
// Listen to n ports, write n as the first parameter of ioEventGroup
EventGroup ioEventGroup = new EventGroup(2, Executors.newCachedThreadPool());
EventGroup workerEventGroup = new EventGroup(10, Executors.newCachedThreadPool());

// When the current EventRunner has no tasks, it is allowed to steal tasks from other EventRunners
workerEventGroup.setSteal(EventEnum.STEAL.YES);

TCPServer tcpServer = Magician.createTCPServer(ioEventGroup, workerEventGroup)
                         .scan("The package name of the handler")

tcpServer.bind(8080);
tcpServer.bind(8088);
```

## 2. Create WebSocket
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

## 3. Create UDP Server

### Create Handler
```java
@UDPHandler
public class DemoUDPHandler implements UDPBaseHandler {

    @Override
    public void receive(ByteArrayOutputStream byteArrayOutputStream) {

    }
}
```

### Create UDP Server
```java
Magician.createUdpServer()
                .scan("The package name of the handler")
                .bind(8088);
```

## More components

Use these components to easily develop web projects

![image](https://user-images.githubusercontent.com/39583360/127818314-350cb46b-4103-4b09-9722-dad38633cddd.png)

[Magician-Web](https://github.com/yuyenews/Magician-Web) | 
[Magician-JDBC](https://github.com/yuyenews/Magician-JDBC) | 
[Magician-Transaction](https://github.com/yuyenews/Magician-Transaction) | 
[Martian](https://github.com/yuyenews/Martian)

## TFB test results (second round, continuous optimization)
![image](https://user-images.githubusercontent.com/39583360/127732256-8b7c55a1-227a-4b8b-a0f6-d7e515f12fd3.png)

## Documentation and examples
[Document](http://magician-io.com/docs/en/index.html) | 
[Example](https://github.com/yuyenews/Magician-Example) | 
[Kotlin-Explme](https://github.com/yuyenews/Magician-Kolin-Explme)