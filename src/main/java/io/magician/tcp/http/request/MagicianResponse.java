package io.magician.tcp.http.request;

import java.io.InputStream;

/**
 * 响应管理
 */
public class MagicianResponse {

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
     * 设置响应数据
     * @param code
     * @param data
     */
    public void sendText(int code, String data){
        httpExchange.sendText(code, data);
    }

    /**
     * 设置响应文件流
     * @param bytes
     * @throws Exception
     */
    public void sendResponseBody(byte[] bytes) throws Exception {
        httpExchange.setResponseBody(bytes);
    }

    /**
     * 设置响应文件流
     * @param inputStream
     * @throws Exception
     */
    public void sendResponseBody(InputStream inputStream) throws Exception {
        httpExchange.setResponseBody(inputStream);
    }
}
