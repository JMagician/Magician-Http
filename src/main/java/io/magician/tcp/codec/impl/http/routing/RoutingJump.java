package io.magician.tcp.codec.impl.http.routing;

import io.magician.tcp.codec.impl.http.parsing.param.ParamParsing;
import io.magician.tcp.codec.impl.websocket.connection.WebSocketSession;
import io.magician.tcp.handler.MagicianHandler;
import io.magician.tcp.codec.impl.http.parsing.HttpMessageWrite;
import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;
import io.magician.tcp.codec.impl.http.request.MagicianRequest;
import io.magician.tcp.codec.impl.websocket.handler.WebSocketHandler;
import io.magician.tcp.codec.impl.websocket.parsing.WebSocketMessageWrite;

/**
 * 根据不同的协议执行不同的逻辑
 */
public class RoutingJump {

    /**
     * webSocket处理
     * @param httpExchange
     * @param webSocketHandler
     */
    public static void websocket(MagicianHttpExchange httpExchange, WebSocketHandler webSocketHandler) throws Exception {
        WebSocketSession socketSession = new WebSocketSession();
        socketSession.setMagicianHttpExchange(httpExchange);
        socketSession.setWebSocketHandler(webSocketHandler);

        /* 将session加入附件 */
        httpExchange.getSelectionKey().attach(socketSession);

        socketSession.getWebSocketHandler().onOpen(socketSession);
        WebSocketMessageWrite.builder(socketSession).completed();
    }

    /**
     * http处理
     * @param httpExchange
     * @throws Exception
     */
    public static void http(MagicianHttpExchange httpExchange, MagicianHandler serverHandler) throws Exception {
        /* 执行handler */
        MagicianRequest magicianRequest = new MagicianRequest();
        magicianRequest.setMartianHttpExchange(httpExchange);
        magicianRequest = ParamParsing.getMagicianRequest(magicianRequest);

        serverHandler.request(magicianRequest);
        /* 响应数据 */
        HttpMessageWrite.builder(httpExchange).completed();
    }
}
