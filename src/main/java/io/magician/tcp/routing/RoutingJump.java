package io.magician.tcp.routing;

import io.magician.tcp.http.handler.MagicianHandler;
import io.magician.tcp.http.parsing.WriteCompletionHandler;
import io.magician.tcp.http.parsing.param.ParamParsing;
import io.magician.tcp.http.request.MagicianHttpExchange;
import io.magician.tcp.http.request.MagicianRequest;
import io.magician.tcp.websocket.WebSocketSession;
import io.magician.tcp.websocket.cache.ConnectionCache;
import io.magician.tcp.websocket.constant.WebSocketEnum;
import io.magician.tcp.websocket.parsing.WriteCreateSocketConnectionHandler;

/**
 * 路由跳转
 */
public class RoutingJump {

    /**
     * webSocket处理
     * @param socketSession
     */
    public static void websocket(WebSocketSession socketSession, WebSocketEnum webSocketEnum) throws Exception {
        switch (webSocketEnum){
            case OPEN:
                ConnectionCache.addSession(socketSession);
                socketSession.getWebSocketHandler().onOpen(socketSession);
                WriteCreateSocketConnectionHandler.builder(socketSession).completed();
                break;
            case CLOSE:
                socketSession.getWebSocketHandler().onClose(socketSession);
                ConnectionCache.removeSession(socketSession.getId());
                break;
        }
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
        WriteCompletionHandler.builder(httpExchange).completed();
    }
}
