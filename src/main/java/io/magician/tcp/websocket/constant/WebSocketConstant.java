package io.magician.tcp.websocket.constant;

/**
 * Socket常量
 */
public class WebSocketConstant {

    /* **************** 建立Socket连接需要的请求头 *************** */
    public static final String CONNECTION = "CONNECTION";
    public static final String UPGRADE = "UPGRADE";
    public static final String SEC_WEBSOCKET_KEY = "SEC-WEBSOCKET-KEY";
    public static final String SEC_WEBSOCKET_PROTOCOL = "SEC-WEBSOCKET-PROTOCOL";
    public static final String SOCKET_RESPONSE_ONE_LINE = "HTTP/1.1 101 Switching Protocols";

    /**
     * 秘钥SocketKey加密秘钥
     */
    public static final String SOCKET_SECRET_KEY = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
}
