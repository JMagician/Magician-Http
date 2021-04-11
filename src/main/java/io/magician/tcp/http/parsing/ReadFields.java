package io.magician.tcp.http.parsing;

import io.magician.tcp.http.request.MagicianHttpExchange;

import java.io.ByteArrayOutputStream;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * 从channel读数据需要用到的字段
 */
public class ReadFields {

    /**
     * 通道
     */
    protected AsynchronousSocketChannel channel;
    /**
     * 请求对象
     */
    protected MagicianHttpExchange magicianHttpExchange;
    /**
     * 是否已经读完head了
     */
    protected boolean readHead = false;
    /**
     * 从报文寻找head结束标识的起始坐标
     */
    protected int startIndex;
    /**
     * 从报文寻找head结束标识的结束坐标
     */
    protected int endIndex;
    /**
     * head结束符的二进制
     */
    protected byte[] headEndBytes;
    /**
     * head的长度，用来计算body长度
     */
    protected int headLength = 0;
    /**
     * 内容长度
     */
    protected long contentLength = Long.MAX_VALUE;
    /**
     * 读完了没
     */
    protected boolean readOver = false;
    /**
     * 读取到的数据缓存到这里
     */
    protected ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
}
