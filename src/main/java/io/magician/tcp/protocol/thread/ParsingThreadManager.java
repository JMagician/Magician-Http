package io.magician.tcp.protocol.thread;

import io.magician.common.threadpool.ThreadPoolManager;
import io.magician.tcp.HttpServerConfig;
import io.magician.tcp.protocol.model.ProtocolDataModel;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * 解析请求的线程管理
 */
public class ParsingThreadManager extends ThreadPoolManager<ProtocolDataModel> {

    private static ParsingThreadManager threadManager = new ParsingThreadManager();

    private ParsingThreadManager(){
        init();
    }

    public static ParsingThreadManager getParsingThreadManager(){
        return threadManager;
    }

    /**
     * 初始化线程数量
     */
    public void init() {
        int threadSize = HttpServerConfig.getReadThreadSize();
        blockingDeque = new LinkedBlockingDeque<>();

        for (int i = 0; i < threadSize; i++) {
            ParsingThread parsingThread = new ParsingThread(blockingDeque);
            parsingThread.start();
        }
    }
}
