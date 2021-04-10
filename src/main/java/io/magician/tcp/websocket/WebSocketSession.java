package io.magician.tcp.websocket;

import io.magician.tcp.http.constant.MagicianConstant;
import io.magician.tcp.http.request.MagicianHttpExchange;
import io.magician.tcp.websocket.handler.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * webSocket会话
 */
public class WebSocketSession {

    private Logger logger = LoggerFactory.getLogger(WebSocketSession.class);

    /**
     * ID，UUID生成，每个session唯一
     */
    private String id;
    /**
     * 最后活跃时间，用来判断是否应该回收
     */
    private long activeTime;
    /**
     * http请求处理器，用来获取请求头
     */
    private MagicianHttpExchange magicianHttpExchange;
    /**
     * 这个session要用handler
     */
    private WebSocketHandler webSocketHandler;

    public WebSocketSession(){
        this.id = UUID.randomUUID().toString();
        this.activeTime = System.currentTimeMillis();
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
     * 同步发送二进制消息
     * @param message
     */
    public void send(byte[] message) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        send(message, countDownLatch);
        countDownLatch.await();
    }

    /**
     * 同步发送字符串消息
     * @param message
     * @throws UnsupportedEncodingException
     */
    public void send(String message) throws Exception {
        send(message.getBytes(MagicianConstant.ENCODING));
    }

    /**
     * 异步发送二进制消息
     * @param message
     */
    public void asyncSend(byte[] message) {
        send(message, null);
    }

    /**
     * 异步发送字符串消息
     * @param message
     * @throws UnsupportedEncodingException
     */
    public void asyncSend(String message) throws UnsupportedEncodingException {
        asyncSend(message.getBytes(MagicianConstant.ENCODING));
    }

    /**
     * 发送消息
     * @param message
     * @param countDownLatch
     */
    private void send(byte[] message, CountDownLatch countDownLatch){
        // TODO 开发中
    }
}
