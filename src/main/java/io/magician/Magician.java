package io.magician;

import io.magician.tcp.http.handler.MagicianHandler;
import io.magician.tcp.http.server.HttpServerCreate;
import io.magician.tcp.websocket.handler.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 服务，采用AIO
 */
public class Magician {

    private Logger log = LoggerFactory.getLogger(Magician.class);

    /**
     * 构建一个MartianServer
     * @return
     */
    public static Magician builder(){
        return new Magician();
    }

    /**
     * 绑定端口
     * @param port
     * @return
     */
    public Magician bind(int port){
        bind(port,100);
        return this;
    }

    /**
     * 绑定端口，设置最大连接数
     * @param port
     * @param backLog
     * @return
     */
    public Magician bind(int port, int backLog){
        MagicianConfig.setPort(port);
        MagicianConfig.setBackLog(backLog);
        return this;
    }

    /**
     * 设置读取超时时间
     * @param readTimeout
     * @return
     */
    public Magician readTimeout(long readTimeout){
        MagicianConfig.setReadTimeout(readTimeout);
        return this;
    }
    /**
     * 设置写入超时时间
     * @param writeTimeout
     * @return
     */
    public Magician writeTimeout(long writeTimeout){
        MagicianConfig.setWriteTimeout(writeTimeout);
        return this;
    }

    /**
     * 设置每次读取大小
     * @param readSize
     * @return
     */
    public Magician readSize(int readSize){
        MagicianConfig.setReadSize(readSize);
        return this;
    }

    /**
     * 单个文件限制
     * @param fileSizeMax
     * @return
     */
    public Magician fileSizeMax(long fileSizeMax){
        MagicianConfig.setFileSizeMax(fileSizeMax);
        return this;
    }

    /**
     * 文件总大小限制
     * @param sizeMax
     * @return
     */
    public Magician sizeMax(long sizeMax){
        MagicianConfig.setSizeMax(sizeMax);
        return this;
    }

    /**
     * 设置线程池
     * @param threadPoolExecutor
     * @return
     */
    public Magician threadPool(ThreadPoolExecutor threadPoolExecutor){
        MagicianConfig.setThreadPoolExecutor(threadPoolExecutor);
        return this;
    }

    /**
     * 设置联络器
     * @param magicianHandler
     * @return
     */
    public Magician httpHandler(String path, MagicianHandler magicianHandler) throws Exception {
        MagicianConfig.addMartianServerHandler(path, magicianHandler);
        return this;
    }

    /**
     * 设置联络器
     * @param webSocketHandler
     * @return
     */
    public Magician webSocketHandler(String path, WebSocketHandler webSocketHandler) throws Exception {
        MagicianConfig.addMartianWebSocketHandler(path, webSocketHandler);
        return this;
    }

    /**
     * 开启服务
     * @throws Exception
     */
    public void start() throws Exception {
        /* 创建服务 */
        HttpServerCreate.create();

        /* 标识服务是否已经启动 */
        log.info("启动成功");

        /* 阻塞主线程，防止进程停掉 */
        while (true){
            Thread.sleep(10000000);
        }
    }
}
