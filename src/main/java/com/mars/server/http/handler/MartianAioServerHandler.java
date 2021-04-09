package com.mars.server.http.handler;

import com.mars.server.MartianServerConfig;
import com.mars.server.http.parsing.ReadCompletionHandler;
import com.mars.server.http.request.MartianHttpExchange;
import com.mars.server.http.util.ChannelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

/**
 * 异步处理请求
 */
public class MartianAioServerHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

    private Logger logger = LoggerFactory.getLogger(MartianAioServerHandler.class);

    @Override
    public void completed(AsynchronousSocketChannel channel, AsynchronousServerSocketChannel serverSocketChannel) {
        serverSocketChannel.accept(serverSocketChannel, this);

        MartianHttpExchange martianHttpExchange = new MartianHttpExchange();
        martianHttpExchange.setSocketChannel(channel);

        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(800);
            channel.read(byteBuffer, MartianServerConfig.getReadTimeout(), TimeUnit.MILLISECONDS, byteBuffer, new ReadCompletionHandler(martianHttpExchange));
        } catch (Exception e){
            logger.error("处理请求异常", e);
            ChannelUtil.errorResponseText(e, martianHttpExchange);
            ChannelUtil.close(channel);
        }
    }

    @Override
    public void failed(Throwable exc, AsynchronousServerSocketChannel serverSocketChannel) {
        logger.error("建立连接异常", exc);
        serverSocketChannel.accept(serverSocketChannel, this);
    }
}
