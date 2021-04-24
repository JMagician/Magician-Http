package io.magician.tcp.protocol.execute.thread;

import io.magician.common.threadpool.ThreadPoolManager;
import io.magician.tcp.HttpServerConfig;
import io.magician.tcp.protocol.execute.ExecuteModel;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * 解析请求的线程管理
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
        int threadSize = HttpServerConfig.getExecuteThreadSize();
        blockingDeque = new LinkedBlockingDeque<>();

        for (int i = 0; i < threadSize; i++) {
            TCPHandlerThread TCPHandlerThread = new TCPHandlerThread(blockingDeque);
            TCPHandlerThread.start();
        }
    }
}
