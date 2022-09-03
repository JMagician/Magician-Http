package io.magician.network;

import io.magician.common.config.MagicianConfig;
import io.magician.network.processing.HttpServerInitializer;
import io.magician.network.load.LoadResource;
import io.magician.application.thread.BusinessThreadPool;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class HttpServer {

    private Logger logger = LoggerFactory.getLogger(HttpServer.class);

    /**
     * Startup parameter
     */
    private MagicianConfig magicianConfig = new MagicianConfig();

    private ServerBootstrap bootstrap = null;
    private EventLoopGroup boss = null;
    private EventLoopGroup work = null;

    private int portCount = 0;

    /**
     * scan handler
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
     * Set launch configuration
     *
     * @param magicianConfig
     * @return
     */
    public HttpServer setConfig(MagicianConfig magicianConfig) {
        this.magicianConfig = magicianConfig;
        return this;
    }

    /**
     * Create ServerBootstrap
     */
    private void createBootstrap() {
        bootstrap = new ServerBootstrap();
        boss = new NioEventLoopGroup(magicianConfig.getBossThreads());
        work = new NioEventLoopGroup(magicianConfig.getWorkThreads());
        bootstrap.group(boss, work)
                .handler(new LoggingHandler(magicianConfig.getNettyLogLevel()))
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpServerInitializer(magicianConfig));
    }

    /**
     * Initialize the business thread pool
     */
    private void initBusinessThreadPool(){
        BusinessThreadPool.init(magicianConfig.getCorePoolSize(),
                magicianConfig.getMaximumPoolSize(),
                magicianConfig.getKeepAliveTime()
        );
    }

    /**
     * start http service
     *
     * @param port
     * @throws Exception
     */
    public void bind(int port) throws Exception {
        if (bootstrap == null) {
            createBootstrap();
            initBusinessThreadPool();
       }

        if (portCount >= magicianConfig.getNumberOfPorts()) {
            shutdown();
            throw new Exception("This instance can only listen to " + magicianConfig.getNumberOfPorts() + " ports at most, if you want to listen more, please adjust the numberOfPorts field under the MagicianConfig class");
        }

        portCount++;

        ChannelFuture f = bootstrap.bind(new InetSocketAddress(port)).sync();
        f.channel().closeFuture();

        logger.info("Start HTTP service successfully, port: [{}]", port);
    }

    /**
     * shut down service
     */
    public void shutdown(){
        if(work != null){
            work.shutdownGracefully();
        }
        if(boss != null){
            boss.shutdownGracefully();
        }
    }
}
