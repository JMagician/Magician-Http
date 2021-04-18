package io.magician.tcp.http.parsing.thread;

import io.magician.tcp.http.parsing.ReadCompletionHandler;
import io.magician.tcp.http.request.MagicianHttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 执行请求的线程
 */
public class ParsingThread extends Thread {

    private Logger logger = LoggerFactory.getLogger(ParsingThread.class);

    private LinkedBlockingDeque<SocketChannel> blockingDeque;

    public ParsingThread(){
        blockingDeque = new LinkedBlockingDeque<>();
    }

    /**
     * 添加一个任务
     * @param channel
     */
    public void addTask(SocketChannel channel){
        this.blockingDeque.add(channel);
    }

    /**
     * 轮询执行队列里的任务
     */
    @Override
    public void run() {
        while (true){
            try{
                SocketChannel channel = blockingDeque.take();

                MagicianHttpExchange magicianHttpExchange = new MagicianHttpExchange();
                magicianHttpExchange.setSocketChannel(channel);
                ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(magicianHttpExchange);
                readCompletionHandler.completed();
            } catch (Exception e){
                logger.error("执行请求的队列出现异常", e);
            }
        }
    }
}
