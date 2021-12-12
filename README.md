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
master分支处于更新中状态，现在的最新源码在 NIO分支上，需要的话可以去NIO上拉取
master属于2.0版本的开发中代码，目前已完成90%，还剩websocket没完成
2.0 将底层全部抛弃了，全部转向了netty，尽请期待哦

The master branch is in an updated state, the latest source code is now on the NIO branch, you can pull it from NIO if you need it
master is part of the 2.0 version of the code under development, and is currently 90% complete, with the websocket still to be completed.
2.0 has dumped all the underlying layers and moved to netty, so look forward to that!
</div>


## Project Description


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

## 1. create HTTP service
### Create Handler
```java
@TCPHandler(path="/")
public class DemoHandler implements TCPBaseHandler {

    @Override
    public void request(MagicianRequest magicianRequest, MagicianResponse response) {
        // response data
        magicianRequest.getResponse()
                .sendJson(200, "{'status':'ok'}");
    }
}
```

### Create HTTP Server (Default thread pool configuration)
```java
Magician.createHttp()
                    .scan("The package name of the handler")
                    .bind(8080);
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
[Kotlin-Example](https://github.com/yuyenews/Magician-Kotlin-Example)