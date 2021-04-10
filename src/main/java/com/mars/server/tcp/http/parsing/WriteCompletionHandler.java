package com.mars.server.tcp.http.parsing;

import com.mars.server.MartianServerConfig;
import com.mars.server.tcp.http.constant.MartianServerConstant;
import com.mars.server.tcp.http.request.MartianHttpExchange;
import com.mars.server.tcp.http.util.ChannelUtil;
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
    private MartianHttpExchange martianHttpExchange;

    public WriteCompletionHandler(MartianHttpExchange martianHttpExchange, OutputStream outputStream){
        this.channel = martianHttpExchange.getSocketChannel();
        this.outputStream = outputStream;
        this.martianHttpExchange = martianHttpExchange;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        try {
            if(attachment.hasRemaining()){
                channel.write(attachment, MartianServerConfig.getWriteTimeout(),
                        TimeUnit.MILLISECONDS, attachment,this);
            } else {
                if(isClose()){
                    ChannelUtil.close(channel);
                }
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

    /**
     * 是否需要关闭
     * @return
     */
    private boolean isClose(){
        String connection = martianHttpExchange.getResponseHeaders().get(MartianServerConstant.CONNECTION);
        if(connection != null && connection.equals(MartianServerConstant.CONNECTION_CLOSE)){
            return true;
        }
        return false;
    }
}
