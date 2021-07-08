package io.magician.tcp.codec.impl.http;

import io.magician.tcp.TCPServerConfig;
import io.magician.tcp.attach.AttachUtil;
import io.magician.tcp.attach.AttachmentModel;
import io.magician.tcp.codec.impl.http.cache.HttpParsingCacheManager;
import io.magician.tcp.codec.impl.http.model.HttpParsingCacheModel;
import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;
import io.magician.tcp.codec.impl.websocket.WebSocketCodec;
import io.magician.tcp.codec.impl.websocket.connection.WebSocketExchange;
import io.magician.tcp.codec.ProtocolCodec;
import io.magician.tcp.workers.Worker;
import io.magician.tcp.codec.impl.http.parsing.HttpMessageParsing;
import io.magician.tcp.codec.impl.http.routing.RoutingParsing;

import java.io.ByteArrayOutputStream;
import java.nio.channels.SelectionKey;

/**
 * HTTP协议解析器
 */
public class HttpProtocolCodec implements ProtocolCodec<Object> {

    /**
     * websocket解码器
     */
    private WebSocketCodec webSocketCodec = new WebSocketCodec();

    /**
     * 用于分析当前http请求是否需要升级为webSocket
     */
    private static RoutingParsing routingParsing;

    /**
     * 解析报文
     * @param worker
     * @return
     */
    public Object codecData(Worker worker, TCPServerConfig tcpServerConfig) throws Exception {
        boolean webSocket = isWebSocket(worker);
        if(webSocket){
            /* 解析webSocket报文 */
            return webSocketCodec.codecData(worker, tcpServerConfig);
        } else {
            /* 解析Http报文 */
            ByteArrayOutputStream outputStream = worker.getOutputStream();
            if(outputStream == null || outputStream.size() < 1){
                return null;
            }

            HttpParsingCacheModel httpParsingCacheModel = HttpParsingCacheManager.getHttpParsingCacheModel(worker);
            MagicianHttpExchange magicianHttpExchange = httpParsingCacheModel.getMagicianHttpExchange();

            /* 解析报文，如果报文不完整就返回null，让本次事件停止 */
            magicianHttpExchange = HttpMessageParsing.completed(magicianHttpExchange, outputStream, worker);

            /*
             * http要等前一个响应后才能发送下一个请求，所以不会出现粘包
             * 获取完整报文后清除缓存即可
             * 后续的报文只会在下次请求才会发过来
             */
            if(magicianHttpExchange != null){
                HttpParsingCacheManager.clear(worker);
                worker.clear();
            }

            return magicianHttpExchange;
        }
    }

    /**
     * 执行handler
     * @param object
     */
    public void handler(Object object, TCPServerConfig tcpServerConfig) throws Exception {
        if(object instanceof MagicianHttpExchange){
            /* 走Http流程 */
            if(HttpProtocolCodec.routingParsing == null){
                HttpProtocolCodec.routingParsing = new RoutingParsing(tcpServerConfig);
            }
            HttpProtocolCodec.routingParsing.parsing((MagicianHttpExchange)object);
        } else if(object instanceof WebSocketExchange){
            /* 走webSocket流程 */
            webSocketCodec.handler(object, tcpServerConfig);
        }
    }

    /**
     * 是否是webSocket
     * @param worker
     * @return
     */
    private boolean isWebSocket(Worker worker){
        SelectionKey selectionKey = worker.getSelectionKey();
        if(selectionKey == null){
            return false;
        }

        AttachmentModel attachmentModel = AttachUtil.getAttachmentModel(selectionKey);

        /* 如果附件里面的 WebSocketSession对象不为空，则肯定是webSocket */
        if(attachmentModel.getWebSocketSession() != null){
            return true;
        }
        return false;
    }
}
