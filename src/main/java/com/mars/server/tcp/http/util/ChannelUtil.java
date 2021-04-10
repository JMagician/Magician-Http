package com.mars.server.tcp.http.util;

import com.mars.server.tcp.http.constant.MartianServerConstant;
import com.mars.server.tcp.http.parsing.WriteParsing;
import com.mars.server.tcp.http.request.MartianHttpExchange;

import java.io.OutputStream;
import java.nio.channels.AsynchronousSocketChannel;

public class ChannelUtil {

    /**
     * 异常的时候给前端一个响应
     * @param e
     */
    public static void errorResponseText(Throwable e, MartianHttpExchange marsHttpExchange){
        try {
            marsHttpExchange.setResponseHeader(MartianServerConstant.CONTENT_TYPE, MartianServerConstant.JSON_CONTENT_TYPE);
            WriteParsing.builder(marsHttpExchange).responseText(MsgUtil.getMes(500,"处理请求异常:" + e.getMessage()));
        } catch (Exception ex){
        }
    }

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
