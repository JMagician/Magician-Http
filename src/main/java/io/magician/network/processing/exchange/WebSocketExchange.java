package io.magician.network.processing.exchange;

import io.magician.application.request.WebSocketSession;
import io.magician.network.handler.WebSocketBaseHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

/**
 * WebSocket Data Exchanger
 */
public class WebSocketExchange {

    /**
     * object of netty
     */
    private WebSocketServerHandshaker webSocketServerHandshaker;

    /**
     * The handler bound to this connection
     */
    private WebSocketBaseHandler webSocketBaseHandler;

    /**
     * connection session
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
