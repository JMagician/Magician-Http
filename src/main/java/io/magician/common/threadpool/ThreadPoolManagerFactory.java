package io.magician.common.threadpool;

import io.magician.tcp.protocol.threads.handler.thread.TCPHandlerThreadManager;
import io.magician.tcp.protocol.threads.codec.ParsingThreadManager;
import io.magician.udp.parsing.thread.UDPHandlerThreadManager;

/**
 * 线程池工厂
 */
public class ThreadPoolManagerFactory {

    /**
     * 解析TCP数据的线程池
     */
    public static final String TCP_CODEC = "TCP_CODEC";
    /**
     * 执行TCP业务逻辑的线程池
     */
    public static final String TCP_HANDLER = "TCP_HANDLER";
    /**
     * 执行UDP业务逻辑的线程池
     */
    public static final String UDP_HANDLER = "UDP_HANDLER";

    /**
     * 获取线程池
     * @param type
     * @return
     */
    public synchronized static ThreadPoolManager getThreadPoolManager(String type){
        switch (type){
            case TCP_CODEC:
                return ParsingThreadManager.getParsingThreadManager();
            case TCP_HANDLER:
                return TCPHandlerThreadManager.getHandlerThreadManager();
            case UDP_HANDLER:
                return UDPHandlerThreadManager.getUDPHandlerThreadManager();
        }
        return null;
    }
}
