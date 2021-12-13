package io.magician.network.processing.exchange;

import io.magician.application.request.WebSocketSession;
import io.magician.network.handler.WebSocketBaseHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

/**
 * WebSocket数据交换器
 */
public class WebSocketExchange {

    /**
     * netty原生对象
     */
    private WebSocketServerHandshaker webSocketServerHandshaker;

    /**
     * 本次连接绑定的handler
     */
    private WebSocketBaseHandler webSocketBaseHandler;

    /**
     * 连接会话
     */
    private WebSocketSession webSocketSession;

    public WebSocketServerHandshaker getWebSocketServerHandshaker() {
        return webSocketServerHandshaker;
    }

    public void setWebSocketServerHandshaker(WebSocketServerHandshaker webSocketServerHandshaker) {
        this.webSocketServerHandshaker = webSocketServerHandshaker;
    }

    public WebSocketBaseHandler getWebSocketBaseHandler() {
        return webSocketBaseHandler;
    }

    public void setWebSocketBaseHandler(WebSocketBaseHandler webSocketBaseHandler) {
        this.webSocketBaseHandler = webSocketBaseHandler;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }
}
