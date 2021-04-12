package io.magician.tcp.http.request;

import java.io.InputStream;

/**
 * 响应管理
 */
public class MagicianResponse {

    private static String contentType = "Content-type";

    private MagicianHttpExchange httpExchange;

    public MagicianResponse(MagicianHttpExchange httpExchange){
        this.httpExchange = httpExchange;
    }

    /**
     * 设置响应头
     * @param key
     * @param value
     */
    public MagicianResponse setResponseHeader(String key, String value){
        httpExchange.setResponseHeader(key,value);
        return this;
    }

    /**
     * 响应文本数据
     * @param code
     * @param data
     */
    public void sendText(int code, String data){
        setResponseHeader(contentType, "text/plain;charset=UTF-8");
        httpExchange.sendText(code, data);
    }

    /**
     * 响应html数据
     * @param code
     * @param data
     */
    public void sendHtml(int code, String data){
        setResponseHeader(contentType, "text/html;charset=UTF-8");
        httpExchange.sendText(code, data);
    }

    /**
     * 响应自定义格式的数据
     * 需要自己设置 content-type
     * @param code
     * @param data
     */
    public void sendData(int code, String data){
        httpExchange.sendText(code, data);
    }

    /**
     * 响应json数据
     * @param code
     * @param data
     */
    public void sendJson(int code, String data){
        setResponseHeader(contentType, "application/json;charset=UTF-8");
        httpExchange.sendText(code, data);
    }

    /**
     * 响应二进制
     * @param bytes
     * @throws Exception
     */
    public void sendStream(byte[] bytes) throws Exception {
        setResponseHeader(contentType, "application/octet-stream");
        httpExchange.setResponseBody(bytes);
    }

    /**
     * 响应文件流
     * @param inputStream
     * @throws Exception
     */
    public void sendStream(InputStream inputStream) throws Exception {
        setResponseHeader(contentType, "application/octet-stream");
        httpExchange.setResponseBody(inputStream);
    }
}
