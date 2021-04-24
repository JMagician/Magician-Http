package io.magician.common.threadpool;

import io.magician.tcp.protocol.execute.thread.TCPHandlerThreadManager;
import io.magician.tcp.protocol.thread.ParsingThreadManager;
import io.magician.udp.parsing.thread.UDPHandlerThreadManager;

/**
 * 线程池工厂
 */
public class ThreadPoolManagerFactory {

    /**
     * 解析TCP数据的线程池
     */
    public static final String TCP_READ = "TCP_READ";
    /**
     * 执行TCP业务逻辑的线程池
     */
    public static final String TCP_EXECUTE = "TCP_EXECUTE";

    /**
     * 执行UDP业务逻辑的线程池
     */
    public static final String UDP_EXECUTE = "UDP_EXECUTE";

    /**
     * 获取线程池
     * @param type
     * @return
     */
    public synchronized static ThreadPoolManager getThreadPoolManager(String type){
        switch (type){
            case TCP_READ:
                return ParsingThreadManager.getParsingThreadManager();
            case TCP_EXECUTE:
                return TCPHandlerThreadManager.getHandlerThreadManager();
            case UDP_EXECUTE:
                return UDPHandlerThreadManager.getUDPHandlerThreadManager();
        }
        return null;
    }
}
