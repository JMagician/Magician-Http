package io.magician.tcp.codec.impl.http.constant;

/**
 * 常量
 */
public class HttpConstant {

    /**
     * 无返回值
     */
    public static final String NO_DATA = "No data!";

    /**
     * 回车换行符
     */
    public static String CARRIAGE_RETURN = "\r\n";

    /**
     * 头结束标识
     */
    public static String HEAD_END = "\r\n\r\n";

    /**
     * 冒号分割符
     */
    public static String SEPARATOR = ":";

    /**
     * 响应的基础信息
     */
    public static final String BASIC_RESPONSE = "HTTP/1.1 {statusCode} OK";

    /**
     * 请求内容类型
     */
    public static final String CONTENT_TYPE  = "CONTENT-TYPE";

    /**
     * 内容长度
     */
    public static final String CONTENT_LENGTH = "CONTENT-LENGTH";

    /**
     * 连接状态
     */
    public static final String CONNECTION = "Connection";
    /**
     * 连接状态:关闭
     */
    public static final String CONNECTION_CLOSE = "close";

    /**
     * 协议标记
     */
    public static final String TARGET = "HTTP/";
}
