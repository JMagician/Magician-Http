package io.magician.tcp.codec.impl.http.routing;

import io.magician.tcp.TCPServerConfig;
import io.magician.tcp.attach.AttachUtil;
import io.magician.tcp.attach.AttachmentModel;
import io.magician.tcp.codec.impl.http.parsing.param.ParamParsing;
import io.magician.tcp.codec.impl.websocket.connection.WebSocketSession;
import io.magician.tcp.handler.MagicianHandler;
import io.magician.tcp.codec.impl.http.parsing.HttpMessageWrite;
import io.magician.tcp.codec.impl.http.request.MagicianHttpExchange;
import io.magician.tcp.codec.impl.http.request.MagicianRequest;
import io.magician.tcp.codec.impl.websocket.handler.WebSocketHandler;
import io.magician.tcp.codec.impl.websocket.parsing.WebSocketMessageWrite;

/**
 * 根据不同的协议执行不同的逻辑
 */
public class RoutingJump {

    /**
     * 配置类
     */
    private TCPServerConfig tcpServerConfig;

    /**
     * 参数解析
     */
    private ParamParsing paramParsing;

    public RoutingJump(TCPServerConfig tcpServerConfig){
        this.tcpServerConfig = tcpServerConfig;
        paramParsing = new ParamParsing(this.tcpServerConfig);
    }

    /**
     * webSocket处理
     * @param httpExchange
     * @param webSocketHandler
     */
    public void websocket(MagicianHttpExchange httpExchange, WebSocketHandler webSocketHandler) throws Exception {
        WebSocketSession socketSession = new WebSocketSession();
        socketSession.setMagicianHttpExchange(httpExchange);
        socketSession.setWebSocketHandler(webSocketHandler);

        /* 将session加入附件 */
        AttachmentModel attachmentModel = AttachUtil.getAttachmentModel(httpExchange.getSelectionKey());
        attachmentModel.setWebSocketSession(socketSession);

        WebSocketMessageWrite.builder(socketSession).completed();
        webSocketHandler.onOpen(socketSession);
    }

    /**
     * http处理
     * @param httpExchange
     * @throws Exception
     */
    public void http(MagicianHttpExchange httpExchange, MagicianHandler serverHandler) throws Exception {
        /* 执行handler */
        MagicianRequest magicianRequest = new MagicianRequest();
        magicianRequest.setMartianHttpExchange(httpExchange);
        magicianRequest = paramParsing.getMagicianRequest(magicianRequest);

        serverHandler.request(magicianRequest);
        /* 响应数据 */
        HttpMessageWrite.builder(httpExchange).completed();
    }
}
