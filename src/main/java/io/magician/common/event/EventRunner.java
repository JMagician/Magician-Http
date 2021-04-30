package io.magician.common.event;

import io.magician.common.constant.EventEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 事件执行器
 */
public class EventRunner {

    private Logger logger = LoggerFactory.getLogger(EventRunner.class);

    /**
     * 队列
     */
    private LinkedBlockingQueue<EventTask> queue;

    /**
     * 所属的组合
     */
    private EventGroup eventGroup;

    /**
     * 获取队列
     * @return
     */
    public LinkedBlockingQueue<EventTask> getQueue() {
        return queue;
    }

    /**
     * 创建事件执行器
     * @param eventGroup
     */
    public EventRunner(EventGroup eventGroup){
        this.queue = new LinkedBlockingQueue();
        this.eventGroup = eventGroup;
    }

    /**
     * 添加事件
     * @param eventTask
     */
    public void addEvent(EventTask eventTask){
        queue.add(eventTask);
    }

    /**
     * 开启当前事件执行器
     */
    public void process(){
        this.eventGroup.getThreadPool().execute(()->{
            while (true){
                try {
                    EventTask eventTask = queue.poll(2000, TimeUnit.MILLISECONDS);

                    /* 如果当前执行器没任务了，就去其他执行器窃取 */
                    if(eventTask == null && EventEnum.STEAL.YES.equals(eventGroup.getSteal())){
                        eventTask = this.eventGroup.stealTask();
                    }

                    if(eventTask != null){
                        eventTask.run();
                    } else {
                        if(eventGroup.getThreadPool().isShutdown()){
                            logger.error("EventRunner所在的线程池关闭了，所以EventRunner也停止了");
                            return;
                        }
                    }
                } catch (Exception e){
                    logger.error("EventRunner执行事件，出现异常", e);
                }
            }
        });
    }
}
