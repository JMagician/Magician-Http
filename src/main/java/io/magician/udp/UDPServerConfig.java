package io.magician.udp;

import io.magician.udp.handler.MagicianUDPHandler;

/**
 * UDP服务配置
 */
public class UDPServerConfig {

    /**
     * 端口号
     */
    private static int port;

    /**
     * 线程数
     */
    private static int threadSize = 3;

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

    public static int getThreadSize() {
        return threadSize;
    }

    public static void setThreadSize(int threadSize) {
        UDPServerConfig.threadSize = threadSize;
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
