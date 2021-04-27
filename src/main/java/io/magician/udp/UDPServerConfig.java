package io.magician.udp;

import io.magician.udp.handler.MagicianUDPHandler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * UDP服务配置
 */
public class UDPServerConfig {

    /**
     * 端口号
     */
    private static int port;

    /**
     * 业务线程池
     */
    private static Executor threadPool;

    /**
     * 接收数据的缓冲区大小
     */
    private static int readSize = 1024;

    /**
     * 联络器
     */
    private static MagicianUDPHandler magicianUDPHandler;

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        UDPServerConfig.port = port;
    }

    public static Executor getThreadPool() {
        if(threadPool == null){
            threadPool = Executors.newCachedThreadPool();
        }
        return threadPool;
    }

    public static void setThreadPool(Executor threadPool) {
        UDPServerConfig.threadPool = threadPool;
    }

    public static MagicianUDPHandler getMagicianUDPHandler() {
        return magicianUDPHandler;
    }

    public static void setMagicianUDPHandler(MagicianUDPHandler magicianUDPHandler) {
        UDPServerConfig.magicianUDPHandler = magicianUDPHandler;
    }

    public static int getReadSize() {
        return readSize;
    }

    public static void setReadSize(int readSize) {
        UDPServerConfig.readSize = readSize;
    }
}
