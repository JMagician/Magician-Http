package com.mars.server.tcp.routing;

import com.mars.server.tcp.http.handler.MartianServerHandler;
import com.mars.server.tcp.http.handler.ext.HttpExchangeHandler;
import com.mars.server.tcp.http.handler.ext.HttpRequestHandler;
import com.mars.server.tcp.http.parsing.WriteParsing;
import com.mars.server.tcp.http.parsing.param.ParamParsing;
import com.mars.server.tcp.http.request.MartianHttpExchange;
import com.mars.server.tcp.http.request.MartianHttpRequest;
import com.mars.server.tcp.websocket.WebSocketSession;
import com.mars.server.tcp.websocket.cache.ConnectionCache;
import com.mars.server.tcp.websocket.constant.WebSocketEnum;
import com.mars.server.tcp.websocket.parsing.SocketWriteParsing;
import com.mars.server.tcp.websocket.process.SocketConnectionProcess;

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
                SocketWriteParsing.builder(socketSession).responseText();

                SocketConnectionProcess.process();
                break;
            case CLOSE:
                socketSession.getWebSocketHandler().onClose(socketSession);
                ConnectionCache.removeSession(socketSession.getId());
                break;
        }
    }

    /**
     * http处理
     * @param marsHttpExchange
     * @throws Exception
     */
    public static void http(MartianHttpExchange marsHttpExchange, MartianServerHandler marsServerHandler) throws Exception {
        /* 执行handler */
        if (marsServerHandler instanceof HttpRequestHandler) {
            MartianHttpRequest martianHttpRequest = new MartianHttpRequest();
            martianHttpRequest.setMartianHttpExchange(marsHttpExchange);
            martianHttpRequest = ParamParsing.getHttpMarsRequest(martianHttpRequest);
            marsServerHandler.request(martianHttpRequest);
        } else if (marsServerHandler instanceof HttpExchangeHandler) {
            marsServerHandler.request(marsHttpExchange);
        }
        /* 响应数据 */
        WriteParsing.builder(marsHttpExchange).responseData();
    }
}
