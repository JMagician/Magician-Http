package io.magician.tcp.handler;

/**
 * 自定义联络器接口
 */
public interface MagicianHandler<T> {

    void request(T t);
}
