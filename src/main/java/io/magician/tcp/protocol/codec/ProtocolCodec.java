package io.magician.tcp.protocol.codec;

import io.magician.tcp.protocol.model.ProtocolDataModel;

/**
 * 协议数据解码器
 * @param <T>
 */
public interface ProtocolCodec<T> {

    /**
     * 解码
     * @param protocolDataModel NIO读到的数据
     * @return
     * @throws Exception
     */
    T parsingData(ProtocolDataModel protocolDataModel) throws Exception;

    /**
     * 解码完毕后，如果是一个完整的报文，则自动调用这个方法执行handler
     * @param t
     * @throws Exception
     */
    void execute(T t) throws Exception;
}
