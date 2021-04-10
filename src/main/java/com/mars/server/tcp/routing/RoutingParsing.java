package com.mars.server.tcp.routing;

import com.mars.server.MartianServerConfig;
import com.mars.server.tcp.http.constant.ReqMethod;
import com.mars.server.tcp.http.handler.MartianServerHandler;
import com.mars.server.tcp.http.model.HttpHeaders;
import com.mars.server.tcp.http.request.MartianHttpExchange;
import com.mars.server.tcp.websocket.WebSocketSession;
import com.mars.server.tcp.websocket.constant.WebSocketConstant;
import com.mars.server.tcp.websocket.constant.WebSocketEnum;
import com.mars.server.tcp.websocket.handler.WebSocketHandler;

import java.util.Map;

/**
 * 路由解析，判断是http还是websocket
 */
public class RoutingParsing {

    /**
     * 根据判断结果进行不同的处理
     * @param marsHttpExchange
     * @throws Exception
     */
    public static void parsing(MartianHttpExchange marsHttpExchange) throws Exception {

        Map<String, MartianServerHandler> martianServerHandlerMap = MartianServerConfig.getMartianServerHandlerMap();
        Map<String, WebSocketHandler> martianWebSocketHandlerMap = MartianServerConfig.getMartianWebSocketHandlerMap();

        String uri = marsHttpExchange.getRequestURI().toString();
        uri = getUri(uri);

        /* 判断是否为webSocket */
        if(isWebSocket(marsHttpExchange)){
            WebSocketHandler webSocketHandler = martianWebSocketHandlerMap.get(uri);
            if(webSocketHandler != null){
                /* 如果是socket就建立连接 */
                WebSocketSession socketSession = new WebSocketSession();
                socketSession.setMartianHttpExchange(marsHttpExchange);
                socketSession.setWebSocketHandler(webSocketHandler);
                RoutingJump.websocket(socketSession, WebSocketEnum.OPEN);
                return;
            }
            throw new Exception("没有找到对应的websocketHandler，handler:[" + uri + "]");
        }

        /* 不是webSocket的话，就当http处理 */
        MartianServerHandler rootServerHandler = martianServerHandlerMap.get("/");
        MartianServerHandler rouServerHandler = martianServerHandlerMap.get(uri);
        if(rootServerHandler == null && rouServerHandler == null){
            throw new Exception("没有找到对应的httpHandler，handler:[" + uri + "]");
        }

        if(rootServerHandler != null){
            RoutingJump.http(marsHttpExchange, rootServerHandler);
        }
        if(rouServerHandler != null){
            RoutingJump.http(marsHttpExchange, rouServerHandler);
        }
    }

    /**
     * 判断是不是socket连接
     * @param marsHttpExchange
     * @return
     */
    private static boolean isWebSocket(MartianHttpExchange marsHttpExchange){
        String method = marsHttpExchange.getRequestMethod();
        if(!method.toUpperCase().equals(ReqMethod.GET.toString())){
            return false;
        }

        HttpHeaders httpHeaders = marsHttpExchange.getRequestHeaders();
        String upgrade = httpHeaders.get(WebSocketConstant.UPGRADE);
        String connection = httpHeaders.get(WebSocketConstant.CONNECTION);
        String swKey = httpHeaders.get(WebSocketConstant.SEC_WEBSOCKET_KEY);
        if(upgrade == null || connection == null || swKey == null){
            return false;
        }

        if(!connection.toUpperCase().equals(WebSocketConstant.UPGRADE)){
            return false;
        }

        return true;
    }

    /**
     * 获取纯净的uri
     * @param uri
     * @return
     */
    private static String getUri(String uri){
        int startIndex = 0;
        int endIndex = uri.length();
        if(!uri.startsWith("/")){
            startIndex = uri.indexOf("/");
        }

        if(uri.lastIndexOf("?") > -1){
            endIndex = uri.lastIndexOf("?");
        }
        return uri.substring(startIndex, endIndex);
    }
}
