package com.mars.server.http.parsing;

import com.mars.server.MartianServerConfig;
import com.mars.server.http.util.ChannelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

/**
 * 写入数据
 */
public class WriteCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private Logger logger = LoggerFactory.getLogger(WriteCompletionHandler.class);

    private AsynchronousSocketChannel channel;
    private OutputStream outputStream;

    public WriteCompletionHandler(AsynchronousSocketChannel channel, OutputStream outputStream){
        this.channel = channel;
        this.outputStream = outputStream;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        try {
            if(attachment.hasRemaining()){
                channel.write(attachment, MartianServerConfig.getWriteTimeout(),
                        TimeUnit.MILLISECONDS, attachment,this);
            } else {
                ChannelUtil.close(channel);
                ChannelUtil.closeOutputStream(outputStream);
            }
        } catch (Exception e){
            logger.error("给客户端写入响应数据异常", e);
            ChannelUtil.close(channel);
            ChannelUtil.closeOutputStream(outputStream);
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        logger.error("给客户端写入响应数据异常", exc);
        ChannelUtil.close(channel);
        ChannelUtil.closeOutputStream(outputStream);
    }
}
