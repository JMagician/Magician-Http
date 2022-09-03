package io.magician.application.request;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Date;

/**
 * Websocket session management
 */
public class WebSocketSession {

    /**
     * Session ID, unique across all websocket connections
     */
    private String id;

    private ChannelHandlerContext channelHandlerContext;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    /**
     * Send binary data to client
     * @param message
     */
    public void sendBytes(byte[] message){
        ByteBuf byteBuf = Unpooled.buffer(message.length);
        byteBuf.writeBytes(message);
        channelHandlerContext.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
    }

    /**
     * Send string data to client
     * @param message
     */
    public void sendString(String message){
        channelHandlerContext.writeAndFlush(new TextWebSocketFrame(message));
    }
}
