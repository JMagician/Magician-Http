package io.magician.tcp;

import io.magician.tcp.handler.TCPBaseHandler;
import io.magician.tcp.codec.ProtocolCodec;
import io.magician.tcp.codec.impl.http.HttpProtocolCodec;
import io.magician.tcp.handler.WebSocketBaseHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * tcp服务配置
 */
public class TCPServerConfig {

    /**
     * 读取超时时间
     * 暂时没用到
     */
    @Deprecated
    private long readTimeout = 10000;
    /**
     * 写入超时时间
     */
    private long writeTimeout = 10000;
    /**
     * 每次读取大小
     */
    private int readSize = 1024;
    /**
     * 单个文件限制
     */
    private long fileSizeMax = 2*1024*1024;
    /**
     * 上传文件总大小限制
     */
    private long sizeMax = 10*1024*1024;
    /**
     * 长连接超时时间
     */
    private long keepTimeout = 10000;
    /**
     * 协议解析器
     */
    private ProtocolCodec protocolCodec = new HttpProtocolCodec();
    /**
     * 处理器
     */
    private Map<String, TCPBaseHandler> magicianHandlerMap = new HashMap<>();
    /**
     * webSocket处理器
     */
    private Map<String, WebSocketBaseHandler> webSocketHandlerMap = new HashMap<>();

    @Deprecated
    public long getReadTimeout() {
        return readTimeout;
    }

    @Deprecated
    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public long getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public int getReadSize() {
        return readSize;
    }

    public void setReadSize(int readSize) {
        this.readSize = readSize;
    }

    public long getFileSizeMax() {
        return fileSizeMax;
    }

    public void setFileSizeMax(long fileSizeMax) {
        this.fileSizeMax = fileSizeMax;
    }

    public long getSizeMax() {
        return sizeMax;
    }

    public void setSizeMax(long sizeMax) {
        this.sizeMax = sizeMax;
    }

    public Map<String, TCPBaseHandler> getMagicianHandlerMap() {
        return magicianHandlerMap;
    }

    public Map<String, WebSocketBaseHandler> getWebSocketHandlerMap() {
        return webSocketHandlerMap;
    }

    public void setMagicianHandlerMap(Map<String, TCPBaseHandler> magicianHandlerMap) {
        this.magicianHandlerMap = magicianHandlerMap;
    }

    public void setWebSocketHandlerMap(Map<String, WebSocketBaseHandler> webSocketHandlerMap) {
        this.webSocketHandlerMap = webSocketHandlerMap;
    }

    public void addMagicianHandler(String path, TCPBaseHandler tcpBaseHandler) throws Exception {
        path = path.toUpperCase();
        if(magicianHandlerMap.containsKey(path)
                || webSocketHandlerMap.containsKey(path)){
            throw new Exception("已经存在地址为["+path+"]的handler");
        }
        this.magicianHandlerMap.put(path, tcpBaseHandler);
    }

    public void addWebSocketHandler(String path, WebSocketBaseHandler webSocketBaseHandler) throws Exception {
        if(path.equals("/")){
            throw new Exception("webSocketHandler不可以监听根路径");
        }
        path = path.toUpperCase();
        if(magicianHandlerMap.containsKey(path)
                || webSocketHandlerMap.containsKey(path)){
            throw new Exception("已经存在地址为["+path+"]的handler");
        }
        this.webSocketHandlerMap.put(path, webSocketBaseHandler);
    }

    public long getKeepTimeout() {
        return keepTimeout;
    }

    public void setKeepTimeout(long keepTimeout) {
        this.keepTimeout = keepTimeout;
    }

    public ProtocolCodec getProtocolCodec() {
        if(this.protocolCodec == null){
            return new HttpProtocolCodec();
        }
        return protocolCodec;
    }

    public void setProtocolCodec(ProtocolCodec protocolCodec) {
        this.protocolCodec = protocolCodec;
    }
}
