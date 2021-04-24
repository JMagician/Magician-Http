package io.magician.tcp.websocket;

import io.magician.tcp.websocket.constant.WebSocketEnum;

import java.io.ByteArrayOutputStream;

/**
 * webSocket数据中转器
 */
public class WebSocketExchange {

    /**
     * 会话
     */
    private WebSocketSession webSocketSession;
    /**
     * webSocket状态
     */
    private WebSocketEnum webSocketEnum;
    /**
     * 报文数据
     */
    private ByteArrayOutputStream outputStream;

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public WebSocketEnum getWebSocketEnum() {
        return webSocketEnum;
    }

    public void setWebSocketEnum(WebSocketEnum webSocketEnum) {
        this.webSocketEnum = webSocketEnum;
    }

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(ByteArrayOutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
