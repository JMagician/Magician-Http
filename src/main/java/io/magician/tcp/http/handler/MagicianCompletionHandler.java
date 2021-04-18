package io.magician.tcp.http.handler;

import io.magician.tcp.http.parsing.thread.ParsingThreadManager;
import io.magician.tcp.http.util.ChannelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.*;

/**
 * 处理请求
 */
public class MagicianCompletionHandler  {

    private static Logger logger = LoggerFactory.getLogger(MagicianCompletionHandler.class);

    /**
     * 轮询选择器，根据状态进行对应的操作
     * @param serverSocketChannel
     */
    public static void completed(ServerSocketChannel serverSocketChannel) {
        while (true){
            SocketChannel channel = null;
            try {
                channel = serverSocketChannel.accept();

                /* 将任务添加到队列里执行 */
                ParsingThreadManager.addTaskToParsingThread(channel);
            } catch (Exception e){
                logger.error("处理请求出现异常", e);
                ChannelUtil.close(channel);
            }
        }
    }
}
