package io.magician.tcp.protocol.execute;

import io.magician.tcp.protocol.parsing.ProtocolParsing;

public class ExecuteModel {

    /**
     * 协议解析器
     */
    private ProtocolParsing protocolParsing;

    /**
     *
     */
    private Object data;

    public ProtocolParsing getProtocolParsing() {
        return protocolParsing;
    }

    public void setProtocolParsing(ProtocolParsing protocolParsing) {
        this.protocolParsing = protocolParsing;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
