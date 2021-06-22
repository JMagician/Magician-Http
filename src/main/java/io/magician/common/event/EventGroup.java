package io.magician.common.event;

import io.magician.common.constant.EventEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 事件执行器组合
 */
public class EventGroup {

    /**
     * 下标，用来做轮询
     */
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    /**
     * 这个事件组里的所有事件执行器
     */
    private List<EventRunner> eventRunnerList;

    /**
     * 线程池
     */
    private ExecutorService executorService;

    /**
     * 是否允许执行器相互窃取任务
     * 默认YES
     */
    private EventEnum.STEAL steal = EventEnum.STEAL.YES;

    /**
     * 获取线程池
     *
     * @return
     */
    public ExecutorService getThreadPool() {
        return executorService;
    }

    /**
     * 创建一个事件执行器组合
     *
     * @param size
     */
    public EventGroup(int size, ExecutorService executorService) {
        init(size, executorService);
    }

    /**
     * 创建一个事件执行器组合
     *
     * @param size
     */
    public EventGroup(int size) {
        init(size, Executors.newCachedThreadPool());
    }

    /**
     * 初始化
     *
     * @param size
     * @param executorService
     */
    private void init(int size, ExecutorService executorService) {
        this.eventRunnerList = new ArrayList<>(size);
        this.executorService = executorService;
        for (int i = 0; i < size; i++) {
            EventRunner eventRunner = new EventRunner(this);
            eventRunnerList.add(eventRunner);
            eventRunner.process();
        }
    }

    /**
     * 轮询获取这个组合里的事件执行器
     *
     * @return
     */
    public EventRunner getEventRunner() {
        int index = atomicInteger.getAndUpdate((val) -> {
            int newVal = val + 1;
            return (eventRunnerList.size() == newVal) ? 0 : newVal;
        });

        EventRunner eventRunner = eventRunnerList.get(index);
        return eventRunner;
    }

    /**
     * 窃取其他事件执行器里的任务
     * 用于在某个事件执行器空闲后，帮助其他执行器消费队列
     *
     * @return EventTask 对象
     */
    public EventTask stealTask() {
        /* 获取任务最多的EventRunner */
        EventRunner maxQueueEventRunner = null;
        for (EventRunner eventRunner : eventRunnerList) {
            if (eventRunner == null || eventRunner.getQueue().size() < 1) {
                continue;
            }

            if (maxQueueEventRunner == null || eventRunner.getQueue().size() > maxQueueEventRunner.getQueue().size()) {
                maxQueueEventRunner = eventRunner;
                continue;
            }
        }

        if (maxQueueEventRunner == null) {
            return null;
        }

        /* 从筛选出来的eventRunner里获取任务 */
        LinkedBlockingQueue<EventTask> eventQueue = maxQueueEventRunner.getQueue();
        synchronized (eventQueue) {
            return eventQueue.poll();
        }
    }

    /**
     * 停止线程池（停止后，这个事件组对应的功能就会全部停止）
     */
    public void shutdown(){
        if(!this.executorService.isShutdown()){
            this.executorService.shutdown();
        }
    }

    public EventEnum.STEAL getSteal() {
        return steal;
    }

    public void setSteal(EventEnum.STEAL steal) {
        this.steal = steal;
    }
}
