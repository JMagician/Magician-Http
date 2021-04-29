package io.magician.tcp;

import io.magician.common.event.EventGroup;
import io.magician.tcp.handler.MagicianHandler;
import io.magician.tcp.codec.ProtocolCodec;
import io.magician.tcp.codec.impl.websocket.handler.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.concurrent.Executors;

/**
 * tcp服务创建
 */
public class TCPServer {

    private Logger log = LoggerFactory.getLogger(TCPServer.class);

    private ServerSocketChannel serverSocketChannel;

    private int port;
    private int backLog;
    private EventGroup ioEventGroup;
    private EventGroup workerEventGroup;
    private TCPServerConfig tcpServerConfig;

    public TCPServer() {
        this(
            new EventGroup(1, Executors.newCachedThreadPool()),
            new EventGroup(3, Executors.newCachedThreadPool())
        );
    }

    public TCPServer(EventGroup ioEventGroup, EventGroup workerEventGroup){
        try {
            this.ioEventGroup = ioEventGroup;
            this.workerEventGroup = workerEventGroup;
            this.tcpServerConfig = new TCPServerConfig();

            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
        } catch (Exception e){
            log.error("打开serverSocketChannel，出现异常", e);
        }
    }

    /**
     * 绑定端口
     * @param port
     * @return
     */
    public TCPServer bind(int port){
        bind(port, 100);
        return this;
    }

    /**
     * 绑定端口，设置最大连接数
     * @param port
     * @param backLog
     * @return
     */
    public TCPServer bind(int port, int backLog){
        this.port = port;
        this.backLog = backLog;
        return this;
    }

    public TCPServer config(TCPServerConfig tcpServerConfig){
        this.tcpServerConfig = tcpServerConfig;
        return this;
    }

    /**
     * 设置协议解析器
     * @param protocolCodec
     * @return
     */
    public TCPServer protocolCodec(ProtocolCodec protocolCodec){
        this.tcpServerConfig.setProtocolCodec(protocolCodec);
        return this;
    }

    /**
     * 设置处理器
     * @param magicianHandler
     * @return
     */
    public TCPServer httpHandler(String path, MagicianHandler magicianHandler) throws Exception {
        this.tcpServerConfig.addMartianServerHandler(path, magicianHandler);
        return this;
    }

    /**
     * 设置处理器
     * @param webSocketHandler
     * @return
     */
    public TCPServer webSocketHandler(String path, WebSocketHandler webSocketHandler) throws Exception {
        this.tcpServerConfig.addMartianWebSocketHandler(path, webSocketHandler);
        return this;
    }

    /**
     * 开启服务
     * @throws Exception
     */
    public void start() throws Exception {

        /* 开始监听端口 */
        serverSocketChannel.bind(new InetSocketAddress(port), backLog);

        /* 开启NIOSelector监听器 */
        ioEventGroup
                .getEventRunner()
                .addEvent(new TCPServerMonitorTask(serverSocketChannel, tcpServerConfig, ioEventGroup, workerEventGroup));

        /* 标识服务是否已经启动 */
        log.info("启动成功");
    }
}
