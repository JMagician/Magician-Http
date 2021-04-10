package io.magician.tcp.http.util;

import java.io.OutputStream;
import java.nio.channels.AsynchronousSocketChannel;

public class ChannelUtil {

    /**
     * 释放资源
     *
     * @param socketChannel
     */
    public static void close(AsynchronousSocketChannel socketChannel) {
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
     * 关闭输出流
     * @param outputStream
     */
    public static void closeOutputStream(OutputStream outputStream) {
        if(outputStream == null){
            return;
        }
        try {
            outputStream.close();
        } catch (Exception e){
        }
    }
}
