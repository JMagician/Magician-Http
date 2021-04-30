package io.magician.tcp.codec.impl.http;

import io.magician.tcp.TCPServerConfig;
import io.magician.tcp.attach.AttachUtil;
import io.magician.tcp.attach.AttachmentModel;
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
     * 配置
     */
    private TCPServerConfig tcpServerConfig;

    /**
     * 用于分析当前http请求是否需要升级为webSocket
     */
    private RoutingParsing routingParsing;

    public HttpProtocolCodec(TCPServerConfig tcpServerConfig){
        this.tcpServerConfig = tcpServerConfig;
        this.routingParsing = new RoutingParsing(this.tcpServerConfig);
    }

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
            if(outputStream == null || outputStream.size() < 1){
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
                worker.clear();
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
            routingParsing.parsing((MagicianHttpExchange)object);
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

        /* 如果附件里面的 WebSocketSession对象不为空，则肯定是webSocket */
        if(attachmentModel.getWebSocketSession() != null){
            return true;
        }
        return false;
    }
}
