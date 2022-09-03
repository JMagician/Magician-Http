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

    /**
     * uploaded file
     */
    private Map<String, List<MixedFileUpload>> files;

    public HttpExchange getHttpExchange() {
        return httpExchange;
    }

    public void setHttpExchange(HttpExchange httpExchange, MagicianResponse magicianResponse) {
        this.httpExchange = httpExchange;
        this.magicianResponse = magicianResponse;
        this.files = new ConcurrentHashMap<>();

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

    /**
     * get all parameters
     * @return
     */
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

    /**
     * Get json parameters
     * @return
     */
    public String getJsonParam() {
        return httpExchange.getJsonParam();
    }

    /**
     * get all files
     * @return
     */
    public Map<String, List<MixedFileUpload>> getFileMap() {
        return files;
    }

    /**
     * Get all files corresponding to the request name
     * @param name
     * @return
     */
    public List<MixedFileUpload> getFiles(String name){
        if(files == null){
            return null;
        }
        return files.get(name);
    }

    /**
     * Get a corresponding file by request name
     * @param name
     * @return
     */
    public MixedFileUpload getFile(String name){
        if(files == null){
            return null;
        }

        List<MixedFileUpload> mixedFileUploadList = files.get(name);
        if(mixedFileUploadList == null){
            return null;
        }
        return mixedFileUploadList.get(0);
    }


    /**
     * get a parameter
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
     * Get multiple parameters with the same name
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
