package io.magician.tcp.websocket.process;

import io.magician.tcp.websocket.WebSocketSession;
import io.magician.tcp.websocket.cache.ConnectionCache;

import java.util.*;

/**
 * socket管理
 */
public class SocketConnectionProcess {

    /**
     * 开始处理连接
     */
    public static void process(){
        Map<String, WebSocketSession> socketSessionMap = ConnectionCache.getSessionMap();
        if(socketSessionMap == null || socketSessionMap.size() < 1){
            return;
        }
    }

    /**
     * 监听通道，获取socket发来的消息
     */
    private static void monitor() {
        // TODO 开发中
    }
}
