package io.magician.tcp.protocol.threads.handler.thread;

import io.magician.common.threadpool.ThreadPoolManager;
import io.magician.tcp.TCPServerConfig;
import io.magician.tcp.protocol.threads.handler.ExecuteModel;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * 执行handler的线程管理
 */
public class TCPHandlerThreadManager extends ThreadPoolManager<ExecuteModel> {

    private static TCPHandlerThreadManager threadManager = new TCPHandlerThreadManager();

    private TCPHandlerThreadManager(){
        init();
    }

    public static TCPHandlerThreadManager getHandlerThreadManager(){
        return threadManager;
    }

    /**
     * 初始化线程数量
     */
    public void init() {
        int threadSize = TCPServerConfig.getExecuteThreadSize();
        blockingDeque = new LinkedBlockingDeque<>();

        for (int i = 0; i < threadSize; i++) {
            TCPHandlerThread TCPHandlerThread = new TCPHandlerThread(blockingDeque);
            TCPHandlerThread.start();
        }
    }
}
