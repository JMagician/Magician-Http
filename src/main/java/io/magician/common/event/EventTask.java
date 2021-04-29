package io.magician.common.event;

/**
 * 事件任务
 */
public interface EventTask {

    /**
     * 执行任务
     * @throws Exception
     */
    void run() throws Exception;
}
