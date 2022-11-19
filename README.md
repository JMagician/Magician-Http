<h1> 
    <a href="https://magician-io.com">Magician</a> ·
    <img src="https://img.shields.io/badge/licenes-MIT-brightgreen.svg"/>
    <img src="https://img.shields.io/badge/jdk-8+-brightgreen.svg"/>
    <img src="https://img.shields.io/badge/maven-3.5.4+-brightgreen.svg"/>
    <img src="https://img.shields.io/badge/release-master-brightgreen.svg"/>
</h1>

Magician is a small HTTP service package based on Netty that makes it very easy to start an http service, and also supports WebSocket, using annotated configuration Handler.

If you want to develop an http service with netty but find it cumbersome, then Magician may help you.

In addition, we provide many other components that make up a toolkit that will work well for you in blockchain and web development.

## Running environment

JDK8+

## Documentation

[https://magician-io.com](https://magician-io.com)

## Example

### Importing dependencies
```xml
<dependency>
    <groupId>com.github.yuyenews</groupId>
    <artifactId>Magician</artifactId>
    <version>2.0.7</version>
</dependency>

<!-- This is the logging package, you must have it or the console will not see anything, any logging package that can bridge with slf4j is supported -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-jdk14</artifactId>
    <version>1.7.12</version>
</dependency>
```

### Creating an http service

Create a Handler

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

Start the http service

```java
Magician.createHttp()
    .scan("handler所在的包名")
    .bind(8080);
```

### Creating WebSocket

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
    public void onMessage(WebSocketSession webSocketSession, byte[] message) {

    }
}
```