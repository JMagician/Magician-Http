package io.magician.tcp.handler;

/**
 * 自定义处理器接口
 */
public interface TCPBaseHandler<T> {

    void request(T t);
}
