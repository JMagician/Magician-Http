package io.magician.tcp.protocol.codec.impl.http.request;

import io.magician.tcp.protocol.codec.impl.http.model.HttpHeaders;
import io.magician.tcp.protocol.codec.impl.http.model.MagicianFileUpLoad;
import io.magician.tcp.protocol.codec.impl.http.constant.ReqMethod;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

/**
 * MartianHttpExchange的扩展
 * 提供了参数的获取方法
 */
public class MagicianRequest {

    private MagicianHttpExchange httpExchange;

    /**
     * 响应管理
     */
    private MagicianResponse magicianResponse;

    /**
     * 参数
     */
    private Map<String, List<String>> magicianParams;

    /**
     * json参数
     */
    private String jsonParam;

    /**
     * 上传的文件
     */
    private Map<String, MagicianFileUpLoad> files;

    public MagicianHttpExchange getMartianHttpExchange() {
        return httpExchange;
    }

    public void setMartianHttpExchange(MagicianHttpExchange magicianHttpExchange) {
        this.httpExchange = magicianHttpExchange;
    }

    public Map<String, List<String>> getMagicianParams() {
        return magicianParams;
    }

    public void setMagicianParams(Map<String, List<String>> magicianParams) {
        this.magicianParams = magicianParams;
    }

    public String getJsonParam() {
        return jsonParam;
    }

    public void setJsonParam(String jsonParam) {
        this.jsonParam = jsonParam;
    }

    public Map<String, MagicianFileUpLoad> getFiles() {
        return files;
    }

    public void setFiles(Map<String, MagicianFileUpLoad> files) {
        this.files = files;
    }

    /**
     * 获取一个参数
     * @param name
     * @return
     */
    public String getParam(String name){
        List<String> params = getParams(name);
        if(params == null){
            return null;
        }
        return params.get(0);
    }

    /**
     * 获取多个name相同的参数
     * @param name
     * @return
     */
    public List<String> getParams(String name){
        if(magicianParams == null){
            return null;
        }
        List<String> params = magicianParams.get(name);
        if(params == null || params.size() < 1){
            return null;
        }
        return params;
    }

    /**
     * 获取参数类型
     * @return 参数类型
     */
    public String getContentType(){
        try {
            if(getMethod().toUpperCase().equals(ReqMethod.GET.toString())){
                return "N";
            }
            String contentType = httpExchange.getContentType();
            if(contentType == null){
                return "N";
            }
            return contentType;
        } catch (Exception e){
            return "N";
        }
    }

    /**
     * 获取请求方法
     * @return 请求方法
     */
    public String getMethod() {
        return httpExchange.getRequestMethod();
    }

    /**
     * 获取要请求的uri
     * @return 请求方法
     */
    public String getUrl() {
        return httpExchange.getRequestURI().toString();
    }

    /**
     * 获取remote地址
     * @return
     * @throws IOException
     */
    public SocketAddress getRemoteAddress() throws IOException {
        return httpExchange.getSocketChannel().getRemoteAddress();
    }

    /**
     * 获取local地址
     * @return
     * @throws IOException
     */
    public SocketAddress getLocalAddress() throws IOException {
        return httpExchange.getSocketChannel().getLocalAddress();
    }

    /**
     * 获取所有请求头
     * @return
     */
    public HttpHeaders getRequestHeaders(){
        return httpExchange.getRequestHeaders();
    }

    /**
     * 获取请求头数据
     * @param key 键
     * @return 头数据
     */
    public String getRequestHeader(String key) {
        return httpExchange.getRequestHeaders().get(key.toUpperCase());
    }

    /**
     * 获取响应对象
     * @return
     */
    public MagicianResponse getResponse(){
        if(magicianResponse == null){
            magicianResponse = new MagicianResponse(httpExchange);
        }
        return magicianResponse;
    }
}
