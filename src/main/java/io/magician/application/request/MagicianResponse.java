package io.magician.application.request;

import io.magician.common.constant.CommonConstant;
import io.magician.common.constant.HttpConstant;
import io.magician.network.processing.exchange.HttpExchange;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 响应管理
 */
public class MagicianResponse {

    private static String contentType = "Content-Type";
    private static String contentDis = "Content-Disposition";

    private HttpExchange httpExchange;

    private Map<String, String> headers;

    public MagicianResponse(){
        headers = new HashMap<>();
    }

    public HttpExchange getHttpExchange() {
        return httpExchange;
    }

    public void setHttpExchange(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    /**
     * 设置响应头
     * @param key
     * @param value
     */
    public MagicianResponse setResponseHeader(String key, String value){
        headers.put(key, value);
        return this;
    }

    /**
     * 响应文本数据
     * @param data
     */
    public void sendText(String data) throws Exception {
        setResponseHeader(contentType, "text/plain;charset=UTF-8");
        responseWrite(data.getBytes(CommonConstant.ENCODING));
    }

    /**
     * 响应html数据
     * @param data
     */
    public void sendHtml(String data) throws Exception {
        setResponseHeader(contentType, "text/html;charset=UTF-8");
        responseWrite(data.getBytes(CommonConstant.ENCODING));
    }

    /**
     * 响应自定义格式的数据
     * 需要自己设置 content-type
     * @param data
     */
    public void sendData(String data) throws Exception {
        responseWrite(data.getBytes(CommonConstant.ENCODING));
    }

    /**
     * 响应json数据
     * @param data
     */
    public void sendJson(String data) throws Exception {
        setResponseHeader(contentType, "application/json;charset=UTF-8");
        responseWrite(data.getBytes(CommonConstant.ENCODING));
    }

    /**
     * 响应二进制
     * @param bytes
     * @throws Exception
     */
    public void sendStream(String fileName, byte[] bytes) throws Exception {
        setResponseHeader(contentType, "application/octet-stream");
        setResponseHeader(contentDis, "attachment; filename=" + URLEncoder.encode(fileName, CommonConstant.ENCODING));
        responseWrite(bytes);
    }

    /**
     * 响应错误提示
     * @param code
     * @param msg
     * @throws Exception
     */
    public void sendErrorMsg(int code, String msg) throws Exception {
        String msgTemplate = CommonConstant.ERROR_MSG;

        msgTemplate = msgTemplate.replace("{code}",  String.valueOf(code));
        msgTemplate = msgTemplate.replace("{msg}", msg);

        sendJson(msgTemplate);
    }

    /**
     * 响应数据
     * @param content
     */
    private void responseWrite(byte[] content){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        for (Map.Entry<String, String> entry : headers.entrySet()){
            response.headers().set(entry.getKey(), entry.getValue());
        }
        response.headers().set(HttpConstant.CONTENT_LENGTH, content.length);
        ByteBuf buf = response.content();
        buf.writeBytes(content);

        httpExchange.getChannelHandlerContext().writeAndFlush(response);
    }
}
