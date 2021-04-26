package io.magician.common.util;

import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;

import java.io.OutputStream;
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
            if (socketChannel != null && socketChannel.isOpen()) {
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
}
