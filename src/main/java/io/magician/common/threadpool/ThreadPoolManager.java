package io.magician.common.threadpool;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * 解析请求的线程管理
 */
public abstract class ThreadPoolManager<T> {

    protected LinkedBlockingDeque<T> blockingDeque;

    /**
     * 初始化线程
     */
    public abstract void init();

    /**
     * 添加任务到队列里
     *
     * @param executeModel
     */
    public void addTask(T executeModel) {
        blockingDeque.add(executeModel);
    }
}
