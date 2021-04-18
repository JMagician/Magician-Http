package io.magician.tcp.http.parsing.thread;

import io.magician.tcp.http.server.HttpServerConfig;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 解析请求的线程管理
 */
public class ParsingThreadManager {

    /**
     * 用来计算坐标，实现轮询
     */
    private static AtomicInteger atomicInteger;

    /**
     * 保存初始化后的线程
     */
    private static List<ParsingThread> parsingThreadList;

    /**
     * 根据配置的线程数量 生成对应的线程
     */
    private static void initThreadList(){
        int threadSize = HttpServerConfig.getThreadSize();
        atomicInteger = new AtomicInteger(0);

        parsingThreadList = new ArrayList<>();
        for(int i=0;i<threadSize;i++){
            ParsingThread parsingThread = new ParsingThread();
            parsingThread.start();

            parsingThreadList.add(parsingThread);
        }
    }

    /**
     * 添加任务到队列里
     * @param channel
     */
    public synchronized static void addTaskToParsingThread(SocketChannel channel){
        if(parsingThreadList == null){
            initThreadList();
        }

        /* 计算坐标，轮询获取线程 */
        int index = atomicInteger.getAndIncrement();
        if(index < 0 || index > (parsingThreadList.size() - 1)){
            index = 0;
            atomicInteger.set(1);
        }

        /* 往获取的线程里添加任务 */
        ParsingThread parsingThread = parsingThreadList.get(index);
        parsingThread.addTask(channel);
    }
}
