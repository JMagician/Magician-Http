<br/>

<div align=center>
<img width="260px;" src="http://magician-io.com/img/logo-white.png"/>
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
public class DemoHandler implements MagicianHandler<MagicianRequest> {

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
                    .handler("/", new DemoHandler())
                    .bind(8080);
```

### Create TCP Server (custom thread pool configuration)
```java
EventGroup ioEventGroup = new EventGroup(1, Executors.newCachedThreadPool());
EventGroup workerEventGroup = new EventGroup(10, Executors.newCachedThreadPool());

// When the current EventRunner has no tasks, it is allowed to steal tasks from other EventRunners
workerEventGroup.setSteal(EventEnum.STEAL.YES);

Magician.createTCPServer(ioEventGroup, workerEventGroup)
                    .handler("/", new DemoHandler())
                    .bind(8080);
```

### Create TCP Server (Listen on multiple ports)
```java
// Listen to n ports, write n as the first parameter of ioEventGroup
EventGroup ioEventGroup = new EventGroup(2, Executors.newCachedThreadPool());
EventGroup workerEventGroup = new EventGroup(10, Executors.newCachedThreadPool());

// When the current EventRunner has no tasks, it is allowed to steal tasks from other EventRunners
workerEventGroup.setSteal(EventEnum.STEAL.YES);

TCPServer tcpServer = Magician
                         .createTCPServer(ioEventGroup, workerEventGroup)
                         .handler("/", new DemoHandler())

tcpServer.bind(8080);
tcpServer.bind(8088);
```

## 2. Create WebSocket
Just add a handler when creating the http service
```java
Magician.createTCPServer()
                    .handler("/", new DemoHandler())
                    .webSocketHandler("/websocket", new DemoSocketHandler())
                    .bind(8080);
```

## 3. Create UDP Server
```java
Magician.createUdpServer()
                .handler(outputStream -> {
                    // outputStream is of type ByteArrayOutputStream
                    // It is the data sent by the client, you can parse it by yourself
                }).bind(8088);
```
In addition to this way of writing, you can also create a separate handler, add here

## TFB test results (second round, continuous optimization)
![image](https://user-images.githubusercontent.com/39583360/119000098-6175ce00-b9bd-11eb-9e1d-dcc82c0c135f.png)

[TFB地址](https://www.techempower.com/benchmarks/#section=test&runid=63f03f07-c45e-4772-806e-908fa02c448f&hw=ph&test=json&l=zijbpb-e7&a=2)
## Documentation and examples
- Document: [http://magician-io.com/docs/en/index.html](http://magician-io.com/docs/index.html)
- example: [https://github.com/yuyenews/Magician-Example](https://github.com/yuyenews/Magician-Example)