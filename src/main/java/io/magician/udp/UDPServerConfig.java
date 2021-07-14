package io.magician.udp;

import io.magician.udp.handler.UDPBaseHandler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * UDP服务配置
 */
public class UDPServerConfig {

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
    private static UDPBaseHandler udpBaseHandler;

    public static Executor getThreadPool() {
        if(threadPool == null){
            threadPool = Executors.newCachedThreadPool();
        }
        return threadPool;
    }

    public static void setThreadPool(Executor threadPool) {
        UDPServerConfig.threadPool = threadPool;
    }

    public static UDPBaseHandler getUdpBaseHandler() {
        return udpBaseHandler;
    }

    public static void setUdpBaseHandler(UDPBaseHandler udpBaseHandler) {
        UDPServerConfig.udpBaseHandler = udpBaseHandler;
    }

    public static int getReadSize() {
        return readSize;
    }

    public static void setReadSize(int readSize) {
        UDPServerConfig.readSize = readSize;
    }
}
