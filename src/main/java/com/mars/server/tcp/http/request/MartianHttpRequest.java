package com.mars.server.tcp.http.request;

import com.mars.server.tcp.http.constant.ReqMethod;
import com.mars.server.tcp.http.model.MarsFileUpLoad;

import java.util.List;
import java.util.Map;

/**
 * MartianHttpExchange的扩展
 * 提供了参数的获取方法
 */
public class MartianHttpRequest {

    private MartianHttpExchange httpExchange;

    /**
     * 参数
     */
    private Map<String, List<String>> marsParams;

    /**
     * json参数
     */
    private String jsonParam;

    /**
     * 上传的文件
     */
    private Map<String, MarsFileUpLoad> files;

    public MartianHttpExchange getMartianHttpExchange() {
        return httpExchange;
    }

    public void setMartianHttpExchange(MartianHttpExchange martianHttpExchange) {
        this.httpExchange = martianHttpExchange;
    }

    public Map<String, List<String>> getMarsParams() {
        return marsParams;
    }

    public void setMarsParams(Map<String, List<String>> marsParams) {
        this.marsParams = marsParams;
    }

    public String getJsonParam() {
        return jsonParam;
    }

    public void setJsonParam(String jsonParam) {
        this.jsonParam = jsonParam;
    }

    public Map<String, MarsFileUpLoad> getFiles() {
        return files;
    }

    public void setFiles(Map<String, MarsFileUpLoad> files) {
        this.files = files;
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
     * 获取请求头数据
     * @param key 键
     * @return 头数据
     */
    public String getRequestHeader(String key) {
        return httpExchange.getRequestHeaders().get(key.toUpperCase());
    }

    /**
     * 设置响应头
     * @param key
     * @param value
     */
    public void setResponseHeader(String key, String value){
        httpExchange.setResponseHeader(key,value);
    }
}
