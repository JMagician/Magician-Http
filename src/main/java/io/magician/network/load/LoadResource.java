package io.magician.network.load;

import io.magician.common.annotation.TCPHandler;
import io.magician.common.annotation.WebSocketHandler;
import io.magician.common.cache.MagicianHandlerCache;
import io.magician.common.util.ScanUtil;
import io.magician.network.handler.HttpBaseHandler;
import io.magician.network.handler.WebSocketBaseHandler;

import java.util.Set;

/**
 * 加载TCP所需资源
 */
public class LoadResource {

    /**
     * 加载handler
     * @param packageName
     * @throws Exception
     */
    public static void loadHandler(String packageName) throws Exception {

        Set<String> packageSet = ScanUtil.loadClass(packageName);

        for(String className : packageSet){
            Class<?> cls = Class.forName(className);
            TCPHandler tcpHandler = cls.getAnnotation(TCPHandler.class);
            WebSocketHandler webSocketHandler = cls.getAnnotation(WebSocketHandler.class);
            if(tcpHandler != null && webSocketHandler != null){
                throw new Exception("handler只能是TCP或者WebSocket，不可以两个都配，类名:" + className);
            }

            if(tcpHandler != null){
                MagicianHandlerCache.addHttpHandler(tcpHandler.path(), (HttpBaseHandler) cls.getDeclaredConstructor().newInstance());
            }

            if(webSocketHandler != null){
                MagicianHandlerCache.addWebSocketHandler(webSocketHandler.path(), (WebSocketBaseHandler) cls.getDeclaredConstructor().newInstance());
            }
        }
    }
}
