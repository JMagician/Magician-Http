package io.magician.common.cache;

import io.magician.network.handler.HttpBaseHandler;
import io.magician.network.handler.WebSocketBaseHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MagicianHandlerCache {

    private static Map<String, HttpBaseHandler> httpHandler = new ConcurrentHashMap<>();
    private static Map<String, WebSocketBaseHandler> webSocketHandler = new ConcurrentHashMap<>();

    public static void addHttpHandler(String path, HttpBaseHandler httpBaseHandler){
        httpHandler.put(path, httpBaseHandler);
    }

    public static void addWebSocketHandler(String path, WebSocketBaseHandler webSocketBaseHandler){
        webSocketHandler.put(path, webSocketBaseHandler);
    }

    public static HttpBaseHandler getHttpHandler(String path){
        return httpHandler.get(path);
    }

    public static WebSocketBaseHandler getWebSocketHandler(String path){
        return webSocketHandler.get(path);
    }
}
