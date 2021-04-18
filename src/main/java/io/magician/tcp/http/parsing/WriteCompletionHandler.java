package io.magician.tcp.http.parsing;

import io.magician.tcp.http.constant.MagicianConstant;
import io.magician.tcp.http.request.MagicianHttpExchange;
import io.magician.tcp.http.util.ChannelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * 写入解析
 */
public class WriteCompletionHandler {

    private Logger logger = LoggerFactory.getLogger(WriteCompletionHandler.class);

    /**
     * 请求处理器
     */
    private MagicianHttpExchange magicianHttpExchange;

    /**
     * 构建一个解析器
     * @param magicianHttpExchange
     * @return
     */
    public static WriteCompletionHandler builder(MagicianHttpExchange magicianHttpExchange){
        WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler();
        writeCompletionHandler.magicianHttpExchange = magicianHttpExchange;
        return writeCompletionHandler;
    }

    /**
     * 执行响应操作
     * @throws IOException
     */
    public void completed() throws Exception {
        if(magicianHttpExchange.getResponseBody() != null){
            /* 只要响应流不为空，就按文件下载处理 */
            responseFile();
        } else {
            responseText();
        }
    }

    /**
     * 响应文件流
     */
    private void responseFile() throws Exception {
        /* 加载响应头 */
        magicianHttpExchange.setResponseHeader(MagicianConstant.CONTENT_TYPE, "application/octet-stream");
        StringBuffer buffer = getCommonResponse(magicianHttpExchange.getResponseBody().size());

        /* 转成ByteBuffer */
        byte[] bytes = buffer.toString().getBytes(MagicianConstant.ENCODING);

        /* 加载要响应的数据 */
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length + magicianHttpExchange.getResponseBody().size());
        byteBuffer.put(bytes);
        byteBuffer.put(magicianHttpExchange.getResponseBody().toByteArray());

        byteBuffer.flip();
        doWrite(byteBuffer);
    }

    /**
     * 响应字符串
     *
     * @return
     */
    private void responseText() throws Exception {
        String text = magicianHttpExchange.getSendText();
        if(text == null || text.equals("")){
            text = MagicianConstant.NO_DATA;
        }

        /* 加载响应头 */
        StringBuffer buffer = getCommonResponse(text.getBytes().length);

        /* 加载要响应的数据 */
        buffer.append(text);

        /* 转成ByteBuffer */
        byte[] bytes = buffer.toString().getBytes(MagicianConstant.ENCODING);
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
            while (byteBuffer.hasRemaining()){
                magicianHttpExchange.getSocketChannel().write(byteBuffer);
            }

            if(isClose()){
                ChannelUtil.close(magicianHttpExchange.getSocketChannel());
            }
            ChannelUtil.closeOutputStream(magicianHttpExchange.getResponseBody());
        } catch (Exception e) {
            logger.error("给客户端写入响应数据异常", e);
            ChannelUtil.close(magicianHttpExchange.getSocketChannel());
            ChannelUtil.closeOutputStream(magicianHttpExchange.getResponseBody());
        }
    }

    /**
     * 是否需要关闭
     * @return
     */
    private boolean isClose(){
        String connection = magicianHttpExchange.getResponseHeaders().get(MagicianConstant.CONNECTION);
        if(connection != null && connection.equals(MagicianConstant.CONNECTION_CLOSE)){
            return true;
        }
        return false;
    }

    /**
     * 获取公共的返回信息
     * @return
     */
    private StringBuffer getCommonResponse(int length){
        StringBuffer buffer = new StringBuffer();

        /* 加载初始化头 */
        buffer.append(MagicianConstant.BASIC_RESPONSE.replace("{statusCode}", String.valueOf(magicianHttpExchange.getStatusCode())));
        buffer.append(MagicianConstant.CARRIAGE_RETURN);
        buffer.append(MagicianConstant.CONTENT_LENGTH + ": " + length);
        buffer.append(MagicianConstant.CARRIAGE_RETURN);

        /* 加载自定义头 */
        for(Map.Entry<String, String> entry : magicianHttpExchange.getResponseHeaders().entrySet()){
            String value = entry.getValue();
            if(value == null){
                continue;
            }
            buffer.append(entry.getKey() + ":" + value);
            buffer.append(MagicianConstant.CARRIAGE_RETURN);
        }
        buffer.append(MagicianConstant.CARRIAGE_RETURN);
        return buffer;
    }
}
