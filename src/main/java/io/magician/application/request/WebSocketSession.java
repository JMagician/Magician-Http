package io.magician.application.request;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Date;

public class WebSocketSession {

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

    public void sendBytes(byte[] message){
        ByteBuf byteBuf = Unpooled.buffer(message.length);
        byteBuf.writeBytes(message);
        channelHandlerContext.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
    }

    public void sendString(String message){
        channelHandlerContext.writeAndFlush(new TextWebSocketFrame(message));
    }
}
