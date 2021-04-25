package io.magician.tcp.protocol.threads.handler;

import io.magician.tcp.protocol.codec.ProtocolCodec;

public class ExecuteModel {

    /**
     * 协议解析器
     */
    private ProtocolCodec protocolCodec;

    /**
     *
     */
    private Object data;

    public ProtocolCodec getProtocolCodec() {
        return protocolCodec;
    }

    public void setProtocolCodec(ProtocolCodec protocolCodec) {
        this.protocolCodec = protocolCodec;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
