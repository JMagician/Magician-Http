package io.magician.tcp.protocol.parsing;

import io.magician.tcp.protocol.model.ProtocolDataModel;

public interface ProtocolParsing<T> {

    boolean isThis(ProtocolDataModel protocolDataModel) throws Exception;

    T parsingData(ProtocolDataModel protocolDataModel) throws Exception;

    void execute(T t) throws Exception;
}
