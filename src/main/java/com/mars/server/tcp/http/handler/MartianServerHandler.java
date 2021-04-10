package com.mars.server.tcp.http.handler;

/**
 * 自定义联络器接口
 */
public interface MartianServerHandler<T> {

    void request(T t);
}
