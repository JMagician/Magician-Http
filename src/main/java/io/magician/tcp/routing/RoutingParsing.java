package io.magician.tcp.routing;

import io.magician.tcp.http.server.HttpServerConfig;
import io.magician.tcp.http.request.MagicianHttpExchange;
import io.magician.tcp.websocket.WebSocketSession;
import io.magician.tcp.websocket.constant.WebSocketConstant;
import io.magician.tcp.http.constant.ReqMethod;
import io.magician.tcp.http.handler.MagicianHandler;
import io.magician.tcp.http.model.HttpHeaders;
import io.magician.tcp.websocket.constant.WebSocketEnum;
import io.magician.tcp.websocket.handler.WebSocketHandler;

import java.util.Map;

/**
 * 路由解析，判断是http还是websocket
 */
public class RoutingParsing {

    /**
     * 根据判断结果进行不同的处理
     * @param httpExchange
     * @throws Exception
     */
    public static void parsing(MagicianHttpExchange httpExchange) throws Exception {

        Map<String, MagicianHandler> martianServerHandlerMap = HttpServerConfig.getMartianServerHandlerMap();
        Map<String, WebSocketHandler> martianWebSocketHandlerMap = HttpServerConfig.getMartianWebSocketHandlerMap();

        String uri = httpExchange.getRequestURI().toString();
        uri = getUri(uri);

        /* 判断是否为webSocket */
        if(isWebSocket(httpExchange)){
            WebSocketHandler webSocketHandler = martianWebSocketHandlerMap.get(uri);
            if(webSocketHandler != null){
                /* 如果是socket就建立连接 */
                WebSocketSession socketSession = new WebSocketSession();
                socketSession.setMagicianHttpExchange(httpExchange);
                socketSession.setWebSocketHandler(webSocketHandler);
                RoutingJump.websocket(socketSession, WebSocketEnum.OPEN);
                return;
            }
            throw new Exception("没有找到对应的websocketHandler，handler:[" + uri + "]");
        }

        /* 不是webSocket的话，就当http处理 */
        MagicianHandler rootServerHandler = martianServerHandlerMap.get("/");
        MagicianHandler rouServerHandler = martianServerHandlerMap.get(uri);
        if(rootServerHandler == null && rouServerHandler == null){
            throw new Exception("没有找到对应的httpHandler，handler:[" + uri + "]");
        }

        if(rootServerHandler != null){
            RoutingJump.http(httpExchange, rootServerHandler);
        }
        if(rouServerHandler != null){
            RoutingJump.http(httpExchange, rouServerHandler);
        }
    }

    /**
     * 判断是不是socket连接
     * @param httpExchange
     * @return
     */
    private static boolean isWebSocket(MagicianHttpExchange httpExchange){
        String method = httpExchange.getRequestMethod();
        if(!method.toUpperCase().equals(ReqMethod.GET.toString())){
            return false;
        }

        HttpHeaders httpHeaders = httpExchange.getRequestHeaders();
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
        return uri.substring(startIndex, endIndex).toUpperCase();
    }
}
