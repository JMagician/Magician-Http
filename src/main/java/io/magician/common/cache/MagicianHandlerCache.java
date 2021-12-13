package io.magician.common.cache;

import io.magician.network.handler.HttpBaseHandler;
import io.magician.network.handler.WebSocketBaseHandler;
import io.magician.network.processing.exchange.WebSocketExchange;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MagicianHandlerCache {

    private static Map<String, HttpBaseHandler> httpHandler = new ConcurrentHashMap<>();
    private static Map<String, WebSocketBaseHandler> webSocketHandler = new ConcurrentHashMap<>();
    private static Map<String, WebSocketExchange> webSocketSessionMap = new ConcurrentHashMap<>();

    public static void addHttpHandler(String path, HttpBaseHandler httpBaseHandler){
        if(!path.startsWith("/")){
            path = "/" + path;
        }
        httpHandler.put(path, httpBaseHandler);
    }

    public static void addWebSocketHandler(String path, WebSocketBaseHandler webSocketBaseHandler){
        if(!path.startsWith("/")){
            path = "/" + path;
        }
        webSocketHandler.put(path, webSocketBaseHandler);
    }

    public static HttpBaseHandler getHttpHandler(String path){
        return httpHandler.get(path);
    }

    public static WebSocketBaseHandler getWebSocketHandler(String path){
        return webSocketHandler.get(path);
    }

    public static void addWebSocketSessionMap(String channelId, WebSocketExchange webSocketExchange){
        webSocketSessionMap.put(channelId, webSocketExchange);
    }

    public static WebSocketExchange getWebSocketSessionMap(String channelId){
        return webSocketSessionMap.get(channelId);
    }

    public static void removeWebSocketExchange(String channelId){
        webSocketSessionMap.remove(channelId);
    }
}
