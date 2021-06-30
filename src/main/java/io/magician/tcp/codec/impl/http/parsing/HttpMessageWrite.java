package io.magician.tcp.codec.impl.http.parsing;

import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;
import io.magician.common.constant.CommonConstant;
import io.magician.tcp.codec.impl.http.constant.HttpConstant;
import io.magician.common.util.ChannelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * 写入解析
 */
public class HttpMessageWrite {

    private Logger logger = LoggerFactory.getLogger(HttpMessageWrite.class);

    /**
     * 请求处理器
     */
    private MagicianHttpExchange magicianHttpExchange;

    /**
     * 构建一个解析器
     * @param magicianHttpExchange
     * @return
     */
    public static HttpMessageWrite builder(MagicianHttpExchange magicianHttpExchange){
        HttpMessageWrite httpMessageWrite = new HttpMessageWrite();
        httpMessageWrite.magicianHttpExchange = magicianHttpExchange;
        return httpMessageWrite;
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
        magicianHttpExchange.setResponseHeader(HttpConstant.CONTENT_TYPE, "application/octet-stream");
        StringBuffer buffer = getCommonResponse(magicianHttpExchange.getResponseBody().size());

        /* 转成ByteBuffer */
        byte[] bytes = buffer.toString().getBytes(CommonConstant.ENCODING);

        /* 加载要响应的数据 */
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bytes.length + magicianHttpExchange.getResponseBody().size());
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
            text = HttpConstant.NO_DATA;
        }

        /* 加载响应头 */
        StringBuffer buffer = getCommonResponse(text.getBytes().length);

        /* 加载要响应的数据 */
        buffer.append(text);

        /* 转成ByteBuffer */
        byte[] bytes = buffer.toString().getBytes(CommonConstant.ENCODING);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bytes.length);
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
        } catch (Exception e) {
            logger.error("往客户端写数据异常", e);
        } finally {
            String connectionValue = magicianHttpExchange.getResponseHeaders().get(HttpConstant.CONNECTION);
            if(connectionValue == null
                    || connectionValue.equals(HttpConstant.CONNECTION_CLOSE)){
                /* 如果响应头里面通知了客户端关闭连接，那么服务端必须关闭连接 */
                ChannelUtil.destroy(magicianHttpExchange);
            }

            ChannelUtil.closeOutputStream(magicianHttpExchange.getResponseBody());
        }
    }

    /**
     * 获取公共的返回信息
     * @return
     */
    private StringBuffer getCommonResponse(int length){
        StringBuffer buffer = new StringBuffer();

        /* 加载初始化头 */
        buffer.append(HttpConstant.BASIC_RESPONSE.replace("{statusCode}", String.valueOf(magicianHttpExchange.getStatusCode())));
        buffer.append(HttpConstant.CARRIAGE_RETURN);
        buffer.append(HttpConstant.CONTENT_LENGTH + ": " + length);
        buffer.append(HttpConstant.CARRIAGE_RETURN);

        String connection = getConnectionValue();
        if(connection != null
                && connection.toUpperCase().equals(HttpConstant.KEEP_ALIVE_UP)){
            /* 如果有keep-alive请求头，就通知客户端此连接已保留 */
            magicianHttpExchange.getResponseHeaders().put(HttpConstant.CONNECTION, HttpConstant.KEEP_ALIVE);
        } else {
            /* 如果没有keep-alive请求头，就通知客户端连接已关闭 */
            magicianHttpExchange.getResponseHeaders().put(HttpConstant.CONNECTION, HttpConstant.CONNECTION_CLOSE);
        }

        /* 加载自定义头 */
        for(Map.Entry<String, String> entry : magicianHttpExchange.getResponseHeaders().entrySet()){
            String value = entry.getValue();
            if(value == null){
                continue;
            }
            buffer.append(entry.getKey() + ":" + value);
            buffer.append(HttpConstant.CARRIAGE_RETURN);
        }
        buffer.append(HttpConstant.CARRIAGE_RETURN);
        return buffer;
    }

    /**
     * 获取connection请求头的value
     * @return
     */
    private String getConnectionValue(){
        return magicianHttpExchange
                .getRequestHeaders()
                .get(HttpConstant.CONNECTION_UP);
    }
}
