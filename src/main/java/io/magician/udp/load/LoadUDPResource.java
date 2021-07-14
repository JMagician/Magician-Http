package io.magician.udp.load;

import io.magician.common.annotation.UDPHandler;
import io.magician.common.util.ScanUtil;
import io.magician.udp.UDPServerConfig;
import io.magician.udp.handler.UDPBaseHandler;

import java.util.Set;

/**
 * 加载UDP所需资源
 */
public class LoadUDPResource {

    /**
     * 加载handler
     * @param packageName
     * @throws Exception
     */
    public static void loadHandler(String packageName) throws Exception {

        Set<String> packageSet = ScanUtil.loadClass(packageName);

        for(String className : packageSet){
            Class<?> cls = Class.forName(className);
            UDPHandler tcpHandler = cls.getAnnotation(UDPHandler.class);

            if(tcpHandler != null){
                UDPServerConfig.setUdpBaseHandler((UDPBaseHandler) cls.getDeclaredConstructor().newInstance());
            }
        }
    }
}
