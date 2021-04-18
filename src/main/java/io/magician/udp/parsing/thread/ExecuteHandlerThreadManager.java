package io.magician.udp.parsing.thread;

import io.magician.udp.UDPServerConfig;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 管理执行业务逻辑的线程
 */
public class ExecuteHandlerThreadManager {

    /**
     * 用来计算坐标，实现轮询
     */
    private static AtomicInteger atomicInteger;

    /**
     * 保存初始化后的线程
     */
    private static List<ExecuteHandlerThread> executeHandlerThreadList;

    /**
     * 根据配置的线程数量 生成对应的线程
     */
    private static void initThreadList(){
        int threadSize = UDPServerConfig.getThreadSize();
        atomicInteger = new AtomicInteger(0);

        executeHandlerThreadList = new ArrayList<>();
        for(int i=0;i<threadSize;i++){
            ExecuteHandlerThread executeHandlerThread = new ExecuteHandlerThread();
            executeHandlerThread.start();

            executeHandlerThreadList.add(executeHandlerThread);
        }
    }

    /**
     * 添加任务到队列里
     * @param outputStream
     */
    public synchronized static void addTaskToParsingThread(ByteArrayOutputStream outputStream){
        if(executeHandlerThreadList == null){
            initThreadList();
        }

        /* 计算坐标，轮询获取线程 */
        int index = atomicInteger.getAndIncrement();
        if(index < 0 || index > (executeHandlerThreadList.size() - 1)){
            index = 0;
            atomicInteger.set(1);
        }

        /* 往获取的线程里添加任务 */
        ExecuteHandlerThread executeHandlerThread = executeHandlerThreadList.get(index);
        executeHandlerThread.addTask(outputStream);
    }
}
