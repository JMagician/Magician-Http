package io.magician.network.processing.exchange;

import io.magician.network.processing.model.ParamModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

import java.util.Map;

/**
 * http data exchange
 */
public class HttpExchange {

    /**
     * url of this request
     */
    private String url;

    /**
     * request method
     */
    private HttpMethod method;

    /**
     * request header
     */
    private HttpHeaders httpHeaders;

    /**
     * request parameter
     */
    private Map<String, ParamModel> param;

    /**
     * json parameter, string
     */
    private String jsonParam;

    /**
     * netty's channel
     */
    private ChannelHandlerContext channelHandlerContext;

    /**
     * netty's request
     */
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
