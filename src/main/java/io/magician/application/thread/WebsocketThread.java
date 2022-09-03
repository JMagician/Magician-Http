package io.magician.application.thread;

import io.magician.common.cache.MagicianHandlerCache;
import io.magician.network.processing.exchange.WebSocketExchange;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;

/**
 * websocket business thread
 */
public class WebsocketThread implements Runnable {

    private ChannelHandlerContext ctx;

    private WebSocketFrame frame;

    public WebsocketThread(ChannelHandlerContext ctx, WebSocketFrame frame){
        this.ctx = ctx;
        this.frame = frame;
    }

    /**
     * Handling websocket messages
     */
    @Override
    public void run() {
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
