package io.magician.tcp.websocket.process;

import io.magician.tcp.routing.RoutingJump;
import io.magician.tcp.websocket.WebSocketSession;
import io.magician.tcp.websocket.cache.ConnectionCache;
import io.magician.tcp.websocket.constant.WebSocketEnum;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * socket管理
 */
public class SocketConnectionProcess {

    private static Timer timer;

    /**
     * 开始处理连接
     */
    public synchronized static void process(){
        if(timer != null){
            return;
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Map<String, WebSocketSession> socketSessionMap = ConnectionCache.getSessionMap();
                if(socketSessionMap == null || socketSessionMap.size() < 1){
                    timer.cancel();
                    timer = null;
                    return;
                }
                monitor(socketSessionMap);
            }
        }, new Date(), 1000);
    }

    /**
     * 监听通道，获取socket发来的消息
     */
    private static void monitor(Map<String, WebSocketSession> socketSessionMap) {
        for(WebSocketSession session : socketSessionMap.values()){
            doMonitor(session);
        }
    }

    /**
     * 监听WebSocketSession
     * @param session
     */
    private static void doMonitor(WebSocketSession session){
        // TODO 开发中
        try {
            /* 如果超过10秒没有读写过数据，就移除掉 */
            if(System.currentTimeMillis() - session.getActiveTime() > 10000){
                RoutingJump.websocket(session, WebSocketEnum.CLOSE);
                return;
            }

            session.send("okokok");

            /* 否则就尝试从通道读取数据 */
            session.readyRead();
            SocketChannel socketChannel = session.getMagicianHttpExchange().getSocketChannel();

            Integer result = 0;
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            do{
                result = socketChannel.read(byteBuffer);
                if(result > 0){
                    System.out.println(result);
                 }
            } while (result > 0);

            session.updateActiveTime();

            session.readEnd();

        } catch (Exception e){
            session.readEnd();
            e.printStackTrace();
        }
    }
}
