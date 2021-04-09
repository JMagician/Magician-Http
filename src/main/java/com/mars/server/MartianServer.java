package com.mars.server;

import com.mars.server.http.handler.MartianAioServerHandler;
import com.mars.server.http.handler.MartianServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 服务，采用AIO
 */
public class MartianServer {

    private Logger log = LoggerFactory.getLogger(MartianServer.class);

    /**
     * 构建一个MartianServer
     * @return
     */
    public static MartianServer builder(){
        return new MartianServer();
    }

    /**
     * 绑定端口，设置最大连接数
     * @param port
     * @param backLog
     * @return
     */
    public MartianServer bind(int port, int backLog){
        MartianServerConfig.setPort(port);
        MartianServerConfig.setBackLog(backLog);
        return this;
    }
    /**
     * 设置读取超时时间
     * @param readTimeout
     * @return
     */
    public MartianServer readTimeout(long readTimeout){
        MartianServerConfig.setReadTimeout(readTimeout);
        return this;
    }
    /**
     * 设置写入超时时间
     * @param writeTimeout
     * @return
     */
    public MartianServer writeTimeout(long writeTimeout){
        MartianServerConfig.setWriteTimeout(writeTimeout);
        return this;
    }

    /**
     * 设置每次读取大小
     * @param readSize
     * @return
     */
    public MartianServer readSize(int readSize){
        MartianServerConfig.setReadSize(readSize);
        return this;
    }

    /**
     * 单个文件限制
     * @param fileSizeMax
     * @return
     */
    public MartianServer fileSizeMax(long fileSizeMax){
        MartianServerConfig.setFileSizeMax(fileSizeMax);
        return this;
    }

    /**
     * 文件总大小限制
     * @param sizeMax
     * @return
     */
    public MartianServer sizeMax(long sizeMax){
        MartianServerConfig.setSizeMax(sizeMax);
        return this;
    }

    /**
     * 设置线程池
     * @param threadPoolExecutor
     * @return
     */
    public MartianServer threadPool(ThreadPoolExecutor threadPoolExecutor){
        MartianServerConfig.setThreadPoolExecutor(threadPoolExecutor);
        return this;
    }

    /**
     * 设置联络器
     * @param martianServerHandler
     * @return
     */
    public MartianServer handler(MartianServerHandler martianServerHandler){
        MartianServerConfig.setMartianServerHandler(martianServerHandler);
        return this;
    }

    /**
     * 开启服务
     * @throws Exception
     */
    public void start() throws Exception {

        /* 创建线程组 */
        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withThreadPool(MartianServerConfig.getThreadPoolExecutor());
        /* 创建服务器通道 */
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
        /* 开始监听端口 */
        serverSocketChannel.bind(new InetSocketAddress(MartianServerConfig.getPort()), MartianServerConfig.getBackLog());
        /* 添加handler */
        serverSocketChannel.accept(serverSocketChannel,
                new MartianAioServerHandler());

        /* 标识服务是否已经启动 */
        log.info("启动成功");

        /* 阻塞主线程，防止进程停掉 */
        while (true){
            Thread.sleep(10000000);
        }
    }
}
