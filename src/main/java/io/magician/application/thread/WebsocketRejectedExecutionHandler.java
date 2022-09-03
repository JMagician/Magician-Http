package io.magician.application.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * When the websocket thread pool has no resources, execute this strategy
 */
public class WebsocketRejectedExecutionHandler implements RejectedExecutionHandler {

    private static Logger logger = LoggerFactory.getLogger(WebsocketRejectedExecutionHandler.class);

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        logger.error("The thread pool is full and refuses to process this Websocket message");
    }
}
