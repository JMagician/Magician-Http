package io.magician.tcp.codec.impl.websocket;

import io.magician.tcp.cache.ProtocolDataCacheManager;
import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;
import io.magician.tcp.codec.impl.websocket.connection.WebSocketExchange;
import io.magician.tcp.codec.impl.websocket.connection.WebSocketSession;
import io.magician.tcp.codec.impl.websocket.constant.WebSocketEnum;
import io.magician.tcp.codec.impl.websocket.parsing.WebSocketMessageParsing;
import io.magician.common.constant.CommonConstant;
import io.magician.common.util.ChannelUtil;
import io.magician.tcp.codec.ProtocolCodec;
import io.magician.tcp.cache.ProtocolDataModel;

import java.io.ByteArrayOutputStream;

public class WebSocketCodec implements ProtocolCodec<Object> {

    @Override
    public Object parsingData(ProtocolDataModel protocolDataModel) throws Exception {
        WebSocketExchange webSocketExchange = new WebSocketExchange();
        Object obj = protocolDataModel.getSelectionKey().attachment();
        if(obj != null){
            webSocketExchange.setWebSocketSession((WebSocketSession) obj);
        }

        webSocketExchange = new WebSocketMessageParsing(
                protocolDataModel,
                webSocketExchange
        ).completed();

        if(webSocketExchange != null){
            ProtocolDataCacheManager.remove(protocolDataModel.getSocketChannel());
        }

        return webSocketExchange;
    }

    @Override
    public void execute(Object object) throws Exception {
        WebSocketExchange webSocketExchange = (WebSocketExchange)object;
        WebSocketSession webSocketSession = webSocketExchange.getWebSocketSession();
        if(webSocketSession == null){
            return;
        }

        if(webSocketExchange.getWebSocketEnum().equals(WebSocketEnum.CLOSE)){

            webSocketSession.getWebSocketHandler().onClose(webSocketSession);
            MagicianHttpExchange magicianHttpExchange = webSocketSession.getMagicianHttpExchange();
            magicianHttpExchange.getSelectionKey().attach(null);
            ChannelUtil.destroy(magicianHttpExchange);

        } else if(webSocketExchange.getWebSocketEnum().equals(WebSocketEnum.MESSAGE)){
            ByteArrayOutputStream outputStream = webSocketExchange.getOutputStream();
            if(outputStream == null || outputStream.size() < 1){
                return;
            }
            webSocketSession.getWebSocketHandler()
                    .onMessage(
                            outputStream.toString(CommonConstant.ENCODING),
                            webSocketSession
                    );
        }
    }
}
