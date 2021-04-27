package io.magician.tcp.thread;

import io.magician.tcp.workers.selector.WorkerSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 业务Selector监听线程
 */
public class WorkerSelectorThread extends Thread {

    private Logger log = LoggerFactory.getLogger(WorkerSelectorThread.class);

    /**
     * 开始监听
     */
    @Override
    public void run() {
        try {
            /* 开启worker监听器 */
            WorkerSelector.startSelector();
        } catch (Exception e){
            log.error("开启NioSelector监听器异常", e);
        }
    }
}
