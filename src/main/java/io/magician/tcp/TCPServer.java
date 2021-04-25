package io.magician.tcp;

import io.magician.tcp.handler.MagicianHandler;
import io.magician.tcp.protocol.codec.ProtocolCodec;
import io.magician.tcp.protocol.codec.impl.websocket.handler.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.*;

/**
 * http服务创建
 */
public class TCPServer {

    private Logger log = LoggerFactory.getLogger(TCPServer.class);

    /**
     * 绑定端口
     * @param port
     * @return
     */
    public TCPServer bind(int port){
        bind(port,100);
        return this;
    }

    /**
     * 绑定端口，设置最大连接数
     * @param port
     * @param backLog
     * @return
     */
    public TCPServer bind(int port, int backLog){
        TCPServerConfig.setPort(port);
        TCPServerConfig.setBackLog(backLog);
        return this;
    }

    /**
     * 设置读取超时时间,
     * 暂时没用到
     * @param readTimeout
     * @return
     */
    @Deprecated
    public TCPServer readTimeout(long readTimeout){
        TCPServerConfig.setReadTimeout(readTimeout);
        return this;
    }
    /**
     * 设置写入超时时间
     * 暂时没用到
     * @param writeTimeout
     * @return
     */
    @Deprecated
    public TCPServer writeTimeout(long writeTimeout){
        TCPServerConfig.setWriteTimeout(writeTimeout);
        return this;
    }

    /**
     * 设置每次读取大小
     * @param readSize
     * @return
     */
    public TCPServer readSize(int readSize){
        TCPServerConfig.setReadSize(readSize);
        return this;
    }

    /**
     * 单个文件限制
     * @param fileSizeMax
     * @return
     */
    public TCPServer fileSizeMax(long fileSizeMax){
        TCPServerConfig.setFileSizeMax(fileSizeMax);
        return this;
    }

    /**
     * 文件总大小限制
     * @param sizeMax
     * @return
     */
    public TCPServer sizeMax(long sizeMax){
        TCPServerConfig.setSizeMax(sizeMax);
        return this;
    }

    /**
     * 设置允许几个线程同时解析数据
     * @param threadSize
     * @return
     */
    public TCPServer readThreadSize(int threadSize){
        TCPServerConfig.setReadThreadSize(threadSize);
        return this;
    }

    /**
     * 设置允许几个线程同时执行业务逻辑
     * @param threadSize
     * @return
     */
    public TCPServer execThreadSize(int threadSize){
        TCPServerConfig.setExecuteThreadSize(threadSize);
        return this;
    }

    /**
     * 设置协议解析器
     * @param protocolCodec
     * @return
     */
    public TCPServer protocolCodec(ProtocolCodec protocolCodec){
        TCPServerConfig.setProtocolCodec(protocolCodec);
        return this;
    }

    /**
     * 设置联络器
     * @param magicianHandler
     * @return
     */
    public TCPServer httpHandler(String path, MagicianHandler magicianHandler) throws Exception {
        TCPServerConfig.addMartianServerHandler(path, magicianHandler);
        return this;
    }

    /**
     * 设置联络器
     * @param webSocketHandler
     * @return
     */
    public TCPServer webSocketHandler(String path, WebSocketHandler webSocketHandler) throws Exception {
        TCPServerConfig.addMartianWebSocketHandler(path, webSocketHandler);
        return this;
    }

    /**
     * 开启服务
     * @throws Exception
     */
    public void start() throws Exception {

        /* 开始监听端口 */
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(TCPServerConfig.getPort()), TCPServerConfig.getBackLog());

        /* 标识服务是否已经启动 */
        log.info("启动成功");

        /* 监听Http */
        TCPServerMonitor.doMonitor(serverSocketChannel);
    }
}
