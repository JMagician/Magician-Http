package io.magician.tcp.codec.impl.http.constant;

/**
 * 请求方式
 *
 * @author yuye
 *
 */
public enum ReqMethod {

    POST("POST"), GET("GET"),
    PUT("PUT"), DELETE("DELETE"),
    HEAD("HEAD"), OPTIONS("OPTIONS"),
    TRACE("TRACE"), CONNECT("CONNECT");

    private String code;

    ReqMethod(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
