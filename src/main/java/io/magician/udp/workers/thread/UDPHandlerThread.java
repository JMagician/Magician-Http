package io.magician.udp.workers.thread;

import io.magician.udp.workers.ReceiveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

/**
 * 执行业务逻辑的线程
 */
public class UDPHandlerThread implements Runnable {

    private Logger logger = LoggerFactory.getLogger(UDPHandlerThread.class);

    private ByteArrayOutputStream outputStream;

    public UDPHandlerThread(ByteArrayOutputStream outputStream){
        this.outputStream = outputStream;
    }

    /**
     * 轮询执行队列里的任务
     */
    @Override
    public void run() {
        try{
            ReceiveHandler.completed(outputStream);
        } catch (Exception e){
            logger.error("执行请求的队列出现异常", e);
        }
    }
}
