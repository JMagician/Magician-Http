package com.mars.server.tcp.websocket.parsing;

import com.mars.server.MartianServerConfig;
import com.mars.server.tcp.http.constant.MartianServerConstant;
import com.mars.server.tcp.http.parsing.WriteParsing;
import com.mars.server.tcp.http.util.ChannelUtil;
import com.mars.server.tcp.websocket.WebSocketSession;
import com.mars.server.tcp.websocket.cache.ConnectionCache;
import com.mars.server.tcp.websocket.constant.WebSocketConstant;
import com.mars.server.tcp.websocket.util.SocketEncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * 解析连接请求
 */
public class SocketWriteParsing {

    private Logger logger = LoggerFactory.getLogger(WriteParsing.class);

    /**
     * 请求处理器
     */
    private WebSocketSession socketSession;

    /**
     * 构建一个解析器
     * @return
     */
    public static SocketWriteParsing builder(WebSocketSession socketSession){
        SocketWriteParsing writeParsing = new SocketWriteParsing();
        writeParsing.socketSession = socketSession;
        return writeParsing;
    }

    /**
     * 响应字符串
     *
     * @return
     */
    public void responseText() throws Exception {
        /* 加载响应头 */
        StringBuffer buffer = getCommonResponse();

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
        AsynchronousSocketChannel channel = socketSession.getMartianHttpExchange().getSocketChannel();
        try {
            channel.write(byteBuffer,
                    MartianServerConfig.getWriteTimeout(),
                    TimeUnit.MILLISECONDS,
                    byteBuffer,
                    new WriteCreateConnectionSocketHandler(channel, socketSession)
            );
        } catch (Exception e) {
            logger.error("给客户端写入响应数据异常", e);
            ChannelUtil.close(channel);
            ConnectionCache.removeSession(socketSession.getId());
        }
    }

    /**
     * 获取公共的返回信息
     * @return
     */
    private StringBuffer getCommonResponse() throws Exception {
        StringBuffer buffer = new StringBuffer();

        /* 加载初始化头 */
        buffer.append(WebSocketConstant.SOCKET_RESPONSE_ONE_LINE);
        buffer.append(MartianServerConstant.CARRIAGE_RETURN);
        buffer.append("Upgrade:websocket");
        buffer.append(MartianServerConstant.CARRIAGE_RETURN);
        buffer.append("Connection:Upgrade");
        buffer.append(MartianServerConstant.CARRIAGE_RETURN);
        buffer.append("Sec-WebSocket-Accept:" + getAccept());
        buffer.append(MartianServerConstant.CARRIAGE_RETURN);
        buffer.append(MartianServerConstant.CARRIAGE_RETURN);
        return buffer;
    }

    /**
     * 获取Accept
     * @return
     * @throws Exception
     */
    private String getAccept() throws Exception {
        String swKey =socketSession.getMartianHttpExchange().getRequestHeaders().get(WebSocketConstant.SEC_WEBSOCKET_KEY);
        swKey = swKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        return new String(SocketEncryptionUtil.getSha1AndBase64(swKey), MartianServerConstant.ENCODING);
    }
}
