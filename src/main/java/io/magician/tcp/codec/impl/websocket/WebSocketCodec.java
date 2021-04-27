package io.magician.tcp.codec.impl.websocket;

import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;
import io.magician.tcp.codec.impl.websocket.connection.WebSocketExchange;
import io.magician.tcp.codec.impl.websocket.connection.WebSocketSession;
import io.magician.tcp.codec.impl.websocket.constant.WebSocketEnum;
import io.magician.tcp.codec.impl.websocket.parsing.WebSocketMessageParsing;
import io.magician.common.constant.CommonConstant;
import io.magician.common.util.ChannelUtil;
import io.magician.tcp.codec.ProtocolCodec;
import io.magician.tcp.workers.Worker;

import java.io.ByteArrayOutputStream;

public class WebSocketCodec implements ProtocolCodec<Object> {

    @Override
    public Object codecData(Worker worker) throws Exception {
        Object obj = worker.getSelectionKey().attachment();

        ByteArrayOutputStream outputStream = worker.getOutputStream();
        WebSocketExchange webSocketExchange = new WebSocketExchange();
        if (obj != null) {
            webSocketExchange.setWebSocketSession((WebSocketSession) obj);
        }

        webSocketExchange = new WebSocketMessageParsing(
                outputStream,
                webSocketExchange
        ).completed();

        /* 如果拿到了一个完整的报文，就从缓存中去除这段数据 */
        if (webSocketExchange != null) {
            ByteArrayOutputStream content = webSocketExchange.getOutputStream();
            if(content != null){
                worker.skipOutputStream(webSocketExchange.getLength());
            }
        }
        return webSocketExchange;
    }

    @Override
    public void handler(Object object) throws Exception {
        WebSocketExchange webSocketExchange = (WebSocketExchange)object;
        WebSocketSession webSocketSession = webSocketExchange.getWebSocketSession();
        if(webSocketSession == null){
            return;
        }

        if(webSocketExchange.getWebSocketEnum().equals(WebSocketEnum.CLOSE)){
            /* 关闭socket */
            webSocketSession.getWebSocketHandler().onClose(webSocketSession);
            MagicianHttpExchange magicianHttpExchange = webSocketSession.getMagicianHttpExchange();
            magicianHttpExchange.getSelectionKey().attach(null);
            ChannelUtil.destroy(magicianHttpExchange);
        } else if(webSocketExchange.getWebSocketEnum().equals(WebSocketEnum.MESSAGE)){
            /* 接收消息 */
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
