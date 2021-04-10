package com.mars.server.tcp.http.parsing.param.formdata;

import com.mars.server.MartianServerConfig;
import com.mars.server.tcp.http.constant.MartianServerConstant;
import com.mars.server.tcp.http.model.MarsFileUpLoad;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.UploadContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析FormData
 */
public class ParsingFormData {

    /**
     * 参数key
     */
    public static final String PARAMS_KEY = "paramsKey";
    /**
     * 文件key
     */
    public static final String FILES_KEY = "filesKey";

    /**
     * 解析
     * @param uploadContext 请求对象
     * @return 参数和文件
     */
    public static Map<String,Object> parsing(UploadContext uploadContext) throws Exception {
        Map<String,Object> result = new HashMap<>();
        Map<String,List<String>> marsParams = new HashMap<>();
        Map<String, MarsFileUpLoad> files = new HashMap<>();

        List<FileItem> fileItemList = getFileItem(uploadContext);

        for(FileItem item : fileItemList){
            if(item.isFormField()){
                String name = item.getFieldName();
                String value = item.getString(MartianServerConstant.ENCODING);
                List<String> params = marsParams.get(name);
                if(params == null){
                    params = new ArrayList<>();
                }
                params.add(value);
                marsParams.put(name,params);
            } else {
                MarsFileUpLoad marsFileUpLoad = new MarsFileUpLoad();
                marsFileUpLoad.setName(item.getFieldName());
                marsFileUpLoad.setInputStream(item.getInputStream());
                marsFileUpLoad.setFileName(item.getName());
                files.put(marsFileUpLoad.getName(),marsFileUpLoad);
            }
        }

        result.put(PARAMS_KEY,marsParams);
        result.put(FILES_KEY,files);
        return result;
    }

    /**
     * 获取文件列表
     * @param uploadContext 请求
     * @return 返回
     * @throws Exception 异常
     */
    public static List<FileItem> getFileItem(UploadContext uploadContext) throws Exception {

        FileItemFactory factory = new DiskFileItemFactory();

        FileUploadBase fileUploadBase = new HttpExchangeFileUpload();
        fileUploadBase.setFileItemFactory(factory);

        fileUploadBase.setFileSizeMax(MartianServerConfig.getFileSizeMax());
        fileUploadBase.setSizeMax(MartianServerConfig.getSizeMax());

        List<FileItem> fileItemList = fileUploadBase.parseRequest(uploadContext);

        return fileItemList;
    }
}
