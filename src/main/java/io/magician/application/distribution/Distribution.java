package io.magician.application.distribution;

import io.magician.application.request.MagicianRequest;
import io.magician.application.request.MagicianResponse;
import io.magician.application.request.WebSocketSession;
import io.magician.common.cache.MagicianHandlerCache;
import io.magician.common.constant.WebSocketConstant;
import io.magician.network.handler.HttpBaseHandler;
import io.magician.network.handler.WebSocketBaseHandler;
import io.magician.network.processing.exchange.HttpExchange;
import io.magician.network.processing.exchange.WebSocketExchange;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 分发器
 */
public class Distribution {

    private static Logger logger = LoggerFactory.getLogger(Distribution.class);

    /**
     * 执行handler
     * @param exchange
     */
    public static void execute(HttpExchange exchange){
        try {
            MagicianRequest request = new MagicianRequest();
            MagicianResponse response = new MagicianResponse();

            request.setHttpExchange(exchange, response);
            response.setHttpExchange(exchange);

            String path = getPath(request);

            if (isWebSocket(request)) {
                // 如果是websocket，就升级协议
                WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(request.getHttpExchange().getFullHttpRequest()), null, true, Integer.MAX_VALUE);
                WebSocketServerHandshaker webSocketServerHandshaker = wsFactory.newHandshaker(request.getHttpExchange().getFullHttpRequest());
                if (webSocketServerHandshaker == null) {
                    WebSocketServerHandshakerFactory
                            .sendUnsupportedVersionResponse(request.getHttpExchange().getChannelHandlerContext().channel());
                } else {
                    webSocketServerHandshaker.handshake(request.getHttpExchange().getChannelHandlerContext().channel(), request.getHttpExchange().getFullHttpRequest());
                }
                String channelId = request.getHttpExchange().getChannelHandlerContext().channel().id().asLongText();

                // 创建会话对象
                WebSocketSession webSocketSession = new WebSocketSession();
                webSocketSession.setChannelHandlerContext(exchange.getChannelHandlerContext());
                webSocketSession.setId(channelId);

                // 找到对应的handler
                WebSocketBaseHandler webSocketBaseHandler = MagicianHandlerCache.getWebSocketHandler(path);
                if (webSocketBaseHandler == null) {
                    logger.error("没有对应的handler: [" + path + "]");
                    return;
                }
                // 将session和handler 跟 此连接绑定，后续所有的消息都通过这个handler处理
                WebSocketExchange webSocketExchange = new WebSocketExchange();
                webSocketExchange.setWebSocketBaseHandler(webSocketBaseHandler);
                webSocketExchange.setWebSocketServerHandshaker(webSocketServerHandshaker);
                webSocketExchange.setWebSocketSession(webSocketSession);
                MagicianHandlerCache.addWebSocketSessionMap(channelId, webSocketExchange);

                webSocketBaseHandler.onOpen(webSocketSession);
                return;
            }

            // 如果是http请求，就直接执行handler
            HttpBaseHandler httpBaseHandler = MagicianHandlerCache.getHttpHandler(path);
            if (httpBaseHandler != null) {
                httpBaseHandler.request(request, response);
                return;
            }

            httpBaseHandler = MagicianHandlerCache.getHttpHandler("/");
            if (httpBaseHandler != null) {
                httpBaseHandler.request(request, response);
                return;
            }

            response.sendErrorMsg(404, "没有此handler:[" + path + "]");
        } catch (Exception e) {
            logger.error("处理请求出现异常", e);
            exchange.getChannelHandlerContext().close();
        }
    }

    /**
     * 获取请求路径
     * @param request
     * @return
     */
    private static String getPath(MagicianRequest request){
        String url = request.getUrl();
        int lastIndex = url.lastIndexOf("?");

        if (lastIndex > -1) {
            url = url.substring(0, lastIndex);
        }
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        return url;
    }

    /**
     * 是否是webSocket
     * @param request
     * @return
     */
    private static boolean isWebSocket(MagicianRequest request){
        if(!HttpMethod.GET.equals(request.getMethod())){
            return false;
        }

        HttpHeaders httpHeaders = request.getRequestHeaders();
        String upgrade = httpHeaders.get(WebSocketConstant.UPGRADE);
        String connection = httpHeaders.get(WebSocketConstant.CONNECTION);
        String swKey = httpHeaders.get(WebSocketConstant.SEC_WEBSOCKET_KEY);
        if(upgrade == null || connection == null || swKey == null){
            return false;
        }

        if(connection.toUpperCase().indexOf(WebSocketConstant.UPGRADE.toUpperCase()) == -1){
            return false;
        }

        return true;
    }

    /**
     * 获取WebSocketLocation，用于建立连接
     * @param req
     * @return
     */
    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HttpHeaderNames.HOST) + req.uri();
        return "ws://" + location;
    }

    /**
     * 处理webSocket
     * @return
     */
    public static void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        String channelId = ctx.channel().id().asLongText();

        WebSocketExchange webSocketExchange = MagicianHandlerCache.getWebSocketSessionMap(channelId);

        if (frame instanceof CloseWebSocketFrame) {
            webSocketExchange.getWebSocketBaseHandler().onClose(webSocketExchange.getWebSocketSession());
            webSocketExchange.getWebSocketServerHandshaker().close(ctx.channel(), (CloseWebSocketFrame) frame.retain());

            MagicianHandlerCache.removeWebSocketExchange(channelId);
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof TextWebSocketFrame || frame instanceof BinaryWebSocketFrame) {
            ByteBuf byteBuf = frame.content();
            byte[] bytes = null;
            if(byteBuf != null){
                bytes = ByteBufUtil.getBytes(byteBuf);
            }
            if(bytes == null){
                bytes = new byte[0];
            }
            webSocketExchange.getWebSocketBaseHandler().onMessage(webSocketExchange.getWebSocketSession(), bytes);
            return;
        }
    }
}
