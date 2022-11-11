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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Extension of MartianHttpExchange
 * Provides a method for obtaining parameters
 */
public class MagicianRequest {

    private HttpExchange httpExchange;

    /**
     * Response management
     */
    private MagicianResponse magicianResponse;

    public MagicianRequest(HttpExchange httpExchange, MagicianResponse magicianResponse){
        this.httpExchange = httpExchange;
        this.magicianResponse = magicianResponse;
    }

    public HttpExchange getHttpExchange() {
        return httpExchange;
    }

    /**
     * get all parameters
     * @return
     */
    public Map<String, List> getParamsMap() {
        Map<String, ParamModel> paramMap = httpExchange.getParam();
        if (paramMap == null) {
            return null;
        }

        Map<String, List> params = new HashMap<>();
        for (Map.Entry<String, ParamModel> entry : paramMap.entrySet()) {
            ParamModel paramModel = entry.getValue();
            if (paramModel == null || ParamType.OTHER.equals(paramModel.getType()) == false) {
                continue;
            }
            params.put(entry.getKey(), paramModel.getValues());
        }

        return params;
    }

    /**
     * get all files
     * @return
     */
    public Map<String, List<MixedFileUpload>> getFilesMap() {
        Map<String, ParamModel> paramMap = httpExchange.getParam();
        if (paramMap == null) {
            return null;
        }

        Map<String, List<MixedFileUpload>> files = new ConcurrentHashMap<>();

        for (Map.Entry<String, ParamModel> entry : paramMap.entrySet()){
            ParamModel paramModel = entry.getValue();
            if (paramModel == null || ParamType.FILE.equals(paramModel.getType()) == false) {
                continue;
            }

            files.put(entry.getKey(), paramModel.getFiles());
        }

        return files;
    }

    /**
     * Get json parameters
     * @return
     */
    public String getJsonParam() {
        return httpExchange.getJsonParam();
    }

    /**
     * Get all files corresponding to the request name
     * @param name
     * @return
     */
    public List<MixedFileUpload> getFiles(String name){
        Map<String, ParamModel> paramModelMap = httpExchange.getParam();
        if(paramModelMap == null || paramModelMap.size() < 1){
            return null;
        }

        ParamModel paramModel = httpExchange.getParam().get(name);
        if(paramModel == null || ParamType.FILE.equals(paramModel.getType()) == false){
            return null;
        }

        return paramModel.getFiles();
    }

    /**
     * Get a corresponding file by request name
     * @param name
     * @return
     */
    public MixedFileUpload getFile(String name){
        List<MixedFileUpload> mixedFileUploadList = getFiles(name);
        if(mixedFileUploadList == null || mixedFileUploadList.size() < 1){
            return null;
        }
        return mixedFileUploadList.get(0);
    }

    /**
     * Get multiple parameters with the same name
     * @param name
     * @return
     */
    public List getParams(String name){
        Map<String, ParamModel> paramModelMap = httpExchange.getParam();
        if(paramModelMap == null || paramModelMap.size() < 1){
            return null;
        }

        ParamModel paramModel = paramModelMap.get(name);
        if(paramModel == null || ParamType.OTHER.equals(paramModel.getType()) == false){
            return null;
        }

        return paramModel.getValues();
    }

    /**
     * get a parameter
     * @param name
     * @return
     */
    public String getParam(String name){
        List params = getParams(name);
        if(params == null || params.size() < 1){
            return null;
        }
        return String.valueOf(params.get(0));
    }

    /**
     * get content type
     * @return content type
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
     * get request method
     * @return request method
     */
    public HttpMethod getMethod() {
        return httpExchange.getMethod();
    }

    /**
     * Get the uri to request
     * @return uri
     */
    public String getUrl() {
        return httpExchange.getUrl();
    }

    /**
     * Get remote address
     * @return
     * @throws IOException
     */
    public SocketAddress getRemoteAddress() throws IOException {
        return null;
    }

    /**
     * get all request headers
     * @return
     */
    public HttpHeaders getRequestHeaders(){
        return httpExchange.getHttpHeaders();
    }

    /**
     * Get request header data
     * @param key
     * @return
     */
    public String getRequestHeader(String key) {
        return httpExchange.getHttpHeaders().get(key);
    }

    /**
     * get response object
     * @return
     */
    public MagicianResponse getResponse(){
        return magicianResponse;
    }
}
