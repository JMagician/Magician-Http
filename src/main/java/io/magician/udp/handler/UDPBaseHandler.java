package io.magician.udp.handler;

import java.io.ByteArrayOutputStream;

/**
 * 自定义联络器接口
 */
public interface UDPBaseHandler {

    /**
     * 接收数据的方法
     * @param byteArrayOutputStream
     */
    void receive(ByteArrayOutputStream byteArrayOutputStream);
}
