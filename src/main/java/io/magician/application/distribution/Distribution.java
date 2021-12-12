package io.magician.application.distribution;

import io.magician.application.request.MagicianRequest;
import io.magician.application.request.MagicianResponse;
import io.magician.common.cache.MagicianHandlerCache;
import io.magician.network.handler.HttpBaseHandler;
import io.magician.network.processing.HttpExchange;
import io.netty.channel.ChannelHandlerContext;
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
     * 处理webSocket
     * @return
     */
    public static void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
//            webSocketServerHandshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof TextWebSocketFrame) {
            System.out.println(ctx.name() + "\t" + ((TextWebSocketFrame) frame).text());

            return;
        }
        if (frame instanceof BinaryWebSocketFrame) {
            ctx.write(frame.retain());
        }
    }
}
