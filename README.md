<div align=center>
<img width="200px;" src="http://mars-framework.com/img/logo-github.png"/>
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

基于AIO的网络编程包

</div>

## 项目简介

Martian-Server 是一个基于AIO的网络编程包，支持http，websocket等协议【暂时只支持http】

## 安装步骤

### 一、导入依赖

```xml
<dependency>
    <groupId>com.github.yuyenews</groupId>
    <artifactId>Martian-server</artifactId>
    <version>最新版</version>
</dependency>

<!-- 这个是日志包，支持任意可以跟slf4j桥接的包 -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-jdk14</artifactId>
    <version>1.7.12</version>
</dependency>
```
### 二、创建Handler【二选一】
```java
// 用起来较为复杂的handler
public class DemoHandler implements HttpExchangeHandler {

    @Override
    public void request(MartianHttpExchange martianHttpExchange) {
        // 获取请求头
        HttpHeaders httpHeaders = martianHttpExchange.getRequestHeaders();
        
        // 获取请求内容，是一个文件流 需要自己解析
        InputStream inputStream = martianHttpExchange.getRequestBody();
    
        // 也可以自己直接操作channel
        AsynchronousSocketChannel socketChannel = martianHttpExchange.getSocketChannel();
    
        /* *************************设置响应头************************* */
        // 如果不想让框架自己关闭channel的话，这句是必须的
        martianHttpExchange.setResponseHeader(MartianServerConstant.CONNECTION,"keep-alive");
        // 设置响应格式为json
        martianHttpExchange.setResponseHeader(MartianServerConstant.CONTENT_TYPE,MartianServerConstant.JSON_CONTENT_TYPE);
        // 设置响应状态码以及数据
        martianHttpExchange.sendText(200,"ok");
    }
}

// 用起来较为简单的handler
public class DemoRequestHandler implements HttpRequestHandler {

    @Override
    public void request(MartianHttpRequest martianHttpRequest) {
        
        // 如果是json格式提交的，就用这个方法获取json字符串
        martianHttpRequest.getJsonParam();
        
        // 如果是其他方式提交的，就用这个方法获取参数
        martianHttpRequest.getMarsParams();
        
        // 如果是文件上传就用这个方法获取文件们
        martianHttpRequest.getFiles();
        
        MartianHttpExchange martianHttpExchange = martianHttpRequest.getMartianHttpExchange();
        
        /* *************************设置响应头************************* */
        // 如果不想让框架自己关闭channel的话，这句是必须的
        martianHttpRequest.setResponseHeader(MartianServerConstant.CONNECTION,"keep-alive");
        // 设置响应格式为json
        martianHttpRequest.setResponseHeader(MartianServerConstant.CONTENT_TYPE,MartianServerConstant.JSON_CONTENT_TYPE);
        // 设置响应状态码以及数据
        martianHttpExchange.sendText(200,"ok");
    }
}
```

### 三、创建服务
```java
// 链式写法
MartianServer.builder()
                    .bind(8080, 100)
                    .threadPool(传入一个线程池)
                    .httpHandler("/", new DemoHandler())
                    .start();

// 常规写法
MartianServer martianServer = MartianServer.builder();
martianServer.bind(8080, 100);
martianServer.threadPool(传入一个线程池);
martianServer.httpHandler("/", new DemoHandler());
martianServer.start();
```

### 官方资源
- 官方网站: [http://mars-framework.com](http://mars-framework.com)
- 使用示例: [https://github.com/yuyenews/MartianServer-Example](https://github.com/yuyenews/MartianServer-Example)
- 开发文档: [http://mars-framework.com/doc.html?tag=server](http://mars-framework.com/doc.html?tag=server)