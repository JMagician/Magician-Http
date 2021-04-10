package com.mars.server.tcp.http.server;

import com.mars.server.MartianServerConfig;
import com.mars.server.tcp.http.handler.MartianAioServerHandler;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;

/**
 * http服务创建
 */
public class HttpServerCreate {

    /**
     * 创建服务
     * @throws Exception
     */
    public static void create() throws Exception {
        /* 创建线程组 */
        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withThreadPool(MartianServerConfig.getThreadPoolExecutor());
        /* 创建服务器通道 */
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
        /* 开始监听端口 */
        serverSocketChannel.bind(new InetSocketAddress(MartianServerConfig.getPort()), MartianServerConfig.getBackLog());
        /* 添加handler */
        serverSocketChannel.accept(serverSocketChannel,
                new MartianAioServerHandler());
    }
}
