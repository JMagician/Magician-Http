package io.magician.tcp;

import io.magician.tcp.handler.MagicianHandler;
import io.magician.tcp.codec.ProtocolCodec;
import io.magician.tcp.codec.impl.http.HttpProtocolCodec;
import io.magician.tcp.codec.impl.websocket.handler.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * http服务配置
 */
public class TCPServerConfig {

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
     * 业务线程池
     */
    private static Executor threadPool;
    /**
     * 协议解析器
     */
    private static ProtocolCodec protocolCodec = new HttpProtocolCodec();
    /**
     * 联络器
     */
    private static Map<String, MagicianHandler> martianServerHandlerMap = new HashMap<>();
    /**
     * webSocket联络器
     */
    private static Map<String, WebSocketHandler> martianWebSocketHandlerMap = new HashMap<>();

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        TCPServerConfig.port = port;
    }

    public static int getBackLog() {
        return backLog;
    }

    public static void setBackLog(int backLog) {
        TCPServerConfig.backLog = backLog;
    }

    public static long getReadTimeout() {
        return readTimeout;
    }

    public static void setReadTimeout(long readTimeout) {
        TCPServerConfig.readTimeout = readTimeout;
    }

    public static long getWriteTimeout() {
        return writeTimeout;
    }

    public static void setWriteTimeout(long writeTimeout) {
        TCPServerConfig.writeTimeout = writeTimeout;
    }

    public static int getReadSize() {
        return readSize;
    }

    public static void setReadSize(int readSize) {
        TCPServerConfig.readSize = readSize;
    }

    public static long getFileSizeMax() {
        return fileSizeMax;
    }

    public static void setFileSizeMax(long fileSizeMax) {
        TCPServerConfig.fileSizeMax = fileSizeMax;
    }

    public static long getSizeMax() {
        return sizeMax;
    }

    public static void setSizeMax(long sizeMax) {
        TCPServerConfig.sizeMax = sizeMax;
    }

    public static Executor getThreadPool() {
        if(threadPool == null){
            threadPool = Executors.newCachedThreadPool();
        }
        return threadPool;
    }

    public static void setThreadPool(Executor threadPool) {
        TCPServerConfig.threadPool = threadPool;
    }

    public static Map<String, MagicianHandler> getMartianServerHandlerMap() {
        return martianServerHandlerMap;
    }

    public static Map<String, WebSocketHandler> getMartianWebSocketHandlerMap() {
        return martianWebSocketHandlerMap;
    }

    public static void addMartianServerHandler(String path, MagicianHandler magicianHandler) throws Exception {
        path = path.toUpperCase();
        if(martianServerHandlerMap.containsKey(path)
                || martianWebSocketHandlerMap.containsKey(path)){
            throw new Exception("已经存在地址为"+path+"的handler");
        }
        TCPServerConfig.martianServerHandlerMap.put(path, magicianHandler);
    }

    public static void addMartianWebSocketHandler(String path, WebSocketHandler webSocketHandler) throws Exception {
        if(path.equals("/")){
            throw new Exception("webSocket不可以监听根路径");
        }
        path = path.toUpperCase();
        if(martianServerHandlerMap.containsKey(path)
                || martianWebSocketHandlerMap.containsKey(path)){
            throw new Exception("已经存在地址为"+path+"的handler");
        }
        TCPServerConfig.martianWebSocketHandlerMap.put(path, webSocketHandler);
    }

    public static ProtocolCodec getProtocolCodec() {
        if(TCPServerConfig.protocolCodec == null){
            return new HttpProtocolCodec();
        }
        return protocolCodec;
    }

    public static void setProtocolCodec(ProtocolCodec protocolCodec) {
        TCPServerConfig.protocolCodec = protocolCodec;
    }
}
