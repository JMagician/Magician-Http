package io.magician.network;

import io.magician.network.processing.HttpServerInitializer;
import io.magician.network.load.LoadResource;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class HttpServer {

    private Logger logger = LoggerFactory.getLogger(HttpServer.class);

    /**
     * 扫描handler
     * @param scanPackage
     * @return
     * @throws Exception
     */
    public HttpServer scan(String scanPackage) throws Exception {
        LoadResource.loadHandler(scanPackage);
        return this;
    }

    /**
     * 启动http服务
     * @param port
     * @throws Exception
     */
    public void bind(int port) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        bootstrap.group(boss,work)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpServerInitializer());

        ChannelFuture f = bootstrap.bind(new InetSocketAddress(port)).sync();
        logger.info("启动HTTP服务成功, port: [{}]", port);
        f.channel().closeFuture().sync();
    }
}
