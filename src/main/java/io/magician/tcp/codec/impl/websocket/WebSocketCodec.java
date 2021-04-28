package io.magician.tcp.codec.impl.websocket;

import io.magician.tcp.attach.AttachUtil;
import io.magician.tcp.attach.AttachmentModel;
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

/**
 * websocket协议解析器
 */
public class WebSocketCodec implements ProtocolCodec<Object> {

    /**
     * 解析协议报文
     * @param worker 一个工作者
     * @return
     * @throws Exception
     */
    @Override
    public Object codecData(Worker worker) throws Exception {
        AttachmentModel attachmentModel = AttachUtil.getAttachmentModel(worker.getSelectionKey());

        ByteArrayOutputStream outputStream = worker.getOutputStream();
        if(outputStream == null){
            return null;
        }

        WebSocketExchange webSocketExchange = new WebSocketExchange();
        if (attachmentModel != null) {
            webSocketExchange.setWebSocketSession(attachmentModel.getWebSocketSession());
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

    /**
     * 执行handler
     * @param object 解析出来的完整报文
     * @throws Exception
     */
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
