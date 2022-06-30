package io.magician.network.handler;

import io.magician.application.request.WebSocketSession;

/**
 * socket处理器
 */
public interface WebSocketBaseHandler {

    /**
     * 发起连接时调用
     */
    void onOpen(WebSocketSession webSocketSession);

    /**
     * 断开连接时调用
     */
    void onClose(WebSocketSession webSocketSession);

    /**
     * 收到消息时调用
     * @param message
     */
    void onMessage(WebSocketSession webSocketSession, byte[] message);

}
