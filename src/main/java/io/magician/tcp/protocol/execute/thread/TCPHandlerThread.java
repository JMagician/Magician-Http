package io.magician.tcp.protocol.execute.thread;

import io.magician.tcp.protocol.execute.ExecuteModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * 执行请求的线程
 */
public class TCPHandlerThread extends Thread {

    private Logger logger = LoggerFactory.getLogger(TCPHandlerThread.class);

    private LinkedBlockingDeque<ExecuteModel> blockingDeque;

    public TCPHandlerThread(LinkedBlockingDeque<ExecuteModel> blockingDeque) {
        this.blockingDeque = blockingDeque;
    }

    /**
     * 轮询执行队列里的任务
     */
    @Override
    public void run() {
        while (true){
            try{
                /* 从队列中获取待处理的业务逻辑 */
                ExecuteModel executeModel = blockingDeque.take();
                executeModel.getProtocolParsing().execute(executeModel.getData());
            } catch (Exception e){
                logger.error("业务线程队列出现异常", e);
            }
        }
    }
}
