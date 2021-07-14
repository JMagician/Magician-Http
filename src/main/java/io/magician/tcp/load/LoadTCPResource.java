package io.magician.tcp.load;

import io.magician.common.annotation.TCPHandler;
import io.magician.common.annotation.WebSocketHandler;
import io.magician.common.util.ScanUtil;
import io.magician.tcp.TCPServerConfig;
import io.magician.tcp.handler.TCPBaseHandler;
import io.magician.tcp.handler.WebSocketBaseHandler;

import java.util.Set;

public class LoadTCPResource {

    /**
     * 加载handler
     * @param packageName
     * @param tcpServerConfig
     * @throws Exception
     */
    public static void loadHandler(String packageName, TCPServerConfig tcpServerConfig) throws Exception {

        Set<String> packageSet = ScanUtil.loadClass(packageName);

        for(String className : packageSet){
            Class<?> cls = Class.forName(className);
            TCPHandler tcpHandler = cls.getAnnotation(TCPHandler.class);
            WebSocketHandler webSocketHandler = cls.getAnnotation(WebSocketHandler.class);
            if(tcpHandler != null && webSocketHandler != null){
                throw new Exception("handler只能是TCP或者WebSocket，不可以两个都配，类名:" + className);
            }

            if(tcpHandler != null){
                tcpServerConfig.addMagicianHandler(tcpHandler.path(), (TCPBaseHandler) cls.getDeclaredConstructor().newInstance());
            }

            if(webSocketHandler != null){
                tcpServerConfig.addWebSocketHandler(webSocketHandler.path(), (WebSocketBaseHandler) cls.getDeclaredConstructor().newInstance());
            }
        }
    }
}
