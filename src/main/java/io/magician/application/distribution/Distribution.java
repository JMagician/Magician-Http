package io.magician.application.distribution;

import io.magician.application.thread.HttpThread;
import io.magician.application.thread.BusinessThreadPool;
import io.magician.application.thread.WebsocketThread;
import io.magician.network.processing.exchange.HttpExchange;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;


/**
 * Handle requests from clients
 */
public class Distribution {

    /**
     * Handling HTTP
     * @param exchange
     */
    public static void execute(HttpExchange exchange){
        BusinessThreadPool.httpExecute(new HttpThread(exchange));
    }

    /**
     * Handling WebSocket
     * @return
     */
    public static void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        BusinessThreadPool.websocketExecute(new WebsocketThread(ctx, frame));
    }
}
