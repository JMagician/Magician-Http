package io.magician.common.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class EventGroup {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    private List<EventRunner> eventRunnerList;

    private ExecutorService executorService;

    public ExecutorService getThreadPool() {
        return executorService;
    }

    public EventGroup(int size, ExecutorService executorService){
        this.eventRunnerList = new ArrayList<>(size);
        this.executorService = executorService;
        for(int i=0;i<size;i++){
            EventRunner eventRunner = new EventRunner(this);
            eventRunnerList.add(eventRunner);
            eventRunner.process();
        }
    }

    public EventRunner getEventRunner(){
        int index = atomicInteger.getAndUpdate((val) ->{
            int newVal = val + 1;
            return (eventRunnerList.size() == newVal) ? 0 : newVal;
        });

        EventRunner eventRunner = eventRunnerList.get(index);
        return eventRunner;
    }

    /**
     * 从任务最多的 EventRunner 窃取任务
     * @return EventTask 对象
     */
    public EventTask stealTask() {
        /* 获取任务最多的EventRunner */
        EventRunner maxQueueEventRunner = null;
        for(EventRunner eventRunner : eventRunnerList) {
            if(eventRunner == null || eventRunner.getQueue().size() < 1){
                continue;
            }

            if(maxQueueEventRunner == null || eventRunner.getQueue().size() > maxQueueEventRunner.getQueue().size()) {
                maxQueueEventRunner = eventRunner;
                continue;
            }
        }

        if(maxQueueEventRunner == null) {
            return null;
        }

        /* 从筛选出来的eventRunner里获取任务 */
        LinkedBlockingQueue<EventTask> eventQueue = maxQueueEventRunner.getQueue();
        synchronized (eventQueue) {
            return eventQueue.poll();
        }
    }
}
