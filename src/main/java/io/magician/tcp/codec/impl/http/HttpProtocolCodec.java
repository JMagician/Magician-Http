package io.magician.tcp.codec.impl.http;

import io.magician.tcp.cache.ProtocolDataCacheManager;
import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;
import io.magician.common.util.ChannelUtil;
import io.magician.tcp.codec.impl.websocket.WebSocketCodec;
import io.magician.tcp.codec.impl.websocket.connection.WebSocketExchange;
import io.magician.tcp.codec.impl.websocket.connection.WebSocketSession;
import io.magician.tcp.codec.ProtocolCodec;
import io.magician.tcp.cache.ProtocolDataModel;
import io.magician.tcp.codec.impl.http.parsing.HttpMessageParsing;
import io.magician.tcp.codec.impl.http.routing.RoutingParsing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * @param protocolDataModel
     * @return
     */
    public Object parsingData(ProtocolDataModel protocolDataModel) throws Exception {
        try {
            boolean webSocket = isWebSocket(protocolDataModel);
            if(webSocket){
                /* 解析webSocket报文 */
                return webSocketCodec.parsingData(protocolDataModel);
            } else {
                /* 解析Http报文 */
                MagicianHttpExchange magicianHttpExchange = new MagicianHttpExchange();
                magicianHttpExchange.setSocketChannel(protocolDataModel.getSocketChannel());
                magicianHttpExchange.setSelectionKey(protocolDataModel.getSelectionKey());

                magicianHttpExchange = new HttpMessageParsing(
                        magicianHttpExchange,
                        protocolDataModel.getByteArrayOutputStream()
                ).completed();

                if(magicianHttpExchange != null){
                    ProtocolDataCacheManager.remove(protocolDataModel.getSocketChannel());
                }

                return magicianHttpExchange;
            }
        } catch (Exception e){
            logger.error("解析报文异常", e);
            ChannelUtil.close(protocolDataModel.getSocketChannel());
            ChannelUtil.cancel(protocolDataModel.getSelectionKey());
            throw e;
        }
    }

    /**
     * 执行handler
     * @param object
     */
    public void execute(Object object) throws Exception {
        if(object instanceof MagicianHttpExchange){
            /* 走Http流程 */
            RoutingParsing.parsing((MagicianHttpExchange)object);
        } else if(object instanceof WebSocketExchange){
            /* 走webSocket流程 */
            webSocketCodec.execute(object);
        }
    }

    /**
     * 是否是webSocket
     * @param protocolDataModel
     * @return
     */
    private boolean isWebSocket(ProtocolDataModel protocolDataModel){
        Object obj = protocolDataModel.getSelectionKey().attachment();
        /*
         * 在用http建立连接的时候，会将session存入SelectionKey的附件，
         * 所以如果附件没东西就不可能是WebSocket
         */
        if(obj == null){
            return false;
        }
        /* 如果附件是 WebSocketSession 类型，那肯定是WebSocket */
        if(obj instanceof WebSocketSession){
            return true;
        }
        return false;
    }
}
