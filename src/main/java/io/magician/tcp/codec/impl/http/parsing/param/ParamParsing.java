package io.magician.tcp.codec.impl.http.parsing.param;

import io.magician.tcp.codec.impl.http.model.MagicianFileUpLoad;
import io.magician.tcp.codec.impl.http.parsing.param.formdata.HttpExchangeRequestContext;
import io.magician.tcp.codec.impl.http.parsing.param.formdata.ParsingFormData;
import io.magician.common.constant.CommonConstant;
import io.magician.tcp.codec.impl.http.constant.ParamTypeConstant;
import io.magician.tcp.codec.impl.http.constant.ReqMethod;
import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;
import io.magician.tcp.codec.impl.http.request.MagicianRequest;
import org.apache.commons.fileupload.UploadContext;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 参数解析
 */
public class ParamParsing {

    /**
     * 从httpExchange中提取出所有的参数，并放置到MagicianRequest中
     *
     * @param request
     * @return 加工后的请求
     * @throws Exception 异常
     */
    public static MagicianRequest getMagicianRequest(MagicianRequest request) throws Exception {
        Map<String, MagicianFileUpLoad> files = new HashMap<>();
        Map<String, List<String>> magicianParams = new HashMap<>();

        MagicianHttpExchange httpExchange = request.getMartianHttpExchange();
        if (httpExchange.getRequestMethod().toUpperCase().equals(ReqMethod.GET.toString())) {
            /* 从get请求中获取参数 */
            String paramStr = httpExchange.getRequestURI().getQuery();
            magicianParams = urlencoded(paramStr, magicianParams, true);
        } else {
            /* 从非GET请求中获取参数 */
            InputStream inputStream = httpExchange.getRequestBody();
            if (inputStream == null) {
                return request;
            }

            /* 根据提交方式，分别处理参数 */
            String contentType = request.getContentType();
            if (ParamTypeConstant.isUrlEncoded(contentType)) {
                /* 正常的表单提交 */
                String paramStr = getParamStr(inputStream);
                magicianParams = urlencoded(paramStr, magicianParams, true);
            } else if (ParamTypeConstant.isFormData(contentType)) {
                /* formData提交，可以用于文件上传 */
                Map<String, Object> result = formData(httpExchange, contentType);
                files = (Map<String, MagicianFileUpLoad>) result.get(ParsingFormData.FILES_KEY);
                magicianParams = (Map<String, List<String>>) result.get(ParsingFormData.PARAMS_KEY);
            } else if (ParamTypeConstant.isJSON(contentType)) {
                /* RAW提交(json) */
                String jsonParams = raw(inputStream);
                request.setJsonParam(jsonParams);
            }
        }
        /* 将提取出来的参数，放置到request中 */
        request.setFiles(files);
        request.setMagicianParams(magicianParams);

        return request;
    }

    /**
     * 从输入流里面读取所有的数据
     *
     * @param inputStream 输入流
     * @return 数据
     * @throws Exception 异常
     */
    private static String getParamStr(InputStream inputStream) throws Exception {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, CommonConstant.ENCODING);
        BufferedReader br = new BufferedReader(inputStreamReader);
        try {
            String line = null;
            StringBuffer paramsStr = new StringBuffer();
            while ((line = br.readLine()) != null) {
                paramsStr.append(line);
            }
            return paramsStr.toString();
        } catch (Exception e){
            throw e;
        } finally {
            br.close();
            inputStreamReader.close();
            inputStream.close();
        }
    }

    /**
     * 表单提交处理
     *
     * @param paramStr   数据
     * @param magicianParams
     * @param hasDecode  是否需要解码 true是，false不是
     * @return 对象
     * @throws Exception 异常
     */
    private static Map<String, List<String>> urlencoded(String paramStr, Map<String, List<String>> magicianParams, boolean hasDecode) throws Exception {
        if (paramStr != null) {
            String[] paramsArray = paramStr.split("&");
            if (paramsArray == null || paramsArray.length < 1) {
                return magicianParams;
            }
            List<String> values = null;
            for (String paramItem : paramsArray) {
                String[] param = paramItem.split("=");
                if (param == null || param.length < 2) {
                    continue;
                }
                String key = param[0];

                values = magicianParams.get(key);
                if (values == null) {
                    values = new ArrayList<>();
                }

                String value = param[1];
                if (hasDecode) {
                    value = URLDecoder.decode(value, CommonConstant.ENCODING);
                }
                values.add(value);
                magicianParams.put(key, values);
            }
        }
        return magicianParams;
    }

    /**
     * RAW提交处理
     *
     * @param inputStream 输入流
     * @return 对象
     * @throws Exception 异常
     */
    private static String raw(InputStream inputStream) throws Exception {
        String paramStr = getParamStr(inputStream);
        if (paramStr == null || paramStr.trim().equals("")) {
            return null;
        }
        return paramStr;
    }

    /**
     * formData提交处理
     *
     * @param exchange    请求对象
     * @param contentType 内容类型
     * @return request的参数对象 和 request的文件参数对象
     * @throws Exception 异常
     */
    private static Map<String, Object> formData(MagicianHttpExchange exchange, String contentType) throws Exception {
        UploadContext uploadContext = new HttpExchangeRequestContext(exchange, contentType);
        return ParsingFormData.parsing(uploadContext);
    }
}
