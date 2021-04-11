package io.magician.tcp.http.parsing;

import io.magician.tcp.http.server.HttpServerConfig;
import io.magician.tcp.http.constant.MagicianConstant;
import io.magician.tcp.http.request.MagicianHttpExchange;
import io.magician.tcp.http.util.ChannelUtil;
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
    private MagicianHttpExchange magicianHttpExchange;

    public WriteCompletionHandler(MagicianHttpExchange magicianHttpExchange, OutputStream outputStream){
        this.channel = magicianHttpExchange.getSocketChannel();
        this.outputStream = outputStream;
        this.magicianHttpExchange = magicianHttpExchange;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        try {
            if(attachment.hasRemaining()){
                channel.write(attachment, HttpServerConfig.getWriteTimeout(),
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
        String connection = magicianHttpExchange.getResponseHeaders().get(MagicianConstant.CONNECTION);
        if(connection != null && connection.equals(MagicianConstant.CONNECTION_CLOSE)){
            return true;
        }
        return false;
    }
}
