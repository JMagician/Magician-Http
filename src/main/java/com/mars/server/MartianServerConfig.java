package com.mars.server;

import com.mars.server.http.handler.MartianServerHandler;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 服务配置
 */
public class MartianServerConfig {

    /**
     * 端口号
     */
    private static int port;
    /**
     * 最大连接数
     */
    private static int backLog;
    /**
     * 读取超时时间
     */
    private static long readTimeout = 10000;
    /**
     * 写入超时时间
     */
    private static long writeTimeout = 10000;
    /**
     * 每次读取大小
     */
    private static int readSize = 1024;
    /**
     * 线程池
     */
    private static ThreadPoolExecutor threadPoolExecutor;
    /**
     * 联络器
     */
    private static MartianServerHandler martianServerHandler;

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        MartianServerConfig.port = port;
    }

    public static int getBackLog() {
        return backLog;
    }

    public static void setBackLog(int backLog) {
        MartianServerConfig.backLog = backLog;
    }

    public static long getReadTimeout() {
        return readTimeout;
    }

    public static void setReadTimeout(long readTimeout) {
        MartianServerConfig.readTimeout = readTimeout;
    }

    public static long getWriteTimeout() {
        return writeTimeout;
    }

    public static void setWriteTimeout(long writeTimeout) {
        MartianServerConfig.writeTimeout = writeTimeout;
    }

    public static int getReadSize() {
        return readSize;
    }

    public static void setReadSize(int readSize) {
        MartianServerConfig.readSize = readSize;
    }

    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public static void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        MartianServerConfig.threadPoolExecutor = threadPoolExecutor;
    }

    public static MartianServerHandler getMartianServerHandler() {
        return martianServerHandler;
    }

    public static void setMartianServerHandler(MartianServerHandler martianServerHandler) {
        MartianServerConfig.martianServerHandler = martianServerHandler;
    }
}
