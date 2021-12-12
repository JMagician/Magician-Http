package io.magician.network.handler;

/**
 * socket处理器
 */
public interface WebSocketBaseHandler {

    /**
     * 发起连接时调用
     */
    void onOpen();

    /**
     * 断开连接时调用
     */
    void onClose();

    /**
     * 收到消息时调用
     * @param message
     */
    void onMessage(String message);

}
