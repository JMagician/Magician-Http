package io.magician.udp.parsing.thread;

import io.magician.common.threadpool.ThreadPoolManager;
import io.magician.udp.UDPServerConfig;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 管理执行业务逻辑的线程
 */
public class UDPHandlerThreadManager extends ThreadPoolManager<ByteArrayOutputStream> {

    private static UDPHandlerThreadManager UDPHandlerThreadManager = new UDPHandlerThreadManager();

    public UDPHandlerThreadManager(){
        init();
    }

    public static UDPHandlerThreadManager getUDPHandlerThreadManager(){
        return UDPHandlerThreadManager;
    }

    /**
     * 根据配置的线程数量 生成对应的线程
     */
    public void init(){
        int threadSize = UDPServerConfig.getThreadSize();
        blockingDeque = new LinkedBlockingDeque<>();

        for(int i=0;i<threadSize;i++){
            UDPHandlerThread UDPHandlerThread = new UDPHandlerThread(blockingDeque);
            UDPHandlerThread.start();
        }
    }
}
