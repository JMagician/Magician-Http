package io.magician.tcp.http.request;

import io.magician.tcp.http.constant.MagicianConstant;
import io.magician.tcp.http.model.RequestURI;
import io.magician.tcp.http.model.HttpHeaders;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * 请求处理器
 */
public class MagicianHttpExchange {

    /**
     * 通道
     */
    private AsynchronousSocketChannel socketChannel;

    /**
     * 请求的地址
     */
    private RequestURI requestURI;

    /**
     * 请求内容
     */
    private InputStream requestBody;

    /**
     * 要发送的字符串
     */
    private String sendText;

    /**
     * 响应文件流
     */
    private ByteArrayOutputStream responseBody;

    /**
     * 请求头
     */
    private HttpHeaders requestHeaders;

    /**
     * 响应头
     */
    private HttpHeaders responseHeaders;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * HTTP版面
     */
    private String httpVersion;

    /**
     * 响应状态
     */
    private int statusCode;

    /**
     * 构造器
     */
    public MagicianHttpExchange(){
        requestHeaders = new HttpHeaders();
        responseHeaders = new HttpHeaders();
        responseHeaders.put(MagicianConstant.CONNECTION, MagicianConstant.CONNECTION_CLOSE);

        sendText = MagicianConstant.NO_DATA;
        statusCode = 200;
    }

    public AsynchronousSocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void setRequestURI(String url) {
        this.requestURI = new RequestURI(url);
    }

    public void setRequestBody(InputStream requestBody) {
        this.requestBody = requestBody;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    /**
     * 获取请求路径
     * @return
     */
    public RequestURI getRequestURI() {
        return requestURI;
    }

    /**
     * 设置请求头
     * @param name
     * @param value
     */
    public void setRequestHeader(String name, String value){
        requestHeaders.put(name.toUpperCase(), value);
    }

    /**
     * 设置响应头
     * @param name
     * @param value
     */
    public void setResponseHeader(String name, String value){
        responseHeaders.put(name, value);
    }

    /**
     * 获取请求头
     * @return
     */
    public HttpHeaders getRequestHeaders() {
        return requestHeaders;
    }

    /**
     * 获取响应头
     * @return
     */
    public HttpHeaders getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * 获取http版本
     * @return
     */
    public String getHttpVersion() {
        return httpVersion;
    }

    /**
     * 获取请求方法
     * @return
     */
    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     * 获取请求内容
     * @return
     */
    public InputStream getRequestBody() {
        return requestBody;
    }

    /**
     * 获取要发送的文字
     * @return
     */
    public String getSendText() {
        return sendText;
    }

    /**
     * 获取要发送的文件流
     * @return
     */
    public ByteArrayOutputStream getResponseBody() {
        return responseBody;
    }

    /**
     * 设置响应文件流
     * @param responseBody
     */
    public void setResponseBody(byte[] responseBody) throws Exception {
        this.responseBody = new ByteArrayOutputStream();
        this.responseBody.write(responseBody);
    }

    /**
     * 设置响应文件流
     * @param inputStream
     * @throws Exception
     */
    public void setResponseBody(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int rc = 0;
        while((rc=inputStream.read(buff, 0, buff.length))>0) {
            byteArrayOutputStream.write(buff, 0, rc);
        }
        this.responseBody = byteArrayOutputStream;
    }

    /**
     * 设置响应数据
     * @param statusCode
     * @param text
     */
    public void sendText(int statusCode, String text){
        this.statusCode = statusCode;
        this.sendText = text;
    }

    /**
     * 获取状态码
     * @return
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * 获取本次的请求类型
     * @return
     */
    public String getContentType(){
        return requestHeaders.get(MagicianConstant.CONTENT_TYPE);
    }

    /**
     * 获取请求长度
     * @return
     */
    public long getRequestContentLength(){
        String contentLength = requestHeaders.get(MagicianConstant.CONTENT_LENGTH);
        if(contentLength == null){
            return -1;
        }
        return Long.parseLong(contentLength);
    }
}
