package io.magician.tcp.http.server;

import io.magician.MagicianConfig;
import io.magician.tcp.http.handler.MagicianCompletionHandler;

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
        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withThreadPool(MagicianConfig.getThreadPoolExecutor());
        /* 创建服务器通道 */
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
        /* 开始监听端口 */
        serverSocketChannel.bind(new InetSocketAddress(MagicianConfig.getPort()), MagicianConfig.getBackLog());
        /* 添加handler */
        serverSocketChannel.accept(serverSocketChannel,
                new MagicianCompletionHandler());
    }
}
