package io.magician.tcp.http.server;

import io.magician.tcp.http.handler.MagicianCompletionHandler;
import io.magician.tcp.http.handler.MagicianHandler;
import io.magician.tcp.websocket.handler.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * http服务创建
 */
public class HttpServerCreate {

    private Logger log = LoggerFactory.getLogger(HttpServerCreate.class);

    /**
     * 绑定端口
     * @param port
     * @return
     */
    public HttpServerCreate bind(int port){
        bind(port,100);
        return this;
    }

    /**
     * 绑定端口，设置最大连接数
     * @param port
     * @param backLog
     * @return
     */
    public HttpServerCreate bind(int port, int backLog){
        HttpServerConfig.setPort(port);
        HttpServerConfig.setBackLog(backLog);
        return this;
    }

    /**
     * 设置读取超时时间
     * @param readTimeout
     * @return
     */
    public HttpServerCreate readTimeout(long readTimeout){
        HttpServerConfig.setReadTimeout(readTimeout);
        return this;
    }
    /**
     * 设置写入超时时间
     * @param writeTimeout
     * @return
     */
    public HttpServerCreate writeTimeout(long writeTimeout){
        HttpServerConfig.setWriteTimeout(writeTimeout);
        return this;
    }

    /**
     * 设置每次读取大小
     * @param readSize
     * @return
     */
    public HttpServerCreate readSize(int readSize){
        HttpServerConfig.setReadSize(readSize);
        return this;
    }

    /**
     * 单个文件限制
     * @param fileSizeMax
     * @return
     */
    public HttpServerCreate fileSizeMax(long fileSizeMax){
        HttpServerConfig.setFileSizeMax(fileSizeMax);
        return this;
    }

    /**
     * 文件总大小限制
     * @param sizeMax
     * @return
     */
    public HttpServerCreate sizeMax(long sizeMax){
        HttpServerConfig.setSizeMax(sizeMax);
        return this;
    }

    /**
     * 设置线程池
     * @param threadPoolExecutor
     * @return
     */
    public HttpServerCreate threadPool(ThreadPoolExecutor threadPoolExecutor){
        HttpServerConfig.setThreadPoolExecutor(threadPoolExecutor);
        return this;
    }

    /**
     * 设置联络器
     * @param magicianHandler
     * @return
     */
    public HttpServerCreate httpHandler(String path, MagicianHandler magicianHandler) throws Exception {
        HttpServerConfig.addMartianServerHandler(path, magicianHandler);
        return this;
    }

    /**
     * 设置联络器
     * @param webSocketHandler
     * @return
     */
    public HttpServerCreate webSocketHandler(String path, WebSocketHandler webSocketHandler) throws Exception {
        HttpServerConfig.addMartianWebSocketHandler(path, webSocketHandler);
        return this;
    }

    /**
     * 开启服务
     * @throws Exception
     */
    public void start() throws Exception {
        /* 创建线程组 */
        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withThreadPool(HttpServerConfig.getThreadPoolExecutor());
        /* 创建服务器通道 */
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
        /* 开始监听端口 */
        serverSocketChannel.bind(new InetSocketAddress(HttpServerConfig.getPort()), HttpServerConfig.getBackLog());
        /* 添加handler */
        serverSocketChannel.accept(serverSocketChannel,
                new MagicianCompletionHandler());

        /* 标识服务是否已经启动 */
        log.info("启动成功");

        /* 阻塞主线程，防止进程停掉 */
        while (true){
            Thread.sleep(10000000);
        }
    }
}
