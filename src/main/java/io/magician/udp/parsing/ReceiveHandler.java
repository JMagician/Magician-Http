package io.magician.udp.parsing;

import io.magician.common.threadpool.ThreadPoolManagerFactory;
import io.magician.common.util.ReadUtil;
import io.magician.udp.handler.MagicianUDPHandler;
import io.magician.udp.UDPServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * 接收器
 */
public class ReceiveHandler {

    private static Logger logger = LoggerFactory.getLogger(ReceiveHandler.class);

    /**
     * 接收数据，并放入队列里
     * @param datagramChannel
     */
    public static void receive(DatagramChannel datagramChannel){
        try{
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteBuffer byteBuffer = ByteBuffer.allocate(UDPServerConfig.getReadSize());

            datagramChannel.receive(byteBuffer);
            ReadUtil.byteBufferToOutputStream(byteBuffer, outputStream);

            /* 异步执行业务逻辑 */
            ThreadPoolManagerFactory
                    .getThreadPoolManager(ThreadPoolManagerFactory.UDP_EXECUTE)
                    .addTask(outputStream);
        } catch (Exception e){
            logger.error("接收数据异常", e);
        }
    }

    /**
     * 执行业务逻辑
     * @param outputStream
     */
    public static void completed(ByteArrayOutputStream outputStream) {
        try{
            MagicianUDPHandler magicianUDPHandler = UDPServerConfig.getMagicianUDPHandler();
            magicianUDPHandler.receive(outputStream);
        } catch (Exception e){
            logger.error("MagicianUDPHandler出现异常", e);
        }
    }
}
