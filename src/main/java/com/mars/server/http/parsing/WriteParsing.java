package com.mars.server.http.parsing;

import com.mars.server.MartianServerConfig;
import com.mars.server.http.constant.MartianServerConstant;
import com.mars.server.http.request.MartianHttpExchange;
import com.mars.server.http.util.ChannelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 写入解析
 */
public class WriteParsing {

    private Logger logger = LoggerFactory.getLogger(WriteParsing.class);

    /**
     * 请求处理器
     */
    private MartianHttpExchange martianHttpExchange;

    /**
     * 构建一个解析器
     * @param martianHttpExchange
     * @return
     */
    public static WriteParsing builder(MartianHttpExchange martianHttpExchange){
        WriteParsing writeParsing = new WriteParsing();
        writeParsing.martianHttpExchange = martianHttpExchange;
        return writeParsing;
    }

    /**
     * 执行响应操作
     * @throws IOException
     */
    public void responseData() throws Exception {
        if(martianHttpExchange.getResponseBody() != null){
            /* 只要响应流不为空，就按文件下载处理 */
            responseFile();
        } else {
            responseText(null);
        }
    }

    /**
     * 响应文件流
     */
    private void responseFile() throws Exception {
        /* 加载响应头 */
        martianHttpExchange.setResponseHeader(MartianServerConstant.CONTENT_TYPE, "application/octet-stream");
        StringBuffer buffer = getCommonResponse(martianHttpExchange.getResponseBody().size());

        /* 转成ByteBuffer */
        byte[] bytes = buffer.toString().getBytes(MartianServerConstant.ENCODING);

        /* 加载要响应的数据 */
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length + martianHttpExchange.getResponseBody().size());
        byteBuffer.put(bytes);
        byteBuffer.put(martianHttpExchange.getResponseBody().toByteArray());

        byteBuffer.flip();
        doWrite(byteBuffer);
    }

    /**
     * 响应字符串
     *
     * @return
     */
    public void responseText(String text) throws Exception {
        if(text == null){
            text = martianHttpExchange.getSendText();
        }
        if(text.equals(MartianServerConstant.VOID)){
            return;
        }
        /* 加载响应头 */
        StringBuffer buffer = getCommonResponse(text.getBytes().length);

        /* 加载要响应的数据 */
        buffer.append(text);

        /* 转成ByteBuffer */
        byte[] bytes = buffer.toString().getBytes(MartianServerConstant.ENCODING);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);

        byteBuffer.flip();

        /* 开始响应 */
        doWrite(byteBuffer);
    }

    /**
     * 往客户端写数据
     * @param byteBuffer
     */
    private void doWrite(ByteBuffer byteBuffer) {
        try {
            martianHttpExchange.getSocketChannel().write(byteBuffer,
                    MartianServerConfig.getWriteTimeout(),
                    TimeUnit.MILLISECONDS,
                    byteBuffer,
                    new WriteCompletionHandler(martianHttpExchange.getSocketChannel(), martianHttpExchange.getResponseBody()));
        } catch (Exception e){
            logger.error("给客户端写入响应数据异常", e);
            ChannelUtil.close(martianHttpExchange.getSocketChannel());
            ChannelUtil.closeOutputStream(martianHttpExchange.getResponseBody());
        }
    }

    /**
     * 获取公共的返回信息
     * @return
     */
    private StringBuffer getCommonResponse(int length){
        StringBuffer buffer = new StringBuffer();

        /* 加载初始化头 */
        buffer.append(MartianServerConstant.BASIC_RESPONSE.replace("{statusCode}", String.valueOf(martianHttpExchange.getStatusCode())));
        buffer.append(MartianServerConstant.CARRIAGE_RETURN);
        buffer.append(MartianServerConstant.CONTENT_LENGTH + ": " + length);
        buffer.append(MartianServerConstant.CARRIAGE_RETURN);

        /* 加载自定义头 */
        for(Map.Entry<String, String> entry : martianHttpExchange.getResponseHeaders().entrySet()){
            String value = entry.getValue();
            if(value == null){
                continue;
            }
            buffer.append(entry.getKey() + ":" + value);
            buffer.append(MartianServerConstant.CARRIAGE_RETURN);
        }
        buffer.append(MartianServerConstant.CARRIAGE_RETURN);
        return buffer;
    }
}
