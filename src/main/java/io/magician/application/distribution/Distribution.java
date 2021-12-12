package io.magician.application.distribution;

import io.magician.application.request.MagicianRequest;
import io.magician.application.request.MagicianResponse;
import io.magician.common.cache.MagicianHandlerCache;
import io.magician.common.constant.WebSocketConstant;
import io.magician.network.handler.HttpBaseHandler;
import io.magician.network.handler.WebSocketBaseHandler;
import io.magician.network.processing.HttpExchange;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.websocketx.*;

/**
 * 分发器
 */
public class Distribution {

    /**
     * 执行handler
     * @param exchange
     */
    public static void execute(HttpExchange exchange){
        try {
            MagicianRequest request = new MagicianRequest();
            MagicianResponse response = new MagicianResponse();

            request.setHttpExchange(exchange);
            response.setHttpExchange(exchange);

            String path = getPath(request);


            if (isWebSocket(request)) {
                WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(request.getHttpExchange().getFullHttpRequest()), null, true, Integer.MAX_VALUE);
                WebSocketServerHandshaker webSocketServerHandshaker = wsFactory.newHandshaker(request.getHttpExchange().getFullHttpRequest());
                if (webSocketServerHandshaker == null) {
                    WebSocketServerHandshakerFactory
                            .sendUnsupportedVersionResponse(request.getHttpExchange().getChannelHandlerContext().channel());
                } else {
                    webSocketServerHandshaker.handshake(request.getHttpExchange().getChannelHandlerContext().channel(), request.getHttpExchange().getFullHttpRequest());
                }
                String channelId = request.getHttpExchange().getChannelHandlerContext().channel().id().asLongText();
                MagicianHandlerCache.addHandshakerMap(channelId, webSocketServerHandshaker);
                MagicianHandlerCache.addWebSocketBaseHandlerMap(channelId, MagicianHandlerCache.getWebSocketHandler(getPath(request)));
                return;
            }


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
            e.printStackTrace();
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

        if(!WebSocketConstant.UPGRADE.equals(connection)){
            return false;
        }

        return true;
    }

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

        WebSocketBaseHandler webSocketBaseHandler = MagicianHandlerCache.getWebSocketBaseHandlerrMap(channelId);

        // todo 搞一个websocketSession
        if (frame instanceof CloseWebSocketFrame) {
            webSocketBaseHandler.onClose();

            WebSocketServerHandshaker webSocketServerHandshaker = MagicianHandlerCache.getHandshakerMap(channelId);
            webSocketServerHandshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof TextWebSocketFrame) {
            webSocketBaseHandler.onMessage(((TextWebSocketFrame) frame).text());
            return;
        }
        if (frame instanceof BinaryWebSocketFrame) {
            ctx.write(frame.retain());
        }
    }
}
