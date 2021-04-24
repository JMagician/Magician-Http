package io.magician.tcp.protocol.parsing.impl;

import io.magician.common.constant.CommonConstant;
import io.magician.common.util.ChannelUtil;
import io.magician.tcp.http.request.MagicianHttpExchange;
import io.magician.tcp.protocol.model.ProtocolDataModel;
import io.magician.tcp.protocol.parsing.ProtocolParsing;
import io.magician.tcp.websocket.WebSocketExchange;
import io.magician.tcp.websocket.WebSocketSession;
import io.magician.tcp.websocket.constant.WebSocketEnum;
import io.magician.tcp.websocket.parsing.ReadCompletionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;


/**
 * WebSocket协议解析器
 */
public class WebSocketProtocolParsing implements ProtocolParsing<WebSocketExchange> {

    private Logger logger = LoggerFactory.getLogger(io.magician.tcp.http.parsing.ReadCompletionHandler.class);

    /**
     * 轻度解析报文，判断是否是WebSocket协议
     * @param protocolDataModel
     * @return
     */
    @Override
    public boolean isThis(ProtocolDataModel protocolDataModel) throws Exception {
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

    /**
     * 彻底解析报文
     * @param protocolDataModel
     * @return
     */
    @Override
    public WebSocketExchange parsingData(ProtocolDataModel protocolDataModel) throws Exception {
        try {
            /* 解析报文 */
            WebSocketExchange webSocketExchange = new WebSocketExchange();
            Object obj = protocolDataModel.getSelectionKey().attachment();
            if(obj != null){
                webSocketExchange.setWebSocketSession((WebSocketSession) obj);
            }

            return new ReadCompletionHandler(
                    protocolDataModel,
                    webSocketExchange
            ).completed();

        } catch (Exception e){
            logger.error("读取数据异常", e);
            throw e;
        }
    }

    /**
     * 执行handler
     * @param webSocketExchange
     */
    @Override
    public void execute(WebSocketExchange webSocketExchange) throws Exception {
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
