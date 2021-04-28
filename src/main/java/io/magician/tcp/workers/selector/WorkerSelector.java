package io.magician.tcp.workers.selector;

import io.magician.common.constant.StatusEnums;
import io.magician.tcp.TCPServerConfig;
import io.magician.tcp.workers.Worker;
import io.magician.tcp.workers.WorkersCacheManager;
import io.magician.tcp.workers.thread.WorkerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * worker选择器，筛选有新数据的worker安排工作
 */
public class WorkerSelector {

    private static Logger logger = LoggerFactory.getLogger(WorkerSelector.class);

    /**
     * 由于countDownLatch不能恢复数值，所以只能new一个新的出来
     * 但是在new的时候 可能会出现其他线程在唤醒的情况
     * 所以需要加锁
     */
    private static Object lock = "lock";

    /**
     * 计数器，用来在没有可读状态worker的时候阻塞任务
     */
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * 通知selector有新的可读状态worker
     */
    public static void notifySelector(){
        synchronized (lock){
            if(countDownLatch.getCount() < 1){
                return;
            }
            countDownLatch.countDown();
        }
    }

    /**
     * 开启一个监听器
     * @throws Exception
     */
    public static void startSelector() {
        while (true){
            try {
                /* 获取read状态的worker */
                selectWorkers();

                Map<SocketChannel, Worker> workerMap = WorkersCacheManager.getProtocolDataModelMap();
                for(Worker worker : workerMap.values()){
                    if(worker.isRead()){
                        if(worker.getSelectionKey() == null || worker.getSocketChannel() == null){
                            continue;
                        }
                        /* 如果worker已经在队列里了，则直接停止，一个连接不需要两个线程来执行worker */
                        if(worker.getStatusEnums().equals(StatusEnums.RUNNING)){
                            continue;
                        }

                        /* 设置成工作中状态 */
                        worker.setStatusEnums(StatusEnums.RUNNING);

                        /* 丢进线程池处理 */
                        TCPServerConfig.getThreadPool().execute(new WorkerThread(worker));
                    }
                }
            } catch (Exception e){
                logger.error("执行startSelector出现异常", e);
                continue;
            }
        }
    }

    /**
     * 从WorkersCacheManager中筛选可读状态的worker
     * @return
     * @throws Exception
     */
    public static void selectWorkers() throws Exception {
        /* 防止线程间的通讯出现一些小概率事件，所以最多阻塞10秒 */
        countDownLatch.await(10000L, TimeUnit.MILLISECONDS);
        synchronized (lock){
            countDownLatch = new CountDownLatch(1);
        }
    }
}
