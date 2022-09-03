package io.magician.network.load;

import io.magician.common.annotation.HttpHandler;
import io.magician.common.annotation.WebSocketHandler;
import io.magician.common.cache.MagicianHandlerCache;
import io.magician.common.util.ScanUtil;
import io.magician.network.handler.HttpBaseHandler;
import io.magician.network.handler.WebSocketBaseHandler;

import java.util.Set;

/**
 * Load the required resources
 */
public class LoadResource {

    /**
     * load handler
     * @param packageName
     * @throws Exception
     */
    public static void loadHandler(String packageName) throws Exception {

        Set<String> packageSet = ScanUtil.loadClass(packageName);

        for(String className : packageSet){
            Class<?> cls = Class.forName(className);
            HttpHandler httpHandler = cls.getAnnotation(HttpHandler.class);
            WebSocketHandler webSocketHandler = cls.getAnnotation(WebSocketHandler.class);
            if(httpHandler != null && webSocketHandler != null){
                throw new Exception("handler can only be HTTP or WebSocket, not both, class name:" + className);
            }

            if(httpHandler != null){
                MagicianHandlerCache.addHttpHandler(httpHandler.path(), (HttpBaseHandler) cls.getDeclaredConstructor().newInstance());
            }

            if(webSocketHandler != null){
                MagicianHandlerCache.addWebSocketHandler(webSocketHandler.path(), (WebSocketBaseHandler) cls.getDeclaredConstructor().newInstance());
            }
        }
    }
}
