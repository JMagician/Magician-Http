package com.mars.server.http.constant;

/**
 * 常量
 */
public class MartianServerConstant {

    /**
     * 无返回值
     */
    public static final String VOID = "void405cb55d6781877e9e930aa8e046098b";

    /**
     * 参数字符编码
     */
    public static final String ENCODING = "UTF-8";

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
     * 响应的内容类型
     */
    public static final String RESPONSE_CONTENT_TYPE = "application/json;charset=" + ENCODING;

    /**
     * 响应头Vary
     */
    public static final String VARY = "Vary";
    /**
     * 响应头Vary的值
     */
    public static final String VARY_VALUE = "Accept-Encoding";

    /**
     * 连接状态
     */
    public static final String KEEP_ALIVE = "Keep-Alive";
    /**
     * 连接保留时间
     */
    public static final String KEEP_ALIVE_VALUE = "timeout=60, max=100";

    /**
     * 连接状态
     */
    public static final String CONNECTION = "Connection";
    /**
     * 连接状态:关闭
     */
    public static final String CONNECTION_CLOSE = "close";

    /**
     * 用来判断当前请求是否是一次预判
     */
    public static final String OPTIONS = "OPTIONS";

    /**
     * 请求内容类型
     */
    public static final String CONTENT_TYPE  = "Content-Type";

    /**
     * 请求内容类型
     */
    public static final String CONTENT_TYPE_LOW  = "Content-type";

    /**
     * 内容长度
     */
    public static final String CONTENT_LENGTH = "Content-Length";

    /**
     * 内容长度
     */
    public static final String CONTENT_LENGTH_LOW = "Content-length";

    /**
     * 内容描述
     */
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
}
