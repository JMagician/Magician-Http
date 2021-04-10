package io.magician.tcp.websocket.parsing;

import io.magician.MagicianConfig;
import io.magician.tcp.http.constant.MagicianConstant;
import io.magician.tcp.http.parsing.WriteParsing;
import io.magician.tcp.http.util.ChannelUtil;
import io.magician.tcp.websocket.WebSocketSession;
import io.magician.tcp.websocket.cache.ConnectionCache;
import io.magician.tcp.websocket.constant.WebSocketConstant;
import io.magician.tcp.websocket.util.SocketEncryptionUtil;
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
        AsynchronousSocketChannel channel = socketSession.getMagicianHttpExchange().getSocketChannel();
        try {
            channel.write(byteBuffer,
                    MagicianConfig.getWriteTimeout(),
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
        buffer.append(MagicianConstant.CARRIAGE_RETURN);
        buffer.append("Upgrade:websocket");
        buffer.append(MagicianConstant.CARRIAGE_RETURN);
        buffer.append("Connection:Upgrade");
        buffer.append(MagicianConstant.CARRIAGE_RETURN);
        buffer.append("Sec-WebSocket-Accept:" + getAccept());
        buffer.append(MagicianConstant.CARRIAGE_RETURN);
        buffer.append(MagicianConstant.CARRIAGE_RETURN);
        return buffer;
    }

    /**
     * 获取Accept
     * @return
     * @throws Exception
     */
    private String getAccept() throws Exception {
        String swKey = socketSession.getMagicianHttpExchange().getRequestHeaders().get(WebSocketConstant.SEC_WEBSOCKET_KEY);
        swKey = swKey + WebSocketConstant.SOCKET_SECRET_KEY;
        return new String(SocketEncryptionUtil.getSha1AndBase64(swKey), MagicianConstant.ENCODING);
    }
}
