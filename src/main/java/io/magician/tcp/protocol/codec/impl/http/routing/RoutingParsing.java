package io.magician.tcp.protocol.codec.impl.http.routing;

import io.magician.tcp.TCPServerConfig;
import io.magician.tcp.protocol.codec.impl.http.request.MagicianHttpExchange;
import io.magician.tcp.protocol.codec.impl.websocket.constant.WebSocketConstant;
import io.magician.tcp.protocol.codec.impl.http.constant.ReqMethod;
import io.magician.tcp.handler.MagicianHandler;
import io.magician.tcp.protocol.codec.impl.http.model.HttpHeaders;
import io.magician.tcp.protocol.codec.impl.websocket.handler.WebSocketHandler;

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

        Map<String, MagicianHandler> martianServerHandlerMap = TCPServerConfig.getMartianServerHandlerMap();
        Map<String, WebSocketHandler> martianWebSocketHandlerMap = TCPServerConfig.getMartianWebSocketHandlerMap();

        String uri = httpExchange.getRequestURI().toString();
        uri = getUri(uri);

        /* 判断是否为webSocket */
        if(isWebSocket(httpExchange)){
            WebSocketHandler webSocketHandler = martianWebSocketHandlerMap.get(uri);
            if(webSocketHandler != null){
                /* 如果是socket就建立连接 */
                RoutingJump.websocket(httpExchange, webSocketHandler);
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
        if(!uri.startsWith("/")){
            uri = "/"+uri;
        }
        int endIndex = uri.length();
        if(uri.lastIndexOf("?") > -1){
            endIndex = uri.lastIndexOf("?");
        }
        return uri.substring(0, endIndex).toUpperCase();
    }
}
