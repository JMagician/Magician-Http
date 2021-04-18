package io.magician.tcp.websocket.process;

import io.magician.tcp.http.constant.MagicianConstant;
import io.magician.tcp.http.util.ReadUtil;
import io.magician.tcp.routing.RoutingJump;
import io.magician.tcp.websocket.WebSocketSession;
import io.magician.tcp.websocket.cache.ConnectionCache;
import io.magician.tcp.websocket.constant.WebSocketEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.TimerTask;

/**
 * socket定时任务
 * 定时扫描channel有无数据进来
 */
public class SocketTimerTask extends TimerTask {

    private Logger logger = LoggerFactory.getLogger(SocketTimerTask.class);

    @Override
    public void run() {
        try {
            Map<String, WebSocketSession> socketSessionMap = ConnectionCache.getSessionMap();
            if(socketSessionMap == null || socketSessionMap.size() < 1){
                return;
            }
            /* 监听通道，获取socket发来的消息 */
            for(WebSocketSession session : socketSessionMap.values()){
                doMonitor(session);
            }
        } catch (Exception e){
            logger.error("监控socket连接的收出了异常", e);
        }
    }

    /**
     * 监听WebSocketSession
     * @param session
     */
    private void doMonitor(WebSocketSession session){
        try {
            /* 如果超过1分钟没有数据交流，就移除掉 */
            if(System.currentTimeMillis() - session.getActiveTime() > 60000){
                RoutingJump.websocket(session, WebSocketEnum.CLOSE);
                return;
            }

            /* 尝试从通道读取数据 */
            String message = readData(session);

            /* 没数据就直接return */
            if(message == null){
                return;
            }

            /* 更新最后活跃时间 */
            session.updateActiveTime();

            /* 如果客户端断开了，就执行handler的close方法 */
            if(message.equals(WebSocketEnum.CLOSE.toString())){
                RoutingJump.websocket(session, WebSocketEnum.CLOSE);
                return;
            }

            /* 把消息传给handler */
            session.getWebSocketHandler().onMessage(message, session);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 从通道读数据
     * @param session
     * @return
     * @throws Exception
     */
    private String readData(WebSocketSession session) {
        try {
            session.readyRead();

            SocketChannel socketChannel = session.getMagicianHttpExchange().getSocketChannel();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream message = new ByteArrayOutputStream();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            while (socketChannel.read(byteBuffer) > -1) {
                ReadUtil.byteBufferToOutputStream(byteBuffer, outputStream);

                byte[] bytesData = outputStream.toByteArray();
                if (bytesData.length < 1) {
                    return null;
                }

                int opcode = bytesData[0] & 0x0f;
                if (opcode == 8) {
                    return WebSocketEnum.CLOSE.toString();
                }
                if (bytesData.length < 6) {
                    continue;
                }

                byte payloadLength = (byte) (bytesData[1] & 0x7f);
                byte[] mask = Arrays.copyOfRange(bytesData, 2, 6);
                byte[] payloadData = Arrays.copyOfRange(bytesData, 6, bytesData.length);
                for (int i = 0; i < payloadData.length; i++) {
                    payloadData[i] = (byte) (payloadData[i] ^ mask[i % 4]);
                }
                message.write(payloadData);
                if (message.size() >= payloadLength) {
                    return message.toString(MagicianConstant.ENCODING);
                }
            }
            return null;

        } catch (Exception e) {
            logger.error("读取数据异常", e);
            return null;
        } finally {
            session.readEnd();
        }
    }
}
