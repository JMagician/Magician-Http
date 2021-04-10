package com.mars.server.tcp.http.constant;

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
    public static final String JSON_CONTENT_TYPE = "application/json;charset=" + ENCODING;

    /**
     * 请求内容类型
     */
    public static final String CONTENT_TYPE  = "CONTENT-TYPE";

    /**
     * 内容长度
     */
    public static final String CONTENT_LENGTH = "CONTENT-LENGTH";

    /**
     * 内容描述
     */
    public static final String CONTENT_DISPOSITION = "CONTENT-DISPOSITION";

    /**
     * 连接状态
     */
    public static final String CONNECTION = "Connection";
    /**
     * 连接状态:关闭
     */
    public static final String CONNECTION_CLOSE = "close";
}
