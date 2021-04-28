package io.magician.tcp.codec.impl.http;

import io.magician.common.constant.StatusEnums;
import io.magician.tcp.attach.AttachUtil;
import io.magician.tcp.attach.AttachmentModel;
import io.magician.tcp.workers.WorkersCacheManager;
import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;
import io.magician.tcp.codec.impl.websocket.WebSocketCodec;
import io.magician.tcp.codec.impl.websocket.connection.WebSocketExchange;
import io.magician.tcp.codec.ProtocolCodec;
import io.magician.tcp.workers.Worker;
import io.magician.tcp.codec.impl.http.parsing.HttpMessageParsing;
import io.magician.tcp.codec.impl.http.routing.RoutingParsing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.nio.channels.SelectionKey;

/**
 * HTTP协议解析器
 */
public class HttpProtocolCodec implements ProtocolCodec<Object> {

    private Logger logger = LoggerFactory.getLogger(HttpMessageParsing.class);

    /**
     * websocket解码器
     */
    private WebSocketCodec webSocketCodec = new WebSocketCodec();

    /**
     * 解析报文
     * @param worker
     * @return
     */
    public Object codecData(Worker worker) throws Exception {
        boolean webSocket = isWebSocket(worker);
        if(webSocket){
            /* 解析webSocket报文 */
            return webSocketCodec.codecData(worker);
        } else {
            /* 解析Http报文 */
            ByteArrayOutputStream outputStream = worker.getOutputStream();
            if(outputStream == null){
                return null;
            }

            MagicianHttpExchange magicianHttpExchange = new MagicianHttpExchange();
            magicianHttpExchange.setSocketChannel(worker.getSocketChannel());
            magicianHttpExchange.setSelectionKey(worker.getSelectionKey());

            magicianHttpExchange = new HttpMessageParsing(
                    magicianHttpExchange,
                    outputStream
            ).completed();

            /* 如果报文读完整了，就返回magicianHttpExchange，让业务可以往下走 */
            if(magicianHttpExchange != null){
                /*
                 * http要等前一个响应后才能发送下一个请求，所以不会出现粘包
                 * 获取完整报文后清除缓存即可
                 * 后续的报文只会在下次请求才会发过来
                 */
                WorkersCacheManager.clear(worker.getSocketChannel());
            }

            return magicianHttpExchange;
        }
    }

    /**
     * 执行handler
     * @param object
     */
    public void handler(Object object) throws Exception {
        if(object instanceof MagicianHttpExchange){
            /* 走Http流程 */
            RoutingParsing.parsing((MagicianHttpExchange)object);
        } else if(object instanceof WebSocketExchange){
            /* 走webSocket流程 */
            webSocketCodec.handler(object);
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
        /*
         * 在用http建立连接的时候，会将session存入SelectionKey的附件，
         * 所以如果附件没东西就不可能是WebSocket
         */
        if(attachmentModel == null){
            return false;
        }
        /* 如果附件里面的 WebSocketSession对象不为空，则肯定是webSocket */
        if(attachmentModel.getWebSocketSession() != null){
            return true;
        }
        return false;
    }
}
