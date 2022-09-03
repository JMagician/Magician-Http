package io.magician.network.processing;

import io.magician.common.config.MagicianConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Set netty as http service
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private MagicianConfig magicianConfig;

    public HttpServerInitializer(MagicianConfig magicianConfig){
        this.magicianConfig = magicianConfig;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new HttpServerCodec(magicianConfig.getMaxInitialLineLength(), magicianConfig.getMaxHeaderSize(), magicianConfig.getMaxChunkSize())); // http codec
        pipeline.addLast("httpAggregator",new HttpObjectAggregator(Integer.MAX_VALUE)); // http message aggregator
        pipeline.addLast("http-chunked",new ChunkedWriteHandler());
        pipeline.addLast(new HttpRequestHandler()); // process the request
    }
}
