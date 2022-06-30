package io.magician.network;

import io.magician.common.config.MagicianConfig;
import io.magician.network.processing.HttpServerInitializer;
import io.magician.network.load.LoadResource;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpServer {

    private Logger logger = LoggerFactory.getLogger(HttpServer.class);

    /**
     * 启动参数配置
     */
    private MagicianConfig magicianConfig = new MagicianConfig();

    private ServerBootstrap bootstrap = null;

    private ThreadPoolExecutor threadPoolExecutor = null;

    private int portCount = 0;

    /**
     * 扫描handler
     *
     * @param scanPackage
     * @return
     * @throws Exception
     */
    public HttpServer scan(String scanPackage) throws Exception {
        LoadResource.loadHandler(scanPackage);
        return this;
    }

    /**
     * 设置配置项
     *
     * @param magicianConfig
     * @return
     */
    public HttpServer setConfig(MagicianConfig magicianConfig) {
        this.magicianConfig = magicianConfig;
        return this;
    }

    /**
     * 创建ServerBootstrap
     */
    private void createBootstrap() {
        bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup(magicianConfig.getBossThreads());
        EventLoopGroup work = new NioEventLoopGroup(magicianConfig.getWorkThreads());
        bootstrap.group(boss, work)
                .handler(new LoggingHandler(magicianConfig.getNettyLogLevel()))
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpServerInitializer(magicianConfig));
    }

    /**
     * 启动http服务
     *
     * @param port
     * @throws Exception
     */
    public void bind(int port) throws Exception {
        if (bootstrap == null) {
            createBootstrap();

            // 这个线程池 只需要放监听端口的N个线程，所以这里干脆把核心数，最大数，队列长度 都设置为一样的
            threadPoolExecutor = new ThreadPoolExecutor(magicianConfig.getNumberOfPorts(), magicianConfig.getNumberOfPorts(), 3, TimeUnit.SECONDS, new LinkedBlockingQueue<>(magicianConfig.getNumberOfPorts()));
        }

        if (portCount >= magicianConfig.getNumberOfPorts()) {
            throw new Exception("本实例最多只能监听" + magicianConfig.getNumberOfPorts() + "个端口，如果你想监听更多，请调整MagicianConfig类下面的numberOfPorts字段");
        }

        portCount++;
        threadPoolExecutor.execute(() -> {
            try {
                ChannelFuture f = bootstrap.bind(new InetSocketAddress(port)).sync();
                f.channel().closeFuture();

                logger.info("启动HTTP服务成功, port: [{}]", port);
            } catch (Exception e) {
                logger.error("启动HTTP服务异常, port: [{}]", port, e);
            }
        });
    }
}
