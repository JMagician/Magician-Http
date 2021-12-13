package io.magician.network.processing.exchange;

import io.magician.network.processing.model.ParamModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

import java.util.Map;

/**
 * http数据交换器
 */
public class HttpExchange {

    /**
     * 本次请求的url
     */
    private String url;

    /**
     * 请求方式
     */
    private HttpMethod method;

    /**
     * 请求头
     */
    private HttpHeaders httpHeaders;

    /**
     * 参数
     */
    private Map<String, ParamModel> param;

    /**
     * json参数，字符串
     */
    private String jsonParam;

    /**
     * netty原生的channel和request
     */
    private ChannelHandlerContext channelHandlerContext;
    private FullHttpRequest fullHttpRequest;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public Map<String, ParamModel> getParam() {
        return param;
    }

    public void setParam(Map<String, ParamModel> param) {
        this.param = param;
    }

    public String getJsonParam() {
        return jsonParam;
    }

    public void setJsonParam(String jsonParam) {
        this.jsonParam = jsonParam;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public FullHttpRequest getFullHttpRequest() {
        return fullHttpRequest;
    }

    public void setFullHttpRequest(FullHttpRequest fullHttpRequest) {
        this.fullHttpRequest = fullHttpRequest;
    }
}
