package io.magician.application.thread;

import io.magician.common.cache.MagicianHandlerCache;

import java.util.concurrent.*;

/**
 * BusinessThread
 * Used to separate and decouple business processing from netty's IO thread
 */
public class BusinessThreadPool {

    /**
     * Thread pool for handling http
     */
    private static ThreadPoolExecutor httpThreadPoolExecutor;
    /**
     * Thread pool for handling websocket
     */
    private static ThreadPoolExecutor websocketThreadPoolExecutor;

    public static void httpExecute(Runnable runnable){
        httpThreadPoolExecutor.execute(runnable);
    }

    public static void websocketExecute(Runnable runnable){
        websocketThreadPoolExecutor.execute(runnable);
    }

    /**
     * Initialize the thread pool
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     */
    public static void init(int corePoolSize, int maximumPoolSize, long keepAliveTime){
        // Initialize the http thread pool
        httpThreadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(),
                new HttpRejectedExecutionHandler()
        );

        // The websocket thread pool needs to be initialized only when the developer writes the websocketHandler
        if(MagicianHandlerCache.getWebsocketHandlerSize() > 0){
            websocketThreadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
                    maximumPoolSize,
                    keepAliveTime,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque<>(),
                    new WebsocketRejectedExecutionHandler()
            );
        }
    }
}
