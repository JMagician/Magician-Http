package io.magician.tcp.codec.impl.websocket.connection;

import io.magician.common.constant.CommonConstant;
import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;
import io.magician.tcp.codec.impl.websocket.handler.WebSocketHandler;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * webSocket会话
 */
public class WebSocketSession {

    /**
     * 计数器用来控制每个session只能有一个线程读取
     */
    private CountDownLatch countDownLatch;

    /**
     * ID，UUID生成，每个session唯一
     */
    private String id;
    /**
     * 最后活跃时间，用来判断是否应该回收
     */
    private long activeTime;
    /**
     * http请求处理器
     */
    private MagicianHttpExchange magicianHttpExchange;
    /**
     * 这个session要用的handler
     */
    private WebSocketHandler webSocketHandler;

    public WebSocketSession(){
        this.id = UUID.randomUUID().toString();
        this.activeTime = System.currentTimeMillis();
        countDownLatch = new CountDownLatch(0);
    }

    /**
     * 准备读取数据
     * @throws Exception
     */
    public synchronized void readyRead() throws Exception {
        countDownLatch.await();
        countDownLatch = new CountDownLatch(1);
    }

    /**
     * 读取完毕
     */
    public void readEnd() {
        if(countDownLatch.getCount() > 0){
            countDownLatch.countDown();
        }
    }


    public MagicianHttpExchange getMagicianHttpExchange() {
        return magicianHttpExchange;
    }

    public void setMagicianHttpExchange(MagicianHttpExchange magicianHttpExchange) {
        this.magicianHttpExchange = magicianHttpExchange;
    }

    public WebSocketHandler getWebSocketHandler() {
        return webSocketHandler;
    }

    public void setWebSocketHandler(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    public String getId() {
        return id;
    }

    public void updateActiveTime(){
        this.activeTime = System.currentTimeMillis();
    }

    public long getActiveTime() {
        return activeTime;
    }

    /**
     * 发送二进制消息
     * @param message
     */
    public void send(byte[] message) throws Exception {
        SocketChannel channel = magicianHttpExchange.getSocketChannel();
        byte[] boardCastData = new byte[2 + message.length];
        boardCastData[0] = (byte) 0x81;
        boardCastData[1] = (byte) message.length;
        System.arraycopy(message, 0, boardCastData, 2, message.length);

        ByteBuffer byteBuffer = ByteBuffer.wrap(boardCastData);

        while (byteBuffer.hasRemaining()){
            channel.write(byteBuffer);
        }
    }

    /**
     * 发送字符串消息
     * @param message
     * @throws UnsupportedEncodingException
     */
    public void send(String message) throws Exception {
        send(message.getBytes(CommonConstant.ENCODING));
    }
}
