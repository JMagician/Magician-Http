package io.magician.tcp.codec;

import io.magician.tcp.workers.Worker;

/**
 * 协议数据解码器
 * @param <T>
 */
public interface ProtocolCodec<T> {

    /**
     * 解码
     * @param worker 一个工作者
     * @return
     * @throws Exception
     */
    T codecData(Worker worker) throws Exception;

    /**
     * 解码完毕后，如果是一个完整的报文，则自动调用这个方法执行handler
     * @param t
     * @throws Exception
     */
    void handler(T t) throws Exception;
}
