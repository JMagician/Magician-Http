package com.mars.server;

import com.mars.server.tcp.http.handler.MartianServerHandler;
import com.mars.server.tcp.websocket.handler.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;
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
     * 单个文件限制
     */
    private static long fileSizeMax = 2*1024*1024;
    /**
     * 上传文件总大小限制
     */
    private static long sizeMax = 10*1024*1024;
    /**
     * 线程池
     */
    private static ThreadPoolExecutor threadPoolExecutor;
    /**
     * 联络器
     */
    private static Map<String, MartianServerHandler> martianServerHandlerMap = new HashMap<>();
    /**
     * webSocket联络器
     */
    private static Map<String, WebSocketHandler> martianWebSocketHandlerMap = new HashMap<>();

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

    public static long getFileSizeMax() {
        return fileSizeMax;
    }

    public static void setFileSizeMax(long fileSizeMax) {
        MartianServerConfig.fileSizeMax = fileSizeMax;
    }

    public static long getSizeMax() {
        return sizeMax;
    }

    public static void setSizeMax(long sizeMax) {
        MartianServerConfig.sizeMax = sizeMax;
    }

    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public static void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        MartianServerConfig.threadPoolExecutor = threadPoolExecutor;
    }

    public static Map<String, MartianServerHandler> getMartianServerHandlerMap() {
        return martianServerHandlerMap;
    }

    public static Map<String, WebSocketHandler> getMartianWebSocketHandlerMap() {
        return martianWebSocketHandlerMap;
    }

    public static void addMartianServerHandler(String path, MartianServerHandler martianServerHandler) throws Exception {
        if(martianServerHandlerMap.containsKey(path)
                || martianWebSocketHandlerMap.containsKey(path)){
            throw new Exception("已经存在地址为"+path+"的handler");
        }
        MartianServerConfig.martianServerHandlerMap.put(path, martianServerHandler);
    }

    public static void addMartianWebSocketHandler(String path, WebSocketHandler webSocketHandler) throws Exception {
        if(path.equals("/")){
            throw new Exception("webSocket不可以监听根路径");
        }
        if(martianServerHandlerMap.containsKey(path)
                || martianWebSocketHandlerMap.containsKey(path)){
            throw new Exception("已经存在地址为"+path+"的handler");
        }
        MartianServerConfig.martianWebSocketHandlerMap.put(path, webSocketHandler);
    }
}
