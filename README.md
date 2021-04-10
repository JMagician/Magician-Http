<div align=center>
<img width="200px;" src="http://mars-framework.com/img/logo-black.png"/>
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

Magician 是一个基于AIO的网络编程包，支持http，websocket等协议【暂时只支持http】

## 安装步骤

### 一、导入依赖

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
### 二、创建Handler
```java
public class DemoHandler implements MagicianHandler {

    @Override
    public void request(MagicianRequest magicianRequest) {
        // 如果是json格式提交的，就用这个方法获取参数字符串
        String jsonStr = magicianRequest.getJsonParam();

        /* *********如果是其他方式提交的，就用这个方法获取参数********* */
        String list = magicianRequest.getParam("参数的name");

        /* *********如果是文件上传就用这个方法获取文件们********* */
        Map<String, MagicianFileUpLoad> fileUpLoadMap = magicianRequest.getFiles();
        // 可以这样获取到文件
        MagicianFileUpLoad magicianFileUpLoad = fileUpLoadMap.get("参数的name");
        magicianFileUpLoad.getFileName();// 文件名
        magicianFileUpLoad.getInputStream(); // 文件流
        magicianFileUpLoad.getName();// 参数的name

        // 设置响应头
        magicianRequest.getResponse()
                .setResponseHeader("content-type", "application/json;charset=UTF-8")
                .sendText(200, "ok");
    }
}
```

### 三、创建服务
```java
Magician.builder().bind(8080, 100)
                    .threadPool(传入一个线程池)
                    .httpHandler("/", new DemoHandler())
                    .start();
```

### 官方资源
- 官方网站: [http://mars-framework.com](http://mars-framework.com)
- 使用示例: [https://github.com/yuyenews/MartianServer-Example](https://github.com/yuyenews/MartianServer-Example)
- 开发文档: [http://mars-framework.com/doc.html?tag=server](http://mars-framework.com/doc.html?tag=server)