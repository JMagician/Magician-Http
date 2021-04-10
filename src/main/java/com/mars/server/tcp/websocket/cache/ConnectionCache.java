package com.mars.server.tcp.websocket.cache;

import com.mars.server.tcp.http.util.ChannelUtil;
import com.mars.server.tcp.websocket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接缓存管理
 */
public class ConnectionCache {

    private static Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    /**
     * 添加一个连接
     * @param session
     */
    public static void addSession(WebSocketSession session){
        if(!sessionMap.containsKey(session.getId())){
            sessionMap.put(session.getId(), session);
        }

    }

    /**
     * 删除一个连接
     * @param key
     */
    public static void removeSession(String key){
        WebSocketSession socketSession = sessionMap.get(key);
        if(socketSession != null){
            ChannelUtil.close(socketSession.getMartianHttpExchange().getSocketChannel());
            sessionMap.remove(key);
        }
    }

    /**
     * 获取session
     * @param key
     * @return
     */
    public static WebSocketSession getSession(String key){
        return sessionMap.get(key);
    }

    /**
     * 是否存在
     * @param channel
     * @return
     */
    public static Boolean existChannel(String channel){
        return sessionMap.get(channel) != null;
    }

    /**
     * 获取所有通道
     * @return
     */
    public static Map<String, WebSocketSession> getSessionMap() {
        return sessionMap;
    }
}
