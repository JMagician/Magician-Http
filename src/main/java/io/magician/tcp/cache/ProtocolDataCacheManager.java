package io.magician.tcp.cache;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtocolDataCacheManager {

    public static Map<SocketChannel, ProtocolDataModel> protocolDataModelMap = new ConcurrentHashMap<>();

    public static ProtocolDataModel get(SocketChannel channel){
        ProtocolDataModel protocolDataModel = protocolDataModelMap.get(channel);
        if(protocolDataModel == null){
            protocolDataModel = new ProtocolDataModel();
        }

        return protocolDataModel;
    }

    public static void put(SocketChannel channel, ProtocolDataModel protocolDataModel){
        if(!protocolDataModelMap.containsKey(channel)){
            protocolDataModelMap.put(channel, protocolDataModel);
        }
    }

    public static void remove(SocketChannel channel){
        if(protocolDataModelMap.containsKey(channel)){
            protocolDataModelMap.remove(channel);
        }
    }
}
