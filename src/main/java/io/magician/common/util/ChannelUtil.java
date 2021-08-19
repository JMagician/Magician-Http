package io.magician.common.util;

import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ChannelUtil {

    /**
     * 释放资源
     *
     * @param magicianHttpExchange
     */
    public static void destroy(MagicianHttpExchange magicianHttpExchange) {
        close(magicianHttpExchange.getSocketChannel());
        cancel(magicianHttpExchange.getSelectionKey());
    }

    /**
     * 释放资源
     *
     * @param socketChannel
     */
    public static void close(SocketChannel socketChannel) {
        try {
            if (socketChannel != null) {
                socketChannel.shutdownInput();
                socketChannel.shutdownOutput();
                socketChannel.close();
            }
        } catch (Exception e) {
        }
    }

    /**
     * 取消SelectionKey
     * @param selectionKey
     */
    public static void cancel(SelectionKey selectionKey){
        try {
            if(selectionKey != null){
                selectionKey.attach(null);
                selectionKey.cancel();
            }
        } catch (Exception e){
        }
    }

    /**
     * 关闭输出流
     * @param outputStream
     */
    public static void closeOutputStream(OutputStream outputStream) {
        try {
            if(outputStream != null){
                outputStream.close();
            }
        } catch (Exception e){
        }
    }

    /**
     * 往channel写数据
     * @param byteBuffer
     * @param channel
     * @throws Exception
     */
    public static boolean write(ByteBuffer byteBuffer, SocketChannel channel, long writeTimeout) throws Exception {
        synchronized (channel){
            long start = System.currentTimeMillis();
            int count=0;

            while (byteBuffer.hasRemaining()){
                int result = channel.write(byteBuffer);

                /* 如果出现了5次写入失败 并且已经超过了写入超时时间，直接掐断 */
                if(result < 1){
                    count++;
                    if(count > 5 && (System.currentTimeMillis() - start) > writeTimeout){
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
