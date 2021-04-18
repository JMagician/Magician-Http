package io.magician.tcp.websocket.process;

import io.magician.tcp.HttpServerConfig;
import io.magician.tcp.websocket.handler.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * socket管理
 */
public class SocketConnectionProcess {

    private static Logger logger = LoggerFactory.getLogger(SocketConnectionProcess.class);

    /**
     * 开始处理连接
     */
    public static void process() {
        try {
            Map<String, WebSocketHandler> stringWebSocketHandlerMap = HttpServerConfig.getMartianWebSocketHandlerMap();
            if(stringWebSocketHandlerMap == null || stringWebSocketHandlerMap.size() < 1){
                return;
            }

            /* 200毫秒读一次socketChannel, 用来实时接口数据 */
            new Timer().scheduleAtFixedRate(new SocketTimerTask(),
                            new Date(),
                            100);
        } catch (Exception e){
            logger.error("开启socket监听异常", e);
        }
    }
}
