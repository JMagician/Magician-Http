package io.magician.application.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * When the http thread pool has no resources, execute this strategy
 */
public class HttpRejectedExecutionHandler implements RejectedExecutionHandler {

    private static Logger logger = LoggerFactory.getLogger(HttpRejectedExecutionHandler.class);

    /**
     * Enforce Denial Policy
     * @param r the runnable task requested to be executed
     * @param executor the executor attempting to execute this task
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            HttpThread businessThread = (HttpThread)r;
            businessThread.rejectedExecution();
        } catch (Exception e){
            logger.error("An exception occurred while executing RejectedExecution", e);
        }
    }
}
