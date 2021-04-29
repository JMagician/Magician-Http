package io.magician.tcp;

import io.magician.tcp.handler.MagicianHandler;
import io.magician.tcp.codec.ProtocolCodec;
import io.magician.tcp.codec.impl.http.HttpProtocolCodec;
import io.magician.tcp.codec.impl.websocket.handler.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * tcp服务配置
 */
public class TCPServerConfig {

    /**
     * 读取超时时间
     */
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
     * 协议解析器
     */
    private ProtocolCodec protocolCodec = new HttpProtocolCodec(this);
    /**
     * 处理器
     */
    private Map<String, MagicianHandler> martianServerHandlerMap = new HashMap<>();
    /**
     * webSocket处理器
     */
    private Map<String, WebSocketHandler> martianWebSocketHandlerMap = new HashMap<>();

    public long getReadTimeout() {
        return readTimeout;
    }

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

    public Map<String, MagicianHandler> getMartianServerHandlerMap() {
        return martianServerHandlerMap;
    }

    public Map<String, WebSocketHandler> getMartianWebSocketHandlerMap() {
        return martianWebSocketHandlerMap;
    }

    public void addMartianServerHandler(String path, MagicianHandler magicianHandler) throws Exception {
        path = path.toUpperCase();
        if(martianServerHandlerMap.containsKey(path)
                || martianWebSocketHandlerMap.containsKey(path)){
            throw new Exception("已经存在地址为["+path+"]的handler");
        }
        this.martianServerHandlerMap.put(path, magicianHandler);
    }

    public void addMartianWebSocketHandler(String path, WebSocketHandler webSocketHandler) throws Exception {
        if(path.equals("/")){
            throw new Exception("webSocket不可以监听根路径");
        }
        path = path.toUpperCase();
        if(martianServerHandlerMap.containsKey(path)
                || martianWebSocketHandlerMap.containsKey(path)){
            throw new Exception("已经存在地址为["+path+"]的handler");
        }
        this.martianWebSocketHandlerMap.put(path, webSocketHandler);
    }

    public ProtocolCodec getProtocolCodec() {
        if(this.protocolCodec == null){
            return new HttpProtocolCodec(this);
        }
        return protocolCodec;
    }

    public void setProtocolCodec(ProtocolCodec protocolCodec) {
        this.protocolCodec = protocolCodec;
    }
}
