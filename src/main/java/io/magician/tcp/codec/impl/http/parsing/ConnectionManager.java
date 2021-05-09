package io.magician.tcp.codec.impl.http.parsing;

import io.magician.common.util.ChannelUtil;
import io.magician.tcp.TCPServerConfig;
import io.magician.tcp.attach.AttachUtil;
import io.magician.tcp.attach.AttachmentModel;
import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;

import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接管理
 */
public class ConnectionManager {

    /**
     * 定时任务
     */
    private static Timer timer;
    /**
     * 超时时间
     */
    private static long timeout = 10000;
    /**
     * 长连接缓存
     */
    private static Map<SocketChannel, AttachmentModel> connectionMap = new ConcurrentHashMap<>();

    /**
     * 设置超时时间
     * @param timeout
     */
    public static void setTimeout(long timeout) {
        ConnectionManager.timeout = timeout;
    }

    /**
     * 添加长连接
     * @param magicianHttpExchange
     */
    public static void addConnection(MagicianHttpExchange magicianHttpExchange){
        SocketChannel channel = magicianHttpExchange.getSocketChannel();
        if(connectionMap.containsKey(channel)){
            return;
        }
        /* 获取附件 */
        AttachmentModel attachmentModel = AttachUtil.getAttachmentModel(magicianHttpExchange.getSelectionKey());
        connectionMap.put(channel, attachmentModel);
    }

    /**
     * 定时清理长连接
     */
    public static void processClear(){
        if(timer != null){
            return;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(connectionMap.isEmpty()){
                    return;
                }
                Map<SocketChannel, AttachmentModel> doRemoveMap = new HashMap<>();
                for(Map.Entry<SocketChannel, AttachmentModel> entry : connectionMap.entrySet()){
                    AttachmentModel attachmentModel = entry.getValue();
                    if(attachmentModel == null){
                        continue;
                    }

                    if((System.currentTimeMillis() - attachmentModel.getCreateTime()) > timeout){
                        doRemoveMap.put(entry.getKey(), entry.getValue());
                    }
                }

                if(doRemoveMap.isEmpty()){
                    return;
                }

                for(Map.Entry<SocketChannel, AttachmentModel> entry : doRemoveMap.entrySet()){
                    connectionMap.remove(entry.getKey());
                    ChannelUtil.close(entry.getKey());
                    ChannelUtil.cancel(entry.getValue().getWorker().getSelectionKey());
                }
            }
        }, new Date(), 1000);
    }
}
