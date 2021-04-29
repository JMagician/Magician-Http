package io.magician.tcp.codec.impl.websocket.handler;

import io.magician.tcp.codec.impl.websocket.connection.WebSocketSession;

/**
 * socket处理器
 */
public interface WebSocketHandler {

    /**
     * 发起连接时调用
     * @param session
     */
    void onOpen(WebSocketSession session);

    /**
     * 断开连接时调用
     * @param session
     */
    void onClose(WebSocketSession session);

    /**
     * 收到消息时调用
     * @param message
     * @param session
     */
    void onMessage(String message, WebSocketSession session);

}
