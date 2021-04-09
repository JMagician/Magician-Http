package com.mars.server.http.handler.ext;

import com.mars.server.http.handler.MartianServerHandler;

import java.nio.channels.AsynchronousSocketChannel;

/**
 * 普通的channel联络器
 */
public interface MartianServerChannelHandler extends MartianServerHandler<AsynchronousSocketChannel> {

    void request(AsynchronousSocketChannel channel);
}
