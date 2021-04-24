package io.magician.tcp.http.parsing;

import io.magician.tcp.http.request.MagicianHttpExchange;

import java.io.ByteArrayOutputStream;

/**
 * 从channel读数据需要用到的字段
 */
public class ReadFields {
    /**
     * 请求对象
     */
    protected MagicianHttpExchange magicianHttpExchange;
    /**
     * head的长度，用来计算body长度
     */
    protected int headLength = 0;

    /**
     * 读到的数据
     */
    protected ByteArrayOutputStream outputStream;;
}
