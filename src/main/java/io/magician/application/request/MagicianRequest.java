package io.magician.application.request;

import io.magician.common.constant.HttpConstant;
import io.magician.network.processing.exchange.HttpExchange;
import io.magician.network.processing.enums.ParamType;
import io.magician.network.processing.model.ParamModel;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.multipart.MixedFileUpload;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MartianHttpExchange的扩展
 * 提供了参数的获取方法
 */
public class MagicianRequest {

    private HttpExchange httpExchange;

    /**
     * 响应管理
     */
    private MagicianResponse magicianResponse;

    /**
     * 上传的文件
     */
    private Map<String, List<MixedFileUpload>> files;

    public HttpExchange getHttpExchange() {
        return httpExchange;
    }

    public void setHttpExchange(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;

        Map<String, ParamModel> paramMap = httpExchange.getParam();
        if (paramMap != null) {
            for (Map.Entry<String, ParamModel> entry : paramMap.entrySet()){
                ParamModel paramModel = entry.getValue();
                if (paramModel == null) {
                    continue;
                }

                if (ParamType.FILE.equals(paramModel.getType())) {
                    List list = paramModel.getValue();
                    if (list == null) {
                        continue;
                    }
                    List<MixedFileUpload> mixedFileUploadList = new ArrayList<>();
                    for (Object item : list){
                        if (item == null) {
                            continue;
                        }
                        mixedFileUploadList.add((MixedFileUpload) item);
                    }
                    files.put(entry.getKey(), mixedFileUploadList);
                }
            }
        }
    }

    public Map<String, List> getMagicianParams() {
        Map<String, ParamModel> paramMap = httpExchange.getParam();
        if (paramMap == null) {
            return null;
        }
        Map<String, List> params = new HashMap<>();
        for (Map.Entry<String, ParamModel> entry : paramMap.entrySet()) {
            ParamModel paramModel = entry.getValue();
            if (paramModel == null) {
                continue;
            }
            params.put(entry.getKey(), paramModel.getValue());
        }

        return params;
    }

    public String getJsonParam() {
        return httpExchange.getJsonParam();
    }

    public Map<String, List<MixedFileUpload>> getFileMap() {
        return files;
    }

    public List<MixedFileUpload> getFiles(String name){
        return files.get(name);
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
        Map<String, List> magicianParams = getMagicianParams();
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
            if(HttpMethod.GET.equals(getMethod())){
                return "N";
            }
            String contentType = httpExchange.getHttpHeaders().get(HttpConstant.CONTENT_TYPE);
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
    public HttpMethod getMethod() {
        return httpExchange.getMethod();
    }

    /**
     * 获取要请求的uri
     * @return 请求方法
     */
    public String getUrl() {
        return httpExchange.getUrl();
    }

    /**
     * 获取remote地址
     * @return
     * @throws IOException
     */
    public SocketAddress getRemoteAddress() throws IOException {
        return null;
    }

    /**
     * 获取所有请求头
     * @return
     */
    public HttpHeaders getRequestHeaders(){
        return httpExchange.getHttpHeaders();
    }

    /**
     * 获取请求头数据
     * @param key 键
     * @return 头数据
     */
    public String getRequestHeader(String key) {
        return httpExchange.getHttpHeaders().get(key);
    }

    /**
     * 获取响应对象
     * @return
     */
    public MagicianResponse getResponse(){
        if(magicianResponse == null){
            magicianResponse = new MagicianResponse();
            magicianResponse.setHttpExchange(httpExchange);
        }
        return magicianResponse;
    }
}
