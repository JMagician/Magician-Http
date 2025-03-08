## Magician

### 运行环境

JDK8+

### 引入依赖

```xml
<dependency>
    <groupId>com.github.yuyenews</groupId>
    <artifactId>Magician</artifactId>
    <version>2.0.7</version>
</dependency>

<!-- 这是日志包，必须有，不然控制台看不到东西，支持任意可以和slf4j桥接的日志包 -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-jdk14</artifactId>
    <version>1.7.12</version>
</dependency>
```

### 创建Handler(HTTP服务)

```java
@HttpHandler(path="/demo")
public class DemoHandler implements HttpBaseHandler {

    @Override
    public void request(MagicianRequest request, MagicianResponse response) {
        // response data
        request.getResponse()
                .sendJson("{'status':'ok'}");
    }
}
```

### 接收参数

```java
// 根据参数名称 获取一个参数
request.getParam("param name");

// 获取参数名称相同的多个参数
request.getParams("param name");

// 根据参数名称 获取文件
request.getFile("param name");

// 获取参数名称相同的多个文件
request.getFiles("param name");

// 获取本次请求传输的所有文件，key为 参数名称
request.getFileMap();

// 如果本次请求时json传参，可以用这个方法获取json字符串
request.getJsonParam();

// 根据名称 获取请求头
request.getRequestHeader("header name");

// 获取所有请求头
request.getRequestHeaders();
```

### 响应参数

```java
// 响应文本数据
request.getResponse().sendText("");

// 响应html数据
request.getResponse().sendHtml("");

// 响应其他数据，这种方式需要 先设置响应头的Content-Type
request.getResponse().sendData("");

// 响应json数据
request.getResponse().sendJson("");

// 响应流数据，一般用作文件下载
request.getResponse().sendStream("");

// 响应错误数据，格式 {"code":500, "msg":""}
request.getResponse().sendErrorMsg(500, "");
```

### 创建WebSocketHandler(WebSocket服务)

```java
@WebSocketHandler(path = "/websocket")
public class DemoSocketHandler implements WebSocketBaseHandler {
   
    /**
     * 当连接进来时，触发这个方法
     */
    @Override
    public void onOpen(WebSocketSession webSocketSession) {
        // 给客户端发送消息
        webSocketSession.sendString("send message");
    }
    
    /**
     * 当连接断开时，触发这个方法
     */
    @Override
    public void onClose(WebSocketSession webSocketSession) {
        
    }

    /**
     * 当客户端发来消息时，触发这个方法
     * 第二个参数 message 就是客户端发送过来的消息
     */
    @Override
    public void onMessage(WebSocketSession webSocketSession, byte[] message) {
        System.out.println("收到了:" + new String(message));

        // 给客户端发送消息
        webSocketSession.sendString("send message");
    }
}
```

### 启动服务

无论是HTTP服务 还是WebSocket服务，都是这么启动

基础启动
```java
Magician.createHttp()
        .scan("com.test")// 扫描范围（包名）
        .bind(8080); // 监听的端口
```

自定义配置启动
```java
// 这段配置可以提取出去，不用跟下面的启动代码放在一起
MagicianConfig magicianConfig = new MagicianConfig();
magicianConfig.setNumberOfPorts(3); // 允许同时监听的端口数量，默认1个
magicianConfig.setBossThreads(1); // netty的boss线程数量 默认1个
magicianConfig.setWorkThreads(3); // netty的work线程数量 默认3个
magicianConfig.setNettyLogLevel(LogLevel.DEBUG); // netty的日志打印级别
magicianConfig.setMaxInitialLineLength(4096); // http解码器的构造参数1，默认4096 跟netty一样
magicianConfig.setMaxHeaderSize(8192); // http解码器的构造参数2，默认8192 跟netty一样
magicianConfig.setMaxChunkSize(8192); // http解码器的构造参数3，默认8192 跟netty一样


HttpServer httpServer = Magician.createHttp()
        .scan("com.test")// 扫描范围（包名）
        .setConfig(magicianConfig); // 添加配置

httpServer.bind(8080); // 监听端口

// 如果要监听多个端口
httpServer.bind(8081); 
httpServer.bind(8082); 
```