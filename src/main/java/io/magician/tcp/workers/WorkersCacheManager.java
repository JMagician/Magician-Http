package io.magician.tcp.workers;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * workers缓存管理
 * 一般一个连接就会对应一个worker，连接断了以后就会删除这个worker
 * worker 什么时候运行，什么时候停止，什么时候清除 由WorkerThread线程管理
 */
public class WorkersCacheManager {

    /**
     * 缓存
     */
    private static Map<SocketChannel, Worker> protocolDataModelMap = new ConcurrentHashMap<>();

    public static Map<SocketChannel, Worker> getProtocolDataModelMap() {
        return protocolDataModelMap;
    }

    /**
     * 获取一个worker
     * @param channel
     * @return
     */
    public static Worker get(SocketChannel channel){
        Worker worker = protocolDataModelMap.get(channel);
        if(worker == null){
            worker = new Worker();
            protocolDataModelMap.put(channel, worker);
        }

        return worker;
    }

    /**
     * 添加一个worker
     * @param channel
     * @param worker
     */
    public static void put(SocketChannel channel, Worker worker){
        if(!protocolDataModelMap.containsKey(channel)){
            protocolDataModelMap.put(channel, worker);
        }
    }

    /**
     * 删除一个worker
     * @param channel
     */
    public static void remove(SocketChannel channel){
        if(protocolDataModelMap.containsKey(channel)){
            protocolDataModelMap.remove(channel);
        }
    }

    /**
     * 清除缓存
     * @param channel
     */
    public static void clear(SocketChannel channel){
        protocolDataModelMap.put(channel, new Worker());
    }
}
