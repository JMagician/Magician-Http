package io.magician.tcp.codec.impl.websocket.cache;

import io.magician.tcp.codec.impl.websocket.connection.WebSocketExchange;

/**
 * WebSocket解析器附件实体类
 */
public class WebSocketParsingModel {

    /**
     * WebSocket数据中专器
     */
    private WebSocketExchange webSocketExchange;

    public WebSocketExchange getWebSocketExchange() {
        return webSocketExchange;
    }

    public void setWebSocketExchange(WebSocketExchange webSocketExchange) {
        this.webSocketExchange = webSocketExchange;
    }
}
