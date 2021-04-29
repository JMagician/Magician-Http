package io.magician.common.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class EventRunner {

    private Logger logger = LoggerFactory.getLogger(EventRunner.class);

    private LinkedBlockingQueue<EventTask> queue;

    private EventGroup eventGroup;

    public LinkedBlockingQueue<EventTask> getQueue() {
        return queue;
    }

    public EventRunner(EventGroup eventGroup){
        this.queue = new LinkedBlockingQueue();
        this.eventGroup = eventGroup;
    }

    public void addEvent(EventTask eventTask){
        if(!queue.contains(eventTask)){
            queue.add(eventTask);
        }
    }

    public void process(){
        this.eventGroup.getThreadPool().execute(()->{
            while (true){
                try {
                    EventTask eventTask = queue.poll(2000, TimeUnit.MILLISECONDS);

                    /* 如果当前队列没任务了，就去其他队列窃取 */
                    if(eventTask == null){
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
