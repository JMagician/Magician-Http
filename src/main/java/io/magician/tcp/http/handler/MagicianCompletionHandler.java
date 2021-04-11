package io.magician.tcp.http.handler;

import io.magician.tcp.http.server.HttpServerConfig;
import io.magician.tcp.http.parsing.ReadCompletionHandler;
import io.magician.tcp.http.request.MagicianHttpExchange;
import io.magician.tcp.http.util.ChannelUtil;
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
public class MagicianCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

    private Logger logger = LoggerFactory.getLogger(MagicianCompletionHandler.class);

    @Override
    public void completed(AsynchronousSocketChannel channel, AsynchronousServerSocketChannel serverSocketChannel) {
        serverSocketChannel.accept(serverSocketChannel, this);
        try {
            MagicianHttpExchange magicianHttpExchange = new MagicianHttpExchange();
            magicianHttpExchange.setSocketChannel(channel);

            ByteBuffer byteBuffer = ByteBuffer.allocate(800);
            channel.read(byteBuffer, HttpServerConfig.getReadTimeout(), TimeUnit.MILLISECONDS, byteBuffer, new ReadCompletionHandler(magicianHttpExchange));
        } catch (Exception e) {
            logger.error("处理请求异常", e);
            ChannelUtil.close(channel);
        }
    }

    @Override
    public void failed(Throwable exc, AsynchronousServerSocketChannel serverSocketChannel) {
        logger.error("建立连接异常", exc);
        serverSocketChannel.accept(serverSocketChannel, this);
    }
}
