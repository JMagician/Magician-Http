package io.magician.tcp.codec.impl.websocket.connection;

import io.magician.common.constant.CommonConstant;
import io.magician.common.util.ByteUtil;
import io.magician.common.util.ChannelUtil;
import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;
import io.magician.tcp.handler.WebSocketBaseHandler;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.UUID;

/**
 * webSocket会话
 */
public class WebSocketSession {

    /**
     * ID，UUID生成，每个session唯一
     */
    private String id;
    /**
     * http请求处理器
     */
    private MagicianHttpExchange magicianHttpExchange;
    /**
     * 这个session要用的handler
     */
    private WebSocketBaseHandler webSocketBaseHandler;

    private long writeTimeout;

    public WebSocketSession(long writeTimeout){
        this.id = UUID.randomUUID().toString();
        this.writeTimeout = writeTimeout;
    }

    public MagicianHttpExchange getMagicianHttpExchange() {
        return magicianHttpExchange;
    }

    public void setMagicianHttpExchange(MagicianHttpExchange magicianHttpExchange) {
        this.magicianHttpExchange = magicianHttpExchange;
    }

    public WebSocketBaseHandler getWebSocketBaseHandler() {
        return webSocketBaseHandler;
    }

    public void setWebSocketBaseHandler(WebSocketBaseHandler webSocketBaseHandler) {
        this.webSocketBaseHandler = webSocketBaseHandler;
    }

    public String getId() {
        return id;
    }

    /**
     * 发送二进制消息
     * @param message
     */
    public synchronized void send(byte[] message) throws Exception {
        SocketChannel channel = magicianHttpExchange.getSocketChannel();
        if(channel == null || !channel.isOpen()){
            throw new Exception("客户端已断开");
        }

        int startIndex = 2;
        byte[] boardCastData = null;

        if(message.length < 126){
            boardCastData = new byte[2 + message.length];
            boardCastData[0] = (byte) 0x81;
            boardCastData[1] = (byte) message.length;
        } else if(message.length >= 126 && message.length < 65535){
            boardCastData = new byte[4 + message.length];
            byte[] bytes = ByteUtil.intToBytes(message.length, 2);
            boardCastData[0] = (byte) 0x81;
            boardCastData[1] = 126;
            boardCastData[2] = bytes[0];
            boardCastData[3] = bytes[1];
            startIndex = 4;
        } else {
            throw new Exception("最大支持的消息长度为65534个字节");
        }

        System.arraycopy(message, 0, boardCastData, startIndex, message.length);

        ByteBuffer byteBuffer = ByteBuffer.wrap(boardCastData);

        ChannelUtil.write(byteBuffer, channel, writeTimeout);
    }

    /**
     * 发送字符串消息
     * @param message
     * @throws UnsupportedEncodingException
     */
    public synchronized void send(String message) throws Exception {
        send(message.getBytes(CommonConstant.ENCODING));
    }
}
